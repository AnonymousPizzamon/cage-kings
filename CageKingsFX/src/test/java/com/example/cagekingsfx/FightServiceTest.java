package com.example.cagekingsfx;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the JSON parsing in {@link FightService}.
 *
 * parseFights is pure input/output so it can be exercised with fixture strings
 * without touching the network or needing an API key.
 */
class FightServiceTest {

    private final FightService service = new FightService();

    /** Builds a fights payload in the shape the api-sports.io endpoint returns. */
    private String payload(String fightsJson) {
        return "{\"response\":[" + fightsJson + "]}";
    }

    private String fight(String name1, boolean win1, String name2, boolean win2, String category) {
        return "{"
                + "\"category\":\"" + category + "\","
                + "\"fighters\":{"
                + "\"first\":{\"id\":1,\"name\":\"" + name1 + "\",\"winner\":" + win1 + "},"
                + "\"second\":{\"id\":2,\"name\":\"" + name2 + "\",\"winner\":" + win2 + "}"
                + "}}";
    }

    @Test
    @DisplayName("parses fighter names, category and ids from a well formed response")
    void parsesBasicFields() {
        List<FightModel> fights = service.parseFights(
                payload(fight("Jon Jones", true, "Stipe Miocic", false, "Heavyweight")));

        assertEquals(1, fights.size());
        FightModel f = fights.get(0);
        assertEquals("Jon Jones", f.getFighter1());
        assertEquals("Stipe Miocic", f.getFighter2());
        assertEquals("Heavyweight", f.getCategory());
        assertEquals(1, f.getFighter1Id());
        assertEquals(2, f.getFighter2Id());
    }

    @Test
    @DisplayName("winner resolves to whichever fighter has the winner flag set")
    void resolvesWinnerFromFlag() {
        List<FightModel> first = service.parseFights(
                payload(fight("A", true, "B", false, "Lightweight")));
        assertEquals("A", first.get(0).getWinner());

        List<FightModel> second = service.parseFights(
                payload(fight("A", false, "B", true, "Lightweight")));
        assertEquals("B", second.get(0).getWinner());
    }

    @Test
    @DisplayName("a bout with no winner flag is reported as a draw")
    void noWinnerFlagIsDraw() {
        List<FightModel> fights = service.parseFights(
                payload(fight("A", false, "B", false, "Welterweight")));

        assertEquals("Draw", fights.get(0).getWinner());
    }

    @Test
    @DisplayName("missing fighter names fall back to Unknown rather than throwing")
    void missingNamesFallBack() {
        String json = "{\"response\":[{\"category\":\"Featherweight\",\"fighters\":"
                + "{\"first\":{\"id\":7},\"second\":{\"id\":8}}}]}";

        List<FightModel> fights = service.parseFights(json);

        assertEquals(1, fights.size());
        assertEquals("Unknown", fights.get(0).getFighter1());
        assertEquals("Unknown", fights.get(0).getFighter2());
    }

    @Test
    @DisplayName("a missing category defaults to N/A")
    void missingCategoryDefaults() {
        String json = "{\"response\":[{\"fighters\":"
                + "{\"first\":{\"id\":1,\"name\":\"A\"},\"second\":{\"id\":2,\"name\":\"B\"}}}]}";

        assertEquals("N/A", service.parseFights(json).get(0).getCategory());
    }

    @Test
    @DisplayName("an entry with no fighters block is skipped instead of producing a null row")
    void skipsEntryWithoutFighters() {
        String json = "{\"response\":[{\"category\":\"Bantamweight\"}]}";

        assertTrue(service.parseFights(json).isEmpty());
    }

    @Test
    @DisplayName("malformed and empty input return an empty list rather than throwing")
    void malformedInputIsHandled() {
        assertTrue(service.parseFights("").isEmpty());
        assertTrue(service.parseFights("not json at all").isEmpty());
        assertTrue(service.parseFights("{}").isEmpty());
        assertTrue(service.parseFights("{\"response\":[]}").isEmpty());
    }

    @Test
    @DisplayName("multiple fights in one response all come back")
    void parsesMultipleFights() {
        String json = payload(
                fight("A", true, "B", false, "Heavyweight") + ","
                        + fight("C", false, "D", true, "Flyweight"));

        List<FightModel> fights = service.parseFights(json);

        assertEquals(2, fights.size());
        assertEquals("A", fights.get(0).getWinner());
        assertEquals("D", fights.get(1).getWinner());
    }

    @Test
    @DisplayName("accented fighter names survive parsing")
    void handlesAccentedNames() {
        List<FightModel> fights = service.parseFights(
                payload(fight("José Aldo", true, "Conor McGregor", false, "Featherweight")));

        assertEquals("José Aldo", fights.get(0).getFighter1());
        assertEquals("José Aldo", fights.get(0).getWinner());
    }
}
