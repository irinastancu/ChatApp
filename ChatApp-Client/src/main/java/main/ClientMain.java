package main;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import proto.Chat;
import proto.ChatServiceGrpc;

import java.io.IOException;

public class ClientMain extends Application {
    private ObservableList<String> messages = FXCollections.observableArrayList();
    private ObservableList<String> users = FXCollections.observableArrayList();

    private ListView<String> messagesView = new ListView<>();
    private ListView<String> usersView = new ListView<>();
    private TextField name = new TextField("YourName");
    private TextField message = new TextField();
    private Button send = new Button();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        messagesView.setItems(messages);
        usersView.setItems(users);

        usersView.setStyle("-fx-background-color: linear-gradient(from 0px 0px to 300px 200px, #73D4FFff, #958BE8ff); -fx-control-inner-background: transparent");

        send.setText("Send");
        send.setStyle("-fx-text-fill: BLACK; -fx-background-color: SKYBLUE");

        BorderPane pane = new BorderPane();
        pane.setLeft(name);
        pane.setCenter(message);
        pane.setRight(send);

        BorderPane root = new BorderPane();
        root.setCenter(messagesView);
        root.setLeft(usersView);
        root.setBottom(pane);
        BorderPane.setAlignment(usersView, Pos.BOTTOM_LEFT);

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setTitle("Chat");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/gui/logos/chatapplogo.png")));

        primaryStage.show();

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8999).usePlaintext().build();
        ChatServiceGrpc.ChatServiceStub chatService = ChatServiceGrpc.newStub(channel);
        StreamObserver<Chat.ChatMessage> chat = chatService.chat(new StreamObserver<Chat.ChatMessageFromServer>() {
            @Override
            public void onNext(Chat.ChatMessageFromServer value) {
                Platform.runLater(() -> {
                    messages.add(value.getMessage().getFrom() + ": " + value.getMessage().getMessage());
                    messagesView.scrollTo(messages.size());

                    if (!users.contains(name.getText()) && !name.getText().equals("YourName")) {
                        users.add(name.getText());
                        usersView.scrollTo(users.size());

                    }

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
        primaryStage.setOnCloseRequest(e -> {
            chat.onCompleted();
            channel.shutdown();
        });
    }

}
