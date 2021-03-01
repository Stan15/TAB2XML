package converter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Score {
    public List<MeasureCollection> measureCollectionList;
    // Score.ROOT_STRING is only public for the JUnit tester to work. Its access modifier should be protected so that
    // it will not be changed by anything outside the converter package as the "position" instance variable of other
    // classes in this package (e.g MeasureLine) shows the position of the measure line in this String, thus they depend
    // on this String staying the same. It cannot be final as we will want to create different Score objects to convert
    // different Strings.
    public String rootString;
    public Map<Integer, String> rootStringFragments;

    public Score(String rootString) {
        this.rootString = rootString;
        this.rootStringFragments = this.getStringFragments(rootString);
        this.measureCollectionList = this.createMeasureCollectionList(this.rootStringFragments);
    }

    /**
     * Creates a List of MeasureCollection objects from the extracted fragments of a String.
     * These MeasureCollection objects are not guaranteed to be valid. you can find out if all the MeasureCollection
     * objects in this score are actually valid by calling the Score().validate() method.
     * @param stringFragments A Map which maps an Integer to a String, where the String is the broken up fragments of a
     *                        piece of text, and the Integer is the starting index at which the fragment starts in the
     *                        original text from which the fragments were derived.
     * @return a list of MeasureCollection objects.
     */
    private List<MeasureCollection> createMeasureCollectionList(Map<Integer, String> stringFragments) {
        List<MeasureCollection> msurCollectionList = new ArrayList<>();

        for (Map.Entry<Integer, String> fragment : stringFragments.entrySet()) {
            List<MeasureCollection> msurCollectionSubList = MeasureCollection.getInstances(fragment.getValue(), fragment.getKey());
            //it may be that the text is completely not understood in the slightest as a measure collection
            //and the MeasureCollection.getInstance() returns null
            for (MeasureCollection msurCollection : msurCollectionSubList)
                msurCollectionList.add(msurCollection);
        }
        return msurCollectionList;
    }

    /**
     * Breaks input text (at wherever it finds blank lines) up into smaller pieces to make further analysis of each
     * piece of text with regex more efficient
     * @param rootStr the string which is to be broken up into its fragments
     * @return an ordered map mapping the position of each broken up piece of text(Integer[startIndex, endIndex]) to the
     * actual piece of text (String)
     */
    public LinkedHashMap<Integer, String> getStringFragments(String rootStr) {
        LinkedHashMap<Integer, String> stringFragments = new LinkedHashMap<>();

        //finding the point where there is a break between two pieces of text. (i.e a newline, then a blank line(a line containing nothing or just whitespace) then another newline is considered to be where there is a break between two pieces of text)
        Pattern textBreakPattern = Pattern.compile("(\\n[ ]*(?=\\n))+|$");
        Matcher textBreakMatcher = textBreakPattern.matcher(rootStr);

        int previousBreakEndIdx = 0;
        while(textBreakMatcher.find()) {
            String fragment = rootString.substring(previousBreakEndIdx,textBreakMatcher.start());
            if (!fragment.strip().isEmpty()) {
                int position = previousBreakEndIdx;
                stringFragments.put(position, fragment);
            }
            previousBreakEndIdx = textBreakMatcher.end();
        }
        return stringFragments;
    }

    /** TODO modify this javadoc to reflect the new validation paradigm
     * Ensures that all the lines of the root string (the whole tablature file) is understood as multiple measure collections,
     * and if so, it validates all MeasureCollection objects it aggregates. It stops evaluation at the first aggregated object which fails validation.
     * TODO fix the logic. One rootString fragment could contain what is identified as multiple measures (maybe?) and another could be misunderstood so they cancel out and validation passes when it shouldn't
     * TODO maybe have a low priority validation error when there are no measures detected in the Score.
     * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
     * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
     * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
     * found in the root string from which it was derived (i.e Score.ROOT_STRING).
     * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public List<HashMap<String,String>> validate() {
        List<HashMap<String,String>> result = new ArrayList<>();

        StringBuilder errorRanges = new StringBuilder();

        int prevEndIdx = 0;
        for (MeasureCollection msurCollction : this.measureCollectionList) {
            String uninterpretedFragment = this.rootString.substring(prevEndIdx,msurCollction.position);
            if (!uninterpretedFragment.isBlank()) {
                if (!errorRanges.isEmpty()) errorRanges.append(";");
                errorRanges.append("["+prevEndIdx+","+(prevEndIdx+uninterpretedFragment.length())+"]");
            }

            prevEndIdx = msurCollction.endIndex;
        }

        String restOfDocument = this.rootString.substring(prevEndIdx);
        if (!restOfDocument.isBlank()) {
            if (!errorRanges.isEmpty()) errorRanges.append(";");
            errorRanges.append("["+prevEndIdx+","+(prevEndIdx+restOfDocument.length())+"]");
        }

        if (!errorRanges.isEmpty()) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "This text can't be understood.");
            response.put("positions", errorRanges.toString());
            response.put("priority", "3");
            result.add(response);
        }

        //--------------Validate your aggregates (regardless of if you're valid, as there is no validation performed upon yourself that preclude your aggregates from being valid)-------------------
        for (MeasureCollection colctn : this.measureCollectionList) {
            result.addAll(colctn.validate());
        }

        return result;
    }

    public String toXML() {
        StringBuilder scoreXML = new StringBuilder();
        scoreXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE score-partwise PUBLIC\n" +
                " \"-//Recordare//DTD MusicXML 3.1 Partwise//EN\"\n" +
                " \"http://www.musicxml.org/dtds/partwise.dtd\">\n");
        scoreXML.append("<score-partwise version=\"3.1\">\n" +
                " <part-list>\n" +
                " <score-part id=\"P1\">\n" +
                " <part-name>Music</part-name>\n" +
                " </score-part>\n" +
                " </part-list>\n" +
                " <part id=\"P1\">\n");
        for (MeasureCollection msurCollection : this.measureCollectionList) {
            for (MeasureGroup msurGroup : msurCollection.measureGroupList)
                scoreXML.append(msurGroup.toXML());
        }
        scoreXML.append("</part>\n" +
                "</score-partwise>");

        return scoreXML.toString();
    }
}
