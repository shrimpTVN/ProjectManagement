package com.app.src.controllers;

import com.app.src.core.session.UserSession;
import com.app.src.models.User;
import com.app.src.services.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class InformationController implements Initializable {

    @FXML
    private Label nameLabel;

    @FXML
    private Label dataEmailLabel;

    @FXML
    private Label dataSexLabel;

    @FXML
    private Label dataTeleLabel;

    @FXML
    private Label dataDateofbirthLabel;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Rectangle Userbackground;

    @FXML
    private Button editButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label updateMessageLabel;

    @FXML
    private Label nameValueLabel;

    @FXML
    private Label dobValueLabel;

    @FXML
    private Label sexValueLabel;

    @FXML
    private Label teleValueLabel;

    @FXML
    private TextField nameField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private ComboBox<String> sexCombo;

    @FXML
    private TextField teleField;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sexCombo.setItems(FXCollections.observableArrayList("Male", "Female"));
        setEditMode(false);
        refreshFromSession();
    }

    @FXML
    private void handleEditClick() {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null || currentUser.getUserId() <= 0) {
            showMessage("Khong tim thay thong tin nguoi dung.", true);
            return;
        }

        nameField.setText(safe(currentUser.getUserName()));
        teleField.setText(safe(currentUser.getUserPhoneNumber()));
        dobPicker.setValue(parseDate(currentUser.getUserDoB()));
        sexCombo.setValue(currentUser.isUserGender() ? "Male" : "Female");

        showMessage("", false);
        setEditMode(true);
    }

    @FXML
    private void handleCancelClick() {
        showMessage("", false);
        setEditMode(false);
        refreshFromSession();
    }

    @FXML
    private void handleSaveClick() {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null || currentUser.getUserId() <= 0) {
            showMessage("Khong tim thay thong tin nguoi dung.", true);
            return;
        }

        String name = safe(nameField.getText()).trim();
        String phone = safe(teleField.getText()).trim();
        LocalDate dob = dobPicker.getValue();
        String sex = sexCombo.getValue();

        if (name.isEmpty()) {
            showMessage("Ten khong duoc de trong.", true);
            return;
        }
        if (phone.isEmpty()) {
            showMessage("So dien thoai khong duoc de trong.", true);
            return;
        }
        if (!phone.matches("^[0-9+\\-\\s]{8,15}$")) {
            showMessage("So dien thoai khong hop le.", true);
            return;
        }
        if (dob == null) {
            showMessage("Vui long chon ngay sinh.", true);
            return;
        }
        if (sex == null || sex.isBlank()) {
            showMessage("Vui long chon gioi tinh.", true);
            return;
        }

        currentUser.setUserName(name);
        currentUser.setUserPhoneNumber(phone);
        currentUser.setUserDoB(dob.toString());
        currentUser.setUserGender("Male".equalsIgnoreCase(sex));

        boolean updated = userService.updateProfile(currentUser);
        if (!updated) {
            showMessage("Cap nhat that bai. Vui long thu lai.", true);
            return;
        }

        UserSession.getInstance().setUser(currentUser);
        setEditMode(false);
        refreshFromSession();
        showMessage("Cap nhat thanh cong.", false);
    }

    private void refreshFromSession() {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            return;
        }

        String name = safeOrNA(currentUser.getUserName());
        String email = currentUser.getAccount() != null ? safeOrNA(currentUser.getAccount().getUserName()) : "N/A";
        String gender = currentUser.isUserGender() ? "Male" : "Female";
        String telephone = safeOrNA(currentUser.getUserPhoneNumber());
        String dob = safeOrNA(currentUser.getUserDoB());

        nameLabel.setText(name);
        dataEmailLabel.setText(email);

        nameValueLabel.setText(name);
        dataDateofbirthLabel.setText(dob);
        dataSexLabel.setText(gender);
        dataTeleLabel.setText(telephone);

        dobValueLabel.setText(dob);
        sexValueLabel.setText(gender);
        teleValueLabel.setText(telephone);

        applyAvatarAndBackground(currentUser.isUserGender());
    }

    private void applyAvatarAndBackground(boolean isMale) {
        String imagePath = isMale ? "/image/profile2.png" : "/image/woman.png";
        try {
            Image profileImage = new Image(getClass().getResource(imagePath).toExternalForm());
            profileImageView.setImage(profileImage);
        } catch (Exception ignored) {
            // Keep current image if loading fails.
        }

        Userbackground.setFill(Color.web(isMale ? "#76a7e8" : "#FFB6C1"));
    }

    private void setEditMode(boolean editing) {
        editButton.setVisible(!editing);
        editButton.setManaged(!editing);

        saveButton.setVisible(editing);
        saveButton.setManaged(editing);
        cancelButton.setVisible(editing);
        cancelButton.setManaged(editing);

        nameValueLabel.setVisible(!editing);
        nameValueLabel.setManaged(!editing);
        dobValueLabel.setVisible(!editing);
        dobValueLabel.setManaged(!editing);
        sexValueLabel.setVisible(!editing);
        sexValueLabel.setManaged(!editing);
        teleValueLabel.setVisible(!editing);
        teleValueLabel.setManaged(!editing);

        nameField.setVisible(editing);
        nameField.setManaged(editing);
        dobPicker.setVisible(editing);
        dobPicker.setManaged(editing);
        sexCombo.setVisible(editing);
        sexCombo.setManaged(editing);
        teleField.setVisible(editing);
        teleField.setManaged(editing);
    }

    private void showMessage(String message, boolean isError) {
        updateMessageLabel.setText(message == null ? "" : message);
        updateMessageLabel.setTextFill(Color.web(isError ? "#B91C1C" : "#166534"));
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String safeOrNA(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
    }
}
