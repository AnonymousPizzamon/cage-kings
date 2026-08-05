package com.example.cagekingsfx;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the model class of which is responsible for all balance and bet data
 * It handles the reading and writing to CSV files for user balance and bet history
 *
 * All records are keyed by username so that multiple accounts can share the same
 * data files without overwriting each other. The active account is read from
 * {@link Session#currentUser}.
 */
public class BalanceModel {

    /** Header written to UserBalance.csv. */
    static final String BALANCE_HEADER = "username,balance";

    /** Header written to BetsTracked.csv. */
    static final String BETS_HEADER = "username,fighter1,fighter2,chosen,amount,result";

    /**
     * Overrides the directory the CSV files live in. Only set by tests; when null
     * the location is resolved from the working directory as it always was.
     */
    private static String dataDirOverride = null;

    private static String getDataPath() {
        if (dataDirOverride != null) return dataDirOverride;
        String base = System.getProperty("user.dir");
        File direct = new File(base + "/data");
        if (direct.exists()) return base + "/data/";
        return base + "/CageKingsFX/data/";
    }

    /**
     * Points the model at a different data directory. Used by tests to work
     * against a temporary folder instead of the real one.
     *
     * @param dir directory ending in a separator, or null to restore the default
     */
    static void setDataDirForTesting(String dir) {
        dataDirOverride = dir;
    }

    /**
     * @return full path to the balance CSV
     */
    public static String csvPath() {
        return getDataPath() + "UserBalance.csv";
    }

    /**
     * @return full path to the bet history CSV
     */
    public static String betsCsvPath() {
        return getDataPath() + "BetsTracked.csv";
    }

    /**
     * Resolves the account whose data should be read or written.
     * Falls back to "player1" so the app still behaves if no one has logged in,
     * which is what the old single-user code assumed.
     *
     * @return the active username
     */
    private static String activeUser() {
        String user = Session.currentUser;
        if (user == null || user.trim().isEmpty()) return "player1";
        return user.trim();
    }

    /**
     * Reads every username/balance pair out of the balance CSV.
     * Rows that are malformed or unparseable are skipped rather than aborting the read.
     *
     * @return balances keyed by username, in file order
     */
    private static Map<String, Double> readAllBalances() {
        Map<String, Double> balances = new LinkedHashMap<>();
        File file = new File(csvPath());
        if (!file.exists()) return balances;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip CSV header
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                try {
                    balances.put(parts[0].trim(), Double.parseDouble(parts[1].trim()));
                } catch (NumberFormatException ignored) {
                    // a corrupt row should not take down the whole file
                }
            }
        } catch (IOException e) {
            System.out.println("No balance file found.");
        }
        return balances;
    }

    /**
     * Writes the full set of balances back out, replacing the file contents.
     *
     * @param balances balances keyed by username
     */
    private static void writeAllBalances(Map<String, Double> balances) {
        File file = new File(csvPath());
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(BALANCE_HEADER);
            for (Map.Entry<String, Double> entry : balances.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("Error saving balance: " + e.getMessage());
        }
    }

    /**
     * Reads the balance belonging to the logged in user.
     *
     * @return the current user's balance, or 0.0 if they have no row yet
     */
    public static double readBalanceFromCSV() {
        return readBalanceFor(activeUser());
    }

    /**
     * Reads the balance for a specific account.
     *
     * @param username the account to look up
     * @return that account's balance, or 0.0 if it has no row
     */
    public static double readBalanceFor(String username) {
        Double balance = readAllBalances().get(username);
        return balance == null ? 0.00 : balance;
    }

    /**
     * Saves the balance for the logged in user, rounded to 2 decimal places.
     * Other accounts' rows are preserved.
     *
     * @param balance the new balance being saved
     */
    public static void saveBalanceToCSV(double balance) {
        saveBalanceFor(activeUser(), balance);
    }

    /**
     * Saves the balance for a specific account without disturbing any other account.
     *
     * @param username the account to update
     * @param balance the new balance being saved
     */
    public static void saveBalanceFor(String username, double balance) {
        balance = Math.round(balance * 100.0) / 100.0;
        Map<String, Double> balances = readAllBalances();
        balances.put(username, balance);
        writeAllBalances(balances);
    }

    /**
     * Adds a bet record for the logged in user.
     * It will create the file and a header row if it doesn't exist.
     *
     * @param fighter1 name of the fighter 1
     * @param fighter2 name of fighter 2
     * @param chosenFighter the fighter bet on
     * @param betAmount the bet amount
     * @param result the result of the fight
     */
    public static void saveBetToCSV(String fighter1, String fighter2, String chosenFighter, double betAmount, String result) {
        String username = activeUser();
        File file = new File(betsCsvPath());
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        boolean needsHeader = !file.exists() || file.length() == 0;
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            if (needsHeader) {
                writer.println(BETS_HEADER);
            }
            writer.println(username + "," + fighter1 + "," + fighter2 + "," + chosenFighter + "," + betAmount + "," + result);
        } catch (IOException e) {
            System.out.println("Error saving bet: " + e.getMessage());
        }
    }

    /**
     * Reads the bet history belonging to a specific account.
     * Rows written before bets were user-scoped have 5 fields instead of 6;
     * those are treated as belonging to "player1" so old files still load.
     *
     * @param username the account whose bets should be returned
     * @return that account's bets, each as {fighter1, fighter2, chosen, amount, result}
     */
    public static List<String[]> readBetsFor(String username) {
        List<String[]> bets = new ArrayList<>();
        File file = new File(betsCsvPath());
        if (!file.exists()) return bets;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip CSV header
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");

                String owner;
                String[] bet;
                if (parts.length >= 6) {
                    owner = parts[0].trim();
                    bet = new String[]{parts[1], parts[2], parts[3], parts[4], parts[5]};
                } else if (parts.length == 5) {
                    owner = "player1"; // legacy row from before bets were user-scoped
                    bet = new String[]{parts[0], parts[1], parts[2], parts[3], parts[4]};
                } else {
                    continue;
                }

                if (owner.equals(username)) bets.add(bet);
            }
        } catch (IOException e) {
            return bets;
        }
        return bets;
    }

    /**
     * Reads the bet history belonging to the logged in user.
     *
     * @return the current user's bets
     */
    public static List<String[]> readBetsForCurrentUser() {
        return readBetsFor(activeUser());
    }
}
