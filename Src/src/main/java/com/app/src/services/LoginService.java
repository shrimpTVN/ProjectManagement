package com.app.src.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginService {
    private static Connection connectDB;
    
    public LoginService(Connection connectDB){
        LoginService.connectDB = connectDB;
    }
    
    public ResultSet validateLogin(String userName, String password){

        String verifyLogin = "select count(1) from account where username = '" + userName
                + "' and password='" + password + "'";

        System.out.println(verifyLogin);

        ResultSet resultQuery = null;
        try{
            Statement statement = connectDB.createStatement();
            resultQuery =  statement.executeQuery(verifyLogin);

        }catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
        
        return resultQuery;
    }
}
