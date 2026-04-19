package com.example.cagekingsfx;

public class Fight {

    private String fighter1;
    private String fighter2;
    private String category;
    private String winner;
    private String fighter1Record;
    private String fighter2Record;
    private int fighter1Id;
    private int fighter2Id;

    public Fight() {
        // default constructor (important for parsing)
    }

    public Fight(String fighter1, String fighter2, String category, String winner) {
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
        this.category = category;
        this.winner = winner;
    }

    public String getFighter1() {
        return fighter1;
    }

    public void setFighter1(String fighter1) {
        this.fighter1 = fighter1;
    }

    public String getFighter2() {
        return fighter2;
    }

    public void setFighter2(String fighter2) {
        this.fighter2 = fighter2;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWinner() {
        return winner;
    }

    public void setFighter1Record(String fighter1Record){
        this.fighter1Record = fighter1Record;
    }
    public int getFighter1Id(){
        return fighter1Id;
    }
    public void setFighter1Id(int fighter1Id){
        this.fighter1Id = fighter1Id;
    }
    public int getFighter2Id(){
        return fighter2Id;
    }
    public void setFighter2Id(int fighter2Id){
        this.fighter2Id = fighter2Id;
    }

    public void setFighter2Record(String fighter2Record){
        this.fighter2Record = fighter2Record;
    }
    public String getFighter1Record(){
        return fighter1Record;
    }

    public String getFighter2Record(){
        return fighter2Record;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}