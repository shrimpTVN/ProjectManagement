package com.app.src.ui.components.TaskButton;

import com.app.src.daos.TaskDAO;
import com.app.src.models.Task;
import com.app.src.ui.components.AbstractTaskButton;

public class SubmitReviewTaskButton extends AbstractTaskButton {

    public SubmitReviewTaskButton() {
        super("Send for review");
    }

    @Override
    protected void setupCustomStyle() {
        // Màu vàng cam hoặc xanh dương nhạt cho hành động gửi duyệt
        this.getStyleClass().addAll("task-action-btn", "btn-submit");
    }

    @Override
    protected String getAlertTitle() {
        return "Submit review request";
    }

    @Override
    protected String getAlertMessage() {
        return "Confirm you finished the task and want to send it for Admin review (In Preview)?";
    }

    @Override
    protected boolean updateDatabase() {
        int userId = task.getUser() != null ? task.getUser().getUserId() : 0;
        // Trạng thái cũ: In Progress -> Trạng thái mới: In Preview
        return TaskDAO.getInstance().appendStatusUpdating(
                task.getTaskId(), "In Progressing", "In Preview", "User finished and requested review", userId
        );
    }
}