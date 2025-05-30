package com.example.javabilling;


import java.sql.Date;

public class OrderDetail {
    private int customerId;
    private int orderId;
    private Date orderDate;
    private double totalAmount;
    private String itemName;
    private int quantity;

    public OrderDetail(int customerId, int orderId, Date orderDate, double totalAmount, String itemName, int quantity) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public int getCustomerId() { return customerId; }
    public int getOrderId() { return orderId; }
    public Date getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
}
