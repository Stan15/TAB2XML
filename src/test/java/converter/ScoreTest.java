package converter;

import converter.measure.Measure;
import converter.note.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.Parser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScoreTest {

    @BeforeEach
    void init() {
        Score.INSTRUMENT_MODE = Instrument.AUTO;
    }

    /**
     * Test for Score Class.
     * Testing the method Score.getStringFragments()
     * Which should break down any give string into segments where each segment
     * is separated by a blank line.
     */
    @Test
    void test_Score_getStringFragments() {
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
        String[] inlineRepeatMeasures = {
                """
                e||--------------------5|
                B||---3----------------||
                G||*------------------*||
                D||*----5-------------*||
                A||-------------8------||
                E||--------------------||
                """,

                """
                e||--------------------5|
                B||*--3--------8------*||
                G||*------------------*||
                D||-----5--------------||
                """,

                """
                CC||--------------------5|
                HH||---x----------------||
                SD||*------------------*||
                HT||*----o-------------*||
                MT||-------------o------||
                BD||--------------------||
                """
        };

        for (String inlineRepeatMeasure : inlineRepeatMeasures) {
            Score score = new Score(inlineRepeatMeasure);
            assertEquals(1, score.measureCollectionList.size(), "one measure was expected but found " + score.measureCollectionList.size() + ".");
            Measure measure = score.getMeasure(1);
            assertTrue(measure.isRepeatEnd());
            assertTrue(measure.isRepeatStart());
            List<Note> noteList = measure.getSortedNoteList();
            assertEquals(3, noteList.size(), "three notes were expected in the following measure, but found " + noteList.size() + "."
                    + "\nMeasure:\n" + inlineRepeatMeasure);
            Matcher matcher = Pattern.compile("<words[^>]*>[^<0-9]*" + expectedRepeatCount + "[^<0-9]*</words>").matcher(Parser.parse(score));
            assertTrue(matcher.find(), "repeat count not properly detected and applied");
        }
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
                //------------------------Guitar measures
                    //time signature altered at the start of measure group
                """
                3/4                                               2/4
                e|--------------------| e|--------------------| e|--------------------| e|--------------------|
                B|---3----------------| B|---3----------------| B|---3----------------| B|---3----------------|
                G|--------------------| G|--------------------| G|--------------------| G|--------------------|
                D|-----5--------------| D|-----5--------------| D|-----5--------------| D|-----5--------------|
                A|-------------8------| A|-------------8------| A|-------------8------| A|-------------8------|
                E|--------------------| E|--------------------| E|--------------------| E|--------------------|
                """,
                    //altered in the middle / altered in the end
                """
                                            3/4                                          2/4
                e|--------------------| e|--------------------| e|--------------------| e|--------------------|
                B|---3----------------| B|---3----------------| B|---3----------------| B|---3----------------|
                G|--------------------| G|--------------------| G|--------------------| G|--------------------|
                D|-----5--------------| D|-----5--------------| D|-----5--------------| D|-----5--------------|
                A|-------------8------| A|-------------8------| A|-------------8------| A|-------------8------|
                E|--------------------| E|--------------------| E|--------------------| E|--------------------|
                """,
                    //one altered immediately after another
                """
                                        3/4                         2/4
                e|--------------------| e|--------------------| e|--------------------| e|--------------------|
                B|---3----------------| B|---3----------------| B|---3----------------| B|---3----------------|
                G|--------------------| G|--------------------| G|--------------------| G|--------------------|
                D|-----5--------------| D|-----5--------------| D|-----5--------------| D|-----5--------------|
                A|-------------8------| A|-------------8------| A|-------------8------| A|-------------8------|
                E|--------------------| E|--------------------| E|--------------------| E|--------------------|
                """,



                //-----------------------------Drums
                    //time signature altered at the start of measure group
                """
                3/4                                               2/4
                CC|--------------------| CC|--------------------| CC|--------------------| CC|--------------------|
                HH|---x----------------| HH|---x----------------| HH|---x----------------| HH|---x----------------|
                SD|--------------------| SD|--------------------| SD|--------------------| SD|--------------------|
                HT|-----o--------------| HT|-----o--------------| HT|-----o--------------| HT|-----o--------------|
                MT|-------------O------| MT|-------------O------| MT|-------------O------| MT|-------------O------|
                BD|--------------------| BD|--------------------| BD|--------------------| BD|--------------------|
                """,
                    //altered in the middle / altered in the end
                """
                                            3/4                                             2/4
                CC|--------------------| CC|--------------------| CC|--------------------| CC|--------------------|
                HH|---x----------------| HH|---x----------------| HH|---x----------------| HH|---x----------------|
                SD|--------------------| SD|--------------------| SD|--------------------| SD|--------------------|
                HT|-----o--------------| HT|-----o--------------| HT|-----o--------------| HT|-----o--------------|
                MT|-------------O------| MT|-------------O------| MT|-------------O------| MT|-------------O------|
                BD|--------------------| BD|--------------------| BD|--------------------| BD|--------------------|
                """,
                    //one altered immediately after another
                """
                                            3/4                         2/4
                CC|--------------------| CC|--------------------| CC|--------------------| CC|--------------------|
                HH|---x----------------| HH|---x----------------| HH|---x----------------| HH|---x----------------|
                SD|--------------------| SD|--------------------| SD|--------------------| SD|--------------------|
                HT|-----o--------------| HT|-----o--------------| HT|-----o--------------| HT|-----o--------------|
                MT|-------------O------| MT|-------------O------| MT|-------------O------| MT|-------------O------|
                BD|--------------------| BD|--------------------| BD|--------------------| BD|--------------------|
                """,



                //---------------------------------Bass
                //time signature altered at the start of measure group
                """
                3/4                                               2/4
                e|------------8-------| e|------------8-------| e|------------8-------| e|------------8-------|
                B|---3----------------| B|---3----------------| B|---3----------------| B|---3----------------|
                G|--------------------| G|--------------------| G|--------------------| G|--------------------|
                D|-----5--------------| D|-----5--------------| D|-----5--------------| D|-----5--------------|
                """,
                    //altered in the middle / altered in the end
                """
                                            3/4                                             2/4
                e|------------8-------| e|------------8-------| e|------------8-------| e|------------8-------|
                B|---3----------------| B|---3----------------| B|---3----------------| B|---3----------------|
                G|--------------------| G|--------------------| G|--------------------| G|--------------------|
                D|-----5--------------| D|-----5--------------| D|-----5--------------| D|-----5--------------|
                """,
                    //one altered immediately after another
                """
                                            3/4                         2/4
                e|------------8-------| e|------------8-------| e|------------8-------| e|------------8-------|
                B|---3----------------| B|---3----------------| B|---3----------------| B|---3----------------|
                G|--------------------| G|--------------------| G|--------------------| G|--------------------|
                D|-----5--------------| D|-----5--------------| D|-----5--------------| D|-----5--------------|
                """,
        };

        Score.DEFAULT_BEAT_TYPE = 4;
        Score.DEFAULT_BEAT_COUNT = 4;
        String[][] expectedTimeSigGroups = {
                {"3/4", "3/4", "2/4", "2/4"},
                {"4/4", "3/4", "3/4", "2/4"},
                {"4/4", "3/4", "2/4", "2/4"}
        };

        for (int i=0; i<measureStrings.length; i++) {
            Score score = new Score(measureStrings[i]);
            List<Measure> measureList = score.getMeasureList();
            String[] expectedTimeSigs = expectedTimeSigGroups[i%3];
            for (int j=0; j<measureList.size(); j++) {
                Measure measure = measureList.get(j);
                assertTrue(j<expectedTimeSigs.length, "More measures were detected in the following group than was expected.\nMeasure Group:\n"+measureStrings[i]);
                String expectedTimeSig = expectedTimeSigs[j];
                String actualTimeSig = measure.getBeatCount()+"/"+measure.getBeatType();
                assertEquals(expectedTimeSig, actualTimeSig,
                        "Measure "+j+" in the following measure group was expected to have time signature "+expectedTimeSig+", but time signature "+actualTimeSig+ " was found."
                        +"\nMeasure Group:\n"+measureStrings[i]);
            }
        }

    }

}
