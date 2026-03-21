package com.app.src.services;

import com.app.src.daos.UserDAO;
import com.app.src.models.User;

import java.sql.SQLException;

public class UserService {
    private final UserDAO userDAO;
    private static UserService instance;
    public static UserService getInstance() {
        if (instance == null) instance = new UserService();
        return instance;
    }
    public UserService() {
        userDAO = UserDAO.getInstance();
    }

    public User getUserById(int userId){

        return userDAO.findById(userId);
    }

    public User getUserByName(String userName) {
        return userDAO.findByUserName(userName);
    }

    // Goi ham dang ky tu UserDAO
    public boolean register(String username, String name, String phone, String dob, String selectedGender, String password) {
        return userDAO.registerUser(username, name, phone, dob, selectedGender, password);
    }

    public User findByUserName(String userName) {
        return userDAO.findByUserName(userName);
    }

    public boolean updateProfile(User user) {
        if (user == null || user.getUserId() <= 0) {
            return false;
        }
        return userDAO.update(user.getUserId(), user);
    }
}
