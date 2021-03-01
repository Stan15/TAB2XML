package converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class ScoreTest {

    /**
     * Test for Score Class.
     * Testing the method Score.getStringFragments()
     * Testing if the method receives an invalid input (String is empty/null)
     * that is returns a exception/warning
     */
    @Test//(expected = InvalidInputException.class)
    void test_Score_getStringFragments1(){
        /*String s = null;
        Score test = new Score(s); // test will fail, exceptions not added yet*/
        Assertions.assertThrows(InvalidInputException.class, () -> {
            new Score(null);
        });
    }
    @Test//(expected = InvalidInputException.class)
    void test_Score_getStringFragments2(){
        Assertions.assertThrows(InvalidInputException.class, () -> {
            new Score("");
        });
    }
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

        expectedStringFragments.put(1, "abcd");

        //changed list to set
        Set<Map.Entry<Integer, String>> expected = expectedStringFragments.entrySet();
        Set<Map.Entry<Integer, String>> actual = actualStringFragments.entrySet();


        Assertions.assertTrue(actual.size() == expected.size());

        //no get in Set and list wasnt working.
        for (Iterator<Map.Entry<Integer, String>> it = expected.iterator(); it.hasNext();){
            Map.Entry expectedItem = it.next();
            //Map.Entry actualItem = it.
            Assertions.assertEquals(expectedItem, test.rootStringFragments);

        }
        /*test.rootStringFragments.get(0)
        for (int i = 0; i < expected.size();i++){
            Map.Entry expectedItem = expected.get(i);

        }*/
        //System.out.println("Here is the output" +
        //Assertions.assertEquals(expectedStringFragments.get(1), test.rootStringFragments.get(1));
    }

}
