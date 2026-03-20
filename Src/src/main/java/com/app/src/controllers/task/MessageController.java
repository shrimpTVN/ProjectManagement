package com.app.src.controllers.task;

import com.app.src.models.Comment;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;

public class MessageController {
    @FXML
    private HBox hboxRoot;
    @FXML
    private VBox vboxBubble;
    @FXML
    private Label lblCommentUser;
    @FXML
    private Label lblCommentContent;
    @FXML
    private Label lblCommentTime;

    public void setData(Comment comment, String userName, boolean isMine) {
        lblCommentUser.setText(userName);
        lblCommentContent.setText(comment.getComment());

        if (comment.getDate() != null) {
            lblCommentTime.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(comment.getDate()));
        }

        if (isMine) {
            hboxRoot.setAlignment(Pos.CENTER_RIGHT);
            vboxBubble.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 12;");
            lblCommentUser.setText("You");
        } else {
            hboxRoot.setAlignment(Pos.CENTER_LEFT);
            vboxBubble.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 12;");
        }
    }
}