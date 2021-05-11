package main;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import proto.Chat;
import proto.ChatServiceGrpc;

public class ClientMain extends Application {
    private ObservableList<String> messages = FXCollections.observableArrayList();
    private ListView<String> messagesView = new ListView<>();
    private TextField name = new TextField("nickname");
    private TextField message = new TextField();
    private Button send = new Button();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        messagesView.setItems(messages);

        send.setText("Send");

        BorderPane pane = new BorderPane();
        pane.setLeft(name);
        pane.setCenter(message);
        pane.setRight(send);

        BorderPane root = new BorderPane();
        root.setCenter(messagesView);
        root.setBottom(pane);

        Scene scene = new Scene(root, 500, 500);

        primaryStage.setTitle("Chat");
        primaryStage.setScene(scene);


        primaryStage.show();

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8999).usePlaintext().build();
        ChatServiceGrpc.ChatServiceStub chatService = ChatServiceGrpc.newStub(channel);

        StreamObserver<Chat.ChatMessage> chat = chatService.chat(new StreamObserver<Chat.ChatMessageFromServer>() {
            @Override
            public void onNext(Chat.ChatMessageFromServer value) {
                Platform.runLater(() -> {
                    messages.add(value.getMessage().getFrom() + ": " + value.getMessage().getMessage());
                    messagesView.scrollTo(messages.size());
                });
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                System.out.println("Disconnected");
            }

            @Override
            public void onCompleted() {
                System.out.println("Disconnected");
            }
        });

        send.setOnAction(e -> {
            chat.onNext(Chat.ChatMessage.newBuilder().setFrom(name.getText()).setMessage(message.getText()).build());
            message.setText("");
        });
        primaryStage.setOnCloseRequest(e -> {chat.onCompleted(); channel.shutdown(); });
    }
}
