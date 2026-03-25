package com.app.src.daos;

import com.app.src.dtos.PersonalTaskDTO;
import com.app.src.exceptions.DataAccessException;
import com.app.src.models.StatusUpdating;
import com.app.src.models.Task;
import com.app.src.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO extends AbstractDAO<PersonalTaskDTO> { // Đổi Generic type
    private static Connection connection;
    private static TaskDAO instance;

    public static TaskDAO getInstance() {
        if (instance == null) {
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
                "WHERE t.Task_id = ? and t.task_isRejected = false";

        PersonalTaskDTO task = null;
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
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

            while (rs.next()) {
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
        String sql = "SELECT t.*, p.Pro_name, " +
                "COALESCE((" +
                "   SELECT ts.Sta_name " +
                "   FROM STATUS_UPDATING su " +
                "   JOIN TASK_STATUS ts ON su.Sta_id = ts.Sta_id " +
                "   WHERE su.Task_id = t.Task_id " +
                "   ORDER BY su.StU_id DESC " +
                "   LIMIT 1" +
                "), 'To Do') AS Sta_name " +
                "FROM TASK t " +
                "LEFT JOIN PROJECT p ON t.Pro_id = p.Pro_id " +
                "WHERE t.USER_id = ?;";
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
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
                task.setStatusName(rs.getString("Sta_name"));

                User user = new User();
                user.setUserId(rs.getInt("User_id"));
                task.setUser(user);

                tasks.add(task);
            }
            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi lấy danh sách Task cá nhân: " + ex.getMessage(), ex);
        }
        return tasks;
    }
    public List<PersonalTaskDTO> findAllByProjectId(int projectId) {
        List<PersonalTaskDTO> tasks = new ArrayList<>();

        // Câu SQL tương tự, nhưng đổi WHERE thành Pro_id và JOIN thêm bảng USER
        String sql = "SELECT t.*, p.Pro_name, u.User_name, " +
                "COALESCE((" +
                "   SELECT ts.Sta_name " +
                "   FROM STATUS_UPDATING su " +
                "   JOIN TASK_STATUS ts ON su.Sta_id = ts.Sta_id " +
                "   WHERE su.Task_id = t.Task_id " +
                "   ORDER BY su.StU_id DESC " +
                "   LIMIT 1" +
                "), 'To Do') AS Sta_name " +
                "FROM TASK t " +
                "LEFT JOIN PROJECT p ON t.Pro_id = p.Pro_id " +
                "LEFT JOIN USER u ON t.USER_id = u.User_id " + // Lấy thêm tên User cho Board
                "WHERE t.Pro_id = ?;"; // Lọc theo Project ID

        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, projectId); // Truyền projectId vào đây
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
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
                task.setStatusName(rs.getString("Sta_name"));

                User user = new User();
                user.setUserId(rs.getInt("User_id"));
                // Set thêm UserName để BoardController gọi task.getUser().getUserName()
                user.setUserName(rs.getString("User_name"));
                task.setUser(user);

                tasks.add(task);
            }
            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi lấy danh sách Task theo Project: " + ex.getMessage(), ex);
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
        String sql = "DELETE FROM TASK WHERE Task_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                closeResource(ps, conn, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Lấy danh sách công việc thuộc về một dự án cụ thể.
     * Sử dụng LEFT JOIN để lấy kèm tên người thực hiện (Assignee).
     *
     * @param projectId ID của dự án cần lọc.
     */
    public List<Task> findByProjectId(int projectId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT t.*, u.User_name, " +
                "COALESCE((" +
                "   SELECT ts.Sta_name " +
                "   FROM STATUS_UPDATING su " +
                "   JOIN TASK_STATUS ts ON su.Sta_id = ts.Sta_id " +
                "   WHERE su.Task_id = t.Task_id " +
                "   ORDER BY su.StU_id DESC " +
                "   LIMIT 1" +
                "), 'To Do') AS Sta_name " +
                "FROM TASK t " +
                "LEFT JOIN USER u ON t.User_id = u.User_id " +
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
                task.setTaskStatus(rs.getString("Sta_name"));

                com.app.src.models.User user = new com.app.src.models.User();
                user.setUserName(rs.getString("User_name"));
                user.setUserId(rs.getInt("User_id"));
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
     *
     * @return Danh sách các bản ghi thay đổi trạng thái kèm chuỗi định dạng (Cũ ➜ Mới).
     * //
     */
    public List<StatusUpdating> getStatusHistory(int taskId) {
        List<StatusUpdating> list = new ArrayList<>();
        // 1. Thêm u.User_name vào SELECT và JOIN với bảng USER u
        String sql = "SELECT h.StU_date, u.User_name, s_curr.Sta_name AS new_status, " +
                "LAG(s_curr.Sta_name) OVER (PARTITION BY h.Task_id ORDER BY h.StU_date ASC) AS old_status " +
                "FROM status_updating h " +
                "JOIN task_status s_curr ON h.Sta_id = s_curr.Sta_id " +
                "JOIN user u ON h.User_id = u.User_id " + // <--- JOIN để lấy tên người dùng
                "WHERE h.Task_id = ? " +
                "ORDER BY h.StU_date DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String userNameFromDB = rs.getString("User_name"); // Lấy tên từ cột mới JOIN
                String newSta = rs.getString("new_status");
                String oldSta = rs.getString("old_status");
                String displayOld = (oldSta == null) ? "None" : oldSta;

                // 2. GÓI DỮ LIỆU: "Tên người dùng | Trạng thái cũ -> Trạng thái mới"
                // Dùng dấu gạch đứng "|" để tí nữa Controller dễ tách (split)
                String formattedContent = userNameFromDB + "|" + displayOld + " \u279F " + newSta;

                StatusUpdating item = new StatusUpdating();
                item.setDate(rs.getTimestamp("StU_date"));
                item.setContent(formattedContent);
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Cập nhật trạng thái task và chặn chuyển In Preview -> Done nếu user chỉ là Member.
    public boolean appendStatusUpdating(int taskId, String oldStatus, String newStatus, String content, int userId) {
        String normalizedNew = newStatus == null ? "" : newStatus.trim().toLowerCase();
        int statusId = mapStatusId(normalizedNew);
        if (statusId == -1) {
            return false;
        }

        String sqlInsert = "INSERT INTO STATUS_UPDATING (StU_date, StU_content, Task_id, Sta_id) VALUES (CURRENT_TIMESTAMP, ?, ?, ?)";

        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = getConnection();

            // Kiểm tra rule bằng cả trạng thái cũ trên UI và trạng thái mới nhất trong DB để tránh lọt logic.
            int currentStatusId = getCurrentStatusId(connection, taskId);
            String normalizedOld = oldStatus == null ? "" : oldStatus.trim().toLowerCase();
            boolean fromPreviewToDoneByUi = (normalizedOld.equals("in preview") && statusId == 4);
            boolean fromPreviewToDoneByDb = (currentStatusId == 3 && statusId == 4);

            if (fromPreviewToDoneByUi || fromPreviewToDoneByDb) {
                // Chỉ Admin(2) hoặc Manager(1) mới được duyệt In Preview -> Done.
                int roleId = getRoleIdByTaskAndUser(connection, taskId, userId);
                if (roleId != 1 && roleId != 2) {
                    throw new RuntimeException("Chi Admin/Manager moi duoc chuyen In Preview sang Done");
                }
            }

            ps = connection.prepareStatement(sqlInsert);
            ps.setString(1, content);
            ps.setInt(2, taskId);
            ps.setInt(3, statusId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi cập nhật trạng thái task: " + ex.getMessage(), ex);
        } finally {
            try {
                closeResource(ps, connection, null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Map tên trạng thái về ID trong bảng TASK_STATUS.
    private int mapStatusId(String normalizedStatus) {
        if (normalizedStatus.equals("to do") || normalizedStatus.equals("todo")) {
            return 1;
        }
        if (normalizedStatus.equals("in progress") || normalizedStatus.equals("in processing") || normalizedStatus.equals("in progressing")) {
            return 2;
        }
        if (normalizedStatus.equals("in preview")) {
            return 3;
        }
        if (normalizedStatus.equals("done")) {
            return 4;
        }
        return -1;
    }

    // Lấy trạng thái mới nhất của task theo STATUS_UPDATING.
    public int getCurrentStatusId(Connection connection, int taskId) throws SQLException {
        final String sql = "SELECT su.Sta_id FROM STATUS_UPDATING su WHERE su.Task_id = ? ORDER BY su.Sta_id DESC LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Sta_id");
                }
            }
        }

        return 1;
    }

    public String getStatusNameById(int taskId)
    {
        String statusName = "";
        final String sql = "SELECT ts.Sta_name " +
                "FROM STATUS_UPDATING su " +
                "JOIN TASK_STATUS ts ON su.Sta_id = ts.Sta_id " +
                "WHERE su.Task_id = ? " +
                "ORDER BY su.StU_id DESC LIMIT 1";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, taskId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                statusName = rs.getString("Sta_name");
            } else {
                statusName = "To Do";
            }

            closeResource(ps, connection, rs);
        } catch (Exception e) {
            throw new DataAccessException("Khong the truy cap data base", e);
        }

        return statusName;
    }

    // Lấy role hiện tại của user trong project chứa task.
    private int getRoleIdByTaskAndUser(Connection connection, int taskId, int userId) throws SQLException {
        final String sql = "SELECT pj.Role_id " +
                "FROM TASK t " +
                "LEFT JOIN PROJECT_JOINING pj ON t.Pro_id = pj.Pro_id AND pj.User_id = ? " +
                "WHERE t.Task_id = ? " +
                "ORDER BY pj.PJo_dateJoin DESC LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Role_id");
                }
            }
        }
        return -1;
    }

    public boolean createTask(Task task) {
        final String sql = "INSERT INTO TASK (Task_name, Task_description, Task_startDate, Task_endDate, Pro_id, User_id) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, task.getTaskName());
            ps.setString(2, task.getTaskDescription());

            if (task.getTaskStartTime() != null && !task.getTaskStartTime().isEmpty()) {
                ps.setDate(3, java.sql.Date.valueOf(task.getTaskStartTime()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }

            if (task.getTaskEndTime() != null && !task.getTaskEndTime().isEmpty()) {
                ps.setDate(4, java.sql.Date.valueOf(task.getTaskEndTime()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            if (task.getProjectId() > 0) {
                ps.setInt(5, task.getProjectId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            if (task.getUser() != null && task.getUser().getUserId() > 0) {
                ps.setInt(6, task.getUser().getUserId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                closeResource(ps, conn, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updateTaskDeadline(int taskId, String deadline) {
        final String sql = "UPDATE TASK SET Task_endDate = ? WHERE Task_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(deadline));
            ps.setInt(2, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                closeResource(ps, conn, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** Lấy nhanh ngày bắt đầu và project id của task để validate deadline. */
    public TaskDateMeta findTaskDateMeta(int taskId) {
        final String sql = "SELECT Task_startDate, Pro_id FROM TASK WHERE Task_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TaskDateMeta meta = new TaskDateMeta();
                    meta.setStartDate(rs.getDate("Task_startDate"));
                    int projectId = rs.getInt("Pro_id");
                    meta.setProjectId(rs.wasNull() ? null : projectId);
                    return meta;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class TaskDateMeta {
        private java.sql.Date startDate;
        private Integer projectId;

        public java.sql.Date getStartDate() {
            return startDate;
        }

        public void setStartDate(java.sql.Date startDate) {
            this.startDate = startDate;
        }

        public Integer getProjectId() {
            return projectId;
        }

        public void setProjectId(Integer projectId) {
            this.projectId = projectId;
        }
    }

//    public List<StatusUpdating> getStatusHistory(int taskId) {
//    }
}




