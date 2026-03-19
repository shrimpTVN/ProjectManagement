package com.app.src.authentication;

public class RoleValidator {
    private static RoleValidator instance;
    private final String admin = "Admin";
    private final String manager = "Project Manager";
    private RoleValidator() {}

    public static RoleValidator getInstance() {
        if (instance == null) {
            instance = new RoleValidator();
        }
        return instance;
    }

    public boolean isManager(String roleName)
    {
        return roleName.equals(manager);
    }

    public boolean isAdmin(String roleName){
        return roleName.equals(admin);
    }

    public boolean isManagerOrAdmin(String roleName){
       return roleName.equals(manager) || roleName.equals(admin);
    }

    public boolean canCreatTask(String roleName){
        return isManagerOrAdmin(roleName);
    }

    public boolean canEditProject(String roleName){
        return isAdmin(roleName);
    }



}
