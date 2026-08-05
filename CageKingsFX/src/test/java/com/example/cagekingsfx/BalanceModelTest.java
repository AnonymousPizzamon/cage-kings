package com.example.cagekingsfx;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the per-user balance and bet storage in {@link BalanceModel}.
 *
 * These cover the data isolation bug the README documented, where saving one
 * account's balance destroyed every other account's row.
 */
class BalanceModelTest {

    @TempDir
    Path dataDir;

    @BeforeEach
    void pointModelAtTempDir() {
        BalanceModel.setDataDirForTesting(dataDir.toString() + java.io.File.separator);
        Session.currentUser = null;
    }

    @AfterEach
    void restoreDefaults() {
        BalanceModel.setDataDirForTesting(null);
        Session.currentUser = null;
    }

    // ---------- balances ----------

    @Test
    @DisplayName("a balance saved for one user reads back for that user")
    void savesAndReadsBalance() {
        BalanceModel.saveBalanceFor("alden", 250.0);

        assertEquals(250.0, BalanceModel.readBalanceFor("alden"));
    }

    @Test
    @DisplayName("saving one user's balance leaves other users untouched")
    void savingOneUserPreservesOthers() {
        BalanceModel.saveBalanceFor("alden", 100.0);
        BalanceModel.saveBalanceFor("robert", 500.0);
        BalanceModel.saveBalanceFor("clemente", 75.5);

        // updating one account must not disturb the other two
        BalanceModel.saveBalanceFor("robert", 999.0);

        assertEquals(100.0, BalanceModel.readBalanceFor("alden"));
        assertEquals(999.0, BalanceModel.readBalanceFor("robert"));
        assertEquals(75.5, BalanceModel.readBalanceFor("clemente"));
    }

    @Test
    @DisplayName("an unknown user has a zero balance rather than another user's")
    void unknownUserReadsZero() {
        BalanceModel.saveBalanceFor("alden", 300.0);

        assertEquals(0.0, BalanceModel.readBalanceFor("nobody"));
    }

    @Test
    @DisplayName("balances route through the logged in session user")
    void usesSessionUser() {
        Session.currentUser = "alden";
        BalanceModel.saveBalanceToCSV(420.0);

        Session.currentUser = "robert";
        BalanceModel.saveBalanceToCSV(10.0);

        Session.currentUser = "alden";
        assertEquals(420.0, BalanceModel.readBalanceFromCSV());

        Session.currentUser = "robert";
        assertEquals(10.0, BalanceModel.readBalanceFromCSV());
    }

    @Test
    @DisplayName("balances are rounded to two decimal places")
    void roundsToCents() {
        BalanceModel.saveBalanceFor("alden", 10.005999);

        assertEquals(10.01, BalanceModel.readBalanceFor("alden"));
    }

    @Test
    @DisplayName("repeated saves update in place instead of appending duplicates")
    void repeatedSavesDoNotDuplicateRows() throws IOException {
        BalanceModel.saveBalanceFor("alden", 1.0);
        BalanceModel.saveBalanceFor("alden", 2.0);
        BalanceModel.saveBalanceFor("alden", 3.0);

        List<String> lines = Files.readAllLines(Path.of(BalanceModel.csvPath()));
        // header plus exactly one row
        assertEquals(2, lines.size());
        assertEquals(3.0, BalanceModel.readBalanceFor("alden"));
    }

    @Test
    @DisplayName("reading with no balance file present returns zero")
    void missingFileReadsZero() {
        assertEquals(0.0, BalanceModel.readBalanceFor("alden"));
    }

    @Test
    @DisplayName("a corrupt row is skipped without losing the valid rows")
    void corruptRowIsSkipped() throws IOException {
        try (PrintWriter pw = new PrintWriter(BalanceModel.csvPath())) {
            pw.println(BalanceModel.BALANCE_HEADER);
            pw.println("alden,100.0");
            pw.println("broken,notanumber");
            pw.println("missingfield");
            pw.println("robert,50.0");
        }

        assertEquals(100.0, BalanceModel.readBalanceFor("alden"));
        assertEquals(50.0, BalanceModel.readBalanceFor("robert"));
        assertEquals(0.0, BalanceModel.readBalanceFor("broken"));
    }

    // ---------- bets ----------

    @Test
    @DisplayName("a user only sees their own bets")
    void betsAreScopedToUser() {
        Session.currentUser = "alden";
        BalanceModel.saveBetToCSV("A", "B", "A", 50.0, "Win");
        BalanceModel.saveBetToCSV("C", "D", "D", 25.0, "Loss");

        Session.currentUser = "robert";
        BalanceModel.saveBetToCSV("E", "F", "E", 10.0, "Win");

        assertEquals(2, BalanceModel.readBetsFor("alden").size());
        assertEquals(1, BalanceModel.readBetsFor("robert").size());
        assertEquals(0, BalanceModel.readBetsFor("clemente").size());
    }

    @Test
    @DisplayName("bet fields come back in display order")
    void betFieldsRoundTrip() {
        Session.currentUser = "alden";
        BalanceModel.saveBetToCSV("Jon Jones", "Stipe Miocic", "Jon Jones", 500.0, "Win");

        String[] bet = BalanceModel.readBetsForCurrentUser().get(0);

        assertEquals("Jon Jones", bet[0]);
        assertEquals("Stipe Miocic", bet[1]);
        assertEquals("Jon Jones", bet[2]);
        assertEquals("500.0", bet[3]);
        assertEquals("Win", bet[4]);
    }

    @Test
    @DisplayName("legacy five column rows are attributed to player1 so old files still load")
    void legacyRowsStillLoad() throws IOException {
        try (PrintWriter pw = new PrintWriter(BalanceModel.betsCsvPath())) {
            pw.println("fighter1,fighter2,chosen,amount,result");
            pw.println("A,B,A,50.0,Win");
        }

        List<String[]> legacy = BalanceModel.readBetsFor("player1");

        assertEquals(1, legacy.size());
        assertEquals("A", legacy.get(0)[0]);
        assertEquals("Win", legacy.get(0)[4]);
    }

    @Test
    @DisplayName("the bet header is written once, not before every row")
    void headerWrittenOnce() throws IOException {
        Session.currentUser = "alden";
        BalanceModel.saveBetToCSV("A", "B", "A", 1.0, "Win");
        BalanceModel.saveBetToCSV("C", "D", "C", 2.0, "Loss");

        List<String> lines = Files.readAllLines(Path.of(BalanceModel.betsCsvPath()));

        assertEquals(3, lines.size());
        assertEquals(BalanceModel.BETS_HEADER, lines.get(0));
    }

    @Test
    @DisplayName("reading bets with no file present returns empty, not an error")
    void missingBetsFileReadsEmpty() {
        assertTrue(BalanceModel.readBetsFor("alden").isEmpty());
    }

    @Test
    @DisplayName("with no one logged in the model falls back to the player1 account")
    void fallsBackToPlayer1() {
        Session.currentUser = null;
        BalanceModel.saveBalanceToCSV(60.0);

        assertEquals(60.0, BalanceModel.readBalanceFor("player1"));
    }
}
