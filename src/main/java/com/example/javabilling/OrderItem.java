package com.example.javabilling;

import javafx.beans.property.*;

public class OrderItem {
    private final StringProperty foodName;
    private final IntegerProperty quantity;
    private final DoubleProperty unitPrice;
    private final DoubleProperty totalPrice;

    public OrderItem(String foodName, int quantity, double unitPrice) {
        this.foodName = new SimpleStringProperty(foodName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.unitPrice = new SimpleDoubleProperty(unitPrice);
        this.totalPrice = new SimpleDoubleProperty(unitPrice * quantity);
    }

    // Property getters for TableView binding
    public StringProperty foodNameProperty() { return foodName; }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty unitPriceProperty() { return unitPrice; }
    public DoubleProperty totalPriceProperty() { return totalPrice; }

    // Standard getters
    public String getFoodName() { return foodName.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getUnitPrice() { return unitPrice.get(); }
    public double getTotalPrice() { return totalPrice.get(); }

    // Setters
    public void setFoodName(String foodName) { this.foodName.set(foodName); }
    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
        recalculateTotal();
    }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice.set(unitPrice);
        recalculateTotal();
    }

    // Recalculate total when quantity or unit price changes
    private void recalculateTotal() {
        this.totalPrice.set(unitPrice.get() * quantity.get());
    }
}
