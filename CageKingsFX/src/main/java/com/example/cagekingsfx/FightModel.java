package com.example.cagekingsfx;


/**
 * Model class showing an MMA fight
 * It stores all the data to a fight (weight, category, winner, etc.)
 */
public class FightModel {

    private String fighter1;
    private String fighter2;
    private String category;
    private String winner;
    private String fighter1Record;
    private String fighter2Record;
    private int fighter1Id;
    private int fighter2Id;

    /**
     * Constructor for FightModel with the fight details
     *
     * @param fighter1 name of the fighter 1
     * @param fighter2 name of the fighter 2
     * @param category the weight class of the fight
     * @param winner the winner of the fight
     */
    public FightModel(String fighter1, String fighter2, String category, String winner) {
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
        this.category = category;
        this.winner = winner;
    }

    /**
     * @return the name of the fighter 1
     */
    public String getFighter1() {
        return fighter1;
    }

    /**
     *
     * @return gets the name of fighter 2
     */
    public String getFighter2() {
        return fighter2;
    }

    /**
     *
     * @return gets the category of the fight
     */
    public String getCategory() {
        return category;
    }

    /**
     *
     * @return gets the winner of the fight
     */
    public String getWinner() {
        return winner;
    }

    /**
     * Name is misleading, it's actually stats because the API
     * didn't allow the record to be used. Still set as record, just to display
     * the possibility of future advancements of the app
     *
     * @param fighter1Record sets the fighter's stats
     */
    public void setFighter1Record(String fighter1Record){
        this.fighter1Record = fighter1Record;
    }

    /**
     *
     * @return the API ID of fighter 1
     */
    public int getFighter1Id(){
        return fighter1Id;
    }

    /**
     *
     * @param fighter1Id sets the API ID for fighter 1
     */
    public void setFighter1Id(int fighter1Id){
        this.fighter1Id = fighter1Id;
    }

    /**
     *
     * @return gets the API ID for fighter 2
     */
    public int getFighter2Id(){
        return fighter2Id;
    }

    /**
     *
     * @param fighter2Id sets the fighter 2 API ID
     */
    public void setFighter2Id(int fighter2Id){
        this.fighter2Id = fighter2Id;
    }

    /**
     * Name is misleading, it's actually stats because the API
     * didn't allow the record to be used. Still set as record, just to display
     * the possibility of future advancements of the app
     *
     * @param fighter2Record sets the fighter's stats
     */
    public void setFighter2Record(String fighter2Record){
        this.fighter2Record = fighter2Record;
    }

    /**
     *
     * @return the fighter's stats (height, weight, reach, etc.)
     */
    public String getFighter1Record(){
        return fighter1Record;
    }

    /**
     *
     * @return the fighter's stats (height, weight, reach, etc.)
     */
    public String getFighter2Record(){
        return fighter2Record;
    }

}