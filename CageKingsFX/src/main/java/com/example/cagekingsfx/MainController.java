package com.example.cagekingsfx;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.io.InputStream;
import java.util.Properties;


/**
 * Controller class for the main screen
 * It handles the loading of fights, placing bets, and going to the different screens
 * It also displays all the 2024 events
 *
 */
public class MainController {

    @FXML private TableView<FightModel> fightTable;
    @FXML private TableColumn<FightModel, String> fighter1Col;
    @FXML private TableColumn<FightModel, String> fighter2Col;
    @FXML private TableColumn<FightModel, String> categoryCol;
    @FXML private TextArea outputArea;
    @FXML private TextField betInput;
    @FXML private ImageView wrongChoiceImage;

    @FXML private RadioButton fighter1Radio;
    @FXML private RadioButton fighter2Radio;
    @FXML private ToggleGroup fighterToggleGroup;
    private Properties imageProps = new Properties();

    private FightService service = new FightService();

    /**
     * Initializes main screen
     * It sets up the column data and loads the dog image when a wrong choice is selected
     *
     */
    @FXML
    public void initialize() {

        // This sets up each column to a fight data like fighter 1 name for column 1 and category for column 3
        fighter1Col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFighter1()));
        fighter2Col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFighter2()));
        categoryCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));

        // Whenever a fight is selected, the buttons will update to the names and it loads the fighter stats
        fightTable.getSelectionModel().selectedItemProperty().addListener((obs, oldFight, newFight) -> {
            if (newFight != null) {
                fighter1Radio.setText(newFight.getFighter1());
                fighter2Radio.setText(newFight.getFighter2());
                fighterToggleGroup.selectToggle(null);
                loadFighterRecords(newFight);
            }
        });

        try (InputStream input = getClass().getResourceAsStream("images.properties")) {
            imageProps.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        wrongChoiceImage.setVisible(false);

    }

    /**
     * Gets all 2024 fights from the API and updates the table with it
     */
    @FXML
    private void loadFights() {
        String json = service.getFightsJson();
        List<FightModel> fights = service.parseFights(json);
        fightTable.setItems(FXCollections.observableArrayList(fights));
        outputArea.setText("Fights loaded.\n");
    }


    /**
     * Handles the bet button
     * Makes sure that a fight and fighter are selected and a valid bet amount is placed
     * and that the bet amount is correct for the user's total balance.
     */
    @FXML
    private void handleBet() {
        // 1. Fight must be selected
        FightModel selectedFight = fightTable.getSelectionModel().getSelectedItem();
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
        double currentBalance = BalanceModel.readBalanceFromCSV();
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
            BalanceModel.saveBalanceToCSV(currentBalance);
            BalanceModel.saveBetToCSV(fighter1, fighter2, selectedFighter, betAmount, "Win");
            result = "Correct! Winner was: " + selectedFight.getWinner() + "\nYou won $" + winnings + "\nNew Balance: $" + currentBalance;
        }
        else{
            currentBalance -= betAmount;
            BalanceModel.saveBalanceToCSV(currentBalance);
            BalanceModel.saveBetToCSV(fighter1, fighter2, selectedFighter, betAmount, "Loss");
            result = "Wrong! Winner was: " + selectedFight.getWinner() + "\nYou lost $" + betAmount + "\nNew Balance: $" + currentBalance;
            blastImage();
        }

        outputArea.setText("Fight: " + fighter1 + " vs " + fighter2 + "\n" + "You chose: " + selectedFighter + "\n" + result);

        System.out.println(result);
    }

    /**
     * The animation for the dog when an incorrect bet is placed
     * Triggered whenever the user selects a losing fighter
     * Scale transition and fade transition are used to pop the picture up
     * and fade it out.
     */
    private void blastImage() {

        Image funnyImage = new Image(getClass().getResourceAsStream("images/funnydoggy.jpeg"));
        wrongChoiceImage.setImage(funnyImage);

        wrongChoiceImage.setVisible(true);
        wrongChoiceImage.setOpacity(1.0);
        wrongChoiceImage.setScaleX(0.1);
        wrongChoiceImage.setScaleY(0.1);

        ScaleTransition blast = new ScaleTransition(Duration.millis(300), wrongChoiceImage);
        blast.setToX(1.0);
        blast.setToY(1.0);
        blast.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fade = new FadeTransition(Duration.millis(800), wrongChoiceImage);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.millis(600));
        fade.setOnFinished(e -> wrongChoiceImage.setVisible(false));

        new SequentialTransition(blast, fade).play();
    }

    /**
     * Navigates to balance screen
     *
     * @param event the event of the user selecting balance button
     * @throws IOException if balance FXML file cannot be found/loaded
     */
    @FXML
    void showBalance(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("balance.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    /**
     * Loads the fighters stats such as height, weight, etc.
     * Displays these when the user selects a row in the bottom text area
     *
     * @param fight the selected fight row of which the stats will be fetched for
     */
    private void loadFighterRecords(FightModel fight) {
        outputArea.setText("Loading records...");

        String stats1 = service.getFighterStats(fight.getFighter1Id());
        fight.setFighter1Record(stats1);

        String stats2 = service.getFighterStats(fight.getFighter2Id());
        fight.setFighter2Record(stats2);

        outputArea.setText(fight.getFighter1() + ": " + fight.getFighter1Record() + "\n"
                + fight.getFighter2() + ": " + fight.getFighter2Record());
    }

    /**
     * Navigates to previous bets screen
     *
     * @param event the event of the bet history button being selected
     * @throws IOException if the previous-bets FXML file cannot be loaded/found
     */
    @FXML
    void showPreviousBets(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("previous-bets.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = stage.getScene();
        scene.setRoot(root);
    }
}