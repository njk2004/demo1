package com.example.javabilling;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.Date;

public class PastOrderController {

    @FXML private TextField phoneNumberField;
    @FXML private TextField customerIdField;
    @FXML private Button enterButton;
    @FXML private Button customerIdButton;

    @FXML private TableView<CustomerOrder> tableView;
    @FXML private TableColumn<CustomerOrder, Integer> custIdColumn;
    @FXML private TableColumn<CustomerOrder, String> custNameColumn;
    @FXML private TableColumn<CustomerOrder, Integer> orderIdColumn;
    @FXML private TableColumn<CustomerOrder, Date> orderDateColumn;
    @FXML private TableColumn<CustomerOrder, Double> totalAmountColumn;

    @FXML private TableView<OrderDetail> orderDetailsTable;
    @FXML private TableColumn<OrderDetail, String> foodNameColumn;
    @FXML private TableColumn<OrderDetail, Integer> quantityColumn;
    @FXML private TableColumn<OrderDetail, Double> priceColumn;
    @FXML private TableColumn<OrderDetail, Double> subtotalColumn;

    @FXML private TableView<CustomerLoyaltyDetail> loyaltyDetailsTable;
    @FXML private TableColumn<CustomerLoyaltyDetail, Integer> loyaltyCustIdColumn;
    @FXML private TableColumn<CustomerLoyaltyDetail, String> loyaltyCustNameColumn;
    @FXML private TableColumn<CustomerLoyaltyDetail, Integer> loyaltyPointsColumn;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/billing";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "nayan"; // change this as per your setup

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @FXML
    public void initialize() {
        custIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        custNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        foodNameColumn.setCellValueFactory(new PropertyValueFactory<>("foodName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        loyaltyCustIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        loyaltyCustNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        loyaltyPointsColumn.setCellValueFactory(new PropertyValueFactory<>("loyaltyPoints"));

        enterButton.setOnAction(e -> loadOrdersByPhone());
        customerIdButton.setOnAction(e -> loadOrderDetailsAndLoyalty());
    }

    private void loadOrdersByPhone() {
        String phone = phoneNumberField.getText().trim();
        if (phone.isEmpty()) {
            showAlert("Please enter a phone number.");
            return;
        }

        ObservableList<CustomerOrder> orders = FXCollections.observableArrayList();
        String sql = "SELECT c.customer_id, c.name, o.order_id, o.order_date, o.total_amount " +
                "FROM customers c JOIN `orders` o ON c.customer_id = o.customer_id WHERE c.phone = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orders.add(new CustomerOrder(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getInt("order_id"),
                        rs.getDate("order_date"),
                        rs.getDouble("total_amount")
                ));
            }

            tableView.setItems(orders);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database error: " + e.getMessage());
        }
    }

    private void loadOrderDetailsAndLoyalty() {
        String idText = customerIdField.getText().trim();
        if (idText.isEmpty()) {
            showAlert("Please enter a customer ID.");
            return;
        }

        int customerId;
        try {
            customerId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            showAlert("Invalid customer ID.");
            return;
        }

        ObservableList<OrderDetail> details = FXCollections.observableArrayList();
        String orderDetailsSql = "SELECT oi.item_name, oi.quantity, oi.price, (oi.quantity * oi.price) AS subtotal " +
                "FROM order_items oi JOIN `orders` o ON oi.order_id = o.order_id " +
                "WHERE o.customer_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(orderDetailsSql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                details.add(new OrderDetail(
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("subtotal")
                ));
            }

            orderDetailsTable.setItems(details);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to load order details.");
        }

        ObservableList<CustomerLoyaltyDetail> loyaltyList = FXCollections.observableArrayList();
        String loyaltySql = "SELECT customer_id, name, loyalty_points FROM customers WHERE customer_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(loyaltySql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                loyaltyList.add(new CustomerLoyaltyDetail(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getInt("loyalty_points")
                ));
            }

            loyaltyDetailsTable.setItems(loyaltyList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to load loyalty points.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==== Inner Classes ====

    public static class CustomerOrder {
        private final SimpleIntegerProperty customerId;
        private final SimpleStringProperty customerName;
        private final SimpleIntegerProperty orderId;
        private final SimpleObjectProperty<Date> orderDate;
        private final SimpleDoubleProperty totalAmount;

        public CustomerOrder(int customerId, String customerName, int orderId, Date orderDate, double totalAmount) {
            this.customerId = new SimpleIntegerProperty(customerId);
            this.customerName = new SimpleStringProperty(customerName);
            this.orderId = new SimpleIntegerProperty(orderId);
            this.orderDate = new SimpleObjectProperty<>(orderDate);
            this.totalAmount = new SimpleDoubleProperty(totalAmount);
        }

        public IntegerProperty customerIdProperty() { return customerId; }
        public StringProperty customerNameProperty() { return customerName; }
        public IntegerProperty orderIdProperty() { return orderId; }
        public ObjectProperty<Date> orderDateProperty() { return orderDate; }
        public DoubleProperty totalAmountProperty() { return totalAmount; }
    }

    public static class OrderDetail {
        private final SimpleStringProperty foodName;
        private final SimpleIntegerProperty quantity;
        private final SimpleDoubleProperty price;
        private final SimpleDoubleProperty subtotal;

        public OrderDetail(String foodName, int quantity, double price, double subtotal) {
            this.foodName = new SimpleStringProperty(foodName);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.price = new SimpleDoubleProperty(price);
            this.subtotal = new SimpleDoubleProperty(subtotal);
        }

        public StringProperty foodNameProperty() { return foodName; }
        public IntegerProperty quantityProperty() { return quantity; }
        public DoubleProperty priceProperty() { return price; }
        public DoubleProperty subtotalProperty() { return subtotal; }
    }

    public static class CustomerLoyaltyDetail {
        private final SimpleIntegerProperty customerId;
        private final SimpleStringProperty customerName;
        private final SimpleIntegerProperty loyaltyPoints;

        public CustomerLoyaltyDetail(int customerId, String customerName, int loyaltyPoints) {
            this.customerId = new SimpleIntegerProperty(customerId);
            this.customerName = new SimpleStringProperty(customerName);
            this.loyaltyPoints = new SimpleIntegerProperty(loyaltyPoints);
        }

        public IntegerProperty customerIdProperty() { return customerId; }
        public StringProperty customerNameProperty() { return customerName; }
        public IntegerProperty loyaltyPointsProperty() { return loyaltyPoints; }
    }
}
