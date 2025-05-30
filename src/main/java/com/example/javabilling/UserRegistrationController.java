package com.example.javabilling;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;

public class UserRegistrationController {

    @FXML private TextField nametextfield;
    @FXML private TextField phonenumberfield;
    @FXML private TextField usernamefield;
    @FXML private PasswordField passwordfield;
    @FXML private PasswordField confirmpasswordfield;
    @FXML private Button regsubmitbutton;

    @FXML
    public void initialize() {
        addValidationListeners();
    }

    private void addValidationListeners() {
        // Phone number: 10 digits only
        phonenumberfield.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d{0,10}")) {
                phonenumberfield.setText(oldText);
            }
            setFieldStyle(phonenumberfield, newText.matches("\\d{10}"));
        });

        // Username: at least 8 characters, 1 special character, 1 number
        usernamefield.textProperty().addListener((obs, oldText, newText) -> {
            boolean valid = newText.matches("^(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$");
            setFieldStyle(usernamefield, valid);
        });

        // Password: minimum 8 characters
        passwordfield.textProperty().addListener((obs, oldText, newText) -> {
            boolean valid = newText.length() >= 8;
            setFieldStyle(passwordfield, valid);
            // Also re-validate confirm password
            boolean match = newText.equals(confirmpasswordfield.getText());
            setFieldStyle(confirmpasswordfield, match);
        });

        // Confirm password: must match password
        confirmpasswordfield.textProperty().addListener((obs, oldText, newText) -> {
            boolean match = newText.equals(passwordfield.getText());
            setFieldStyle(confirmpasswordfield, match);
        });
    }

    private void setFieldStyle(TextField field, boolean isValid) {
        if (isValid) {
            field.setStyle("-fx-border-color: green;");
        } else {
            field.setStyle("-fx-border-color: red;");
        }
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        String fullName = nametextfield.getText().trim();
        String phoneNumber = phonenumberfield.getText().trim();
        String username = usernamefield.getText().trim();
        String password = passwordfield.getText();
        String confirmPassword = confirmpasswordfield.getText();

        if (fullName.isEmpty() || phoneNumber.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        if (!phoneNumber.matches("\\d{10}")) {
            showAlert("Error", "Phone number must be exactly 10 digits.");
            return;
        }

        if (!username.matches("^(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$")) {
            showAlert("Error", "Username must be at least 8 characters and contain one special character and one number.");
            return;
        }

        if (password.length() < 8) {
            showAlert("Error", "Password must be at least 8 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        if (insertUserData(fullName, phoneNumber, username, password)) {
            showAlert("Success", "Registration successful.");
            clearFields();
        } else {
            showAlert("Error", "Registration failed. Try again.");
        }
    }

    private boolean insertUserData(String fullName, String phoneNumber, String username, String password) {
        String url = "jdbc:mysql://localhost:3306/billing";
        String user = "root";
        String dbPassword = "nayan";
        String sql = "INSERT INTO user (fullName, phoneNumber, username, password) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, phoneNumber);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, password);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nametextfield.clear();
        phonenumberfield.clear();
        usernamefield.clear();
        passwordfield.clear();
        confirmpasswordfield.clear();
        phonenumberfield.setStyle("");
        usernamefield.setStyle("");
        passwordfield.setStyle("");
        confirmpasswordfield.setStyle("");
    }
}
