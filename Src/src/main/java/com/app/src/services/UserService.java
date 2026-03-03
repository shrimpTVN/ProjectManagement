package com.app.src.services;

import com.app.src.daos.UserDAO;
import com.app.src.models.User;

import java.sql.SQLException;

public class UserService {
    private final UserDAO userDAO ;

    public UserService() {
        userDAO = UserDAO.getInstance();
    }

    public User getUserById(int userId) throws SQLException
    {

        return userDAO.findById(userId);
    }
}
