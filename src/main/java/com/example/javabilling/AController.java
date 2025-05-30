package com.example.javabilling;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AController {
    public Button takeOrderButton;
    public Button pastOrderButton;

    public void takeOrderButtonOnAction(ActionEvent event) {
        try {
            // Load Admin page (admin1.fxml)
            Parent root = FXMLLoader.load(getClass().getResource("adminlogin.fxml"));
            Stage adminStage = new Stage();
            adminStage.setTitle("Admin Page");
            adminStage.setScene(new Scene(root, 900, 700));
            adminStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pastOrderButtonOnAction(ActionEvent event) {
        try {
            // Load User page (user.fxml)
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage userStage = new Stage();
            userStage.setTitle("User Page");
            userStage.setScene(new Scene(root, 958, 700));
            userStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
