package com.app.src.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.app.src.authentication.RoleValidator;
import com.app.src.core.AppContext;
import com.app.src.daos.TaskDAO;
import com.app.src.dtos.PersonalTaskDTO;
import com.app.src.models.Project;
import com.app.src.models.Task;

public class TasklistService {

    private static TaskDAO taskDAO;
    private static TasklistService instance;

    public static TasklistService getInstance() {
        if (instance == null) {
            instance = new TasklistService();
        }
        return instance;
    }

    public TasklistService() {
        // CẬP NHẬT: Sử dụng Singleton pattern chuẩn xác thay vì dùng toán tử new
        this.taskDAO = TaskDAO.getInstance();
    }

    // ==========================================
    // 1. ĐỌC DỮ LIỆU (READ)
    // ==========================================
    public List<PersonalTaskDTO> getAllTasks() {
        return taskDAO.findAll();
    }
    public List<PersonalTaskDTO> getTaskByUser(int userID){
        return taskDAO.findAllByUserId(userID);
    }

    // Cập nhật trạng thái task và truyền cả trạng thái cũ/mới để kiểm tra rule nghiệp vụ chắc chắn hơn.
    public boolean updateTaskStatus(int taskId, String oldStatus, String newStatus, String content, int userId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("Invalid task ID!");
        }
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid status!");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user!");
        }
        return taskDAO.appendStatusUpdating(taskId, oldStatus, newStatus, content, userId);
    }

    public boolean deleteTask(int taskId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("Invalid task ID!");
        }
        return taskDAO.delete(taskId);
    }


    /**
     * Lấy danh sách công việc thuộc về một dự án cụ thể.
     * Hàm này được gọi bởi ListController để hiển thị dữ liệu theo Tab.
     * @param projectId ID của dự án cần lấy task.
     */
    public List<Task> getTasksByProject(int projectId, String userRoleName) {
        List<Task> tasks = new ArrayList<>();
        List<Task> allTask = taskDAO.findByProjectId(projectId);
        if (!RoleValidator.isManagerOrAdmin(userRoleName)) {
            for (Task task : allTask) {
                if (task.getUser().getUserId() == AppContext.getUserData().getUserId()) {
                    tasks.add(task);
                }
            }
        }else{ tasks = allTask; }

        return tasks ;
    }

    public boolean addTask(Task newTask) {
        if (newTask == null) {
            throw new IllegalArgumentException("Task data does not exist!");
        }

        if (newTask.getTaskName() == null || newTask.getTaskName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name is required!");
        }

        if (newTask.getTaskName().length() > 255) {
            throw new IllegalArgumentException("Task name is too long (max 255 characters)!");
        }

        newTask.setTaskName(newTask.getTaskName().trim());

        validateTaskDates(newTask);

        return taskDAO.createTask(newTask);
    }

    public static String getCurrentStatusById(int taskId)
    {

        return taskDAO.getStatusNameById(taskId) ;
    }

    private void validateTaskDates(Task task) {
        Date today = truncateToDate(new Date());

        Date start = parseDate(task.getTaskStartTime());
        Date end = parseDate(task.getTaskEndTime());

        if (start != null && start.before(today)) {
            throw new IllegalArgumentException("Task start date cannot be before today!");
        }

        if (end != null && end.before(today)) {
            throw new IllegalArgumentException("Deadline cannot be before today!");
        }

        if (start != null && end != null && end.before(start)) {
            throw new IllegalArgumentException("Deadline must be on or after the task start date!");
        }

        Project project = resolveProject(task.getProjectId());
        if (project != null) {
            Date projectStart = truncateToDate(project.getProjectStartDate());
            Date projectEnd = truncateToDate(project.getProjectEndDate());

            if (projectStart != null) {
                if (start != null && start.before(projectStart)) {
                    throw new IllegalArgumentException("Task start date cannot be before the project start date!");
                }
                if (end != null && end.before(projectStart)) {
                    throw new IllegalArgumentException("Task deadline cannot be before the project start date!");
                }
            }

            if (projectEnd != null) {
                if (start != null && start.after(projectEnd)) {
                    throw new IllegalArgumentException("Task start date cannot be after the project end date!");
                }
                if (end != null && end.after(projectEnd)) {
                    throw new IllegalArgumentException("Task deadline cannot be after the project end date!");
                }
            }
        }
    }

    public boolean updateTaskDeadline(int taskId, String deadline) {
        Date today = truncateToDate(new Date());
        Date newDeadline = parseDate(deadline);
        if (newDeadline == null) {
            throw new IllegalArgumentException("Invalid deadline!");
        }
        if (newDeadline.before(today)) {
            throw new IllegalArgumentException("Deadline cannot be before today!");
        }

        TaskDAO.TaskDateMeta meta = taskDAO.findTaskDateMeta(taskId);
        if (meta != null) {
            Date startDate = truncateToDate(meta.getStartDate());
            if (startDate != null && newDeadline.before(startDate)) {
                throw new IllegalArgumentException("Deadline cannot be before the task start date!");
            }

            Project project = resolveProject(meta.getProjectId());
            if (project != null) {
                Date projectStart = truncateToDate(project.getProjectStartDate());
                Date projectEnd = truncateToDate(project.getProjectEndDate());
                if (projectStart != null && newDeadline.before(projectStart)) {
                    throw new IllegalArgumentException("Deadline cannot be before the project start date!");
                }
                if (projectEnd != null && newDeadline.after(projectEnd)) {
                    throw new IllegalArgumentException("Deadline cannot be after the project end date!");
                }
            }
        }

        return taskDAO.updateTaskDeadline(taskId, deadline.trim());
    }

    private Date parseDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            return truncateToDate(java.sql.Date.valueOf(raw.trim()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Date truncateToDate(Date date) {
        if (date == null) return null;
        return new java.sql.Date(date.getTime());
    }

    private Project resolveProject(Integer projectId) {
        if (projectId == null || projectId <= 0) {
            return null;
        }
        return AppContext.getProjectById(projectId);
    }
}