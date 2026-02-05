package com.app.src.utils;

import com.app.src.core.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;


public class MySQLDatabaseConnection implements DatabaseConnection {
    private static Connection databaseLink;

    @Override
    public Connection getConnection()
    {
        String databaseName = "mydb";
        String databaseUser = "root";
        String databasePassword = "123456";
        String url = "jdbc:mysql://localhost:3306/" + databaseName;

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);

        }catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }

        return databaseLink;
    }
}
