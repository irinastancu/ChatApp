package server.Tasks;

import com.victorlaerte.asynctask.AsyncTask;
import controllers.MainController;
import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import proto.Chat;
import server.Connector;

public class ChatTasks {
    private Connector connector;
    private MainController controller;


    private static int loadingBooksCounter = 0;

    public ChatTasks(MainController controller) {

        this.connector = new Connector();
        this.controller = controller;

    }
    public ChatTasks(Connector connector, MainController controller) {
        this.connector = connector;
        this.controller = controller;

    }

    //V1
    public void sendMessage(){


        StreamObserver<Chat.ChatMessage> chat = connector.getStub().chat(new StreamObserver<Chat.ChatMessageFromServer>() {
            @Override
            public void onNext(Chat.ChatMessageFromServer value) {
                Platform.runLater(() -> {
                    if(!controller.getUsers().contains(controller.getName().getText())){
                        controller.getUsers().add(controller.getName().getText());
                        controller.getUsersList().scrollTo(controller.getUsers().size());
                    }
                    controller.getMessages().add(value.getMessage().getFrom() + ": " + value.getMessage().getMessage());
                    controller.getMessageView().scrollTo(controller.getMessages().size());
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

    }
       //V2
    public void sendMessages(){
        new AsyncTask<>(){

            @Override
            public void onPreExecute() {

            }

            @Override
            public Object doInBackground(Object... objects) {
                connector.getStub().chat(new StreamObserver<Chat.ChatMessageFromServer>() {
                    @Override
                    public void onNext(Chat.ChatMessageFromServer value) {
                        Platform.runLater(() -> {
                            if(!controller.getUsers().contains(controller.getName().getText())){
                                controller.getUsers().add(controller.getName().getText());
                                controller.getUsersList().scrollTo(controller.getUsers().size());
                            }
                            controller.getMessages().add(value.getMessage().getFrom() + ": " + value.getMessage().getMessage());
                            controller.getMessageView().scrollTo(controller.getMessages().size());
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
                return null;
            }

            @Override
            public void onPostExecute(Object o) {

            }

            @Override
            public void progressCallback(Object... objects) {

            }
        }.execute();
    }

}
