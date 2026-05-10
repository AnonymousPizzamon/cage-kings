package com.example.cagekingsfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LaunchApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    // Start our login screen instead.
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LaunchApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Cage Kings - Login");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }
}
