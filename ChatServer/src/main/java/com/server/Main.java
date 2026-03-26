package com.server;
import com.server.core.ChatServer;
import com.server.service.NotificationService;
import com.server.service.TaskService;
import com.server.thread.DailyDeadlineScheduler;

public class Main {
    public static void main(String[] args) {
        TaskService taskService = new TaskService();
        NotificationService notificationService = new NotificationService();
        DailyDeadlineScheduler scheduler = new DailyDeadlineScheduler(taskService, notificationService);
        scheduler.start();


        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::stop));

        ChatServer server = new ChatServer(8080);
        server.start();
    }
}
