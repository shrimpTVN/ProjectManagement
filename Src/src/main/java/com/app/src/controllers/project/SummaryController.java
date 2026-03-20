package com.app.src.controllers.project;

import com.app.src.models.Project;
import com.app.src.models.Task;
import com.app.src.services.TasklistService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SummaryController implements IProjectDetailSubView {

    @FXML
    private PieChart progressPieChart;
    @FXML
    private Label lblProgressPercent;
    @FXML
    private Label lblDoneOverTotal;
    @FXML
    private VBox statusStatsBox;

    private final TasklistService tasklistService = new TasklistService();

    @Override
    public void renderData(Project project, String adminName) {
        if (project == null) {
            return;
        }

        List<Task> tasks = tasklistService.getTasksByProject(project.getProjectId(), project.getUserRoleName());

        Map<String, Integer> statusCount = new LinkedHashMap<>();
        statusCount.put("To Do", 0);
        statusCount.put("In Progress", 0);
        statusCount.put("In Preview", 0);
        statusCount.put("Done", 0);

        for (Task task : tasks) {
            String normalized = normalizeStatus(task.getTaskStatus());
            statusCount.put(normalized, statusCount.getOrDefault(normalized, 0) + 1);
        }

        int total = tasks.size();
        int done = statusCount.getOrDefault("Done", 0);
        int percent = total == 0 ? 0 : Math.round((done * 100.0f) / total);

        lblProgressPercent.setText(percent + "%");
        lblDoneOverTotal.setText(done + " / " + total + " tasks completed");

        progressPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Done", done),
                new PieChart.Data("Remaining", Math.max(total - done, 0))
        ));

        statusStatsBox.getChildren().clear();
        statusCount.forEach((status, count) -> statusStatsBox.getChildren().add(createStatusRow(status, count)));
    }

    private HBox createStatusRow(String status, int count) {
        Label lblStatus = new Label(status);
        lblStatus.setStyle("-fx-font-size: 15px; -fx-text-fill: #222; -fx-font-family: 'Urbanist Medium';");

        Label lblCount = new Label(String.valueOf(count));
        lblCount.setStyle("-fx-font-size: 15px; -fx-text-fill: #222; -fx-font-family: 'Urbanist Medium';");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, lblStatus, spacer, lblCount);
        row.setStyle("-fx-background-color: white; -fx-padding: 10 12; -fx-background-radius: 8; -fx-border-color: #e5e5e5; -fx-border-radius: 8;");
        return row;
    }

    private String normalizeStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return "To Do";
        }

        String normalized = rawStatus.trim().toLowerCase();
        if (normalized.equals("in progressing") || normalized.equals("progressing") || normalized.equals("in progress")) {
            return "In Progress";
        }
        if (normalized.equals("in preview")) {
            return "In Preview";
        }
        if (normalized.equals("done")) {
            return "Done";
        }
        return "To Do";
    }
}
