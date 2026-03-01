package com.app.src.daos;

import com.app.src.core.DatabaseConnection;
import com.app.src.daos.BaseDAO;

import java.sql.Connection;

// 2. Abstract DAO (Applying the Template Method Pattern )
// This class handles database connection boilerplate so your 9 DAOs don't have to repeat it.
public abstract class AbstractDAO<T> implements BaseDAO<T> {
    protected Connection getConnection() {
        // Return your database connection here
        return DatabaseConnection.getConnection();
    }
    // Shared helper methods for executing queries can go here...
}