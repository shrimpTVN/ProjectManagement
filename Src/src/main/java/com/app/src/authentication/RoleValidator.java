package com.app.src.authentication;

public class RoleValidator {
    private static RoleValidator instance;
    private final static String admin = "Admin";
    private final static String manager = "Project Manager";
    private RoleValidator() {}

    public static RoleValidator getInstance() {
        if (instance == null) {
            instance = new RoleValidator();
        }
        return instance;
    }

    public static boolean isManager(String roleName)
    {
        return roleName.equals(manager);
    }

    public static boolean isAdmin(String roleName){
        return roleName.equals(admin);
    }

    public static boolean isManagerOrAdmin(String roleName){
       return roleName.equals(manager) || roleName.equals(admin);
    }

    public static boolean canCreatTask(String roleName){
        return isManagerOrAdmin(roleName);
    }

    public static boolean canEditProject(String roleName){
        return isAdmin(roleName);
    }



}
