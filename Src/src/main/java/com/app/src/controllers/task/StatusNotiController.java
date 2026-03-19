package com.app.src.controllers.task;


import com.app.src.daos.TaskDAO;
import com.app.src.models.StatusUpdating;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class StatusNotiController {
    @FXML
    private VBox historyContainer; // VBox trống trong file StatusChangeNoti.fxml

    public void renderData(int taskId) {
        // 1. Kiểm tra an toàn
        if (historyContainer == null) return;

        // 2. Dọn dẹp & Reset style (Xóa màu vàng, viền đỏ)
        historyContainer.getChildren().clear();
        historyContainer.setStyle("");

        // 3. Lấy dữ liệu thật
        List<StatusUpdating> historyList = TaskDAO.getInstance().getStatusHistory(taskId);

        // Log kiểm tra số lượng (Dùng System.err để nó hiện chữ đỏ cho dễ nhìn)
        System.err.println("===> DAO tra ve: " + (historyList != null ? historyList.size() : "NULL") + " dong.");

        if (historyList == null || historyList.isEmpty()) {
            historyContainer.getChildren().add(new javafx.scene.control.Label("Chưa có lịch sử thay đổi."));
            return;
        }

        // 4. Nạp dữ liệu
        for (StatusUpdating item : historyList) { // 'item' là dữ liệu thật từ DB
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/TaskDetail/StatusChangeNoti.fxml"));
                Node itemNode = loader.load();

                StatusHistoryController itemController = loader.getController();
                if (itemController != null) {
                    // LỖI CŨ: Có thể bạn đang truyền 'dummy' hoặc 'new StatusUpdating()'
                    // SỬA THÀNH: Truyền đúng cái 'item' của vòng lặp
                    itemController.setData(item);

                    historyContainer.getChildren().add(itemNode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
