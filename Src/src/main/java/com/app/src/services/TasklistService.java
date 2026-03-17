package com.app.src.services;

import java.util.List;
import com.app.src.daos.TaskDAO;
import com.app.src.dtos.PersonalTaskDTO;
import com.app.src.models.Task;

public class TasklistService {

    private TaskDAO taskDAO;

    public TasklistService() {
        // CẬP NHẬT: Sử dụng Singleton pattern chuẩn xác thay vì dùng toán tử new
        this.taskDAO = TaskDAO.getInstance();
    }

    // ==========================================
    // 1. ĐỌC DỮ LIỆU (READ)
    // ==========================================
    public List<PersonalTaskDTO> getAllTasks() {
        return taskDAO.findAll();
    }
    public List<PersonalTaskDTO> getTaskByUser(int userID){
        return taskDAO.findAllByUserId(userID);
    }
    // ==========================================
    // 2. THÊM MỚI (CREATE)
    // ==========================================
    public boolean addTask(PersonalTaskDTO newTask) {
        // --- BƯỚC 1: VALIDATION ---
        if (newTask == null) {
            throw new IllegalArgumentException("Dữ liệu công việc không tồn tại!");
        }

        if (newTask.getTaskName() == null || newTask.getTaskName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên công việc không được để trống!");
        }

        if (newTask.getTaskName().length() > 255) {
            throw new IllegalArgumentException("Tên công việc quá dài (tối đa 255 ký tự)!");
        }

        // --- BƯỚC 2: BUSINESS LOGIC ---
        // Chuẩn hóa tên (Xóa khoảng trắng thừa ở đầu/cuối)
        newTask.setTaskName(newTask.getTaskName().trim());

        // Gợi ý: Set giá trị mặc định cho Status khi tạo mới (nếu model có hỗ trợ)
        // if (newTask.getTaskStatus() == null || newTask.getTaskStatus().trim().isEmpty()) {
        //     newTask.setTaskStatus("To do");
        // }

        // --- BƯỚC 3: GỌI DAO ---
        return taskDAO.create(newTask);
    }

    // ==========================================
    // 3. CẬP NHẬT (UPDATE)
    // ==========================================
    public boolean toggleTaskStatus(PersonalTaskDTO task) {
        if (task == null || task.getTaskId() <= 0) {
            throw new IllegalArgumentException("Công việc không hợp lệ hoặc chưa được lưu!");
        }

        // Logic đảo trạng thái (Mở ra khi bạn có trường status nhé)
        /* String currentStatus = task.getTaskStatus();
        String newStatus = "Done".equalsIgnoreCase(currentStatus) ? "To do" : "Done";
        task.setTaskStatus(newStatus);
        */

        return taskDAO.update(task.getTaskId(), task);
    }

    // Cập nhật trạng thái task và truyền cả trạng thái cũ/mới để kiểm tra rule nghiệp vụ chắc chắn hơn.
    public boolean updateTaskStatus(int taskId, String oldStatus, String newStatus, String content, int userId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ!");
        }
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ!");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("Người dùng không hợp lệ!");
        }
        return taskDAO.appendStatusUpdating(taskId, oldStatus, newStatus, content, userId);
    }

    // ==========================================
    // 4. XÓA CÔNG VIỆC (DELETE)
    // ==========================================
    public boolean deleteTask(int taskId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ!");
        }
        return taskDAO.delete(taskId);
    }


    /**
     * Lấy danh sách công việc thuộc về một dự án cụ thể.
     * Hàm này được gọi bởi ListController để hiển thị dữ liệu theo Tab.
     * @param projectId ID của dự án cần lấy task.
     */
    public List<Task> getTasksByProject(int projectId) {
        return taskDAO.findByProjectId(projectId);
    }

    public boolean addTask(Task newTask) {
        if (newTask == null) {
            throw new IllegalArgumentException("Dữ liệu công việc không tồn tại!");
        }

        if (newTask.getTaskName() == null || newTask.getTaskName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên công việc không được để trống!");
        }

        if (newTask.getTaskName().length() > 255) {
            throw new IllegalArgumentException("Tên công việc quá dài (tối đa 255 ký tự)!");
        }

        newTask.setTaskName(newTask.getTaskName().trim());

        return taskDAO.createTask(newTask);
    }
}