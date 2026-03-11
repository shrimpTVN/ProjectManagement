package com.app.src.controllers;

import com.app.src.core.session.UserSession;
import com.app.src.models.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("InformationController.initialize() called");

        // Get user information from UserSession
        User currentUser = UserSession.getInstance().getUser();
        System.out.println("Current user: " + (currentUser != null ? currentUser.getUserName() : "null"));

        if (currentUser != null) {
            // Set Name
            String name = currentUser.getUserName() != null ? currentUser.getUserName() : "N/A";
            nameLabel.setText(name);
            System.out.println("Name set to: " + name);

            // Set Email (from Account)
            if (currentUser.getAccount() != null) {
                String email = currentUser.getAccount().getUserName() != null ? currentUser.getAccount().getUserName() : "N/A";
                dataEmailLabel.setText(email);
                System.out.println("Email set to: " + email);
            } else {
                dataEmailLabel.setText("N/A");
                System.out.println("Account is null, Email set to: N/A");
            }

            // Set Sex (convert boolean to String)
            String gender = currentUser.isUserGender() ? "Male" : "Female";
            dataSexLabel.setText(gender);
            System.out.println("Gender set to: " + gender);

            // Set Telephone
            String telephone = currentUser.getUserPhoneNumber() != null ? currentUser.getUserPhoneNumber() : "N/A";
            dataTeleLabel.setText(telephone);
            System.out.println("Telephone set to: " + telephone);

            // Set Date of Birth
            String dob = currentUser.getUserDoB() != null ? currentUser.getUserDoB() : "N/A";
            dataDateofbirthLabel.setText(dob);
            System.out.println("Date of Birth set to: " + dob);

            // Set Profile Image based on gender
            String imagePath;
            if (!currentUser.isUserGender()) { // false = Female
                imagePath = "/image/woman.png";
                System.out.println("Setting woman image for female user");
            } else {
                imagePath = "/image/profile2.png";
                System.out.println("Setting default profile image for male user");
            }

            try {
                Image profileImage = new Image(getClass().getResource(imagePath).toExternalForm());
                profileImageView.setImage(profileImage);
                System.out.println("Profile image set successfully");
            } catch (Exception e) {
                System.err.println("Error loading profile image: " + e.getMessage());
            }

            // Set background color based on gender
            if (currentUser.isUserGender()) { // true = Male
                Userbackground.setFill(Color.web("#76a7e8"));
                System.out.println("Background color set to blue for male user");
            } else {
                Userbackground.setFill(Color.web("#FFB6C1"));
                System.out.println("Background color set to pink for female user");
            }
        } else {
            System.out.println("Current user is null");
        }
    }
}
