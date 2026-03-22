package com.app.src.core.service.chat;

import com.app.src.controllers.notification.NotificationController;
import com.app.src.core.async.AsyncExecutor;
import com.app.src.models.Comment;
import com.app.src.models.Notification;
import com.google.gson.Gson;
import javafx.application.Platform;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.*;

public class ChatClientService {
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 8080;

    private Socket socket;
    // 1. SỬA ĐỔI KIỂU DỮ LIỆU CỦA STREAM
    private PrintWriter out;
    private BufferedReader in;

    private volatile boolean isRunning = false;
    private final BlockingQueue<Comment> messageQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Notification> notificationQueue = new LinkedBlockingQueue<>();
    private MessageListener commentListener;
    private NotificationController notificationController;
    private final Gson gson = new Gson();
    private static ChatClientService instance;

    private ChatClientService() {};

    public static ChatClientService getInstance() {
        if (instance == null) {
            instance = new ChatClientService();
        }
        return instance;
    }

    public void setMessageListener(MessageListener listener) {
        this.commentListener = listener;
    }

    public void setNotificationController(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);

        // 2. KHỞI TẠO STREAM CHUẨN ĐỂ TRUYỀN JSON (Hỗ trợ tiếng Việt UTF-8)
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        isRunning = true;

        AsyncExecutor.getInstance().runAsync(this::listenForMessages);
        AsyncExecutor.getInstance().runAsync(this::processCommentQueue);
    }

    public void connectDefault() throws IOException {
        connect(DEFAULT_HOST, DEFAULT_PORT);
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed() && out != null;
    }

    // Luồng 1: Lắng nghe liên tục
    private void listenForMessages() {
        try {
            String incomingJson;
            // 3. ĐỌC DỮ LIỆU CHUẨN XÁC
            while (isRunning && !socket.isClosed() && (incomingJson = in.readLine()) != null) {
                String[] message = incomingJson.split(":", 2);
                String header = message[0];// not -> notification; req -> request to join chat box; com -> comment
                String body = message[1];

                if (header.equals("not")){

                }

                if (header.equals("com")){
                    Comment receivedMsg = gson.fromJson(body, Comment.class);
                    messageQueue.offer(receivedMsg);
                }

            }
        } catch (Exception e) {
            if (isRunning) {
                System.err.println("Lỗi mất kết nối Socket: " + e.getMessage());
            }
        }
    }

    // Luồng 2: Xử lý hàng đợi (Giữ nguyên, bạn làm rất tốt)
    private void processCommentQueue() {
        try {
            while (isRunning) {
                Comment comment = messageQueue.take();
                Platform.runLater(() -> {
                    System.out.println(" load comment cho " + comment.getTaskId() +" tu user " + comment.getUserId());
                    if (commentListener != null) {
                        commentListener.onMessageReceived(comment);
                    }
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //luầng 3: xử lý hàng đợi cho thông báo
    // Luồng 2: Xử lý hàng đợi (Giữ nguyên, bạn làm rất tốt)
    private void processNotificationQueue() {
        try {
            while (isRunning) {
                Notification notification = notificationQueue.take();
                Platform.runLater(() -> {
                    System.out.println(" load message cho tu user " + notification.getUserId());
                    if (notificationController != null) {
                        notificationController.onNotificationReceive(notification);
                    }
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    // --- SENDING for all case: notification, request, comment---
    public boolean sendMessage(String msg) {
        if (!isConnected()) {
            System.err.println("[CHAT-WARN] Chua ket noi server, khong the gui tin nhan.");
            return false;
        }

//        String json = gson.toJson(msg);
        synchronized (this) { // Lock này giúp an toàn nếu nhiều luồng cùng gọi sendMessage
            try {
                // 4. DÙNG PRINTLN ĐỂ TỰ ĐỘNG THÊM KÝ TỰ XUỐNG DÒNG (\n)
                out.println(msg);
                // Không cần out.flush() nữa vì PrintWriter đã khởi tạo với cờ auto-flush = true
                System.out.println("[CHAT-DEBUG] Đã gửi: " + msg);
                return true;
            } catch (Exception e) {
                System.err.println("[CHAT-ERROR] Gui tin nhan that bai: " + e.getMessage());
                return false;
            }
        }
    }

    public void disconnect() {

    }

    public String generateNotification(String title, String content, int userId) {

        return "com:"+gson.toJson(new Notification(title, content, false, String.valueOf( new Date()), userId));
    }
}