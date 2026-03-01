package com.app.src.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.app.src.models.Project;

public class ProjectDAO {
    private static final String URL = "jdbc:sqlite:database.sqlite";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public void insertProject(Project project) {
        String sql = "INSERT INTO PROJECT(Pro_id, Pro_name, Pro_startDate, Pro_endDate, Pro_description) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, project.getProId());
            pstmt.setString(2, project.getProName());
            pstmt.setTimestamp(3, project.getProStartDate());
            pstmt.setTimestamp(4, project.getProEndDate());
            pstmt.setString(5, project.getProDescription());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Project getProjectById(String proId) {
        String sql = "SELECT * FROM PROJECT WHERE Pro_id = ?";
        Project project = null;

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                project = new Project(
                        rs.getString("Pro_id"),
                        rs.getString("Pro_name"),
                        rs.getTimestamp("Pro_startDate"),
                        rs.getTimestamp("Pro_endDate"),
                        rs.getString("Pro_description")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return project;
    }
}