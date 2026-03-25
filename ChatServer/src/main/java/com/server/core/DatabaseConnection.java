package com.server.core;

import java.sql.Connection;

public interface DatabaseConnection {

    public Connection getConnection();
}
