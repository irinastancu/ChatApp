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
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import proto.Chat;
import proto.ChatServiceGrpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientMain extends Application {
    private ObservableList<TextFlow> messages = FXCollections.observableArrayList();
    private ObservableList<String> users = FXCollections.observableArrayList();

    private ListView<TextFlow> messagesView = new ListView<>();
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
                    messages.add(checkMessage(new Text(value.getMessage().getFrom() + ": " + value.getMessage().getMessage())));
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

    public TextFlow checkMessage(Text message){
        List<Pattern> patterns = new ArrayList<Pattern>();

        patterns.add(Pattern.compile("\\ \\*.+\\*\\ ",Pattern.CASE_INSENSITIVE)); //BOLD
        patterns.add(Pattern.compile("\\ \\_.+\\_\\ ",Pattern.CASE_INSENSITIVE)); //ITALIC
        patterns.add(Pattern.compile("\\ \\'.+\\'\\ ",Pattern.CASE_INSENSITIVE)); //UNDERLINE
        patterns.add(Pattern.compile("\\ \\~.+\\~\\ ",Pattern.CASE_INSENSITIVE)); //STRIKETHROUGH

        TextFlow flow = new TextFlow(message);

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher((CharSequence) ((Text) flow.getChildren().get(getLastIndexOfTextFlow(flow))).getText());
            if(matcher.find()) {
                flow = formatMessage(flow, matcher, pattern);
            }
        }

        return flow;
    }

    public TextFlow formatMessage(TextFlow message, Matcher matcher, Pattern pattern){
        Text text = (Text)message.getChildren().get(getLastIndexOfTextFlow(message));

        String formatString = text.getText();
        Text frontText = new Text(formatString.substring(0,matcher.start()+1));
        Text middleText = new Text(formatString.substring(matcher.start()+2,matcher.end()-2));
        Text endText = new Text(formatString.substring(matcher.end()-1,formatString.length()));

        //TextFlowcomboFlow=newTextFlow();
        //comboFlow=checkMessage(middleText);

        switch (pattern.pattern()) {
            case "\\ \\*.+\\*\\ ":
                middleText.setStyle("-fx-font-weight: bold");
                break;
            case "\\ \\_.+\\_\\ " :
                middleText.setFont(Font.font("Segue UI", FontPosture.ITALIC, 12));
                break;
            case "\\ \\'.+\\'\\ " :
                middleText.setUnderline(true);
                break;
            case "\\ \\~.+\\~\\ " :
                middleText.setStrikethrough(true);
                break;
        }
        message.getChildren().remove(getLastIndexOfTextFlow(message));
        message.getChildren().addAll(frontText,middleText,endText);

        //List<Text>textList=newArrayList<Text>();
        //for(javafx.scene.NodecomboText:comboFlow.getChildren()){
        //TexttextToAdd=(Text)comboText;
        //textList.add(textToAdd);
        //}

        //message.getChildren().addAll(textList);
        //message.getChildren().add(endText);

        return message;
    }

    public int getLastIndexOfTextFlow(TextFlow text) {
        int counter = 0;

        for(javafx.scene.Node node : text.getChildren()) {
            counter++;
        }
        return counter - 1;
    }
}
