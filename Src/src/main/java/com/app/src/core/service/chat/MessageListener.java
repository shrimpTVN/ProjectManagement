package com.app.src.core.service.chat;

import com.app.src.models.Comment;

public interface MessageListener {
    void onMessageReceived(Comment comment);
}