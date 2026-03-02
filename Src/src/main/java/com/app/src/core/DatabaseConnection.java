package com.app.src.core;

import java.sql.Connection;

public interface DatabaseConnection {


    public static Connection getConnection();

    Connection getConnection();
}
