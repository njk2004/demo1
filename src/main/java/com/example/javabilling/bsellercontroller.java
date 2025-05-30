package com.example.javabilling;

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

public class bsellercontroller {

    @FXML
    private TableView<bsellercontroller.Bestseller> bestsellerTable;
    @FXML
    private TableColumn<bsellercontroller.Bestseller, String> colFoodName;
    @FXML
    private TableColumn<bsellercontroller.Bestseller, Integer> colQuantitySold;

    private ObservableList<bsellercontroller.Bestseller> bestsellerList = FXCollections.observableArrayList();

    // Database credentials
    private final String URL = "jdbc:mysql://localhost:3306/billing"; // Replace with your database name
    private final String USER = "root";
    private final String PASSWORD = "nayan";

    @FXML
    public void initialize() {
        colFoodName.setCellValueFactory(new PropertyValueFactory<>("foodName"));
        colQuantitySold.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
        loadBestsellerData();
    }

    // Fetch data from MySQL and update TableView
    @FXML
    public void loadBestsellerData() {
        bestsellerList.clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT food_name, quantity_sold FROM inventory ORDER BY quantity_sold DESC LIMIT 5");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bestsellerList.add(new bsellercontroller.Bestseller(
                        rs.getString("food_name"),
                        rs.getInt("quantity_sold")
                ));
            }
            bestsellerTable.setItems(bestsellerList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Refresh button handler
    @FXML
    public void refreshTable() {
        loadBestsellerData(); // Simply reload data
    }

    // Nested Bestseller class inside BsellerController
    public static class Bestseller {
        private String foodName;
        private int quantitySold;

        public Bestseller(String foodName, int quantitySold) {
            this.foodName = foodName;
            this.quantitySold = quantitySold;
        }

        public String getFoodName() { return foodName; }
        public int getQuantitySold() { return quantitySold; }
    }
}