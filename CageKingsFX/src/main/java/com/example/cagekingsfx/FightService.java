package com.example.cagekingsfx;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FightService {

    private static final String API_KEY = loadApiKey();
    private static final String API_URL = "https://v1.mma.api-sports.io/fights?season=2024";

    private static String loadApiKey() {
        Properties props = new Properties();
        try (InputStream stream = FightService.class.getResourceAsStream("/config.properties")) {
            if (stream == null) {
                System.err.println("config.properties not found in resources — copy config.properties.example and add your key.");
                return "";
            }
            props.load(stream);
            return props.getProperty("API_KEY", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // -------------------------------
    // FETCH JSON FROM API
    // -------------------------------
    public String getFightsJson() {

        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(API_URL);
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

        // Debug raw JSON (optional)
        System.out.println(result.toString());

        return result.toString();
    }

    // -------------------------------
    // FIX ENCODING (for accented characters)
    // -------------------------------
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

                    String winnerSide = fight.optString("winner", "");

                    if (winnerSide.equals("first")) {
                        winner = fighter1Name;
                    } else if (winnerSide.equals("second")) {
                        winner = fighter2Name;
                    } else {
                        winner = "Draw";
                    }
                }

                fights.add(new Fight(fighter1Name, fighter2Name, category, winner));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fights;
    }
}