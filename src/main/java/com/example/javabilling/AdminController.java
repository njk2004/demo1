package com.example.javabilling;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Button btnCustomerDetails, btnBestSeller, btnStockStatus, btnTotalSales, btnSignOut, btnUserDetails;

    @FXML
    private void handleCustomerDetails() {
        loadPage("cust_details.fxml");
    }

    @FXML
    private void handleBestSeller() {
        loadPage("bestseller.fxml");
    }

    @FXML
    private void handleStockStatus() {
        loadPage("stock_view.fxml");
    }

    @FXML
    private void handleTotalSales() {
        loadPage("totalsales.fxml");
    }

    @FXML
    private void handleUsermanagement() {
        loadPage("demo.fxml");
    }

    @FXML
    private void handleStockmanagement() {
        loadPage("new.fxml");
    }


    @FXML
    private void handleSignOut() {
        try {
            // Load the admin.fxml page (assuming it's the login page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
            AnchorPane root = loader.load();

            // Get the current stage and close it
            Stage currentStage = (Stage) contentPane.getScene().getWindow();
            currentStage.close();

            // Open the login/admin page in a new window
            Stage stage = new Stage();
            stage.setTitle("Admin Panel"); // Change to "Login Page" if needed
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            AnchorPane newContent = loader.load();
            contentPane.getChildren().setAll(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
