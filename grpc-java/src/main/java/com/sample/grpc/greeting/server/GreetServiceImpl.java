package com.sample.grpc.greeting.server;

import com.proto.greet.Greet;
import com.proto.greet.GreetServiceGrpc;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(Greet.GreetRequest request, StreamObserver<Greet.GreetResponse> responseObserver) {
        Greet.Greeting greeting = request.getGreeting();
        String result = "Hello" + greeting.getFirstName();

        Greet.GreetResponse response =  Greet.GreetResponse.newBuilder().setResult(result).build();
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(Greet.GreetManyTimesRequest request, StreamObserver<Greet.GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();
        try {
            for (int i = 0; i < 10; i++) {
                String result = "Hello " + firstName + ", response Number : " + i;
                Greet.GreetManyTimesResponse greetManyTimesResponse = Greet.GreetManyTimesResponse.newBuilder().setResult(result).build();
                responseObserver.onNext(greetManyTimesResponse);
                Thread.sleep(1000l);
            }
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
        finally {
            responseObserver.onCompleted();
        }

    }

    @Override
    public StreamObserver<Greet.LongGreetRequest> longGreet(StreamObserver<Greet.LongGreetResponse> responseObserver) {
        StreamObserver<Greet.LongGreetRequest> longGreetRequestStreamObserver =
                new StreamObserver<Greet.LongGreetRequest>() {

                    String result = " Hellow, ";
                    @Override
                    public void onNext(Greet.LongGreetRequest value) {
                              result += "Hello , "+ value.getGreeting().getFirstName()+" "+ value.getGreeting().getLastName();
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onCompleted() {
                            responseObserver.onNext(Greet.LongGreetResponse.newBuilder().setResult(result).build());
                            responseObserver.onCompleted();
                    }
                };

        return longGreetRequestStreamObserver;
    }
}
