package main;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import service.ChatServiceImpl;

import java.io.IOException;
import java.util.logging.Level;

public class ChatMain {
    public static void main(String[] args) {

        try {
            Server server = ServerBuilder.forPort(8999).addService(new ChatServiceImpl()).build();
            server.start();
            ChatServiceImpl.logger.log(Level.INFO, "Server started at " + server.getPort());
            server.awaitTermination();
        } catch (IOException | InterruptedException e) {
            ChatServiceImpl.logger.log(Level.INFO, "Error: " + e);
        }
    }
}
