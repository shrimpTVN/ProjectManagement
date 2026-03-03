package com.app.src.daos;

import com.app.src.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAO extends AbstractDAO<User>{

    private static Connection connection;
    private static UserDAO instance;
    public static UserDAO getInstance(){
        if(instance == null){
            instance = new UserDAO();

        }
        return instance;
    }

    @Override
    public User findById(int id) {
        String sql = "select * from user where User_id = ?";
        User user = new User();
        try{
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){

                user.setUserId(id);
                user.setUserName(rs.getString("User_name"));
                user.setUserDoB(rs.getString("User_dateOfBirth"));
                user.setUserGender(rs.getBoolean("User_gender"));
                user.setUserPhoneNumber(rs.getString("User_phoneNumber"));
            }

            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {}
            }
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public boolean create(User entity) {
        return false;
    }

    @Override
    public boolean update(int id, User entity) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }
}
