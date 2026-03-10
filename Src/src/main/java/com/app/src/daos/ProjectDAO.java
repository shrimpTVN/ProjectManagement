package com.app.src.daos;

import com.app.src.models.Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO extends AbstractDAO {
    private static ProjectDAO instance;
    private static Connection connection;

    private ProjectDAO() {
    }

    public static ProjectDAO getInstance() {
        if (instance == null) {
            instance = new ProjectDAO();
        }

        return instance;
    }

    public ArrayList<Project> findByUserId(int userId) {
        ArrayList<Project> projects = new ArrayList<>();
        final String sql = "select pr.Pro_id, pr.Pro_name, pr.Pro_startDate, pr.Pro_endDate, pr.Pro_description " +
                "from project_joining PJ join user on PJ.user_id = user.user_id join project pr on PJ.pro_id=pr.pro_id" +
                " where user.user_id = ?";

        try {
            connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Project project = new Project();
                project.setProjectId(resultSet.getInt("Pro_id"));
                project.setProjectName(resultSet.getString("Pro_name"));
                project.setProjectStartDate(resultSet.getDate("Pro_startDate"));
                project.setProjectEndDate(resultSet.getDate("Pro_endDate"));
                project.setProjectDescription(resultSet.getString("Pro_description"));
//            System.out.println(project);

                projects.add(project);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }


        return projects;
    }

    @Override
    public Object findById(int id) {
        Project p = null;
        final String sql = "SELECT * FROM projects WHERE id = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                p = new Project();
                p.setProjectId(rs.getInt("Pro_id"));
                p.setProjectName(rs.getString("Pro_name"));
                p.setProjectDescription(rs.getString("Pro_description"));
                p.setProjectStartDate(rs.getDate("Pro_startDate"));
                p.setProjectEndDate(rs.getDate("Pro_endDate"));
            }

            closeResource(ps, connection, rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return p;
    }

    @Override
    public List findAll() {
        return List.of();
    }

    @Override
    public boolean create(Object entity) {
        if (!(entity instanceof Project)) {
            return false;       // Nếu đối tượng truyền vào không phải là Project, trả về false để báo lỗi
        }

        Project project = (Project) entity;     // Ép kiểu entity thành Project để lấy dữ liệu

        // Câu lệnh SQL để chèn dữ liệu vào bảng project. Sử dụng dấu ? để đại diện cho các giá trị sẽ được set sau
        final String sql = "INSERT INTO PROJECT (Pro_name, Pro_startDate, Pro_endDate, Pro_description) VALUES (?, ?, ?, ?)";

        try {
            connection = getConnection();   // Lấy kết nối đến database

            // Tạo PreparedStatement để thực thi câu lệnh SQL. PreparedStatement là một interface trong JDBC cho phép thực thi các câu lệnh SQL với tham số. Nó giúp ngăn chặn SQL injection và tối ưu hiệu suất khi thực thi nhiều lần cùng một câu lệnh.
            PreparedStatement ps = connection.prepareStatement(sql);

            // Set các giá trị cho câu lệnh SQL. Các giá trị này sẽ thay thế cho dấu ? trong câu lệnh SQL. Số thứ tự của tham số bắt đầu từ 1.
            ps.setString(1, project.getProjectName());
            ps.setDate(2, new java.sql.Date(project.getProjectStartDate().getTime()));  // Chuyển đổi java.util.Date sang java.sql.Date để phù hợp với kiểu dữ liệu trong database
            ps.setDate(3, new java.sql.Date(project.getProjectEndDate().getTime()));
            ps.setString(4, project.getProjectDescription());

            // Thực thi câu lệnh SQL. executeUpdate() trả về số lượng hàng bị ảnh hưởng bởi câu lệnh SQL. Nếu số lượng hàng > 0, nghĩa là chèn dữ liệu thành công.
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    // Thêm dự án và trả về ID vừa được tạo
    public int createAndReturnId(Project project) {
        final String sql = "INSERT INTO project (Pro_name, Pro_startDate, Pro_endDate, Pro_description) VALUES (?, ?, ?, ?)";
        int generatedId = -1; // generatedId sẽ lưu ID của dự án vừa được tạo. Ban đầu đặt là -1 để biểu thị chưa có ID nào được tạo thành công

        try {
            connection = getConnection();
            // Thêm tham số RETURN_GENERATED_KEYS vào prepareStatement
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, project.getProjectName());
            ps.setDate(2, new java.sql.Date(project.getProjectStartDate().getTime()));
            ps.setDate(3, new java.sql.Date(project.getProjectEndDate().getTime()));
            ps.setString(4, project.getProjectDescription());

            int rowsAffected = ps.executeUpdate();

            // Nếu chèn thành công, lấy ID ra
            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
                rs.close();
                connection.commit();
            }
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {       // Nếu có lỗi, rollback để đảm bảo dữ liệu không bị lỗi
                    connection.rollback();      // rollback sẽ hoàn tác tất cả các thay đổi đã thực hiện trong transaction hiện tại, đưa database trở về trạng thái trước khi bắt đầu transaction. Điều này rất quan trọng để đảm bảo tính toàn vẹn của dữ liệu khi có lỗi xảy ra.
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return generatedId;
    }

    // Lấy project cùng với dữ liệu project_joining (Admin & Manager)
    public Project getProjectWithJoinings(int projectId) {
        Project project = null;
        final String sql = "SELECT * FROM project WHERE Pro_id = ?";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, projectId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                project = new Project();
                project.setProjectId(rs.getInt("Pro_id"));
                project.setProjectName(rs.getString("Pro_name"));
                project.setProjectDescription(rs.getString("Pro_description"));
                project.setProjectStartDate(rs.getDate("Pro_startDate"));
                project.setProjectEndDate(rs.getDate("Pro_endDate"));
            }

            closeResource(ps, connection, rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return project;
    }

    @Override
    public boolean update(int id, Object entity) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    public boolean deleteByProjectId(int projectId) {
        String sqlDeleteJoining = "DELETE FROM project_joining WHERE Pro_id = ?";
        String sqlDeleteProject = "DELETE FROM project WHERE Pro_id = ?";

        Connection conn = null;
        PreparedStatement psJoining = null;
        PreparedStatement psProject = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Xóa thành viên dự án
            psJoining = conn.prepareStatement(sqlDeleteJoining);
            psJoining.setInt(1, projectId);
            psJoining.executeUpdate();

            // 2. Xóa dự án
            psProject = conn.prepareStatement(sqlDeleteProject);
            psProject.setInt(1, projectId);
            int rows = psProject.executeUpdate();

            conn.commit(); // Áp dụng thay đổi xuống database
            return rows > 0;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Có lỗi thì hủy bỏ toàn bộ thao tác xóa
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (psJoining != null) psJoining.close();
                // Tận dụng hàm closeResource bạn đã viết sẵn ở Abstract DAO
                closeResource(psProject, conn, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
