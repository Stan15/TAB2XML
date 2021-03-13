package converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import converter.measure.Measure;
import custom_exceptions.InvalidScoreTypeException;
import custom_exceptions.MixedScoreTypeException;
import custom_exceptions.TXMLException;
import models.Part;
import models.ScorePartwise;
import models.part_list.PartList;
import models.part_list.ScoreInstrument;
import models.part_list.ScorePart;

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
    public static String STRICT_TYPE;
    public static int DEFAULT_BEAT_TYPE = 4;
    public static int DEFAULT_BEAT_COUNT = 4;
    public static int GLOBAL_DIVISIONS = 1;

    public Score(String rootString) {
        Measure.GLOBAL_MEASURE_COUNT = 0;
        this.rootString = rootString;
        this.rootStringFragments = this.getStringFragments(rootString);
        this.measureCollectionList = this.createMeasureCollectionList(this.rootStringFragments);
        if (STRICT_TYPE==null)
            STRICT_TYPE = "";

        // TODO apply instructions (like time signature for specific measures) here. the time signature for each measure has to be set for the following code to be correct

        GLOBAL_DIVISIONS = getDivisions();
        setDurations();
    }

    public Score(String rootString, String strictType) {
        this(rootString);
        STRICT_TYPE = strictType;
    }

    public int getDivisions() {
        int divisions = 0;
        for (MeasureCollection msurCollection : this.measureCollectionList) {
            divisions = Math.max(divisions,  msurCollection.getDivisions());
        }

        return divisions;
    }

    public void setDurations() {
        for (MeasureCollection msurCollection : this.measureCollectionList) {
            msurCollection.setDurations();
        }
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

        boolean isFirstCollection = true;
        for (Map.Entry<Integer, String> fragment : stringFragments.entrySet()) {
            List<MeasureCollection> msurCollectionSubList = MeasureCollection.getInstances(fragment.getValue(), fragment.getKey(), isFirstCollection);
            isFirstCollection = false;
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
    private LinkedHashMap<Integer, String> getStringFragments(String rootStr) {
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

    public ScorePartwise getModel() throws TXMLException {
        boolean isGuitar;
        boolean isDrum = false;

        if (STRICT_TYPE.equals("guitar"))
            isGuitar = true;
        else if (STRICT_TYPE.equals("drum"))
            isDrum = true;
        else {
            isGuitar = this.isGuitar(false);
            isDrum = this.isDrum(false);
            if (isDrum && isGuitar) {
                isDrum = this.isDrum(true);
                isGuitar = this.isGuitar(true);
            }
            if (isDrum && isGuitar && STRICT_TYPE.isEmpty())
                throw new MixedScoreTypeException("A score must be only of one type");
            if (!isDrum && !isGuitar)
                throw new InvalidScoreTypeException("The type of this score could not be detected. Specify its type or fix the error in the text input.");
        }

        List<models.measure.Measure> measures = new ArrayList<>();
        for (MeasureCollection measureCollection : this.measureCollectionList) {
            measures.addAll(measureCollection.getMeasureModels());
        }
        Part part = new Part("P1", measures);
        List<models.Part> parts = new ArrayList<>();
        parts.add(part);

        PartList partList;
        if (isDrum)
            partList = this.getDrumPartList();
        else
            partList = this.getGuitarPartList();

        ScorePartwise scorePartwise = new ScorePartwise("3.1", partList, parts);
        return scorePartwise;
    }

    private PartList getDrumPartList() {
        List<ScorePart> scoreParts = new ArrayList<>();
        ScorePart scorePart = new ScorePart("P1", "Drumset");
        List<ScoreInstrument> scoreInstruments = new ArrayList<>();

        scoreInstruments.add(new ScoreInstrument("P1-I36", "Bass Drum 1"));
        scoreInstruments.add(new ScoreInstrument("P1-I37", "Bass Drum 2"));
        scoreInstruments.add(new ScoreInstrument("P1-I38", "Side Stick"));
        scoreInstruments.add(new ScoreInstrument("P1-I39", "Snare"));
        scoreInstruments.add(new ScoreInstrument("P1-I42", "Low Floor Tom"));
        scoreInstruments.add(new ScoreInstrument("P1-I43", "Closed Hi-Hat"));
        scoreInstruments.add(new ScoreInstrument("P1-I44", "High Floor Tom"));
        scoreInstruments.add(new ScoreInstrument("P1-I45", "Pedal Hi-Hat"));
        scoreInstruments.add(new ScoreInstrument("P1-I46", "Low Tom"));
        scoreInstruments.add(new ScoreInstrument("P1-I47", "Open Hi-Hat"));
        scoreInstruments.add(new ScoreInstrument("P1-I48", "Low-Mid Tom"));
        scoreInstruments.add(new ScoreInstrument("P1-I49", "Hi-Mid Tom"));
        scoreInstruments.add(new ScoreInstrument("P1-I50", "Crash Cymbal 1"));
        scoreInstruments.add(new ScoreInstrument("P1-I51", "High Tom"));
        scoreInstruments.add(new ScoreInstrument("P1-I52", "Ride Cymbal 1"));
        scoreInstruments.add(new ScoreInstrument("P1-I53", "Chinese Cymbal"));
        scoreInstruments.add(new ScoreInstrument("P1-I54", "Ride Bell"));
        scoreInstruments.add(new ScoreInstrument("P1-I55", "Tambourine"));
        scoreInstruments.add(new ScoreInstrument("P1-I56", "Splash Cymbal"));
        scoreInstruments.add(new ScoreInstrument("P1-I57", "Cowbell"));
        scoreInstruments.add(new ScoreInstrument("P1-I58", "Crash Cymbal 2"));
        scoreInstruments.add(new ScoreInstrument("P1-I60", "Ride Cymbal 2"));
        scoreInstruments.add(new ScoreInstrument("P1-I64", "Open Hi Conga"));
        scoreInstruments.add(new ScoreInstrument("P1-I65", "Low Conga"));

        scorePart.setScoreInstruments(scoreInstruments);
        scoreParts.add(scorePart);

        return new PartList(scoreParts);
    }

    private PartList getGuitarPartList() {
        List<ScorePart> scoreParts = new ArrayList<>();
        scoreParts.add(new ScorePart("P1", "Classical Guitar"));
        return new PartList(scoreParts);
    }

    public boolean isGuitar(boolean strictCheck) {
        for (MeasureCollection msurCollection : this.measureCollectionList) {
            if (!msurCollection.isGuitar(strictCheck))
                return false;
        }
        return true;
    }

    public boolean isDrum(boolean strictCheck) {
        for (MeasureCollection msurCollection : this.measureCollectionList) {
            if (!msurCollection.isDrum(strictCheck))
                return false;
        }
        return true;
    }
}
