package com.example.cagekingsfx;

public class Bet {
    private String selectedFighter;
    private String actualWinner;

    public Bet(String selectedFighter, String actualWinner) {
        this.selectedFighter = selectedFighter;
        this.actualWinner = actualWinner;
    }

    public boolean isWin() {
        return selectedFighter.equals(actualWinner);
    }
}
