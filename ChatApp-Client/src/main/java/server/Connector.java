package server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import proto.ChatServiceGrpc;

public class Connector {
    private ManagedChannel channel;
    private ChatServiceGrpc.ChatServiceStub stub;

    public Connector() {
        /* Default Values for connection */

        channel = ManagedChannelBuilder.forAddress("localhost", 8999).usePlaintext().build();
        stub = ChatServiceGrpc.newStub(channel);
    }

    public Connector(ManagedChannel channel, ChatServiceGrpc.ChatServiceStub stub) {
        this.channel = channel;
        this.stub = stub;
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public void setChannel(ManagedChannel channel) {
        this.channel = channel;
    }

    public ChatServiceGrpc.ChatServiceStub getStub() {
        return stub;
    }

    public void setStub(ChatServiceGrpc.ChatServiceStub stub) {
        this.stub = stub;
    }

    public void shutDown(){
        this.channel.shutdown();
    }
}
