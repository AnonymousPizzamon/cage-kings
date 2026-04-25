package com.example.cagekingsfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.TextArea;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BetHistoryController {

    @FXML
    private TextArea outputArea;

    @FXML
    void loadHistory(ActionEvent event) {
        String history = "";
        try(BufferedReader reader = new BufferedReader(new FileReader(BalanceController.BETS_CSV_PATH))){
            reader.readLine();
            String line;
            while((line = reader.readLine()) != null){
                String[] parts = line.split(",");
                history += parts[0] + " vs " + parts[1] + " | Bet on: " + parts[2] + " | $" + parts[3] + " | " + parts[4] + "\n";
            }
            if(history.isEmpty()) history = "No bets placed yet.";
        }catch(IOException e){
            history = "No bet history found.";
        }
        outputArea.setText(history);
    }

    @FXML
    void returnButton(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

}
