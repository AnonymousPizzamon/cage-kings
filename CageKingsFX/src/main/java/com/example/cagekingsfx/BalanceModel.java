package com.example.cagekingsfx;

import java.io.*;

/**
 * This is the model class of which is responsible for all balance and bet data
 * It handles the reading and writing to CSV files for user balance and bet history
 */
public class BalanceModel {

    public static final String CSV_PATH = "data/UserBalance.csv";
    public static final String BETS_CSV_PATH = "data/BetsTracked.csv";

    /**
     * Reads the current balance from UserBalance.csv
     * Skips the top line and parses the balance from there
     *
     * @return the current balance, or 0.0 if the file cannot be read, or doesn't exist
     */
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

    /**
     * Adds a bet record to BetsTracked.csv
     * It will create the file and a header row if it doesn't exist
     *
     * @param fighter1 name of the fighter 1
     * @param fighter2 name of fighter 2
     * @param chosenFighter the fighter bet on
     * @param betAmount the bet amount
     * @param result the result of the fight
     */
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

    /**
     * Saves the balance to the UserBalance.csv, and overwrites the value already in it
     * It rounds the 2 decimal places
     *
     * @param balance the new balance being saved
     */
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
