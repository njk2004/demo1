package com.example.javabilling;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerOrderController {

    @FXML
    private TableView<CustomerOrder> tableView;
    @FXML
    private TableColumn<CustomerOrder, Integer> custIdColumn;
    @FXML
    private TableColumn<CustomerOrder, String> custNameColumn;
    @FXML
    private TableColumn<CustomerOrder, String> phoneNumberColumn;
    @FXML
    private TableColumn<CustomerOrder, String> emailColumn;
    @FXML
    private TableColumn<CustomerOrder, Double> loyaltyPointsColumn;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private Button enterButton;

    private static final String URL = "jdbc:mysql://localhost:3306/billing";
    private static final String USER = "root";
    private static final String PASSWORD = "nayan";

    @FXML
    public void initialize() {
        custIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        custNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        loyaltyPointsColumn.setCellValueFactory(new PropertyValueFactory<>("loyaltyPoints"));

        loadCustomerData();
        enterButton.setOnAction(e -> searchByPhoneNumber());
    }

    private void loadCustomerData() {
        ObservableList<CustomerOrder> customerList = FXCollections.observableArrayList();
        String query = "SELECT customer_id, name, phone, email, loyalty_points FROM customers";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customerList.add(new CustomerOrder(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDouble("loyalty_points")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableView.setItems(customerList);
    }

    private void searchByPhoneNumber() {
        String phoneNumber = phoneNumberField.getText();
        if (phoneNumber.isEmpty()) {
            loadCustomerData();
            return;
        }

        ObservableList<CustomerOrder> filteredList = FXCollections.observableArrayList();
        String query = "SELECT customer_id, name, phone, email, loyalty_points FROM customers WHERE phone = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phoneNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    filteredList.add(new CustomerOrder(
                            rs.getInt("customer_id"),
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getDouble("loyalty_points")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableView.setItems(filteredList);
    }
}
