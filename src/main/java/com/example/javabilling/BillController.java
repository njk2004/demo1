package com.example.javabilling;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class BillController {

    @FXML
    private TextField totalField;
    @FXML
    private TextField discountField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField customerField;
    @FXML
    private TextField dateField;
    @FXML
    private TableView<OrderItem> orderItemsTable;
    @FXML
    private TableColumn<OrderItem, String> foodNameColumn;
    @FXML
    private TableColumn<OrderItem, Integer> quantityColumn;
    @FXML
    private TableColumn<OrderItem, Double> unitPriceColumn;
    @FXML
    private TableColumn<OrderItem, Double> totalPriceColumn; // No discount column needed

    private double discount;
    private List<OrderItem> orderItems;
    private String phoneNumber;

    public void initialize() {
        foodNameColumn.setCellValueFactory(cellData -> cellData.getValue().foodNameProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        unitPriceColumn.setCellValueFactory(cellData -> cellData.getValue().unitPriceProperty().asObject());

        totalPriceColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getQuantity() * cellData.getValue().getUnitPrice()).asObject()
        );
    }


    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        orderItemsTable.setItems(FXCollections.observableArrayList(orderItems));
    }

    public void setTotal(double total) {
        totalField.setText(String.format("%.2f", total));
        totalField.setEditable(false);
        dateField.setText(LocalDate.now().toString());
        dateField.setEditable(false);
        calculateAmountDue();
    }

    public void setDiscount(double discountPercentage) {
        this.discount = discountPercentage;
        discountField.setText(String.format("%.2f", discountPercentage * 100) + "%");
        discountField.setEditable(false);
        calculateAmountDue();
    }

    public void setPhnum(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        customerField.setText(getCustomerNameByPhone(phoneNumber));
        customerField.setEditable(false);
        updateLoyaltyPoints(phoneNumber);
    }

    private void calculateAmountDue() {
        double totalAmount = Double.parseDouble(totalField.getText());
        double discountedAmount = totalAmount - (totalAmount * discount);
        amountField.setText(String.format("%.2f", discountedAmount));
    }

    private String getCustomerNameByPhone(String phoneNumber) {
        String customerName = "Unknown";
        String query = "SELECT name FROM customers WHERE phone = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing", "root", "nayan");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                customerName = rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerName;
    }

    private void updateLoyaltyPoints(String phoneNumber) {
        double totalAmount = Double.parseDouble(totalField.getText());
        int loyaltyPoints = (int) (totalAmount / 10);

        String updateQuery = "UPDATE customers SET loyalty_points = loyalty_points + ? WHERE phone = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing", "root", "nayan");
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setInt(1, loyaltyPoints);
            stmt.setString(2, phoneNumber);
            stmt.executeUpdate();
            System.out.println("Loyalty points updated successfully for customer with phone: " + phoneNumber);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleNextOrder(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("homepage.fxml"));
        Stage homeStage = new Stage();
        homeStage.setTitle("Home");
        homeStage.setScene(new Scene(root, 900, 700));
        homeStage.show();
    }
}