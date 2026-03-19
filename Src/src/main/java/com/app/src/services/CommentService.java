package com.app.src.services;

import com.app.src.daos.CommentDAO;
import com.app.src.models.Comment;

import java.util.Date;
import java.util.List;

public class CommentService {
    private final CommentDAO commentDAO;

    public CommentService() {
        this.commentDAO = CommentDAO.getInstance();
    }

    public List<Comment> getComments(int taskId) {
        return commentDAO.getCommentsByTaskId(taskId);
    }

    public boolean postComment(int taskId, int userId, String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        // 1. Tìm ID tin nhắn gần nhất của NGƯỜI KIA
        Integer otherLastId = commentDAO.getLatestOtherUserCommentId(taskId, userId);

        Comment newComment = new Comment();
        newComment.setTaskId(taskId);
        newComment.setUserId(userId);
        newComment.setComment(text.trim());
        newComment.setDate(new Date());

        // 2. Nếu tìm thấy (tức là thằng kia đã nhắn rồi) thì lấy ID đó
        // Nếu không thấy (mình mở bát hoặc thằng kia chưa nhắn gì) thì để là 0
        int prevId = (otherLastId != null) ? otherLastId : 0;
        newComment.setPreviousCommentId(prevId);

        return commentDAO.insertComment(newComment);
    }
}