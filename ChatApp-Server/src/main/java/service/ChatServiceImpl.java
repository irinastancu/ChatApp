package service;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import proto.Chat;
import proto.ChatServiceGrpc;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

    private Logger logger = Logger.getLogger(ChatServiceImpl.class.getName());
    private static Set<StreamObserver<Chat.ChatMessageFromServer>> observers = ConcurrentHashMap.newKeySet();

    @Override
    public StreamObserver<Chat.ChatMessage> chat(StreamObserver<Chat.ChatMessageFromServer> responseObserver) {
        observers.add(responseObserver);

        return new StreamObserver<Chat.ChatMessage>() {
            @Override
            public void onNext(Chat.ChatMessage value) {
                //System.out.println(value);
                logger.info(value.toString());
                Chat.ChatMessageFromServer message = Chat.ChatMessageFromServer.newBuilder()
                        .setMessage(value)
                        .setTimestamp(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000))
                        .build();

                for (StreamObserver<Chat.ChatMessageFromServer> observer : observers) {
                    observer.onNext(message);
                }
            }

            @Override
            public void onError(Throwable t) {
                //TBA
            }

            @Override
            public void onCompleted() {
                observers.remove(responseObserver);
            }
        };
    }
}

