package com.server.core;

import com.google.gson.Gson;
import com.server.model.Comment;
import com.server.model.Notification;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.*;


public class ChatServer {
    private final int port;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private static final Gson gson = new Gson();

    // Cấu trúc dữ liệu cốt lõi: Map<taskId, Danh sách Client đang online trong task đó>
    private final ConcurrentHashMap<Integer, Set<ClientHandler>> chatBoxs = new ConcurrentHashMap<>();
    // NEW: Global set for all connected clients to receive notifications
    private final static  ConcurrentHashMap<Integer, ClientHandler> connectedClients =  new ConcurrentHashMap<>();

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


                System.out.println("New client connected and registered for notifications. Total online: " + connectedClients.size());
                threadPool.execute(handler); // Giao cho Thread Pool xử lý
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void registerClient(int userId, ClientHandler handler) {
        connectedClients.put(userId, handler);
        System.out.println("User " + userId + " registered. Total online: " + connectedClients.size());
    }

    public static void sendNotificationToUser(int userId, Notification notification) {
        ClientHandler client = connectedClients.get(userId);
        if (client != null ) {
            // Convert the Notification object to JSON or your custom protocol string
            String payload = "not:" + gson.toJson(notification);
            client.sendMessage(payload);
        }
    }

    // Hàm Broadcast: Chỉ gửi tin nhắn cho những ai thuộc có chat trong task
    public void broadcastToChatBox(Comment message, ClientHandler sender) {
        int taskId = message.getTaskId();
        Set<ClientHandler> box = chatBoxs.get(taskId);

        if (box != null) {
            String jsonPayload = gson.toJson(message); // Đóng gói thành JSON
            for (ClientHandler client : box) {
                if (client != sender) {
                    System.out.println("gui tin nhan den " + message.getTaskId());
                    client.sendMessage("com:"+jsonPayload);
                }
            }
        }
    }

    // Quản lý việc Client tham gia vào một chat box
    public void joinChatBox(int taskId, ClientHandler handler) {
        chatBoxs.computeIfAbsent(taskId, k -> ConcurrentHashMap.newKeySet()).add(handler);
        System.out.println("Client tham gia chat box: " + taskId);
    }

    // Quản lý việc Client ngắt kết nối
    public void removeClient(ClientHandler handler) {
        // Xóa client khỏi tất cả các phòng dự án
        chatBoxs.values().forEach(box -> box.remove(handler));

        // UNREGISTER GLOBALLY: Remove from global notifications
        connectedClients.entrySet().removeIf(entry -> entry.getValue() == handler);
        System.out.println("Client disconnected. Remaining online: " + connectedClients.size());
    }

}