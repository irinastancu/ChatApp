package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import server.Tasks.ChatTasks;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private TextField name;

    @FXML
    private TextField message;

    @FXML
    private Button send;

    @FXML
    private ListView<String> usersList;

    @FXML
    private ListView<String> messageView;

    ChatTasks chatTasks;

    private ObservableList<String> messages = FXCollections.observableArrayList();
    private ObservableList<String> users = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chatTasks=new ChatTasks(this);
    }
    public void onSendButtonPressed(MouseEvent mouseEvent) {
        messageView.setItems(messages);
        usersList.setItems(users);

        //-----------> Decomment this block to show how it works without client-server architecture
       /* if(!users.contains(name.getText())){
           users.add(name.getText());
            usersList.scrollTo(users.size());
        }
        messages.add(name.getText() + ": " + message.getText());
        messageView.scrollTo(messages.size());
        message.clear();*/

       //------------> Decomment one of these lines to show how it runs with many clients
        //V1
        // chatTasks.sendMessage();

        //V2
        //chatTasks.sendMessages();
    }

    public TextField getName() {
        return name;
    }

    public void setName(TextField name) {
        this.name = name;
    }

    public TextField getMessage() {
        return message;
    }

    public void setMessage(TextField message) {
        this.message = message;
    }

    public Button getSend() {
        return send;
    }

    public void setSend(Button send) {
        this.send = send;
    }

    public ListView<String> getUsersList() {
        return usersList;
    }

    public void setUsersList(ListView<String> usersList) {
        this.usersList = usersList;
    }

    public ListView<String> getMessageView() {
        return messageView;
    }

    public void setMessageView(ListView<String> messageView) {
        this.messageView = messageView;
    }

    public ChatTasks getChatTasks() {
        return chatTasks;
    }

    public void setChatTasks(ChatTasks chatTasks) {
        this.chatTasks = chatTasks;
    }

    public ObservableList<String> getMessages() {
        return messages;
    }

    public void setMessages(ObservableList<String> messages) {
        this.messages = messages;
    }

    public ObservableList<String> getUsers() {
        return users;
    }

    public void setUsers(ObservableList<String> users) {
        this.users = users;
    }





}
