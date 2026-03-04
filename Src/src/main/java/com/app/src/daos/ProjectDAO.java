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

    public  ArrayList<Project> findByUserId(int userId) {
        ArrayList<Project> projects = new ArrayList<>();
        final String sql = "select pr.Pro_id, pr.Pro_name, pr.Pro_startDate, pr.Pro_endDate, pr.Pro_description " +
                "from project_joining PJ join user on PJ.user_id = user.user_id join project pr on PJ.pro_id=pr.pro_id" +
                " where user.user_id = ?";

        try{
        connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();

        while(resultSet.next()){
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
        } finally{
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
        return false;
    }

    @Override
    public boolean update(int id, Object entity) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }
}
