package com.example.javabilling;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;

public class maaainn extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin.fxml")));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setScene((new Scene(root, 900, 700)));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}