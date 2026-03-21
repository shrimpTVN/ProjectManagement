package com.app.src.ui.components;

import com.app.src.models.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

public abstract class AbstractTaskButton extends Button {

    protected Task task;
    protected Runnable onSuccessCallback;

    // Constructor nhận vào tên nút (ví dụ: "Nhận việc", "Hoàn thành")
    public AbstractTaskButton(String text) {
        super(text);
        // Gắn sự kiện click mặc định cho mọi nút kế thừa lớp này
        this.setOnAction(event -> executeAction());
    }

    // Hàm dùng để nạp dữ liệu từ Controller vào Nút
    public void setup(Task task, Runnable onSuccessCallback) {
        this.task = task;
        this.onSuccessCallback = onSuccessCallback;
        setupCustomStyle(); // Gọi hook để class con tự đổi màu
    }

    // --- TEMPLATE METHOD: Luồng chạy cố định ---
    private void executeAction() {
        if (task == null) return;

        // 1. Hiển thị Alert xác nhận lấy từ class con
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(getAlertTitle());
        alert.setHeaderText("Tác vụ: " + task.getTaskName());
        alert.setContentText(getAlertMessage());

        // 2. Chờ người dùng nhấn OK
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

            // 3. Thực thi logic gọi DB (Do class con quyết định)
            boolean isSuccess = updateDatabase();

            // 4. Báo cáo kết quả
            if (isSuccess) {
                if (onSuccessCallback != null) onSuccessCallback.run(); // Load lại giao diện
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR, "Lỗi cập nhật CSDL. Vui lòng thử lại!");
                error.show();
            }
        }
    }

    // --- CÁC HÀM ABSTRACT MÀ CLASS CON PHẢI TỰ VIẾT ---
    protected abstract String getAlertTitle();
    protected abstract String getAlertMessage();
    protected abstract boolean updateDatabase();

    // --- HOOK METHOD: Class con có thể ghi đè (override) để custom CSS ---
    protected void setupCustomStyle() {
        // Mặc định là nút xám bình thường của JavaFX
    }
}