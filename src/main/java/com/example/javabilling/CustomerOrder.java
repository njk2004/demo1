package com.example.javabilling;

public class CustomerOrder {
    private int customerId;
    private String customerName;
    private String phoneNumber;
    private String email;
    private double loyaltyPoints;

    public CustomerOrder(int customerId, String customerName, String phoneNumber, String email, double loyaltyPoints) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.loyaltyPoints = loyaltyPoints;
    }

    public int getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public double getLoyaltyPoints() { return loyaltyPoints; }
}
