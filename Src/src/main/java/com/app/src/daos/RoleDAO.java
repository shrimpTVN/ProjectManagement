package com.app.src.daos;


import com.app.src.models.ProjectRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RoleDAO extends AbstractDAO<ProjectRole> {
    private static Connection connection;
    private static RoleDAO instance;

    private RoleDAO() {}

    public static RoleDAO getInstance() {
        if (instance == null) instance = new RoleDAO();
        return instance;
    }
    @Override
    public ProjectRole findById(int id) {
        ProjectRole role = null;
        final String sql = "SELECT * FROM project_role WHERE role_id = ?";

        try{
            connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                role = new ProjectRole();
                role.setRoleId(resultSet.getInt("role_id"));
                role.setRoleName(resultSet.getString("role_name"));
            }
            closeResource(statement, connection, resultSet);
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            try {
                closeConnection(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return role;
    }

    @Override
    public List<ProjectRole> findAll() {
        return List.of();
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
