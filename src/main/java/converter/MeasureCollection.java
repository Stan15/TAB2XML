package converter;

import com.sun.javafx.collections.ElementObservableListDecorator;
import converter.instruction.Instruction;
import utility.Patterns;
import utility.Range;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeasureCollection implements ScoreComponent {
    String origin;  //the string that was passed to the constructor upon the instantiation of this class
    int position;   //the index in Score.rootString at which the String "MeasureCollection().origin" is located
    int endIndex;
    List<MeasureGroup> measureGroupList;
    public static String PATTERN = createMeasureCollectionPattern();
    public static String LINE_PATTERN = createLinePattern();
    boolean isFirstCollection;
    private List<Instruction> instructionList = new ArrayList<>();

    /**
     * Static factory method which verifies that the input String "origin" can be recognised as a measure collection
     * before instantiation.
     * @param origin The String representation of a measure collection (a collection of measure groups that happen to be
     *              on the same line. e.g P|----|---|  Hh|----|----| are drum measures which are on the same line) along with instructions pertaining to them
     * @param position the start position of the input String "origin" in Score.ROOT_STRING, from where it was extracted
     * @return a MeasureCollection object if the input String "origin" is properly recognised to be a representation of
     * a measure group(regardless of if the measure group it is representing is valid or not). null otherwise
     */
    public static List<MeasureCollection> getInstances(String origin, int position, boolean isFirstCollection) {
        List<MeasureCollection> msurCollectionList = new ArrayList<>();

        Matcher matcher = Pattern.compile(MeasureCollection.PATTERN).matcher(origin);
        while (matcher.find())
            msurCollectionList.add(new MeasureCollection(matcher.group(), position+matcher.start(), isFirstCollection));

        return msurCollectionList;
    }

    private MeasureCollection(String origin, int position, boolean isFirstCollection) {
        this.origin = origin;
        this.position = position;
        this.endIndex = position+this.origin.length();
        this.isFirstCollection = isFirstCollection;
        createMeasureGroupAndInstructionList(origin);
        for (Instruction instruction : this.instructionList)
            instruction.applyTo(this);
    }

    /**
     * Creates a List of MeasureGroup objects from the provided string representation of a measure group collection.
     * These MeasureGroup objects are not guaranteed to be valid. you can find out if all the MeasureGroup
     * objects in this MeasureCollection are actually valid by calling the MeasureCollection().validate() method.
     * @param measureGroupCollectionString The String representation of the MeasureGroup objects to be created, beginning with
     *                             a String tag indicating the index at which the provided String begins in Score.rootString
     *                             (i.e "[startIdx]stringContent")
     * @return A List of MeasureGroup objects.
     */
    private List<MeasureGroup> createMeasureGroupList(String measureGroupCollectionString) {
        List<MeasureGroup> measureGroupList = new ArrayList<>();

        // separating the start index from the input String
        Matcher tagMatcher = Pattern.compile("^\\[[0-9]+\\]").matcher(measureGroupCollectionString);
        tagMatcher.find();
        int startIdx = Integer.parseInt(tagMatcher.group().replaceAll("[\\[\\]]",""));
        measureGroupCollectionString = measureGroupCollectionString.substring(tagMatcher.end());

        // I can split measureGroupCollectionString by newlines and then go through each line and create the measure groups,
        // but splitting by newlines will make us lose the information of what index each line is positioned at. We may be able
        // to figure the index out, but there will always be an uncertainty of +-1

        List<List<String>> measureGroupStringList = new ArrayList<>();

        //matches every line containing something
        Matcher lineContentMatcher = Pattern.compile("(?<=^|\\n)[^\\n]+(?=$|\\n)").matcher(measureGroupCollectionString);
        while (lineContentMatcher.find()) { // go through each line
            String line = lineContentMatcher.group();
            int lineStartIdx = startIdx+lineContentMatcher.start();

            if (!line.matches(MeasureCollection.LINE_PATTERN)) continue;

            Matcher measureGroupLineMatcher = Pattern.compile(MeasureGroup.LINE_PATTERN).matcher(line);

            int measureGroupCount = 0;   //the number of measure groups on this line
            while (measureGroupLineMatcher.find()) {
                measureGroupCount++;
                int measureGroupLineStartIdx = lineStartIdx + measureGroupLineMatcher.start();
                String measureGroupLine = "["+measureGroupLineStartIdx+"]"+measureGroupLineMatcher.group();
                if (measureGroupStringList.size()<measureGroupCount)
                    measureGroupStringList.add(new ArrayList<>());

                List<String> measureGroupLines = measureGroupStringList.get(measureGroupCount-1);    //-1 cuz of zero indexing.
                measureGroupLines.add(measureGroupLine);
            }
        }
        boolean isFirstGroup = true;
        for (List<String> measureGroupString : measureGroupStringList) {
            measureGroupList.add(new MeasureGroup(measureGroupString, isFirstGroup));
            isFirstGroup = false;
        }
        return measureGroupList;
    }

    /**
     * Separates the provided String representation of a MeasureCollection into Instructions, a measure group collection,
     * and Comments.
     * @param origin the String representation of a MeasureCollection from which the instructions, comments, and the
     *              measure group collection are extracted
     * @return a Map with the following mappings: measure-group-colleciton -> a list<String> containing a single
     * measure group collection; "instructions" -> a List<String> containing the instructions, and "comments" ->
     * containing a List<String> of the comments in the origin String. Each String stored in this Map begins with a tag
     * (i.e "[startIdx]stringContent" ) specifying the start index of the String in Score.rootString
     */
    private Map<String, List<String>> createMeasureGroupAndInstructionList(String origin) {
        Map<String, List<String>> componentsMap = new HashMap<>();
        List<String> measureGroupCollctn = new ArrayList<>();
        List<String> instructionList = new ArrayList<>();
        List<String> commentList = new ArrayList<>();
        componentsMap.put("measure-group-collection", measureGroupCollctn);
        componentsMap.put("instructions", instructionList);
        componentsMap.put("comments", commentList);

        //HashSet<Integer> identifiedComponents = new HashSet<>();
        List<Range> identifiedComponents = new ArrayList<>();       //to prevent the same thing being identified as two different components (e.g being identified as both a comment and an instruction)

        //extract the measure group collection and create the list of MeasureGroup objects with it
        Matcher matcher = Pattern.compile("((^|\\n)"+MeasureCollection.LINE_PATTERN+")+").matcher(origin);
        if(matcher.find()) { // we don't use while loop because we are guaranteed that there is going to be just one of this pattern in this.origin. Look at the static factory method and the createMeasureCollectionPattern method
            this.measureGroupList = this.createMeasureGroupList("[" + (this.position + matcher.start()) + "]" + matcher.group());
            identifiedComponents.add(new Range(matcher.start(), matcher.end()));
        }

        //extract the instruction
        matcher = Pattern.compile("((^|\\n)"+ Instruction.LINE_PATTERN+")+").matcher(origin);
        while(matcher.find()) {
            //first make sure that what was identified as one thing is not being identified as a different thing.
            Range instructionLineRange = new Range(matcher.start(), matcher.end());
            boolean continueWhileLoop = false;
            boolean isTopInstruction = true;
            for (Range range : identifiedComponents) {
                if (range.overlaps(instructionLineRange)) {
                    continueWhileLoop = true;
                    break;
                }
                if (range.getEnd()<=instructionLineRange.getStart())
                    isTopInstruction = false;
            }
            if (continueWhileLoop) continue;
            if (isTopInstruction)
                this.instructionList.addAll(Instruction.from(matcher.group(), this.position+matcher.start(), Instruction.TOP));
            else
                this.instructionList.addAll(Instruction.from(matcher.group(), this.position+matcher.start(), Instruction.BOTTOM));
            identifiedComponents.add(new Range(matcher.start(), matcher.end()));
        }

//        //extract the comments
//        matcher = Pattern.compile("((^|\\n)"+Patterns.COMMENT+")+").matcher(origin);
//        while(matcher.find()) {
//            if (identifiedComponents.contains(matcher.start())) continue;
//            commentList.add("["+(this.position+matcher.start())+"]"+matcher.group());
//            identifiedComponents.add(new Range(matcher.start(), matcher.end()));
//        }
        return componentsMap;
    }

    /**
     * Validates the aggregated MeasureGroup objects of this class. It stops evaluation at the first aggregated object
     * which fails validation.
     * TODO it might be better to not have it stop when one aggregated object fails validation, but instead have it
     *      validate all of them and return a List of all aggregated objects that failed validation, so the user knows
     *      all what is wrong with their tablature file, instead of having to fix one problem before being able to see
     *      what the other problems with their text file is.
     * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
     * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
     * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
     * found in the root string from which it was derived (i.e Score.ROOT_STRING).
     * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public List<HashMap<String, String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>();

        //--------------Validate your aggregates (only if you are valid)-------------------
        if (!result.isEmpty()) return result;

        for (MeasureGroup mGroup : this.measureGroupList) {
            result.addAll(mGroup.validate());
        }

        return result;
    }

    private static String createLinePattern() {
        return "("+ Patterns.WHITESPACE+"*("+MeasureGroup.LINE_PATTERN+Patterns.WHITESPACE+"*)+)";
    }

    /**
     * Creates the regex pattern for detecting a measure collection (i.e a collection of measure groups and their corresponding instructions  and comments)
     * @return a String regex pattern enclosed in brackets that identifies a measure collection pattern (the pattern also captures the newline right before the measure group collection)
     */
    private static String createMeasureCollectionPattern() {
        // zero or more instructions, followed by one or more measure group lines, followed by zero or more instructions
        return "((^|\\n)"+ Instruction.LINE_PATTERN+"+|"+Patterns.COMMENT+")*"          // 0 or more lines separated by newlines, each containing a group of instructions or comments
                + "("                                                                   // then the measure collection line, which is made of...
                +       "(^|\\n)"                                                           // a start of line or a new line
                +       MeasureCollection.createLinePattern()                               // a measure group line followed by whitespace, all repeated one or more times
                + ")+"                                                                  // the measure collection line I just described is repeated one or more times.
                + "(\\n"+ Instruction.LINE_PATTERN+"+)*";
    }

    public List<models.measure.Measure> getMeasureModels() {
        List<models.measure.Measure> measureModels = new ArrayList<>();
        for (MeasureGroup measureGroup : this.measureGroupList) {
            measureModels.addAll(measureGroup.getMeasureModels());
        }
        return measureModels;
    }

    public boolean isGuitar(boolean strictCheck) {
        for (MeasureGroup measureGroup : this.measureGroupList) {
            if (!measureGroup.isGuitar(strictCheck))
                return false;
        }
        return true;
    }

    public boolean isDrum(boolean strictCheck) {
        for (MeasureGroup measureGroup : this.measureGroupList) {
            if (!measureGroup.isDrum(strictCheck))
                return false;
        }
        return true;
    }

    public int getDivisions() {
        int divisions = 0;
        for (MeasureGroup measureGroup : this.measureGroupList) {
            divisions = Math.max(divisions,  measureGroup.getDivisions());
        }

        return divisions;
    }

    public void setDurations() {
        for (MeasureGroup measureGroup : this.measureGroupList) {
            measureGroup.setDurations();
        }
    }

    public List<MeasureGroup> getMeasureGroupList() {
        return this.measureGroupList;
    }
}
// TODO limit the number of consecutive whitespaces there are inside a measure
