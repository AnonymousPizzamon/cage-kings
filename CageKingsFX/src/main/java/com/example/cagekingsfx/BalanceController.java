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

public class BalanceController {

    @FXML
    void returnButton(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }

    @FXML
    private TextField amountInput;

    private double currentBalance = 0.0;
    public static final String CSV_PATH = "data/UserBalance.csv";
    public static final String BETS_CSV_PATH = "data/BetsTracked.csv";


    @FXML
    private Label balanceLabel;

    @FXML
    void addCurrency(ActionEvent event) {
        String input = amountInput.getText();
        try{
            double amount = Double.parseDouble(input);
            currentBalance += amount;
            saveBalanceToCSV(currentBalance);
            balanceLabel.setText("Balance: $" + currentBalance);
            amountInput.clear();
        }catch(NumberFormatException e){
            balanceLabel.setText("Please enter a valid number.");
        }

    }

    @FXML
    public void initialize(){
        currentBalance = readBalanceFromCSV();
        balanceLabel.setText("Balance: $" + currentBalance);
    }
    public static double readBalanceFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_PATH))) {
            reader.readLine();
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                return Double.parseDouble(parts[1]);
            }
        } catch (IOException e) {
            System.out.println("No balance file found.");
        }
        return 0.00;
    }

    public static void saveBetToCSV(String fighter1, String fighter2, String chosenFighter, double betAmount, String result){
        boolean fileExists = new File(BETS_CSV_PATH).exists();
        try(PrintWriter writer = new PrintWriter(new FileWriter(BETS_CSV_PATH, true))){
            if(!fileExists){
                writer.println("fighter1,fighter2,chosen,amount,result");
            }
            writer.println(fighter1 + "," + fighter2 + "," + chosenFighter + "," + betAmount + "," + result);
        }catch(IOException e){
            System.out.println("Error saving bet: " + e.getMessage());
        }
    }
    public static void saveBalanceToCSV(double balance){

        balance = Math.round(balance * 100.0) / 100.0;
        try(PrintWriter writer = new PrintWriter(new FileWriter(CSV_PATH))){
            writer.println("username,balance");
            writer.println("player1," + balance);
        }catch(IOException e){
            System.out.println("Error saving balance: " + e.getMessage());
        }
    }

}
