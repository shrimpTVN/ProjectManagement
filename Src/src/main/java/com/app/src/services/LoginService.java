package com.app.src.services;

import com.app.src.daos.AccountDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginService {
    private final AccountDAO accountDAO;
    public LoginService(){
        accountDAO = new AccountDAO();
    }

    public String validateLogin(String userName, String password) throws SQLException {
        return accountDAO.validateLogin(userName, password);
    }
}
