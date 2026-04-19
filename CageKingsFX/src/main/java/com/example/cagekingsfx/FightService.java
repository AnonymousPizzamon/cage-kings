package com.example.cagekingsfx;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FightService {

    private static final String API_KEY = "REDACTED_ROTATE_AT_PROVIDER";
    private static final String API_URL = "https://v1.mma.api-sports.io/fights?season=2024";

    // FETCH JSON FROM API
    public String getFightsJson() {

        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("x-apisports-key", API_KEY);
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Debug raw JSON (optional)
        System.out.println(result.toString());

        return result.toString();
    }

    /*
        Fix accent characters
     */
    private String fixEncoding(String text) {
        try {
            return new String(text.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return text;
        }
    }

    // -------------------------------
    // PARSE JSON INTO FIGHT OBJECTS
    // -------------------------------
    public List<Fight> parseFights(String json) {

        List<Fight> fights = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray responseArray = obj.getJSONArray("response");

            for (int i = 0; i < responseArray.length(); i++) {

                JSONObject fight = responseArray.getJSONObject(i);

                // Category
                String category = fight.optString("category", "N/A");

                // Fighters
                JSONObject fighters = fight.optJSONObject("fighters");

                String fighter1Name = "Unknown";
                String fighter2Name = "Unknown";
                String winner = "Draw";

                if (fighters != null) {

                    JSONObject first = fighters.optJSONObject("first");
                    JSONObject second = fighters.optJSONObject("second");

                    if (first != null) {
                        fighter1Name = fixEncoding(first.optString("name", "Unknown"));
                    }

                    if (second != null) {
                        fighter2Name = fixEncoding(second.optString("name", "Unknown"));
                    }

                    /* String winnerSide = fight.optString("winner", "");
                    System.out.println("Raw winner value: " + winnerSide);

                    if (winnerSide.equals("first")) {
                        winner = fighter1Name;
                    } else if (winnerSide.equals("second")) {
                        winner = fighter2Name;
                    } else {
                        winner = "Draw";
                    }

                     */

                    boolean fighter1Wins = first != null && first.optBoolean("winner", false);
                    boolean fighter2Wins = second != null && second.optBoolean("winner", false);

                    if(fighter1Wins){
                        winner = fighter1Name;
                    }
                    else if(fighter2Wins){
                        winner = fighter2Name;
                    }
                    else{
                        winner = "Draw";
                    }

                    int fighter1Id = first != null ? first.optInt("id", -1) : -1;
                    int fighter2Id = second != null ? second.optInt("id", -1) : -1;

                    Fight f = new Fight(fighter1Name, fighter2Name, category, winner);
                    f.setFighter1Id(fighter1Id);
                    f.setFighter2Id(fighter2Id);
                    fights.add(f);
                }

                // fights.add(new Fight(fighter1Name, fighter2Name, category, winner));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fights;
    }

    public String getFighterStats(int fighterId) {
        try {
            String json = fetchJson("/fighters?id=" + fighterId);

            JSONObject obj = new JSONObject(json);
            JSONArray response = obj.optJSONArray("response");

            if (response == null || response.length() == 0) return "N/A";

            JSONObject fighter = response.getJSONObject(0);

            String height = fighter.optString("height", "N/A");
            String weight = fighter.optString("weight", "N/A");
            String reach  = fighter.optString("reach", "N/A");
            String stance = fighter.optString("stance", "N/A");

            return height + " | " + weight + " | " + reach + " reach | " + stance;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    // CONVENIENCE: LOOKUP RECORD BY NAME
    // Returns int[] { wins, losses, draws } or null if not found

    // SHARED HTTP HELPER
    private String fetchJson(String endpoint) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL("https://v1.mma.api-sports.io" + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("x-apisports-key", API_KEY);
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


}
