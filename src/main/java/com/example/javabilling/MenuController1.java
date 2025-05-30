package com.example.javabilling;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class MenuController1 implements Initializable {

    @FXML private ComboBox<String> categoryCombo;
    @FXML private TilePane itemsPane;
    @FXML private TableView<OrderItem> orderTable;
    @FXML private TableColumn<OrderItem, String> itemCol;
    @FXML private TableColumn<OrderItem, Integer> qtyCol;
    @FXML private TableColumn<OrderItem, Double> amountCol;
    @FXML private Label totalLabel;

    private Map<String, List<FoodItem>> menuData = new HashMap<>();
    private ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private double tot = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryCombo.setItems(FXCollections.observableArrayList("Fast Food", "Curries", "Drinks", "Breads"));
        categoryCombo.setValue("Fast Food");

        itemCol.setCellValueFactory(cellData -> cellData.getValue().foodNameProperty());
        qtyCol.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        amountCol.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty().asObject());

        orderTable.setItems(orderItems);

        loadMenuData();
        loadCategoryItems();
    }

    private void loadMenuData() {
        menuData.put("Fast Food", List.of(
                new FoodItem("Pizza", 199, "pizza.png"),
                new FoodItem("Burger", 149, "burger.png"),
                new FoodItem("Sandwich", 149, "sandwich.png"),
                new FoodItem("Hotdog", 199, "hotdog.png"),
                new FoodItem("Fries", 119, "fries.png"),
                new FoodItem("Burrito", 199, "burrito.png"),
                new FoodItem("Nuggets", 199, "nuggets.png"),
                new FoodItem("Chicken Bucket", 299, "chicken_bucket.jpg"),
                new FoodItem("Zinger Burger", 299, "zinger.png")
        ));
        menuData.put("Curries", List.of(
                new FoodItem("Butter Chicken", 249, "butter_chicken.jpeg"),
                new FoodItem("Paneer Masala", 199, "paneer_masala.jpeg"),
                new FoodItem("Chole", 149, "chole.jpeg"),
                new FoodItem("Dal Makhani", 179, "dal_makhani.jpeg"),
                new FoodItem("Palak Paneer", 199, "palak_paneer.jpeg"),
                new FoodItem("Fish Curry", 229, "fish_curry.jpeg"),
                new FoodItem("Mutton Rogan", 299, "mutton_rogan.jpeg"),
                new FoodItem("Rajma", 149, "rajma.jpeg"),
                new FoodItem("Kadai Chicken", 249, "kadai_chicken.jpeg")
        ));
        menuData.put("Drinks", List.of(
                new FoodItem("Pepsi", 49, "pepsi.jpeg"),
                new FoodItem("Coca Cola", 49, "coke.jpeg"),
                new FoodItem("Sprite", 49, "sprite.jpeg"),
                new FoodItem("Mango Shake", 99, "mango_shake.jpeg"),
                new FoodItem("Lassi", 89, "lassi.jpeg"),
                new FoodItem("Cold Coffee", 99, "cold_coffee.jpeg"),
                new FoodItem("Tea", 29, "tea.jpeg"),
                new FoodItem("Coffee", 39, "coffee.jpeg"),
                new FoodItem("Buttermilk", 59, "buttermilk.jpeg")
        ));
        menuData.put("Breads", List.of(
                new FoodItem("Butter Naan", 29, "butter_naan.jpeg"),
                new FoodItem("Plain Naan", 25, "plain_naan.jpeg"),
                new FoodItem("Tandoori Roti", 20, "tandoori_roti.jpeg"),
                new FoodItem("Missi Roti", 25, "missi_roti.jpeg"),
                new FoodItem("Paratha", 35, "paratha.jpeg"),
                new FoodItem("Aloo Paratha", 45, "aloo_paratha.jpeg"),
                new FoodItem("Stuffed Kulcha", 50, "stuffed_kulcha.jpeg"),
                new FoodItem("Roomali Roti", 30, "roomali_roti.jpeg"),
                new FoodItem("Lachha Paratha", 40, "lachha_paratha.jpeg")
        ));
    }

    @FXML
    private void loadCategoryItems() {
        itemsPane.getChildren().clear();
        String selectedCategory = categoryCombo.getValue();
        List<FoodItem> items = menuData.get(selectedCategory);

        for (FoodItem item : items) {
            VBox box = createFoodItemBox(item);
            itemsPane.getChildren().add(box);
        }
    }

    private VBox createFoodItemBox(FoodItem item) {
        ImageView img = new ImageView();
        try {
            String imagePath = "/images1/" + item.getImage();
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            img.setImage(image);
        } catch (Exception e) {
            System.out.println("Could not load image for item: " + item.getName() + " - " + e.getMessage());
        }

        img.setFitWidth(100);
        img.setFitHeight(80);
        Label name = new Label(item.getName() + " ₹" + item.getPrice());

        Button addBtn = new Button("+");
        addBtn.setOnAction(e -> addItemToOrder(item));

        Button removeBtn = new Button("-");
        removeBtn.setOnAction(e -> removeItemFromOrder(item));

        HBox buttons = new HBox(5, removeBtn, addBtn);
        buttons.setStyle("-fx-alignment: center;");

        VBox box = new VBox(5.0, img, name, buttons);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #ccc; -fx-background-color: #fff; -fx-alignment: center;");
        return box;
    }

    private void addItemToOrder(FoodItem item) {
        int stock = getCurrentStock(item.getName());
        if (stock <= 0) {
            showAlert("Out of Stock", item.getName() + " is currently out of stock.");
            return;
        }

        boolean found = false;
        for (OrderItem order : orderItems) {
            if (order.getFoodName().equals(item.getName())) {
                order.setQuantity(order.getQuantity() + 1);
                found = true;
                break;
            }
        }
        if (!found) {
            orderItems.add(new OrderItem(item.getName(), 1, item.getPrice()));
        }

        updateInventory(item.getName(), -1);
        orderTable.refresh();
        updateTotal();
    }

    private void removeItemFromOrder(FoodItem item) {
        Iterator<OrderItem> iterator = orderItems.iterator();
        while (iterator.hasNext()) {
            OrderItem order = iterator.next();
            if (order.getFoodName().equals(item.getName())) {
                if (order.getQuantity() > 1) {
                    order.setQuantity(order.getQuantity() - 1);
                } else {
                    iterator.remove();
                }

                updateInventory(item.getName(), 1);
                orderTable.refresh();
                updateTotal();
                return;
            }
        }
    }

    private int getCurrentStock(String foodName) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing", "root", "nayan");
             PreparedStatement stmt = conn.prepareStatement("SELECT quantity_in_stock FROM inventory WHERE food_name = ?")) {
            stmt.setString(1, foodName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantity_in_stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void updateInventory(String foodName, int change) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing", "root", "nayan");
             PreparedStatement selectStmt = conn.prepareStatement("SELECT quantity_in_stock FROM inventory WHERE food_name = ?");
             PreparedStatement updateStmt = conn.prepareStatement("UPDATE inventory SET quantity_in_stock = ? WHERE food_name = ?")) {

            selectStmt.setString(1, foodName);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                int currentStock = rs.getInt("quantity_in_stock");
                int newStock = currentStock + change;
                updateStmt.setInt(1, newStock);
                updateStmt.setString(2, foodName);
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateQuantitySold(List<OrderItem> orderItems) {
        String selectQuery = "SELECT quantity_sold FROM inventory WHERE food_name = ?";
        String updateQuery = "UPDATE inventory SET quantity_sold = ? WHERE food_name = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing", "root", "nayan")) {
            for (OrderItem orderItem : orderItems) {
                String foodName = orderItem.getFoodName();
                int quantity = orderItem.getQuantity();

                try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
                     PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

                    selectStmt.setString(1, foodName);
                    ResultSet rs = selectStmt.executeQuery();

                    if (rs.next()) {
                        int currentSold = rs.getInt("quantity_sold");
                        int newSold = currentSold + quantity;

                        updateStmt.setInt(1, newSold);
                        updateStmt.setString(2, foodName);
                        updateStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTotal() {
        tot = orderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();
        totalLabel.setText("Total: ₹" + tot);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void confirmOrder() {
        handleConfirm(new ActionEvent());
    }

    public void handleConfirm(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Customer Confirmation");
        alert.setHeaderText("Is this customer new?");
        alert.setContentText("Please select your option.");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.showAndWait().ifPresent(response -> {
            List<OrderItem> orderItemsList = new ArrayList<>(orderItems);
            updateQuantitySold(orderItemsList);

            if (response == yesButton) {
                loadCustomerInfo(tot, orderItemsList);
            } else if (response == noButton) {
                askForPhoneNumber(orderItemsList);
            }
        });
    }

    private void askForPhoneNumber(List<OrderItem> orderItems) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Loyal Customer");
        dialog.setHeaderText("Enter Phone Number");
        dialog.setContentText("Phone Number:");

        dialog.showAndWait().ifPresent(phoneNumber -> loadLoyalCustomerInfo(phoneNumber, tot, orderItems));
    }

    private void loadCustomerInfo(double tot, List<OrderItem> orderItems) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("customerinfo.fxml"));
            Parent root = loader.load();
            CInfoController controller = loader.getController();
            controller.setTotalAmount(tot);
            controller.setOrderItems(orderItems);

            Stage menuStage = new Stage();
            menuStage.setTitle("Customer Info");
            menuStage.setScene(new Scene(root, 958, 700));
            menuStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLoyalCustomerInfo(String phoneNumber, double tot, List<OrderItem> orderItems) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("loyalp.fxml"));
            Parent root = loader.load();
            LoyalpController controller = loader.getController();
            controller.setPhoneNumber(phoneNumber);
            controller.setTotalAmount(tot);
            controller.setOrderItems(orderItems);

            Stage menuStage = new Stage();
            menuStage.setTitle("Loyal Customer Info");
            menuStage.setScene(new Scene(root, 958, 700));
            menuStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class FoodItem {
        private final String name;
        private final double price;
        private final String image;

        public FoodItem(String name, double price, String image) {
            this.name = name;
            this.price = price;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getImage() {
            return image;
        }
    }
}