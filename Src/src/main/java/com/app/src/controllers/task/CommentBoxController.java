package com.app.src.controllers.task;

import com.app.src.core.service.chat.ChatClientService;
import com.app.src.core.service.chat.MessageListener;
import com.app.src.core.session.UserSession;
import com.app.src.models.Comment;
import com.app.src.services.CommentService;
import com.app.src.services.UserService;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class CommentBoxController implements MessageListener {
    private final CommentService commentService = new CommentService();
    @FXML
    private VBox vboxChatContent;
    @FXML
    private TextField txtMessage;
    @FXML
    private Button btnSend;
    private int currentTaskId;
    @FXML
    ScrollPane scrollChat;
    private Gson gson = new Gson();

    @FXML
    public void initialize() {
        // Sự kiện khi nhấn nút Gửi
        btnSend.setOnAction(event -> handleSendMessage());

        // Sự kiện khi nhấn Enter trong ô nhập liệu
        txtMessage.setOnAction(event -> handleSendMessage());
    }

    public void renderData(int taskId) {
        this.currentTaskId = taskId;
        refreshComments();
    }

    private void handleSendMessage() {
        String content = txtMessage.getText().trim();
        if (content.isEmpty()) return;

        if (UserSession.getInstance().getUser() == null) {
            System.out.println("Lỗi: Chưa đăng nhập");
            return;
        }

        int currentUserId = UserSession.getInstance().getUser().getUserId();

        sendComment("com", currentUserId, content);
        // luu du lieu xuong DB
        boolean success = commentService.postComment(currentTaskId, currentUserId, content);

        if (success) {
            txtMessage.clear();
            refreshComments();
        }
    }

    private void refreshComments() {
        UserService userService = new UserService();
        vboxChatContent.getChildren().clear();
        List<Comment> comments = commentService.getComments(currentTaskId);

        for (Comment cmt : comments) {
            String realName = null;
                realName = userService.getUserById(cmt.getUserId()).getUserName();
            loadMessageUI(cmt, realName);
        }
        scrollChat.setVvalue(1.0);
    }

    private void loadMessageUI(Comment comment, String userName) {
        try {
            int currentUserId = -1;
            if (UserSession.getInstance().getUser() != null) {
                currentUserId = UserSession.getInstance().getUser().getUserId();
            }

            // Chọn layout phù hợp dựa trên người gửi để tránh lỗi thiếu file FXML
            boolean isMine = (comment.getUserId() == currentUserId);
            String fxmlPath = isMine
                    ? "/components/TaskDetail/MessageSended.fxml"
                    : "/components/TaskDetail/MessageReceived.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();

            MessageController controller = loader.getController();
            controller.setData(comment, userName, isMine);
            vboxChatContent.getChildren().add(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(Comment comment) {

        String userName = UserService.getInstance().getUserById(comment.getUserId()).getUserName();
        loadMessageUI(comment, userName);
        scrollChat.setVvalue(1.0);
    }

    public void sendComment(String header, int currentUserId, String content) {
        //gui object comment den chat server
        String json = gson.toJson(new Comment(currentTaskId, currentUserId, content, new Date()));
        boolean chatSent = ChatClientService.getInstance().sendMessage(header +":"+ json);
        System.out.println(content);
        if (!chatSent) {
            System.err.println("[CHAT-WARN] Tin nhan chua gui duoc len server.");
        }
    }
}