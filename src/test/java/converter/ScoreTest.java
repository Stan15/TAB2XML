package converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        String s = "";
        Score test = new Score(s); // test will fail, exceptions not added yet

    }
    /**
     * Test for Score Class.
     * Testing the method Score.getStringFragments()
     * Which should break down any give string into segments where each segment
     * is separated by a blank line.
     */
    @Test
    void test_Score_getStringFragments(){
        String s = "";
        Score test = new Score (s);
    }

}
