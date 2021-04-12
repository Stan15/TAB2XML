package converter;

import converter.measure.Measure;
import converter.note.Note;
import custom_exceptions.InvalidInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utility.Parser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


        assertEquals(expected.size(), actual.size());

        //no get in Set and list wasnt working.
        for (Integer idx : actualStringFragments.keySet()) {
            assertTrue(expectedStringFragments.containsKey(idx));
            assertEquals(expectedStringFragments.get(idx), actualStringFragments.get(idx));
        }
    }

    @Test
    void repeatInlineTest() {
        int expectedRepeatCount = 5;
        String inlineRepeatMeasure = """
                e||--------------------"""+expectedRepeatCount+"""
                |
                B||---3----------------||
                G||*------------------*||
                D||*----5-------------*||
                A||-------------8------||
                E||--------------------||
                """;
        Score score = new Score(inlineRepeatMeasure);
        assertEquals(1, score.measureCollectionList.size(), "one measure was expected but found "+score.measureCollectionList.size()+".");
        Measure measure = score.getMeasure(1);
        assertTrue(measure.isRepeatEnd());
        assertTrue(measure.isRepeatStart());
        List<Note> noteList = measure.getSortedNoteList();
        assertEquals(3, noteList.size(), "three notes were expected in the following measure, but found "+noteList.size()+"."
                +"\n\tMeasure:\n\t\t"+inlineRepeatMeasure+"\n");
        Matcher matcher = Pattern.compile("<words[^>]*>[^<0-9]*"+expectedRepeatCount+"[^<0-9]*</words>").matcher(Parser.parse(score));
        assertTrue(matcher.find(), "repeat count not properly detected and applied");
    }

    @Test
    void repeatAboveTest() {
        String[] measureStrings = {
                """
                e|--------------------|
                B|---3----------------|
                G|--------------------|
                D|-----5--------------|
                A|-------------8------|
                E|--------------------|
                """,

                """
                CC|--------------------|
                HH|---x----------------|
                SD|--------------------|
                HT|-----o--------------|
                MT|-------------O------|
                BD|--------------------|
                """,

                """
                e|------------8-------|
                B|---3----------------|
                G|--------------------|
                D|-----5--------------|
                """,

        };
        for (String measureString : measureStrings) {
            String[] repeatMeasures = {
                    // 5x
                    "|----repeat 5x-------|\n"+measureString, // no whitespace
                    "|----repeat 5x----   |\n"+measureString, // whitespace at end
                    "|  --repeat 5x-------|\n"+measureString, // whitespace at start
                    "|    repeat 5x       |\n"+measureString, // no -'s
                    "|----repeat 5x  -----|\n"+measureString, // whitespace after text
                    "|--  repeat 5x-------|\n"+measureString, // whitespace before text
                    "|----repeat 5x  ---  |\n"+measureString, // dangling -'s

                    //x5
                    "|----repeat x5-------|\n"+measureString,
                    "|----repeat x5----   |\n"+measureString,
                    "|  --repeat x5-------|\n"+measureString,
                    "|    repeat x5       |\n"+measureString,
                    "|----repeat x5  -----|\n"+measureString,
                    "|--  repeat x5-------|\n"+measureString,
                    "|----repeat x5  ---  |\n"+measureString,

                    // capital X5
                    "|----repeat X5-------|\n"+measureString,
                    "|----repeat X5----   |\n"+measureString,
                    "|  --repeat X5-------|\n"+measureString,
                    "|    repeat X5       |\n"+measureString,
                    "|----repeat X5  -----|\n"+measureString,
                    "|--  repeat X5-------|\n"+measureString,
                    "|----repeat X5  ---  |\n"+measureString,

                    // 5 times
                    "|----repeat 5 times--|\n"+measureString,
                    "|--repeat 5 times--  |\n"+measureString,
                    "|  --repeat 5 times--|\n"+measureString,
                    "|    repeat 5 times  |\n"+measureString,
                    "|--repeat 5 times  --|\n"+measureString,
                    "|--  repeat 5 times--|\n"+measureString,
                    "|--repeat 5 times -- |\n"+measureString,
            };

            for (String repeatMeasure : repeatMeasures) {
                Score score = new Score(repeatMeasure);
                assertEquals(1, score.measureCollectionList.size(), "one measure was expected but found "+score.measureCollectionList.size()+".");
                Measure measure = score.getMeasure(1);
                assertTrue(measure.isRepeatEnd());
                assertTrue(measure.isRepeatStart());
                List<Note> noteList = measure.getSortedNoteList();
                assertEquals(3, noteList.size(), "three notes were expected in the following measure, but found "+noteList.size()+".");
                Matcher matcher = Pattern.compile("<words[^>]*>[^<0-9]*"+5+"[^<0-9]*</words>").matcher(Parser.parse(score));
                assertTrue(matcher.find(), "repeat count not properly detected and applied");
            }
        }

    }

    @Test
    void timeSigFirstMeasureTest() {
        String[] measureStrings = {
                """
                e|--------------------| e|--------------------| e|--------------------| e|--------------------|  
                B|---3----------------| B|---3----------------| B|---3----------------| B|---3----------------|
                G|--------------------| G|--------------------| G|--------------------| G|--------------------|
                D|-----5--------------| D|-----5--------------| D|-----5--------------| D|-----5--------------|
                A|-------------8------| A|-------------8------| A|-------------8------| A|-------------8------|
                E|--------------------| E|--------------------| E|--------------------| E|--------------------|
                """,

                """
                CC|--------------------| CC|--------------------| CC|--------------------| CC|--------------------|
                HH|---x----------------| HH|---x----------------| HH|---x----------------| HH|---x----------------|
                SD|--------------------| SD|--------------------| SD|--------------------| SD|--------------------|
                HT|-----o--------------| HT|-----o--------------| HT|-----o--------------| HT|-----o--------------|
                MT|-------------O------| MT|-------------O------| MT|-------------O------| MT|-------------O------|
                BD|--------------------| BD|--------------------| BD|--------------------| BD|--------------------|
                """,

                """
                e|------------8-------| e|------------8-------| e|------------8-------| e|------------8-------|
                B|---3----------------| B|---3----------------| B|---3----------------| B|---3----------------|
                G|--------------------| G|--------------------| G|--------------------| G|--------------------|
                D|-----5--------------| D|-----5--------------| D|-----5--------------| D|-----5--------------|
                """,

        };
        for (String measureString : measureStrings) {
            String[] repeatMeasures = {
                    "3/4\n"+measureString,
            };

            for (String repeatMeasure : repeatMeasures) {
                Score score = new Score(repeatMeasure);
                assertEquals(1, score.measureCollectionList.size(), "one measure was expected but found "+score.measureCollectionList.size()+".");
                Measure measure = score.getMeasure(1);
                assertTrue(measure.isRepeatEnd());
                assertTrue(measure.isRepeatStart());
                List<Note> noteList = measure.getSortedNoteList();
                assertEquals(3, noteList.size(), "three notes were expected in the following measure, but found "+noteList.size()+".");
                Matcher matcher = Pattern.compile("<words[^>]*>[^<0-9]*"+5+"[^<0-9]*</words>").matcher(Parser.parse(score));
                assertTrue(matcher.find(), "repeat count not properly detected and applied");
            }
        }

    }

}
