package com.example.javabilling;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private Button registerButton;
    @FXML private ImageView brandingImageView;
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Load image from classpath
            Image brandingImage = new Image(getClass().getResource("/Images/logo_nobg.png").toExternalForm());
            brandingImageView.setImage(brandingImage);
        } catch (Exception e) {
            System.out.println("Image not found: " + e.getMessage());
        }
    }

    public void loginButtonOnAction(ActionEvent event) {
        if (!usernameTextField.getText().isBlank() && !passwordField.getText().isBlank()) {
            validateLogin();
        } else {
            showAlert("Please enter both username and password.");
        }
    }

    public void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT password FROM user WHERE username = '" + usernameTextField.getText() + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            if (queryResult.next()) {
                String storedPassword = queryResult.getString("password");

                if (storedPassword.equals(passwordField.getText())) {
                    System.out.println("Login successful!");
                    loadScene("homepage.fxml", "Home");
                } else {
                    showAlert("Incorrect password. Please try again.");
                }
            } else {
                showAlert("Username not found. Please register.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database connection error.");
        }
    }

    public void registerButtonOnAction(ActionEvent event) {
        loadScene("register.fxml", "Register");
    }

    private void loadScene(String fxmlFile, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 900, 700));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Unable to load " + title + " screen.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
