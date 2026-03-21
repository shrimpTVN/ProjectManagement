package com.server;
import com.server.core.ChatServer;

public class Main {
    public static void main(String[] args) {
        ChatServer server = new ChatServer(8080);
        server.start();
    }
}
