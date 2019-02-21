package com.sample.grpc.greeting.client;

import com.proto.dummy.Dummy;
import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.Greet;
import com.proto.greet.GreetServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    ManagedChannel managedChannel;

    public static void main(String[] args){
        System.out.println("Grpc cLIENT");

        new GreetingClient().run();
        //  DummyServiceGrpc.DummyServiceBlockingStub syncClient
        //        = DummyServiceGrpc.newBlockingStub(managedChannel);
        // do something

    }


    private void run(){
        managedChannel = ManagedChannelBuilder.forAddress("localhost",6565)
                .usePlaintext()
                .build();
        //doUnaryCall();
        //doServerStreamingCall(managedChannel);
        doClientStreamingCall(managedChannel);
        System.out.println("Shutting down channel");
        managedChannel.shutdown();

    }

    private void doClientStreamingCall(ManagedChannel managedChannel){
        // create a client;
        GreetServiceGrpc.GreetServiceStub asynClient = GreetServiceGrpc.newStub(managedChannel);

        CountDownLatch latch = new CountDownLatch(1);


        StreamObserver<Greet.LongGreetRequest> requestStreamObserver =
        asynClient.longGreet(new StreamObserver<Greet.LongGreetResponse>() {
            @Override
            public void onNext(Greet.LongGreetResponse value) {
                System.out.println("Received response from server ");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has compleeted sending the messaeg ");
                latch.countDown();
            }
        });

        Greet.Greeting greeting = Greet.Greeting.newBuilder().setFirstName("Jay").setLastName("Saraiya").build();

        requestStreamObserver.onNext(Greet.LongGreetRequest.newBuilder().setGreeting(
                Greet.Greeting.newBuilder().setFirstName("Jay").setLastName("Saraiya").build())
                .build());
        requestStreamObserver.onNext(Greet.LongGreetRequest.newBuilder().setGreeting(
                Greet.Greeting.newBuilder().setFirstName("Mark").setLastName("Payton").build())
                .build());

        requestStreamObserver.onNext(Greet.LongGreetRequest.newBuilder().setGreeting(
                Greet.Greeting.newBuilder().setFirstName("John").setLastName("Engler").build())
                .build());

        requestStreamObserver.onNext(Greet.LongGreetRequest.newBuilder().setGreeting(
                Greet.Greeting.newBuilder().setFirstName("Roy").setLastName("Edwards").build())
                .build());
        requestStreamObserver.onCompleted();
        try {
            latch.await(3l, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doServerStreamingCall(ManagedChannel managedChannel)
    {
        GreetServiceGrpc.GreetServiceBlockingStub greetClient
                = GreetServiceGrpc.newBlockingStub(managedChannel);

        Greet.Greeting greeting = Greet.Greeting.newBuilder().setFirstName("Jay").setLastName("Saraiya").build();
//
//        Greet.GreetResponse response = greetClient.greet(Greet.GreetRequest.newBuilder().setGreeting(greeting).build());
//        System.out.println(response.getResult());
        Greet.GreetManyTimesRequest greetManyTimesRequest = Greet.GreetManyTimesRequest.newBuilder().setGreeting(greeting).build();
        greetClient.greetManyTimes(greetManyTimesRequest).forEachRemaining(response-> System.out.println(response.getResult()));

    }

}
