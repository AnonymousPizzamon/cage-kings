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
    private String selectedFighter = null;

    @FXML
    public void initialize() {

        // Bind columns to Fight class getters
        fighter1Col.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFighter1())
        );
        fighter1Col.setCellFactory(col -> {
            javafx.scene.control.TableCell<Fight, String> cell = new javafx.scene.control.TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    selectedFighter = cell.getItem();
                    outputArea.setText("Selected: " + selectedFighter);
                }
            });
            return cell;
        });

        fighter2Col.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFighter2())
        );
        fighter2Col.setCellFactory(col -> {
            javafx.scene.control.TableCell<Fight, String> cell = new javafx.scene.control.TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    selectedFighter = cell.getItem();
                    outputArea.setText("Selected: " + selectedFighter);
                }
            });
            return cell;
        });

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

        if (selectedFight == null || selectedFighter == null) {
            outputArea.setText("Please click a fighter name to place your bet.");
            return;
        }

        String fighter1 = selectedFight.getFighter1();
        String fighter2 = selectedFight.getFighter2();

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