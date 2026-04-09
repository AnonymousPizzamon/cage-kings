package com.example.cagekingsfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class MainController {

    @FXML
    private TableView<Fight> fightTable;

    @FXML
    private TableColumn<Fight, String> fighter1Col;

    @FXML
    private TableColumn<Fight, String> fighter2Col;

    @FXML
    private TableColumn<Fight, String> categoryCol;

    @FXML
    private TextArea outputArea;

    private FightService service = new FightService();

    @FXML
    public void initialize() {

        // Bind columns to Fight class getters
        fighter1Col.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFighter1())
        );

        fighter2Col.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFighter2())
        );

        categoryCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory())
        );
    }

    @FXML
    private void loadFights() {

        String json = service.getFightsJson();
        List<Fight> fights = service.parseFights(json);

        ObservableList<Fight> list = FXCollections.observableArrayList(fights);
        fightTable.setItems(list);

        outputArea.setText("Fights loaded.\n");
    }

    @FXML
    private void handleBet() {

        Fight selectedFight = fightTable.getSelectionModel().getSelectedItem();

        if (selectedFight == null) {
            outputArea.setText("Please select a fight first.");
            return;
        }

        String fighter1 = selectedFight.getFighter1();
        String fighter2 = selectedFight.getFighter2();

        // VERY BASIC: just always pick fighter1 (you can change later)
        String selectedFighter = fighter1;

        String result;

        if (selectedFighter.equals(selectedFight.getWinner())) {
            result = "Correct! Winner was: " + selectedFight.getWinner();
        } else {
            result = "Wrong! Winner was: " + selectedFight.getWinner();
        }

        // Output to UI
        outputArea.setText(
                "Fight: " + fighter1 + " vs " + fighter2 + "\n" +
                        "You chose: " + selectedFighter + "\n" +
                        result
        );

        // Also print to console
        System.out.println(result);
    }
}