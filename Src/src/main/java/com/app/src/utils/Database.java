//VietDao is author
package com.app.src.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // Đường dẫn tới file database.sqlite (nằm cùng thư mục gốc của project)
    private static final String URL = "jdbc:sqlite:database.sqlite";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            // Bật tính năng kiểm tra khóa ngoại (Foreign Key) cho SQLite
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
        return conn;
    }
}