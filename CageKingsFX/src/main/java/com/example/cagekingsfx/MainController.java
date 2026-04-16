package com.example.cagekingsfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
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

        // Defaulting to fighter1 for now as per your original logic
        String selectedFighter = selectedFight.getFighter1();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bet-view.fxml"));
            Parent root = loader.load();

            BetController betController = loader.getController();
            betController.setData(selectedFight, selectedFighter);

            Stage betStage = new Stage();
            betStage.setTitle("Place Bet");
            betStage.initModality(Modality.APPLICATION_MODAL);
            betStage.setScene(new Scene(root));
            betStage.showAndWait();

            // Result label logic inside BetController,
            // so we just notify the main UI that the action was attempted.
            outputArea.setText("Betting process finished for: " + selectedFight.getFighter1());

        } catch (Exception e) {
            e.printStackTrace();
            outputArea.setText("Failed to open bet window.");
        }
    }


    @FXML
    void showBalance(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("balance.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}