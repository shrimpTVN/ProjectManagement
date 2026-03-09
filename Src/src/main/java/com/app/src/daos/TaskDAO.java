package com.app.src.daos;

import com.app.src.models.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO extends AbstractDAO<Task> {

    // Không nên để Connection là static, dễ gây lỗi khi xử lý đa luồng (multiple calls)
    private static TaskDAO instance;

    public static TaskDAO getInstance(){
        if(instance == null){
            instance = new TaskDAO();
        }
        return instance;
    }

    @Override
    public Task findById(int id) {
        // LỖI CŨ: "select * from user where User_id = ?" -> Đã sửa lại thành bảng Task
        String sql = "SELECT * FROM task WHERE Task_id = ?";
        Task task = null;
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                task = new Task();
                task.setTaskId(id);
                // LỖI CŨ: Lấy getString("Task_id") gán cho Name -> Đã sửa thành "Task_name"
                task.setTaskName(rs.getString("Task_name"));
                task.setTaskStartTime(String.valueOf(rs.getTimestamp("Task_startDate")));
                task.setTaskEndTime(String.valueOf(rs.getTimestamp("Task_endDate")));
                task.setTaskDescription(rs.getString("Task_description"));

                // Set thêm Project và Status nếu database bạn có 2 cột này
                // task.setProjectName(rs.getString("Project_name"));
                // task.setTaskStatus(rs.getString("Task_status"));
            }
            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi tìm Task theo ID: " + ex.getMessage(), ex);
        }
        return task;
    }

    @Override
    public List<Task> findAll() {
        // BẮT BUỘC PHẢI VIẾT HÀM NÀY thì giao diện (TableView) mới có data để in ra
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM task";
        Connection connection = null;
        System.out.println("Đang kết nối Database...");
        try {
            System.out.println("Thực thi SQL thành công, đang đọc kết quả...");
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Task task = new Task();
                task.setTaskId(rs.getInt("Task_id"));
                task.setTaskName(rs.getString("Task_name"));
                task.setTaskStartTime(String.valueOf(rs.getTimestamp("Task_startDate")));
                task.setTaskEndTime(String.valueOf(rs.getTimestamp("Task_endDate")));
                task.setTaskDescription(rs.getString("Task_description"));

                // Nếu DB có cột Project và Status thì map luôn
                // task.setProjectName(rs.getString("Project_name"));
                // task.setTaskStatus(rs.getString("Task_status"));

                tasks.add(task);
                System.out.println("Đã lấy được Task: " + task.getTaskName());
            }
            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi lấy danh sách Task: " + ex.getMessage(), ex);
        }
        return tasks;
    }

    @Override
    public boolean create(Task entity) {
//        // ĐÂY CHÍNH LÀ PHƯƠNG THỨC THÊM (INSERT)
//        // Bạn nhớ thay đổi tên cột (Task_name, Task_startDate...) cho khớp chính xác với CSDL của bạn
//        String sql = "INSERT INTO task (Task_name, Task_startDate, Task_endDate, Task_description) VALUES (?, ?, ?, ?)";
//        Connection connection = null;
//
//        try {
//            connection = getConnection();
//            PreparedStatement ps = connection.prepareStatement(sql);
//
//            ps.setString(1, entity.getTaskName());
//            ps.setTime(2, entity.getTaskStartTime());
//            ps.setTime(3, entity.getTaskEndTime());
//            ps.setString(4, entity.getTaskDescription());
//
//            // Nếu có Project và Status thì thêm vào câu SQL và PreparedStatement tương ứng
//
//            int rowsAffected = ps.executeUpdate();
//            this.closeResource(ps, connection, null);
//
//            return rowsAffected > 0; // Trả về true nếu insert thành công
//
//        } catch (SQLException ex) {
//            System.err.println("Lỗi khi thêm Task mới: " + ex.getMessage());
//            return false;
//        }
        return false;
    }

    @Override
    public boolean update(int id, Task entity) {
        // Tương tự, bạn dùng câu lệnh UPDATE task SET ... WHERE Task_id = ?
        return false;
    }

    @Override
    public boolean delete(int id) {
        // Tương tự, bạn dùng câu lệnh DELETE FROM task WHERE Task_id = ?
        return false;
    }
}