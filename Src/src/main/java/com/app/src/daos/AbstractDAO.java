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

    public void closeResource(Statement statement, Connection connection) throws SQLException {
        if (statement != null) {    // Đảm bảo statement không null trước khi đóng
            statement.close();
        }
        if (connection != null) {   // Đảm bảo connection không null trước khi đóng
            connection.commit();
        }
    }
    public void closeResource(Statement statement, Connection connection, ResultSet resultSet) throws SQLException {

        if(resultSet!=null){
            resultSet.close();
        }
        closeResource(statement, connection);
    }

    public void closeConnection(Connection connection) throws SQLException {
       if (connection != null) {
           connection.close();
       }

    }
    // Shared helper methods for executing queries can go here...
}