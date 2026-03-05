package com.app.src.daos;

import com.app.src.models.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AccountDAO extends AbstractDAO<Account> {
    private static Connection connection;
    private static AccountDAO instance;

    public static AccountDAO getInstance(){
        if (instance == null){
            instance = new AccountDAO();
        }
        return instance;
    }

    private AccountDAO(){
    }

    public int validateLogin(String  username, String password) throws SQLException {
        int userId=-1;
        final String sql="select user_id from account where Acc_userName=? and Acc_password=? ";
        try {
            connection=this.getConnection();
            PreparedStatement ps=connection.prepareStatement(sql);
            ps.setString(1,username);
            ps.setString(2,password);
            ResultSet rs=ps.executeQuery();

            if (rs.next()) {
                userId=rs.getInt("user_id");
            }

            this.closeResource(ps, connection, rs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
          closeConnection(connection);
        }

        return userId;
    }
    @Override
    public Account findById(int id) {
        return null;
    }

    @Override
    public List<Account> findAll() {
        return List.of();
    }

    @Override
    public boolean create(Account entity) {
        return false;
    }

    @Override
    public boolean update(int id, Account entity) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }
}
