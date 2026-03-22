package com.app.src.ui.components.TaskButton;
import com.app.src.daos.TaskDAO;
import com.app.src.models.Task;
import com.app.src.ui.components.AbstractTaskButton;

public class AcceptTaskButton extends AbstractTaskButton {

    public AcceptTaskButton() {
        super("Nhận task"); // Chữ hiển thị trên nút
    }

    @Override
    protected void setupCustomStyle() {
        // Màu xanh lá cây mướt mắt cho hành động tích cực
        this.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
    }

    @Override
    protected String getAlertTitle() {
        return "Xác nhận nhận việc";
    }

    @Override
    protected String getAlertMessage() {
        return "Bạn đã sẵn sàng bắt đầu thực hiện công việc này (Chuyển sang In Progress)?";
    }

    @Override
    protected boolean updateDatabase() {
        int userId = task.getUser() != null ? task.getUser().getUserId() : 0;
        // Trạng thái cũ: To Do -> Trạng thái mới: In Progress
        return TaskDAO.getInstance().appendStatusUpdating(
                task.getTaskId(), "To Do", "In Progressing", "User đã nhận task và bắt đầu làm", userId
        );
    }
}