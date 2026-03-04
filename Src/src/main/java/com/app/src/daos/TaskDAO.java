package com.app.src.daos;

import com.app.src.models.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TaskDAO extends AbstractDAO<Task>{

    private static Connection connection;
    private static UserDAO instance;
    public static UserDAO getInstance(){
        if(instance == null){
            instance = new UserDAO();

        }
        return instance;
    }

    @Override
    public Task findById(int id) {
        String sql = "select * from user where User_id = ?";
        Task task = new Task();
        try{
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){

                task.setTaskId(id);
                task.setTaskName(rs.getString("Task_id"));
                task.setTaskStartTime(rs.getTime("Task_startDate"));
                task.setTaskEndTime(rs.getTime("Task_endDate"));
                task.setTaskDescription(rs.getString("Task_description"));
                //more
            }

            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {}
            }
        }

        return task;
    }

    @Override
    public List<Task> findAll() {
        return List.of();
    }

    @Override
    public boolean create(Task entity) {
        return false;
    }

    @Override
    public boolean update(int id, Task entity) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }
}
