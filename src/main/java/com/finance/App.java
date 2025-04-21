package com.finance;

import com.finance.database.DatabaseConnection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Create database:");
        DatabaseConnection.initializeDatabase();
        
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main_view.fxml"));
        primaryStage.setScene(new Scene(fxmlLoader.load()));
        primaryStage.setTitle("Finance-Tracker");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
