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
    public static String ROOT_STRING;
    public Map<Integer, String> rootStringFragments;

    public Score(String rootString) {
        ROOT_STRING = rootString;
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
            MeasureCollection msurCollection = MeasureCollection.getInstance(fragment.getValue(), fragment.getKey());
            //it may be that the text is completely not understood in the slightest as a measure collection
            //and the MeasureCollection.getInstance() returns null
            if (msurCollection!=null)
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
    private LinkedHashMap<Integer, String> getStringFragments(String rootStr) {
        LinkedHashMap<Integer, String> stringFragments = new LinkedHashMap<>();

        Pattern textGroupPattern = Pattern.compile("([^\\n\\r]+[\\n\\r])+");
        Matcher textLineMatcher = textGroupPattern.matcher(rootStr);
        while(textLineMatcher.find()) {
            String fragment = ROOT_STRING.substring(textLineMatcher.start(), textLineMatcher.end());
            if (!fragment.strip().isEmpty()) {
                int position = textLineMatcher.start();
                stringFragments.put(position, fragment);
            }
        }
        return stringFragments;
    }

    /**
     * Ensures that all the lines of the root string (the whole tablature file) is understood as multiple measure collections,
     * and if so, it validates all MeasureCollection objects it aggregates.
     * TODO fix the logic. One rootString fragment could contain what is identified as multiple measures (maybe?) and another could be misunderatood so they cancel out and validation passes when it shouldn't
     * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
     * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
     * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
     * found in the root string from which it was derived (i.e Score.ROOT_STRING).
     * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public Map<String,String> validate() {
        HashMap<String, String> result = new HashMap<>();
        //------------Validating yourself--------------------
        //check if all the text in the root string is converted into measure collections. If not, then there is some
        // text that wasn't understood to be a measure collection
        if (this.rootStringFragments.size()!=this.measureCollectionList.size()) {
            result.put("success", "false");
            result.put("message", "Some text was not understood.");

            // we want to remove all the elements of rootStringFragments that were successfully understood to be
            // a measure collection, then we will be left with those that weren't understood. Now we can know exactly which
            // pieces of text were not understood.
            LinkedHashMap<Integer, String> rootStrFragmntsCopy = new LinkedHashMap<>();
            rootStrFragmntsCopy.putAll(rootStringFragments);
            for (MeasureCollection msurClctn: this.measureCollectionList) {
                rootStrFragmntsCopy.remove(msurClctn.position);
            }

            //get a list of positions to highlight red showing where the error applies.
            StringBuilder positions = new StringBuilder();
            Iterator<Integer> uninterpretableFragmentPositions = rootStrFragmntsCopy.keySet().iterator();

            while (uninterpretableFragmentPositions.hasNext()) {
                int startIdx = uninterpretableFragmentPositions.next();
                String fragment = this.rootStringFragments.get(startIdx);
                if (positions.isEmpty())
                    positions.append(";");
                positions.append("["+startIdx+","+startIdx+fragment.length()+"]");
            }
            result.put("positions", positions.toString());
            return result;
        }

        //--------------Validating your aggregates-------------------
        for (MeasureCollection colctn : this.measureCollectionList) {
            HashMap<String,String> response = colctn.validate();
            if (response.get("success").equals("false"))
                return response;
        }

        result.put("success", "true");
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
