# grpc-samples

This sample demonstrates the simple implementation of gRPC service with SpringBoot and with out SpringBoot(with simple Java).

* Notice the defintions of greet.proto unders src/main/proto directory.  
The other proto parts are easy to understand. For gRPC streaming point of view, notice the use of word `stream` to indicate the streaming nature of data in either request or response side.

```script
service GreetService{
    //Unary
    rpc Greet(GreetRequest) returns (GreetResponse) {};

    rpc GreetManyTimes(GreetManyTimesRequest) returns (stream GreetManyTimesResponse) {};

    rpc LongGreet(stream LongGreetRequest) returns (LongGreetResponse) {};
}

```

* The project includes the gradle file with required dependancies and functions to generate gRPC server implementation code. The following protobuf-gradle-plugin generates the implementation classes. Please check the current version of this plugin. For new apps, you should use the latest version.
```
classpath 'com.google.protobuf:protobuf-gradle-plugin:0.8.8'
```
Also notice the inclusion of protobuf task that complies the protobuf and generates the gRPC implemented classes.

```
protobuf {
	protoc {
		artifact = 'com.google.protobuf:protoc:3.5.1'
	}
	plugins {
		grpc {
			artifact = "io.grpc:protoc-gen-grpc-java:1.18.0"
		}
	}

	generateProtoTasks {
		ofSourceSet('main').each { task ->
			task.builtins {
				java{
					outputSubDir = 'protoGen'
				}
			}
			task.plugins {
				grpc {
					outputSubDir = 'protoGen'
				}
			}
		}
	}
	generatedFilesBaseDir = "$projectDir/src/"
}

```

#### Serverside streaming Handling
I think, it will be more useful to explain how the streaming is implemented by going through one of the server side function.

```java
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
	```
If you look at the type defintion of function, we get <StreamObserver> type as request and response. As defined in proto, client will send stream of data. However, we can not assume that server will be able to respond only at the end of the clients stream. This is why server should have ability to react in the stream of response. Server can choose to respond at the end or at every defined intevals. 

* How to run Spring-Boot Server
You can run the grpc-spring project and start the spring-boot server through your ide or by ./gradlew run command
* How to run client
You can download grpc-java project in your ide and run the main() method on client.
The spring-boot server starts by default  at port 6565. Make sure you specify the correct cliet port as shown below.
``` code
 managedChannel = ManagedChannelBuilder.forAddress("localhost",6565)
                .usePlaintext()
                .build();
```
* How to run java-server and java-client with gRPC
You can use the grpc-java project from above. This has a server implementation. The server runs on port 50051. Make the change in client calling code to use the same port.

```java
 managedChannel = ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext()
                .build();

```
