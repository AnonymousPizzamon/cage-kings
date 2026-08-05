package com.example.cagekingsfx;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.TextArea;
import javafx.util.Duration;

import javafx.scene.image.ImageView;
import java.io.IOException;

/**
 * The controller class for the bet history screen
 * It reads and displays the user's previous bets from BetsTracked.csv
 * It will show the cat in the bath tub if the winrate is poor
 */
public class BetHistoryController {

    @FXML
    private TextArea outputArea;

    //Picture of the wet cat
    @FXML private ImageView bathCatImage;

    /**
     * Initializes the bet history screen
     * Wet cat is hidden until the button is pressed and winrate is checked
     */
    @FXML
    public void initialize(){
        bathCatImage.setVisible(false);
    }

    /**
     * Loads and displays the user's bet history from BetsTracked.csv
     * Counts total bets and wins, then shows the wet cat if winrate is under 40%
     *
     * @param event the event when the load history button is pressed
     */
    @FXML
    public void loadHistory(ActionEvent event) {
        String history = "";
        int wins = 0;
        int total = 0;

        for(String[] parts : BalanceModel.readBetsForCurrentUser()){
            total++;
            if(parts[4].equalsIgnoreCase("Win")) wins++;
            history += parts[0] + " vs " + parts[1] + " | Bet on: " + parts[2] + " | $" + parts[3] + " | " + parts[4] + "\n";
        }
        if(history.isEmpty()) history = "No bets placed yet.";
        outputArea.setText(history);

        if(total >= 3){
            double winRate = (double)wins / total;
            if(winRate <= 0.4){
                showBathCat();
            }
        }
    }

    /**
     * Plays the popup animation showing a wet cat
     * The image pops up with the scale transition then fades out
     * It is triggered when winrate is 40% or below
     *
     */
    private void showBathCat() {
        bathCatImage.setVisible(true);
        bathCatImage.setOpacity(1.0);
        bathCatImage.setScaleX(0.1);
        bathCatImage.setScaleY(0.1);
        bathCatImage.setFitWidth(200);
        bathCatImage.setFitHeight(200);

        ScaleTransition blast = new ScaleTransition(Duration.millis(300), bathCatImage);
        blast.setToX(1.0);
        blast.setToY(1.0);
        blast.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fade = new FadeTransition(Duration.millis(800), bathCatImage);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.millis(600));
        fade.setOnFinished(e -> bathCatImage.setVisible(false));

        new SequentialTransition(blast, fade).play();
    }

    /**
     * Return button to go back to the main-screen
     *
     * @param event triggered when user clicks return button
     * @throws IOException if the main-screen FXML file isn't able to load
     */
    @FXML
    public void returnButton(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-screen.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

}
