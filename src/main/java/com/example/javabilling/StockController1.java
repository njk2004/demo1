package com.example.javabilling;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.sql.*;

public class StockController1 {
    @FXML private TableView<StockItem> stockTable;
    @FXML private TableColumn<StockItem, String> colFoodName;
    @FXML private TableColumn<StockItem, Integer> colQuantity;
    @FXML private Button btnRefresh, btnSaveChanges;

    private final String DB_URL = "jdbc:mysql://localhost:3306/billing"; // Change this to your actual database name
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "nayan";

    private ObservableList<StockItem> stockList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colFoodName.setCellValueFactory(new PropertyValueFactory<>("foodName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Enable editing for the quantity column
        colQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colQuantity.setOnEditCommit(event -> {
            StockItem item = event.getRowValue();
            int newQuantity = event.getNewValue();
            System.out.println("Editing: " + item.getFoodName() + " | New Quantity: " + newQuantity); // Debugging
            item.setQuantity(newQuantity);
            stockTable.refresh();  // Refresh table after editing
        });

        stockTable.setEditable(true);
        loadStockData();
    }

    private void loadStockData() {
        stockList.clear();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT food_name, quantity_in_stock FROM inventory")) { // Ensure table name is correct

            while (rs.next()) {
                stockList.add(new StockItem(rs.getString("food_name"), rs.getInt("quantity_in_stock")));
            }
            stockTable.setItems(stockList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshTable(ActionEvent event) {
        loadStockData();
    }

    @FXML
    private void saveChanges(ActionEvent event) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("UPDATE inventory SET quantity_in_stock = ? WHERE food_name = ?")) {

            for (StockItem item : stockList) {
                System.out.println("Updating: " + item.getFoodName() + " -> " + item.getQuantity()); // Debugging
                stmt.setInt(1, item.getQuantity());
                stmt.setString(2, item.getFoodName());
                stmt.executeUpdate();
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Stock updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update stock!");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class StockItem {
        private final String foodName;
        private Integer quantity;

        public StockItem(String foodName, int quantity) {
            this.foodName = foodName;
            this.quantity = quantity;
        }

        public String getFoodName() { return foodName; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer newQuantity) { this.quantity = newQuantity; }
    }
}
