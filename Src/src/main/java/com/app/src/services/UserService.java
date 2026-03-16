package com.app.src.services;

import com.app.src.daos.UserDAO;
import com.app.src.models.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        userDAO = UserDAO.getInstance();
    }

    public User getUserById(int userId) throws SQLException {
        return userDAO.findById(userId);
    }

    // Gọi hàm đăng ký từ UserDAO
    public boolean register(String username, String name, String phone, String dob, String selectedGender, String password) {
        return userDAO.registerUser(username, name, phone, dob, selectedGender, password);
    }

    public User findByUserName(String userName) {
        return userDAO.findByUserName(userName);
    }
}