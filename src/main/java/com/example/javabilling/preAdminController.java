package com.example.javabilling;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class preAdminController {
    public Button stockButton;
    public Button bSellerButton;

    // Method for handling the stockButton action
    public void stockButtonOnAction(ActionEvent event) {
        try {
            // Load the stock view page (stock_view.fxml)
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/javabilling/stock_view.fxml"));
            Stage menuStage = new Stage();
            menuStage.setTitle("Stock Details");
            menuStage.setScene(new Scene(root, 596, 442));
            menuStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method for handling the bSellerButton action
    public void bSellerButtonOnAction(ActionEvent event) {
        try {
            // Load the best seller page (bestseller.fxml)
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/javabilling/bestseller.fxml"));
            Stage menuStage = new Stage();
            menuStage.setTitle("Bestsellers");
            menuStage.setScene(new Scene(root, 600, 400));
            menuStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
