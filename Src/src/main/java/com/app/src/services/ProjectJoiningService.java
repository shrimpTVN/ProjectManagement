package com.app.src.services;

import com.app.src.daos.ProjectJoiningDAO;
import com.app.src.daos.RoleDAO;
import com.app.src.exceptions.ServiceException;
import com.app.src.models.ProjectJoining;

import java.util.ArrayList;

import static com.app.src.exceptions.ErrorCode.UNKNOWN_ERROR;

public class ProjectJoiningService {
    private static ProjectJoiningDAO projectJoiningDao;

    public ProjectJoiningService() {
        projectJoiningDao = ProjectJoiningDAO.getInstance();
    }

    public String getAdmin(int projectId) {
        return projectJoiningDao.getAdmin(projectId);
    }

    public static String getRoleInProject(int userId, int projectId) {
        int roleId = ProjectJoiningDAO.getInstance().getRoleId(userId, projectId);
        if (roleId == 0) {
            throw new ServiceException( UNKNOWN_ERROR, "Khong the tim thay role cua user trong du an");
        }

        return RoleDAO.getInstance().findById(roleId).getRoleName();
    }

    public ArrayList<ProjectJoining> findAllJoiningsByProjectId(int projectId) {
        return projectJoiningDao.findAllJoiningsByProjectId(projectId);
    }

    public boolean updateRole(int projectId, int userId, int newRoleId) {
        return projectJoiningDao.updateRole(projectId, userId, newRoleId);
    }
    public boolean createNewJoining(int projectId, int userId, int projectRole) {
        // 1. Lấy danh sách thành viên hiện tại của dự án
        ArrayList<ProjectJoining> allMember = projectJoiningDao.findAllJoiningsByProjectId(projectId);

        // 2. Quét từng thành viên trong danh sách
        for (ProjectJoining member : allMember) {
            // Kiểm tra xem ID của user truyền vào có trùng với ID của user nào trong danh sách không
            if (member.getUser().getUserId() == userId) {
                System.out.println("[DEBUG] User " + userId + " đã tồn tại trong dự án " + projectId);
                return false; // Đã tồn tại -> Ngừng chạy hàm và trả về false ngay lập tức
            }
        }

        // 3. Nếu vòng lặp chạy xong mà không bị return false (tức là user chưa có mặt)
        // thì gọi DAO để insert vào Database
        return projectJoiningDao.createNewJoining(projectId, userId, projectRole);
    }

    public boolean removeMember(int projectId, int userId) {
        return projectJoiningDao.removeMember(projectId, userId);
    }
}
