 package com.app.src.core.service.chat;

import com.app.src.core.async.AsyncExecutor;
import javafx.application.Platform;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;


public class ChatClientService {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean isRunning = false;

    // Hàng đợi chống tràn (Backpressure)
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port); // tao ket noi den socketServer
        out = new ObjectOutputStream(socket.getOutputStream()); //tao doi tuong nhan
        in = new ObjectInputStream(socket.getInputStream());// tao doi tuong gui
        isRunning = true;

        // Bắt đầu luồng lắng nghe Socket
        AsyncExecutor.getInstance().runAsync(this::listenForMessages);

        // Bắt đầu luồng xử lý hàng đợi và update UI
        AsyncExecutor.getInstance().runAsync(this::processMessageQueue);
    }

    // Luồng 1: Lắng nghe liên tục (Persistent Listener)
    private void listenForMessages() {
        try {
            while (isRunning && !socket.isClosed()) {
                String message = (String) in.readObject();
                messageQueue.offer(message); // Đẩy vào hàng đợi thay vì gọi UI ngay
            }
        } catch (Exception e) {
            if (isRunning) throw new RuntimeException("Lỗi mất kết nối Socket", e);
        }
    }

    // Luồng 2: Xử lý hàng đợi và đưa lên UI một cách an toàn
    private void processMessageQueue() {
        try {
            while (isRunning) {
                // Sẽ block nếu hàng đợi trống, không ăn CPU
                String message = messageQueue.take();
                Platform.runLater(() -> {
                    // Cập nhật UI ở đây (hoặc trigger Observer pattern tới UI Controller)
                    System.out.println("UI nhận: " + message);
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Sử dụng Thread Pool cho tác vụ gửi đi (Outbound actions)
    public void sendMessage(String msg) {
        AsyncExecutor.getInstance().runAsync(() -> {
            try {
                out.writeObject(msg);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException("Không thể gửi tin nhắn", e);
            }
        });
    }

    public void disconnect() {
        isRunning = false;
        try { if (socket != null) socket.close(); }
        catch (IOException e) { /* Bỏ qua lỗi lúc đóng */ }
    }
}