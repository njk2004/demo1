package com.example.javabilling;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HomepageController {
    public Button takeOrderButton;
    public Button pastOrderButton;

    public void takeOrderButtonOnAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ggs.fxml"));
            Stage menuStage = new Stage();
            menuStage.setTitle("Menu");
            menuStage.setScene(new Scene(root, 958, 700));
            menuStage.show();

            // Optional: close the current window

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pastOrderButtonOnAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("past.fxml")); // update filename if needed
            Stage pastOrderStage = new Stage();
            pastOrderStage.setTitle("Past Orders");
            pastOrderStage.setScene(new Scene(root, 958, 700));
            pastOrderStage.show();

            // Optional: close the current window

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
