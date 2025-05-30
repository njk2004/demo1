package com.example.javabilling;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class CInfoController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField mobileField;
    @FXML
    private TextField customerLoyaltyPointsField;

    private double tote; // Total amount
    private double discount;
    private List<OrderItem> orderItems; // List to store order items

    public void handleSubmitButtonAction() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phNum = mobileField.getText();

        if (name.isEmpty() || email.isEmpty() || phNum.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        String insertCustomerQuery = "INSERT INTO customers (phone, name, email) VALUES (?, ?, ?)";
        String insertOrderQuery = "INSERT INTO orders (customer_id, order_date, total_amount) VALUES (?, ?, ?)";
        String insertOrderItemQuery = "INSERT INTO order_items (order_id, item_name, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing", "root", "nayan")) {
            conn.setAutoCommit(false); // Start transaction

            int customerId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(insertCustomerQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, phNum);
                stmt.setString(2, name);
                stmt.setString(3, email);
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    customerId = rs.getInt(1);
                }
            }

            if (customerId != -1) {
                int orderId = -1;
                try (PreparedStatement orderStmt = conn.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
                    orderStmt.setInt(1, customerId);
                    orderStmt.setDate(2, Date.valueOf(LocalDate.now()));
                    orderStmt.setDouble(3, tote - discount);
                    orderStmt.executeUpdate();

                    ResultSet rs = orderStmt.getGeneratedKeys();
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    }
                }

                if (orderId != -1 && orderItems != null) {
                    try (PreparedStatement orderItemStmt = conn.prepareStatement(insertOrderItemQuery)) {
                        for (OrderItem item : orderItems) {
                            orderItemStmt.setInt(1, orderId);
                            orderItemStmt.setString(2, item.getFoodName());
                            orderItemStmt.setInt(3, item.getQuantity());
                            orderItemStmt.setDouble(4, item.getUnitPrice());
                            orderItemStmt.addBatch();
                        }
                        orderItemStmt.executeBatch();
                    }
                }

                conn.commit(); // Commit transaction
                showAlert("Success", "Customer, order, and order items have been saved.");

                // Load the bill screen and pass order items
                loadBill(tote, phNum, discount, orderItems);
            } else {
                showAlert("Error", "Failed to retrieve customer ID.");
                conn.rollback(); // Rollback on failure
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while saving customer, order, and order items.");
        }
    }

    private void loadBill(double total, String phoneNumber, double discount, List<OrderItem> orderItems) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bill.fxml"));
            Parent root = loader.load();

            BillController controller = loader.getController();
            controller.setTotal(total);
            controller.setDiscount(discount);
            controller.setPhnum(phoneNumber);
            controller.setOrderItems(orderItems); // Pass order items to BillController

            Stage stage = new Stage();
            stage.setTitle("Bill Details");
            stage.setScene(new Scene(root, 958, 700));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setTotalAmount(double tot) {
        this.tote = tot;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
