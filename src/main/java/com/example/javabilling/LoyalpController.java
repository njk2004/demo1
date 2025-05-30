package com.example.javabilling;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoyalpController {

    @FXML
    private TableView<OrderSummaryItem> orderSummaryTable;
    private List<OrderItem> orderItemsList = new ArrayList<>(); // Stores order items

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItemsList = orderItems;
    }
    @FXML
    private TextField pointsField;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private Button generateButton;

    private String phoneNumber;
    private int lp;
    private double tote;
    private double discount = 0.0;
    private int customerId;
    private int orderId;



    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        System.out.println("Phone number set to: " + phoneNumber); // Debug line
        loadLoyaltyPoints();
        loadCustomerId();
    }

    public void setTotalAmount(double tot) {
        this.tote = tot;
    }

    @FXML
    private void initialize() {
        comboBox.getItems().addAll("Tier 1", "Tier 2", "Tier 3", "No Tier");
        generateButton.setOnAction(event -> handleGenerateBill());
    }

    private void handleGenerateBill() {
        String selectedTier = comboBox.getValue();
        int pointsToDeduct = 0;

        // Determine discount and points deduction based on tier
        switch (selectedTier) {
            case "Tier 1":
                pointsToDeduct = 100;
                discount = 0.10;
                break;
            case "Tier 2":
                pointsToDeduct = 300;
                discount = 0.15;
                break;
            case "Tier 3":
                pointsToDeduct = 500;
                discount = 0.20;
                break;
            default:
                pointsToDeduct = 0;
                discount = 0.0;
                break;
        }

        if (lp >= pointsToDeduct) {
            lp -= pointsToDeduct;
            updateLoyaltyPointsInDatabase(lp);
            saveOrder(); // Save order before generating bill
            loadBillScreen(tote, discount, phoneNumber,orderItemsList);
        } else {
            showAlert("Insufficient Points", "You do not have enough loyalty points for this tier.");
        }
    }

    private void updateLoyaltyPointsInDatabase(int updatedPoints) {
        String query = "UPDATE customers SET loyalty_points = ? WHERE phone = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing", "root", "nayan");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, updatedPoints);
            stmt.setString(2, phoneNumber);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBillScreen(double tots, double disc, String number, List<OrderItem> orderItems) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bill.fxml"));
            Parent root = loader.load();
            BillController controller = loader.getController();

            if (controller != null) {
                controller.setOrderItems(orderItems);
                controller.setTotal(tots);
                controller.setDiscount(disc);
                controller.setPhnum(number);
            } else {
                System.err.println("Error: BillController is null.");
            }

            Stage stage = new Stage();
            stage.setTitle("Bill");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load bill.fxml");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public List<OrderItem> getOrderItems() {
        if (orderSummaryTable == null) {
            System.out.println("Order Summary Table is null!");
            return new ArrayList<>(); // Return an empty list to prevent NullPointerException
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderSummaryItem summaryItem : orderSummaryTable.getItems()) {
            OrderItem orderItem = new OrderItem(
                    summaryItem.getItemName(),
                    summaryItem.getQuantity(),
                    summaryItem.getUnitPrice()
            );
            orderItems.add(orderItem);
        }
        return orderItems;
    }


    private void loadLoyaltyPoints() {
        String query = "SELECT Loyalty_points FROM customers WHERE phone = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing", "root", "nayan");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, phoneNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                lp = resultSet.getInt("Loyalty_points");
                pointsField.setText(String.valueOf(lp));
            } else {
                lp = 0;
                pointsField.setText("0");
            }
            pointsField.setEditable(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerId() {
        String query = "SELECT customer_id FROM customers WHERE phone = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing", "root", "nayan");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                customerId = rs.getInt("customer_id");
            } else {
                customerId = -1; // Indicates customer not found
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveOrder() {
        String insertOrderQuery = "INSERT INTO orders (customer_id, order_date, total_amount) VALUES (?, ?, ?)";
        String getLastOrderIdQuery = "SELECT LAST_INSERT_ID()";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing", "root", "nayan");
             PreparedStatement orderStmt = conn.prepareStatement(insertOrderQuery);
             Statement lastIdStmt = conn.createStatement()) {

            orderStmt.setInt(1, customerId);
            orderStmt.setDate(2, Date.valueOf(LocalDate.now()));
            orderStmt.setDouble(3, tote);
            orderStmt.executeUpdate();

            ResultSet rs = lastIdStmt.executeQuery(getLastOrderIdQuery);
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            saveOrderItems(conn); // Save order items after getting orderId

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveOrderItems(Connection conn) {
        if (orderItemsList == null || orderItemsList.isEmpty()) {
            System.out.println("No items to save for this order.");
            return;
        }

        String insertOrderItemQuery = "INSERT INTO order_items (order_id, item_name, quantity, price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertOrderItemQuery)) {
            for (OrderItem item : orderItemsList) {
                stmt.setInt(1, orderId);
                stmt.setString(2, item.getFoodName());
                stmt.setInt(3, item.getQuantity());
                stmt.setDouble(4, item.getUnitPrice());
                stmt.executeUpdate();
            }
            System.out.println("Order items saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
