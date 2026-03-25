package com.server.core;

import java.io.*;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.server.model.Comment;
import com.server.model.Notification;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ChatServer server;
    private final Gson gson = new Gson();
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String incomingJson;
            while ((incomingJson = in.readLine()) != null) {
                System.out.println("Incoming: " + incomingJson);
                String[] message = incomingJson.split(":", 2);
                String header = message[0];// not -> notification; req -> request to join chat box; com -> comment
                String body = message[1];

                Notification notification = new Notification();
                Comment comment = new Comment();

                if (header.equals("not") || header.equals("con")){
                    notification = gson.fromJson(body, Notification.class);
                } else {
                    comment = parseIncomingComment(body);
                    if (comment == null || comment.getTaskId() <= 0) {
                        System.out.println("Bo qua payload khong hop le tu " + socket.getRemoteSocketAddress());
                        continue;
                    }
                }
                switch (header) {
                    case "not":
                        ChatServer.sendNotificationToUser(notification.getUserId(), notification );
                        break;
                    case "con":
                        ChatServer.registerClient(notification.getUserId(), this);
                        break;
                    case "com":
                        server.broadcastToChatBox(comment, this);
                        break;

                    case "req":
                        server.joinChatBox(comment.getTaskId(), this);
                        break;

                }

            }
        } catch (IOException e) {
            System.out.println("Client ngắt kết nối: " + socket.getRemoteSocketAddress());
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException e) { /* Ignore */ }
        }
    }

    // Gửi JSON String trực tiếp xuống Client
    public synchronized void sendMessage(String jsonPayload) {
        if (out != null) {
            out.println(jsonPayload);
        }
    }

    private Comment parseIncomingComment(String incomingJson) {
        if (incomingJson == null || incomingJson.isBlank()) {
            return null;
        }

        try {
            JsonElement payload = gson.fromJson(incomingJson, JsonElement.class);
            if (payload == null || payload.isJsonNull()) {
                return null;
            }

            // Một số client gửi double-encoded JSON: "{...}".
            if (payload.isJsonPrimitive() && payload.getAsJsonPrimitive().isString()) {
                payload = gson.fromJson(payload.getAsString(), JsonElement.class);
            }

            if (!payload.isJsonObject()) {
                return null;
            }

            return gson.fromJson(payload, Comment.class);
        } catch (JsonParseException | IllegalStateException e) {
            return null;
        }
    }
}