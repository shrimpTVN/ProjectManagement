package com.app.src.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.app.src.models.Status;

public class StatusDAO {
    private static final String URL = "jdbc:sqlite:database.sqlite";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public void insertStatus(Status status) {
        String sql = "INSERT INTO STATUS(Sta_id, Sta_name) VALUES(?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.getStaId());
            pstmt.setString(2, status.getStaName());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Status> getAllStatuses() {
        String sql = "SELECT * FROM STATUS";
        List<Status> statuses = new ArrayList<>();

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Status status = new Status(
                        rs.getString("Sta_id"),
                        rs.getString("Sta_name")
                );
                statuses.add(status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statuses;
    }
}