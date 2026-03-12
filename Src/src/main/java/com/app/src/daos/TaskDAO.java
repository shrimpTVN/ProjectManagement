package com.app.src.daos;

import com.app.src.dtos.PersonalTaskDTO; // Đã sửa import

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO extends AbstractDAO<PersonalTaskDTO> { // Đổi Generic type

    private static TaskDAO instance;

    public static TaskDAO getInstance(){
        if(instance == null){
            instance = new TaskDAO();
        }
        return instance;
    }

    @Override
    public PersonalTaskDTO findById(int id) {
        // Cập nhật câu lệnh SQL có JOIN để lấy tên Project và Status
        // Dùng LEFT JOIN để đề phòng trường hợp Task chưa được gán Project hoặc Status
        String sql = "SELECT t.*, p.Pro_name, s.Sta_name " +
                "FROM TASK t " +
                "LEFT JOIN PROJECT p ON t.Pro_id = p.Pro_id " +
                "LEFT JOIN STATUS s ON t.Sta_id = s.Sta_id " +
                "WHERE t.Task_id = ?";

        PersonalTaskDTO task = null;
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                task = new PersonalTaskDTO();
                task.setTaskId(id);
                task.setTaskName(rs.getString("Task_name"));

                // Tránh lỗi NullPointerException khi ngày tháng bị rỗng trong DB
                if (rs.getTimestamp("Task_startDate") != null) {
                    task.setTaskStartTime(String.valueOf(rs.getTimestamp("Task_startDate")));
                }

                // Đổi thành Task_deadline cho khớp với sơ đồ lớp bạn gửi trước đó
                if (rs.getTimestamp("Task_deadline") != null) {
                    task.setTaskEndTime(String.valueOf(rs.getTimestamp("Task_deadline")));
                }

                task.setTaskDescription(rs.getString("Task_description"));

                // Gán dữ liệu cho 2 thuộc tính mới của DTO
                task.setProjectName(rs.getString("Pro_name"));
                task.setStatusName(rs.getString("Sta_name"));
            }
            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi tìm Task theo ID: " + ex.getMessage(), ex);
        }
        return task;
    }

    @Override
    public List<PersonalTaskDTO> findAll() {
        List<PersonalTaskDTO> tasks = new ArrayList<>();

        // Cập nhật câu lệnh SQL có JOIN
        String sql = "SELECT t.*, p.Pro_name, s.Sta_name " +
                "FROM TASK t " +
                "LEFT JOIN PROJECT p ON t.Pro_id = p.Pro_id " +
                "LEFT JOIN STATUS s ON t.Sta_id = s.Sta_id";

        Connection connection = null;
        System.out.println("Đang kết nối Database...");
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            System.out.println("Thực thi SQL thành công, đang đọc kết quả...");

            while(rs.next()) {
                PersonalTaskDTO task = new PersonalTaskDTO();
                task.setTaskId(rs.getInt("Task_id"));
                task.setTaskName(rs.getString("Task_name"));

                if (rs.getTimestamp("Task_startDate") != null) {
                    task.setTaskStartTime(String.valueOf(rs.getTimestamp("Task_startDate")));
                }
                if (rs.getTimestamp("Task_deadline") != null) {
                    task.setTaskEndTime(String.valueOf(rs.getTimestamp("Task_deadline")));
                }

                task.setTaskDescription(rs.getString("Task_description"));

                // Gán dữ liệu cho 2 thuộc tính mới của DTO
                task.setProjectName(rs.getString("Pro_name"));
                task.setStatusName(rs.getString("Sta_name"));

                tasks.add(task);
                System.out.println("Đã lấy được Task: " + task.getTaskName());
            }
            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi lấy danh sách Task: " + ex.getMessage(), ex);
        }
        return tasks;
    }

    // --- Bổ sung thêm hàm lấy task theo User (Vì bài toán ban đầu là Bảng Task Cá Nhân) ---
    public List<PersonalTaskDTO> findAllByUserId(int userId) {
        List<PersonalTaskDTO> tasks = new ArrayList<>();
        String sql = "SELECT t.*, p.Pro_name FROM TASK t "+
                "    LEFT JOIN PROJECT p ON t.Pro_id = p.Pro_id "+
                "   WHERE t.USER_id = ?;";
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                PersonalTaskDTO task = new PersonalTaskDTO();
                task.setTaskId(rs.getInt("Task_id"));
                task.setTaskName(rs.getString("Task_name"));

                if (rs.getTimestamp("Task_startDate") != null) {
                    task.setTaskStartTime(String.valueOf(rs.getTimestamp("Task_startDate")));
                }
                if (rs.getTimestamp("Task_endDate") != null) {
                    task.setTaskEndTime(String.valueOf(rs.getTimestamp("Task_endDate")));
                }

                task.setTaskDescription(rs.getString("Task_description"));
                task.setProjectName(rs.getString("Pro_name"));
//                task.setStatusName(rs.getString("Sta_name"));
                System.out.println(task.getTaskName());
                tasks.add(task);
            }
            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi lấy danh sách Task cá nhân: " + ex.getMessage(), ex);
        }
        return tasks;
    }

    @Override
    public boolean create(PersonalTaskDTO entity) {
        // Lưu ý: Khi Insert, bạn chỉ insert vào bảng TASK, không insert vào bảng JOIN
        return false;
    }

    @Override
    public boolean update(int id, PersonalTaskDTO entity) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }
}