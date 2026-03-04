package com.app.src.services;

import java.util.List;
import com.app.src.daos.TaskDAO;
import com.app.src.models.Task;

public class TasklistService {

    private TaskDAO taskDAO;

    // Sửa lỗi sai tên Constructor ở bản cũ (TaskListService thay vì TaskService)
    public TasklistService() {
        this.taskDAO = new TaskDAO();
        // Lưu ý: Tạm thời dùng new TaskDAO() vì phần Singleton trong DAO của bạn đang có chút nhầm lẫn (mình có nhắc ở dưới).
    }

    // ==========================================
    // 1. ĐỌC DỮ LIỆU (READ)
    // ==========================================
    public List<Task> getAllTasks() {
        // Cập nhật: Gọi hàm findAll() của Generic DAO
        return taskDAO.findAll();
    }

    // ==========================================
    // 2. THÊM MỚI (CREATE)
    // ==========================================
    public boolean addTask(Task newTask) {
        // --- BƯỚC 1: VALIDATION ---
        if (newTask == null) {
            throw new IllegalArgumentException("Dữ liệu công việc không tồn tại!");
        }

        // Cập nhật: Dùng getTaskName() theo model Task mới
        if (newTask.getTaskName() == null || newTask.getTaskName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên công việc không được để trống!");
        }

        if (newTask.getTaskName().length() > 255) {
            throw new IllegalArgumentException("Tên công việc quá dài (tối đa 255 ký tự)!");
        }

        // --- BƯỚC 2: BUSINESS LOGIC ---
        /* Tạm comment phần này lại vì trong DAO của bạn không thấy lấy trường Status.
           Nếu model Task của bạn có trường trạng thái (ví dụ: taskStatus), bạn mở lại code này nhé:
        if (newTask.getTaskStatus() == null || newTask.getTaskStatus().trim().isEmpty()) {
            newTask.setTaskStatus("PENDING");
        }
        */

        // Chuẩn hóa tên
        newTask.setTaskName(newTask.getTaskName().trim());

        // --- BƯỚC 3: GỌI DAO ---
        // Cập nhật: Dùng hàm create() thay vì addTask()
        return taskDAO.create(newTask);
    }

    // ==========================================
    // 3. CẬP NHẬT TRẠNG THÁI (UPDATE / TOGGLE)
    // ==========================================
    public boolean toggleTaskStatus(Task task) {
        // Cập nhật: Dùng getTaskId()
        if (task == null || task.getTaskId() <= 0) {
            throw new IllegalArgumentException("Công việc không hợp lệ hoặc chưa được lưu!");
        }

        /* Logic đảo trạng thái (Nếu bạn có trường status):
        String currentStatus = task.getTaskStatus();
        String newStatus = "PENDING".equalsIgnoreCase(currentStatus) ? "COMPLETED" : "PENDING";
        task.setTaskStatus(newStatus);
        */

        // Cập nhật: Hàm update của AbstractDAO nhận vào (int id, Task entity)
        // Nghĩa là bạn phải cập nhật lại toàn bộ object Task xuống DB
        return taskDAO.update(task.getTaskId(), task);
    }

    // ==========================================
    // 4. XÓA CÔNG VIỆC (DELETE)
    // ==========================================
    public boolean deleteTask(int taskId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ!");
        }

        // Cập nhật: Dùng hàm delete(int id) của Generic DAO
        return taskDAO.delete(taskId);
    }
}