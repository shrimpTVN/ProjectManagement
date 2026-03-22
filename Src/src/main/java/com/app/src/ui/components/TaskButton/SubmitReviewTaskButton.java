package com.app.src.ui.components.TaskButton;

import com.app.src.daos.TaskDAO;
import com.app.src.models.Task;
import com.app.src.ui.components.AbstractTaskButton;

public class SubmitReviewTaskButton extends AbstractTaskButton {

    public SubmitReviewTaskButton() {
        super("Gửi duyệt");
    }

    @Override
    protected void setupCustomStyle() {
        // Màu vàng cam hoặc xanh dương nhạt cho hành động gửi duyệt
        this.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
    }

    @Override
    protected String getAlertTitle() {
        return "Gửi yêu cầu kiểm duyệt";
    }

    @Override
    protected String getAlertMessage() {
        return "Bạn xác nhận đã hoàn thành xong công việc và muốn gửi cho Admin duyệt (In Preview)?";
    }

    @Override
    protected boolean updateDatabase() {
        int userId = task.getUser() != null ? task.getUser().getUserId() : 0;
        // Trạng thái cũ: In Progress -> Trạng thái mới: In Preview
        return TaskDAO.getInstance().appendStatusUpdating(
                task.getTaskId(), "In Progressing", "In Preview", "User đã làm xong, gửi yêu cầu duyệt", userId
        );
    }
}