package converter;

import custom_exceptions.InvalidInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreTest {

    /**
     * Test for Score Class.
     * Testing the method Score.getStringFragments()
     * Which should break down any give string into segments where each segment
     * is separated by a blank line.
     */
    @Test
    void test_Score_getStringFragments() throws InvalidInputException {
        String s = "abcd";
        Score test = new Score (s);

        LinkedHashMap<Integer, String> expectedStringFragments = new LinkedHashMap<>();
        LinkedHashMap<Integer, String> actualStringFragments = test.getStringFragments(s);

        expectedStringFragments.put(0, "abcd");

        //changed list to set
        Set<Map.Entry<Integer, String>> expected = expectedStringFragments.entrySet();
        Set<Map.Entry<Integer, String>> actual = actualStringFragments.entrySet();


        assertTrue(actual.size() == expected.size());

        //no get in Set and list wasnt working.
        for (Integer idx : actualStringFragments.keySet()) {
            assertTrue(expectedStringFragments.containsKey(idx));
            assertEquals(expectedStringFragments.get(idx), actualStringFragments.get(idx));
        }
    }

}
