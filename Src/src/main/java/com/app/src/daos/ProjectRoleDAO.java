package com.app.src.daos;

import com.app.src.models.ProjectRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectRoleDAO extends AbstractDAO<ProjectRole> {

    private static Connection connection;
    private static ProjectRoleDAO instance;

    private ProjectRoleDAO() {}

    public static ProjectRoleDAO getInstance() {
        if (instance == null) {
            instance = new ProjectRoleDAO();
        }
        return instance;
    }

    @Override
    public List<ProjectRole> findAll() {
        List<ProjectRole> roles = new ArrayList<>();
        String sql = "SELECT * FROM project_role";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ProjectRole role = new ProjectRole();
                role.setRoleId(rs.getInt("Role_id"));
                role.setRoleName(rs.getString("Role_name"));
                roles.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    closeConnection(connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return roles;
    }

    @Override
    public ProjectRole findById(int id) {
        return null; // Có thể triển khai sau nếu cần
    }

    @Override
    public boolean create(ProjectRole entity) {
        return false;
    }

    @Override
    public boolean update(int id, ProjectRole entity) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }
}