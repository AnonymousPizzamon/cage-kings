package com.example.cagekingsfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;

/**
 * Controller for the login screen that handles user login,
 * registration, and navigation to the main screen
 */
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private static final String USERS_FILE;

    static {
        String base = System.getProperty("user.dir");
        File direct = new File(base + "/data");
        if (direct.exists()) {
            USERS_FILE = base + "/data/users.csv";
        } else {
            USERS_FILE = base + "/CageKingsFX/data/users.csv";
        }
    }
    /**
     * Handles the register button click
     * Checks to make sure the fields are not empty, then registers the new user
     * Displays a success or failure message
     */
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

    /**
     * Checks to make sure the username and password match a record in the CSV file
     * @param username the username checked
     * @param password the password checked
     * @return true or false if a record is found or if its not found, respectively
     */
    private boolean validateLogin(String username, String password) {
        System.out.println("USERS FILE: " + new File(USERS_FILE).getAbsolutePath());
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

    /**
     * Handles the login button click
     * Sends the user to the main screen if login is valid
     */
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

    /**
     * Registers a new user by adding their credentials to the CSV file
     * Duplications are prevented
     * Displays a success message and initializes their balance
     *
     * @param username the new user's username
     * @param password the new user's password
     * @return true if registration succeeded, false if otherwise
     */
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

    /**
     * Initializes a new user's balance entry into the csv file
     * @param username the username to initialize the balance for
     */
    private void initializeUserBalance(String username) {
        BalanceModel.saveBalanceFor(username, 0.0);
    }

    /**
     * Navigates the user to the main-screen
     */
    private void navigateToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-screen.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();

            Scene scene = stage.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}