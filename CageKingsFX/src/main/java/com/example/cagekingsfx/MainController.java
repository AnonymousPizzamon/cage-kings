package com.example.cagekingsfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML private TableView<Fight> fightTable;
    @FXML private TableColumn<Fight, String> fighter1Col;
    @FXML private TableColumn<Fight, String> fighter2Col;
    @FXML private TableColumn<Fight, String> categoryCol;
    @FXML private TextArea outputArea;
    @FXML private TextField betInput;

    // --- NEW: radio button fields ---
    @FXML private RadioButton fighter1Radio;
    @FXML private RadioButton fighter2Radio;
    @FXML private ToggleGroup fighterToggleGroup;

    private FightService service = new FightService();

    @FXML
    public void initialize() {
        fighter1Col.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFighter1()));
        fighter2Col.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFighter2()));
        categoryCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));

        // --- NEW: update radio button labels when a fight row is clicked ---
        fightTable.getSelectionModel().selectedItemProperty().addListener((obs, oldFight, newFight) -> {
            System.out.println("Row clicked: " + (newFight != null ? newFight.getFighter1() : "null")); // DEBUG
            if (newFight != null) {
                fighter1Radio.setText(newFight.getFighter1());
                fighter2Radio.setText(newFight.getFighter2());
                fighterToggleGroup.selectToggle(null); // clear selection on new fight
                loadFighterRecords(newFight);
            }
        });
    }

    @FXML
    private void loadFights() {
        String json = service.getFightsJson();
        List<Fight> fights = service.parseFights(json);
        fightTable.setItems(FXCollections.observableArrayList(fights));
        outputArea.setText("Fights loaded.\n");
    }

    @FXML
    private void handleBet() {
        // 1. Fight must be selected
        Fight selectedFight = fightTable.getSelectionModel().getSelectedItem();
        if (selectedFight == null) {
            outputArea.setText("Please select a fight first.");
            return;
        }

        // 2. Fighter must be chosen via radio button
        if (fighterToggleGroup.getSelectedToggle() == null) {
            outputArea.setText("Please select a fighter to bet on.");
            return;
        }

        // 3. Determine chosen fighter from radio selection
        String selectedFighter = fighter1Radio.isSelected() ? selectedFight.getFighter1() : selectedFight.getFighter2();

        // 4. Validate bet amount
        double betAmount;
        try {
            betAmount = Double.parseDouble(betInput.getText().trim());
        } catch (NumberFormatException e) {
            outputArea.setText("Please enter a valid bet amount.");
            return;
        }

        // 5. Check balance
        double currentBalance = BalanceController.readBalanceFromCSV();
        if (betAmount > currentBalance) {
            outputArea.setText("Insufficient balance! Your balance is $" + currentBalance);
            return;
        }

        // 6. Evaluate result
        String fighter1 = selectedFight.getFighter1();
        String fighter2 = selectedFight.getFighter2();
        String result;

        if (selectedFighter.equals(selectedFight.getWinner())) {
            double winnings = betAmount * 0.8;
            currentBalance += winnings;
            BalanceController.saveBalanceToCSV(currentBalance);
            BalanceController.saveBetToCSV(fighter1, fighter2, selectedFighter, betAmount, "Win");
            result = "Correct! Winner was: " + selectedFight.getWinner() + "\nYou won $" + winnings + "\nNew Balance: $" + currentBalance;
        } else {
            currentBalance -= betAmount;
            BalanceController.saveBalanceToCSV(currentBalance);
            BalanceController.saveBetToCSV(fighter1, fighter2, selectedFighter, betAmount, "Loss");
            result = "Wrong! Winner was: " + selectedFight.getWinner() + "\nYou lost $" + betAmount + "\nNew Balance: $" + currentBalance;
        }

        outputArea.setText("Fight: " + fighter1 + " vs " + fighter2 + "\n" + "You chose: " + selectedFighter + "\n" + result);

        System.out.println(result);
    }

    @FXML
    void showBalance(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("balance.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    private void loadFighterRecords(Fight fight) {
        outputArea.setText("Loading records...");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                // Fighter 1
                String stats1 = service.getFighterStats(fight.getFighter1Id());
                fight.setFighter1Record(stats1);

                // Fighter 2
                String stats2 = service.getFighterStats(fight.getFighter2Id());
                fight.setFighter2Record(stats2);

                return null;
            }
        };

        // Update UI once records are loaded
        task.setOnSucceeded(e -> {
            outputArea.setText(fight.getFighter1() + ": " + fight.getFighter1Record() + "\n" + fight.getFighter2() + ": " + fight.getFighter2Record());
        });

        task.setOnFailed(e -> outputArea.setText("Failed to load fighter records."));

        new Thread(task).start();
    }
    @FXML
    void showPreviousBets(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("previous-bets.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = stage.getScene();
        scene.setRoot(root);
    }
}