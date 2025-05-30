package com.example.javabilling;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class adminLoginController implements Initializable {

    @FXML
    private ImageView brandingImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Image brandingImage = new Image(getClass().getResourceAsStream("/Images/logo_nobg.png"));
            brandingImageView.setImage(brandingImage);
        } catch (Exception e) {
            System.out.println("Failed to load branding image: " + e.getMessage());
        }
    }

    @FXML
    public void loginButtonOnAction(ActionEvent event) {
        if (!usernameTextField.getText().isBlank() && !passwordField.getText().isBlank()) {
            validateLogin();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both username and password.");
            alert.showAndWait();
        }
    }

    private void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String username = usernameTextField.getText().trim();
        String password = passwordField.getText().trim();

        String verifyLogin = "SELECT password FROM admin WHERE ad_username = '" + username + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            if (queryResult.next()) {
                String storedPassword = queryResult.getString("password");

                if (storedPassword.equals(password)) {
                    System.out.println("Login successful!");

                    Parent root = FXMLLoader.load(getClass().getResource("ss.fxml"));
                    Stage homeStage = new Stage();
                    homeStage.setTitle("Admin Dashboard");
                    homeStage.setScene(new Scene(root, 780, 500));
                    homeStage.show();
                } else {
                    showAlert("Incorrect password. Please try again.");
                }
            } else {
                showAlert("Username not found. Please check your credentials.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database error occurred. Please try again later.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
