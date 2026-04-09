package com.example.cagekingsfx;

public class Fight {

    private String fighter1;
    private String fighter2;
    private String category;
    private String winner;

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

    public void setWinner(String winner) {
        this.winner = winner;
    }
}