package com.app.src.controllers.project;

import com.app.src.models.Project;
import com.app.src.models.Task;
import com.app.src.services.TasklistService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
    private VBox statusLegendBox;
    @FXML
    private VBox memberProgressBox;

    private final TasklistService tasklistService = new TasklistService();

    @Override
    public void renderData(Project project, String adminName) {
        if (project == null) {
            return;
        }

        List<Task> tasks = tasklistService.getTasksByProject(project.getProjectId(), project.getUserRoleName());

        Map<String, Integer> statusCount = new LinkedHashMap<>();
        statusCount.put("Done", 0);
        statusCount.put("To Do", 0);
        statusCount.put("In Progress", 0);
        statusCount.put("In Preview", 0);
        statusCount.put("Overdue", 0);

        Map<String, Integer> memberTotal = new LinkedHashMap<>();
        Map<String, Integer> memberDone = new LinkedHashMap<>();

        for (Task task : tasks) {
            String normalized = normalizeStatus(task.getTaskStatus());
            statusCount.put(normalized, statusCount.getOrDefault(normalized, 0) + 1);

            if (!"Done".equals(normalized) && isOverdue(task.getTaskEndTime())) {
                statusCount.put("Overdue", statusCount.get("Overdue") + 1);
            }

            String memberName = resolveMemberName(task);
            memberTotal.put(memberName, memberTotal.getOrDefault(memberName, 0) + 1);
            if ("Done".equals(normalized)) {
                memberDone.put(memberName, memberDone.getOrDefault(memberName, 0) + 1);
            }
        }

        int total = tasks.size();
        int done = statusCount.getOrDefault("Done", 0);
        int percent = total == 0 ? 0 : Math.round((done * 100.0f) / total);

        lblProgressPercent.setText(percent + "%");
        lblDoneOverTotal.setText(done + " / " + total + " tasks completed");

        progressPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Done", statusCount.get("Done")),
                new PieChart.Data("To Do", statusCount.get("To Do")),
                new PieChart.Data("In Progress", statusCount.get("In Progress")),
                new PieChart.Data("In Preview", statusCount.get("In Preview")),
                new PieChart.Data("Overdue", statusCount.get("Overdue"))
        ));
        applyPieColors();

        renderLegend(statusCount);
        renderMemberProgress(memberTotal, memberDone);
    }

    private void applyPieColors() {
        // Gán màu theo tên trạng thái để không bị lệch khi JavaFX đổi thứ tự màu mặc định.
        Map<String, String> colorMap = new LinkedHashMap<>();
        colorMap.put("Done", "#4CAF50");       // xanh
        colorMap.put("To Do", "#FFC107");      // vàng
        colorMap.put("In Progress", "#4896FE"); // vàng
        colorMap.put("In Preview", "#8B5CF6"); // tím
        colorMap.put("Overdue", "#FF5252");    // đỏ

        Platform.runLater(() -> {
            for (PieChart.Data data : progressPieChart.getData()) {
                String color = colorMap.get(data.getName());
                if (color == null) {
                    continue;
                }

                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-pie-color: " + color + ";");
                } else {
                    data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            newNode.setStyle("-fx-pie-color: " + color + ";");
                        }
                    });
                }
            }
        });
    }

    private void renderLegend(Map<String, Integer> statusCount) {
        statusLegendBox.getChildren().clear();

        Map<String, String> colorMap = new LinkedHashMap<>();
        colorMap.put("Done", "#4CAF50");
        colorMap.put("To Do", "#FFC107");
        colorMap.put("In Progress", "#4896FE");
        colorMap.put("In Preview", "#8B5CF6");
        colorMap.put("Overdue", "#FF5252");

        colorMap.forEach((status, color) -> {
            int count = statusCount.getOrDefault(status, 0);
            statusLegendBox.getChildren().add(createLegendRow(status, count, color));
        });
    }

    private HBox createLegendRow(String status, int count, String color) {
        Circle colorBox = new Circle(6);
        colorBox.setFill(Color.web(color));

        Label lblStatus = new Label(status);
        lblStatus.getStyleClass().add("summary-legend-status");

        Label lblCount = new Label(String.valueOf(count));
        lblCount.getStyleClass().add("summary-legend-count");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(8, colorBox, lblStatus, spacer, lblCount);
        row.getStyleClass().add("summary-legend-row");
        return row;
    }

    private void renderMemberProgress(Map<String, Integer> memberTotal, Map<String, Integer> memberDone) {
        memberProgressBox.getChildren().clear();

        if (memberTotal.isEmpty()) {
            Label emptyLabel = new Label("No member task data.");
            emptyLabel.getStyleClass().add("summary-member-empty");
            memberProgressBox.getChildren().add(emptyLabel);
            return;
        }

        memberTotal.forEach((member, total) -> {
            int done = memberDone.getOrDefault(member, 0);
            memberProgressBox.getChildren().add(createMemberProgressRow(member, done, total));
        });
    }

    private VBox createMemberProgressRow(String memberName, int done, int total) {
        double ratio = total == 0 ? 0.0 : (done * 1.0 / total);
        int percent = (int) Math.round(ratio * 100);

        String safeName = (memberName == null || memberName.isBlank()) ? "Unassigned" : memberName;
        String avatarText = safeName.substring(0, 1).toUpperCase();

        Label avatarLabel = new Label(avatarText);
        avatarLabel.getStyleClass().add("summary-member-avatar");

        Label nameLabel = new Label(safeName);
        nameLabel.getStyleClass().add("summary-member-name");

        Label subValueLabel = new Label(done + " of " + total + " done");
        subValueLabel.getStyleClass().add("summary-member-subvalue");

        VBox memberMetaBox = new VBox(1, nameLabel, subValueLabel);
        memberMetaBox.getStyleClass().add("summary-member-meta");

        Label valueLabel = new Label(done + "/" + total + " tasks");
        valueLabel.getStyleClass().add("summary-member-value");

        Label percentLabel = new Label(percent + "%");
        percentLabel.getStyleClass().add("summary-member-percent-chip");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topLine = new HBox(10, avatarLabel, memberMetaBox, spacer, valueLabel, percentLabel);
        topLine.getStyleClass().add("summary-member-topline");

        ProgressBar progressBar = new ProgressBar(ratio);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.getStyleClass().add("summary-member-progress");
        if (percent >= 80) {
            progressBar.getStyleClass().add("summary-member-progress-high");
        } else if (percent >= 40) {
            progressBar.getStyleClass().add("summary-member-progress-medium");
        } else {
            progressBar.getStyleClass().add("summary-member-progress-low");
        }

        VBox row = new VBox(7, topLine, progressBar);
        row.getStyleClass().add("summary-member-row");
        return row;
    }

    private String resolveMemberName(Task task) {
        if (task != null && task.getUser() != null && task.getUser().getUserName() != null && !task.getUser().getUserName().isBlank()) {
            return task.getUser().getUserName();
        }
        return "Unassigned";
    }


    private boolean isOverdue(String rawDeadline) {
        if (rawDeadline == null || rawDeadline.isBlank()) {
            return false;
        }

        String deadlineText = rawDeadline.trim();
        if (deadlineText.length() >= 10) {
            deadlineText = deadlineText.substring(0, 10);
        }

        try {
            LocalDate deadline = LocalDate.parse(deadlineText);
            return deadline.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
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
