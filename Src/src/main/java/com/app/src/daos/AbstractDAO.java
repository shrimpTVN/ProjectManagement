package com.app.src.daos;

import com.app.src.core.DatabaseConnection;
import com.app.src.daos.BaseDAO;
import com.app.src.models.Account;
import com.app.src.utils.MySQLDatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// 2. Abstract DAO (Applying the Template Method Pattern )
// This class handles database connection boilerplate so your 9 DAOs don't have to repeat it.
public abstract class AbstractDAO<T> implements BaseDAO<T> {
    public Connection getConnection() throws SQLException {
       Connection connection = (new MySQLDatabaseConnection()).getConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    public void closeResource(Statement statement, Connection connection, ResultSet resultSet) throws SQLException {
        statement.close();
        if(resultSet!=null){
            resultSet.close();
        }
        connection.commit();
        connection.rollback();
    }

    // Shared helper methods for executing queries can go here...
}