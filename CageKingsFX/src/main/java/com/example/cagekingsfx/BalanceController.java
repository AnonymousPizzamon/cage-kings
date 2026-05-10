package com.example.cagekingsfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;

/**
 * Controller class for the balance screen
 * It handles displaying the current user balance and allowing the user to add funds
 */
public class BalanceController {

    /**
     * Handles the return button
     * Navigates the user back to the main screen
     * @param event the action event for when the user clicks the button
     * @throws IOException if the main-screen FXML file isn't found
     */
    @FXML
    void returnButton(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-screen.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    @FXML
    private TextField amountInput;

    private double currentBalance = 0.0;


    @FXML
    private Label balanceLabel;

    /**
     * Handles the add currency button
     * Adds the amount the user wants to the current balance,
     * and saves it to the CSV file and refreshes the label
     * Error message displays if an invalid input is detected
     * @param event the action of the user clicking the button
     */
    @FXML
    void addCurrency(ActionEvent event) {
        String input = amountInput.getText();
        try{
            double amount = Double.parseDouble(input);
            currentBalance += amount;
            BalanceModel.saveBalanceToCSV(currentBalance);
            balanceLabel.setText("Balance: $" + currentBalance);
            amountInput.clear();
        }catch(NumberFormatException e){
            balanceLabel.setText("Please enter a valid number.");
        }

    }

    /**
     * Initializes the balance screen by reading the save balance from CSV and
     * displaying it on the balance label
     */
    @FXML
    public void initialize(){
        currentBalance = BalanceModel.readBalanceFromCSV();
        balanceLabel.setText("Balance: $" + currentBalance);
    }

}
