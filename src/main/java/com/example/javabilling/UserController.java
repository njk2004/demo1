package com.example.javabilling;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class UserController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> colUserId;
    @FXML
    private TableColumn<User, String> colUserName;
    @FXML
    private TableColumn<User, String> colPassword;
    @FXML
    private TextField txtUserName;
    @FXML
    private TextField txtPassword;
    @FXML
    private Button btnAddUser;
    @FXML
    private Button btnRemoveUser;
    @FXML
    private Button btnRefresh;

    private final ObservableList<User> userList = FXCollections.observableArrayList();

    // Database Connection Info
    private final String DB_URL = "jdbc:mysql://127.0.0.1:3306/billing"; //
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "nayan";  // Change if needed

    @FXML
    public void initialize() {
        colUserId.setCellValueFactory(cellData -> cellData.getValue().userIdProperty().asObject());
        colUserName.setCellValueFactory(cellData -> cellData.getValue().userNameProperty());
        colPassword.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());

        loadUsers();
    }

    @FXML
    private void addUser() {
        String username = txtUserName.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Username and Password cannot be empty!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO user (username, password) VALUES (?, ?)")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully!");
            loadUsers();  // Refresh Table
            txtUserName.clear();
            txtPassword.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add user!");
        }
    }

    @FXML
    private void removeUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a user to remove!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM user WHERE iduser = ?")) {

            stmt.setInt(1, selectedUser.getUserId());
            stmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "User removed successfully!");
            loadUsers();  // Refresh Table

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to remove user!");
        }
    }

    @FXML
    private void refreshTable() {
        loadUsers();
    }

    private void loadUsers() {
        userList.clear();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM user")) {

            while (rs.next()) {
                userList.add(new User(rs.getInt("iduser"), rs.getString("username"), rs.getString("password")));
            }
            userTable.setItems(userList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load users!");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner User Class
    public static class User {
        private final IntegerProperty userId;
        private final StringProperty userName;
        private final StringProperty password;

        public User(int userId, String userName, String password) {
            this.userId = new SimpleIntegerProperty(userId);
            this.userName = new SimpleStringProperty(userName);
            this.password = new SimpleStringProperty(password);
        }

        public int getUserId() { return userId.get(); }
        public String getUserName() { return userName.get(); }
        public String getPassword() { return password.get(); }

        public IntegerProperty userIdProperty() { return userId; }
        public StringProperty userNameProperty() { return userName; }
        public StringProperty passwordProperty() { return password; }
    }
}
