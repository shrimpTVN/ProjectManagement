package com.app.src.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.app.src.models.Task;

public class TaskDAO {
    private static final String URL = "jdbc:sqlite:database.sqlite";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public void insertTask(Task task) {
        String sql = "INSERT INTO TASK(Task_id, Task_name, Task_description, Task_startDate, Task_deadline, Pro_id) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getTaskId());
            pstmt.setString(2, task.getTaskName());
            pstmt.setString(3, task.getTaskDescription());
            pstmt.setTimestamp(4, task.getTaskStartDate());
            pstmt.setTimestamp(5, task.getTaskDeadline());
            pstmt.setString(6, task.getProId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getTasksByProjectId(String proId) {
        String sql = "SELECT * FROM TASK WHERE Pro_id = ?";
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Task task = new Task(
                        rs.getString("Task_id"),
                        rs.getString("Task_name"),
                        rs.getString("Task_description"),
                        rs.getTimestamp("Task_startDate"),
                        rs.getTimestamp("Task_deadline"),
                        rs.getString("Pro_id")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}