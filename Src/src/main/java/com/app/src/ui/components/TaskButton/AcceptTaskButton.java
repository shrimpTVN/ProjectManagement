package com.app.src.ui.components.TaskButton;
import com.app.src.daos.TaskDAO;
import com.app.src.models.Task;
import com.app.src.ui.components.AbstractTaskButton;

public class AcceptTaskButton extends AbstractTaskButton {

    public AcceptTaskButton() {
        super("Accept task"); // button label
    }

    @Override
    protected void setupCustomStyle() {
        // Màu xanh lá cây mướt mắt cho hành động tích cực
        this.getStyleClass().addAll("task-action-btn", "btn-submit");
    }

    @Override
    protected String getAlertTitle() {
        return "Confirm task acceptance";
    }

    @Override
    protected String getAlertMessage() {
        return "Ready to start this task and move it to In Progress?";
    }

    @Override
    protected boolean updateDatabase() {
        int userId = task.getUser() != null ? task.getUser().getUserId() : 0;
        // Trạng thái cũ: To Do -> Trạng thái mới: In Progress
        return TaskDAO.getInstance().appendStatusUpdating(
                task.getTaskId(), "To Do", "In Progressing", "User accepted the task and started work", userId
        );
    }
}