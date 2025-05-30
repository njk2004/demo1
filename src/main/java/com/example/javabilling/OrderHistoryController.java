package com.example.javabilling;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
        import java.time.LocalDate;

public class OrderHistoryController {

    @FXML private TableView<OrderDetail> tableView;
    @FXML private TableColumn<OrderDetail, Integer> custIdCol;
    @FXML private TableColumn<OrderDetail, Integer> orderIdCol;
    @FXML private TableColumn<OrderDetail, Date> orderDateCol;
    @FXML private TableColumn<OrderDetail, Double> totalAmountCol;
    @FXML private TableColumn<OrderDetail, String> itemNameCol;
    @FXML private TableColumn<OrderDetail, Integer> quantityCol;

    @FXML private TextField custIdField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    @FXML private Label totalSalesLabel;
    @FXML private Label totalItemsLabel;

    private Connection conn;

    @FXML
    public void initialize() {
        custIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        totalAmountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        connectDB();
    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing", "root", "nayan");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchByCustomer() {
        String customerId = custIdField.getText().trim();
        if (customerId.isEmpty()) return;

        String query = "SELECT o.customer_id, o.order_id, o.order_date, o.total_amount, " +
                "oi.item_name, oi.quantity FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE o.customer_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(customerId));
            ResultSet rs = stmt.executeQuery();

            ObservableList<OrderDetail> data = FXCollections.observableArrayList();
            double totalSales = 0.0;
            int totalItems = 0;

            while (rs.next()) {
                int cid = rs.getInt("customer_id");
                int oid = rs.getInt("order_id");
                Date date = rs.getDate("order_date");
                double total = rs.getDouble("total_amount");
                String item = rs.getString("item_name");
                int qty = rs.getInt("quantity");

                totalSales += total;
                totalItems += qty;

                data.add(new OrderDetail(cid, oid, date, total, item, qty));
            }

            tableView.setItems(data);
            totalSalesLabel.setText(String.valueOf(totalSales));
            totalItemsLabel.setText(String.valueOf(totalItems));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDateFilter() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (fromDate == null || toDate == null) return;

        String query = "SELECT o.customer_id, o.order_id, o.order_date, o.total_amount, " +
                "oi.item_name, oi.quantity FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE o.order_date BETWEEN ? AND ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(fromDate));
            stmt.setDate(2, Date.valueOf(toDate));
            ResultSet rs = stmt.executeQuery();

            ObservableList<OrderDetail> data = FXCollections.observableArrayList();
            double totalSales = 0.0;
            int totalItems = 0;

            while (rs.next()) {
                int cid = rs.getInt("customer_id");
                int oid = rs.getInt("order_id");
                Date date = rs.getDate("order_date");
                double total = rs.getDouble("total_amount");
                String item = rs.getString("item_name");
                int qty = rs.getInt("quantity");

                totalSales += total;
                totalItems += qty;

                data.add(new OrderDetail(cid, oid, date, total, item, qty));
            }

            tableView.setItems(data);
            totalSalesLabel.setText(String.valueOf(totalSales));
            totalItemsLabel.setText(String.valueOf(totalItems));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
