package com.server.core;

import com.google.gson.Gson;
import com.server.model.ChatMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.*;


public class ChatServer {
    private final int port;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final Gson gson = new Gson();

    // Cấu trúc dữ liệu cốt lõi: Map<taskId, Danh sách Client đang online trong task đó>
    private final ConcurrentHashMap<Integer, Set<ClientHandler>> chatBoxs = new ConcurrentHashMap<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Chat Server đang chạy trên port " + port + "...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Có client mới kết nối: " + clientSocket.getRemoteSocketAddress());

                ClientHandler handler = new ClientHandler(clientSocket, this);
                threadPool.execute(handler); // Giao cho Thread Pool xử lý
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hàm Broadcast: Chỉ gửi tin nhắn cho những ai thuộc có chat trong task
    public void broadcastToChatBox(ChatMessage message, ClientHandler sender) {
        int taskId = message.getTaskId();
        Set<ClientHandler> box = chatBoxs.get(taskId);

        if (box != null) {
            String jsonPayload = gson.toJson(message); // Đóng gói thành JSON
            for (ClientHandler client : box) {
                if (client != sender) {
                    client.sendMessage(jsonPayload);
                }
            }
        }
    }

    // Quản lý việc Client tham gia vào một chat box
    public void joinChatBox(int taskId, ClientHandler handler) {
        chatBoxs.computeIfAbsent(taskId, k -> ConcurrentHashMap.newKeySet()).add(handler);
        System.out.println("Client tham gia phòng dự án: " + taskId);
    }

    // Quản lý việc Client ngắt kết nối
    public void removeClient(ClientHandler handler) {
        // Xóa client khỏi tất cả các phòng dự án
        chatBoxs.values().forEach(box -> box.remove(handler));
    }
}