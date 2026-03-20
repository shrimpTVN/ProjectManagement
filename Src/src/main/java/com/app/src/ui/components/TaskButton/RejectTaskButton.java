package com.app.src.ui.components.TaskButton;


import com.app.src.daos.TaskDAO;
import com.app.src.models.Task;
import com.app.src.ui.components.AbstractTaskButton;

public class RejectTaskButton extends AbstractTaskButton {

    public RejectTaskButton() {
        super("Từ chối");
    }

    @Override
    protected void setupCustomStyle() {
        // Màu đỏ cảnh báo cho hành động hủy/từ chối
        this.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
    }

    @Override
    protected String getAlertTitle() {
        return "Cảnh báo từ chối công việc";
    }

    @Override
    protected String getAlertMessage() {
        return "Bạn có chắc chắn KHÔNG NHẬN công việc này và muốn hủy/trả lại không?";
    }

    @Override
    protected boolean updateDatabase() {
        int userId = task.getUser() != null ? task.getUser().getUserId() : 0;
        // Trạng thái cũ: To Do -> Trạng thái mới: Canceled (Hãy đảm bảo bảng STATUS của bạn có trạng thái này)
        return TaskDAO.getInstance().appendStatusUpdating(
                task.getTaskId(), "To Do", "Canceled", "User từ chối nhận task", userId
        );
    }
}