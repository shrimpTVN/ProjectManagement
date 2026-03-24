package com.app.src.ui.components.TaskButton;

import com.app.src.daos.TaskDAO;
import com.app.src.models.Task;
import com.app.src.ui.components.AbstractTaskButton;

public class RejectPreviewTaskButton extends AbstractTaskButton {

    public RejectPreviewTaskButton() {
        super("Reject");
    }

    @Override
    protected void setupCustomStyle() {
        // Màu đỏ/cam cảnh báo
        this.getStyleClass().addAll("task-action-btn", "btn-reject");
    }

    @Override
    protected String getAlertTitle() {
        return "Từ chối duyệt";
    }

    @Override
    protected String getAlertMessage() {
        return "Công việc chưa đạt yêu cầu. Bạn muốn trả lại (chuyển về In Progress) để thành viên làm lại?";
    }

    @Override
    protected boolean updateDatabase() {
        int userId = task.getUser() != null ? task.getUser().getUserId() : 0;
        return TaskDAO.getInstance().appendStatusUpdating(
                task.getTaskId(), "In Preview", "In Progress", "Quản lý yêu cầu làm lại", userId
        );
    }
}