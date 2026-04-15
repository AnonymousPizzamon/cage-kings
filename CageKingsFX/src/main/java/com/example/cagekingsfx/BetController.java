package com.example.cagekingsfx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class BetController {

    @FXML private Label fightLabel;
    @FXML private Label fighterLabel;
    @FXML private TextField amountField;
    @FXML private RadioButton winnerRadio;
    @FXML private RadioButton drawRadio;
    @FXML private Label resultLabel;

    private Fight fight;
    private String selectedFighter;

    public void setData(Fight fight, String selectedFighter) {
        this.fight = fight;
        this.selectedFighter = selectedFighter;
        fightLabel.setText(fight.getFighter1() + " vs " + fight.getFighter2());
        fighterLabel.setText("Your pick: " + selectedFighter);
    }

    @FXML
    private void handleConfirm() {
        int amount;
        try {
            amount = Integer.parseInt(amountField.getText().trim());
        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter a valid number.");
            return;
        }

        if (amount < 1 || amount > 100000) {
            resultLabel.setText("Amount must be between $1 and $100,000.");
            return;
        }

        boolean betOnDraw = drawRadio.isSelected();
        String actualWinner = fight.getWinner();
        boolean isDraw = actualWinner.equals("Draw");

        boolean won;
        if (betOnDraw) {
            won = isDraw;
        } else {
            won = selectedFighter.equals(actualWinner);
        }

        if (won) {
            resultLabel.setText("You won $" + amount + "! Winner was: " + actualWinner);
        } else {
            resultLabel.setText("You lost $" + amount + ". Winner was: " + actualWinner);
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) amountField.getScene().getWindow()).close();
    }
}
