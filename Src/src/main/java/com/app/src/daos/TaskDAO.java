package com.app.src.daos;

import com.app.src.dtos.PersonalTaskDTO; // Đã sửa import
import com.app.src.models.StatusUpdating;
import com.app.src.models.Task;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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


    /**
     * Lấy danh sách công việc thuộc về một dự án cụ thể.
     * Sử dụng LEFT JOIN để lấy kèm tên người thực hiện (Assignee).
     * @param projectId ID của dự án cần lọc.
     */
    public List<Task> findByProjectId(int projectId) {
        List<Task> tasks = new ArrayList<>();
        // JOIN với bảng user để lấy tên người thực hiện
        String sql = "SELECT t.*, u.User_name FROM task t " +
                "LEFT JOIN user u ON t.User_id = u.User_id " +
                "WHERE t.Pro_id = ?";
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Task task = new Task();
                task.setTaskId(rs.getInt("Task_id"));
                task.setTaskName(rs.getString("Task_name"));
                task.setTaskStartTime(String.valueOf(rs.getTimestamp("Task_startDate")));
                task.setTaskEndTime(String.valueOf(rs.getTimestamp("Task_endDate")));
                task.setTaskDescription(rs.getString("Task_description"));

                // Map dữ liệu User (Assignee)
                com.app.src.models.User user = new com.app.src.models.User();
                user.setUserName(rs.getString("User_name"));
                task.setUser(user);

                tasks.add(task);
            }
            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi lấy Task theo Project ID: " + ex.getMessage(), ex);
        }
        return tasks;
    }

    /**
     * Lấy lịch sử thay đổi trạng thái của một công việc.
     * Sử dụng Window Function LAG() để xác định trạng thái trước đó mà không cần truy vấn nhiều lần.
     * * @param taskId ID của công việc cần lấy lịch sử.
     * @return Danh sách các bản ghi thay đổi trạng thái kèm chuỗi định dạng (Cũ ➜ Mới).
     */
   public List<StatusUpdating> getStatusHistory(int taskId) {
       List<StatusUpdating> list = new ArrayList<>();
       // SQL sử dụng Window Function LAG để lấy trạng thái trước đó của cùng một Task
       String sql = "SELECT h.StU_date, s_curr.Sta_name AS new_status, " +
               "LAG(s_curr.Sta_name) OVER (PARTITION BY h.Task_id ORDER BY h.StU_date ASC) AS old_status " +
               "FROM status_updating h " +
               "JOIN task_status s_curr ON h.Sta_id = s_curr.Sta_id " +
               "WHERE h.Task_id = ? " +
               "ORDER BY h.StU_date DESC";

       try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
           ps.setInt(1, taskId);
           ResultSet rs = ps.executeQuery();

           while (rs.next()) {
               String newSta = rs.getString("new_status");
               String oldSta = rs.getString("old_status");

               // Nếu là dòng đầu tiên (không có trạng thái cũ), ta để là "None" hoặc tên trạng thái mới luôn
               String displayOld = (oldSta == null) ? "None" : oldSta;
               String formattedContent = displayOld + " \u279F " + newSta; // Tạo chuỗi: To Do ➔ In Progressing

               StatusUpdating item = new StatusUpdating();
               item.setDate(rs.getTimestamp("StU_date"));
               item.setContent(formattedContent); // Ghi đè chuỗi đẹp vào biến content của Model
               list.add(item);
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return list;
   }
}