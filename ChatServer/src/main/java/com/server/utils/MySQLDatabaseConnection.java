package com.server.utils;

import com.server.core.DatabaseConnection;
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
//        String databasePassword = "Abcd1234";
        String url = "jdbc:mysql://localhost:3306/" + databaseName;

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);

        }catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }

        return this.databaseLink;
    }
}
