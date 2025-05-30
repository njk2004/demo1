package com.example.javabilling; // Ensure this matches your package name

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StockController {

    @FXML
    private TableView<Stock> stockTable;
    @FXML
    private TableColumn<Stock, String> colFoodName;
    @FXML
    private TableColumn<Stock, Integer> colQuantity;

    private ObservableList<Stock> stockList = FXCollections.observableArrayList();

    // Database credentials
    private final String URL = "jdbc:mysql://localhost:3306/billing"; // Replace with your database name
    private final String USER = "root";
    private final String PASSWORD = "nayan";

    @FXML
    public void initialize() {
        colFoodName.setCellValueFactory(new PropertyValueFactory<>("foodName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));
        loadStockData();
    }

    // Fetch data from MySQL and update TableView
    @FXML
    public void loadStockData() {
        stockList.clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT food_name, quantity_in_stock FROM inventory");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stockList.add(new Stock(
                        rs.getString("food_name"),
                        rs.getInt("quantity_in_stock")
                ));
            }
            stockTable.setItems(stockList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Refresh button handler
    @FXML
    public void refreshTable() {
        loadStockData(); // Simply reload data
    }

    // Nested Stock class inside StockController
    public static class Stock {
        private String foodName;
        private int quantityInStock;

        public Stock(String foodName, int quantityInStock) {
            this.foodName = foodName;
            this.quantityInStock = quantityInStock;
        }

        public String getFoodName() { return foodName; }
        public int getQuantityInStock() { return quantityInStock; }
    }
}
