package com.example.javabilling;

public class Customer {
    private String name;
    private String email;
    private int loyaltyPoints;
    private String phNum;

     Customer(String name, String email) {
        this.name = name;
        this.email = email;
        this.loyaltyPoints = 0;
    }

    // Getters and setters
    public String getName() {
        return name;
    }
    public String getPhNum() {
        return phNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }

    public void subtractLoyaltyPoints(int points) {
        if (this.loyaltyPoints >= points) {
            this.loyaltyPoints -= points;
        }
    }

    public void resetLoyaltyPoints() {
        this.loyaltyPoints = 0;
    }

    @Override
    public String toString() {
        return "Customer: " + name + ", Email: " + email + ", Loyalty Points: " + loyaltyPoints;
    }

}
