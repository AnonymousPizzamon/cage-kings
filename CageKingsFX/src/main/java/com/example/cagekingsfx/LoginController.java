package com.example.cagekingsfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private static final String USERS_FILE = "data/users.csv";

    @FXML
    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (validateLogin(user, pass)) {
            // Store the logged-in user somewhere
            Session.currentUser = user;
            navigateToMain();
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    private void handleRegister() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Fields cannot be empty.");
            return;
        }

        if (registerUser(user, pass)) {
            statusLabel.setText("Registration successful! Please login.");
        } else {
            statusLabel.setText("User already exists.");
        }
    }

    private boolean validateLogin(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    private boolean registerUser(String username, String password) {
        if (validateLogin(username, password)) return false; // Prevent duplicates

        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE, true))) {
            pw.println(username + "," + password);
            // Initialize balance
            initializeUserBalance(username);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void initializeUserBalance(String username) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/UserBalance.csv", true))) {
            pw.println(username + ",0.0");
        } catch (IOException ignored) {}
    }

    private void navigateToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();

            Scene scene = stage.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}