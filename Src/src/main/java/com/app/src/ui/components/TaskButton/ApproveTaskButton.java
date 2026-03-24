package com.app.src.ui.components.TaskButton;
import com.app.src.daos.TaskDAO;
import com.app.src.ui.components.AbstractTaskButton;


public class ApproveTaskButton extends AbstractTaskButton {

    public ApproveTaskButton() {
        super("Approve");
    }

    @Override
    protected void setupCustomStyle() {
        // Màu xanh lá để chỉ sự đồng ý
        this.getStyleClass().addAll("task-action-btn", "btn-submit");
    }

    @Override
    protected String getAlertTitle() {
        return "Xác nhận duyệt công việc";
    }

    @Override
    protected String getAlertMessage() {
        return "Bạn xác nhận công việc này đã đạt yêu cầu và chuyển sang trạng thái Done?";
    }

    @Override
    protected boolean updateDatabase() {
        int userId = task.getUser() != null ? task.getUser().getUserId() : 0;
        return TaskDAO.getInstance().appendStatusUpdating(
                task.getTaskId(), "In Preview", "Done", "Quản lý đã duyệt task", userId
        );
    }
}