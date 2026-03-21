package com.server.core;
import java.io.*;
import java.net.Socket;
import com.google.gson.Gson;
import com.server.model.ChatMessage;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private final Gson gson = new Gson();

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
                // 1. Dịch JSON thành Object
                ChatMessage msg = gson.fromJson(incomingJson, ChatMessage.class);

                // 2. Đảm bảo Client này đã được đăng ký vào phòng của task đó
                server.joinChatBox(msg.getTaskId(), this);

                // 3. Yêu cầu Server phát sóng tin nhắn vào phòng
                server.broadcastToChatBox(msg, this);
            }
        } catch (IOException e) {
            System.out.println("Client ngắt kết nối: " + socket.getRemoteSocketAddress());
        } finally {
            server.removeClient(this);
            try { socket.close(); } catch (IOException e) { /* Ignore */ }
        }
    }

    // Gửi JSON String trực tiếp xuống Client
    public synchronized void sendMessage(String jsonPayload) {
        if (out != null) {
            out.println(jsonPayload);
        }
    }
}