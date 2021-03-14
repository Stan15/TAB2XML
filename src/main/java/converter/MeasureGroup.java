package converter;

import converter.instruction.Instruction;
import converter.measure.DrumMeasure;
import converter.measure.GuitarMeasure;
import converter.measure.Measure;
import converter.measure_line.MeasureLine;
import utility.Patterns;
import utility.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeasureGroup implements ScoreComponent {

    //                           a measure line at start of line(with name)          zero or more middle measure lines       (optional |'s and spaces then what's ahead is end of line)
    public static String LINE_PATTERN = "("+MeasureLine.PATTERN_SOL          +          MeasureLine.PATTERN_MIDL+"*"    +   "("+ Patterns.DIVIDER+"*"+Patterns.WHITESPACE+"*)"     +  ")";
    public List<Integer> positions = new ArrayList<>();
    private List<String> lines = new ArrayList<>();
    public List<Measure> measureList;
    public List<Instruction> instructionList;
    boolean isFirstGroup;

    /**
     * Creates a MeasureGroup object from a List of Strings which represent the lines in the measure group
     * @param origin a List<String> containing the lines which are meant to represent a MeasureGroup. Each String in
     *              "origin" begins with a tag indicating the index at which it is positioned in the root string from
     *               which it was extracted (i.e Score.ROOT_STRING). (i.e "[startIdx]stringContent" )
     *               "origin" is guaranteed to be a valid **representation** of a MeasureGroup as it is only instantiated
     *               from the MeasureCollection class, which has to be made up of measure groups before it can be constructed
     *               (look at MeasureCollection.getInstance() and MeasureCollection.PATTERN). However, though it is
     *               guaranteed to be a valid representation of a measure group(i.e it looks like a measure group), the
     *               measure group which it is representing is not guaranteed to be valid itself.
     */
    public MeasureGroup(List<String> origin, boolean isFirstGroup) {
        this.isFirstGroup = isFirstGroup;
        for (String lineWithTag : origin) {
            Matcher tagMatcher = Pattern.compile("^\\[[0-9]+\\]").matcher(lineWithTag);
            tagMatcher.find();
            int startIdx = Integer.parseInt(tagMatcher.group().replaceAll("[\\[\\]]",""));
            String line = lineWithTag.substring(tagMatcher.end());

            this.positions.add(startIdx);
            this.lines.add(line);
        }
        this.measureList = this.createMeasureList(this.lines, this.positions);
        this.instructionList = new ArrayList<>();
    }


    /**
     * Creates a List of Measure objects from the provided string representation of a MeasureGroup.
     * These Measure objects are not guaranteed to be valid. you can find out if all the Measure
     * objects in this MeasureGroup are actually valid by calling the MeasureGroup().validate() method.
     * @param measureGroupLines A List (parallel with "positions") that contains the lines of the measure group which is to
     *                          be split into separate measures
     * @param positions A parallel List (parallel with "measureGroupLines") that contains the index at which the
     *                  corresponding measure group line in "measureGroupLines" can be found in the root string from which
     *                  it was derived (i.e Score.ROOT_STRING)
     * @return A List of Measures derived from their String representation, "measureGroupLines".
     */
    private List<Measure> createMeasureList(List<String> measureGroupLines, List<Integer> positions){
        List<Measure> measureList = new ArrayList<>();

        //setting up two parallel arrays with the information of each measure separated

        //A list of measures where each measure is represented by a list of its lines
        List<List<String>> measureStringList = new ArrayList<>();
        //A list of measures where each measure is represented by a list of the positions of its lines(more specifically,
        // the insides of each measure line). the position of
        // a line is its index in the root string from where it is derived (i.e Score.ROOT_STRING)
        List<List<Integer>> measurePositionsList = new ArrayList<>();
        //A list of measures where each measure is represented by list of the names of its lines
        List<List<String[]>> measureNamesList = new ArrayList<>();

        for (int i=0; i<measureGroupLines.size(); i++) {
            String measureGroupLine = measureGroupLines.get(i);
            int measureGroupStartIdx = positions.get(i);
            String[] lineName = MeasureLine.nameOf(measureGroupLine, measureGroupStartIdx);

            int measureCount = 0;
            Matcher measureInsidesMatcher = Pattern.compile(MeasureLine.INSIDES_PATTERN).matcher(measureGroupLine);
            while (measureInsidesMatcher.find()) {
                measureCount++;
                String measureLineString = measureInsidesMatcher.group();
                int measurePosition = measureGroupStartIdx+measureInsidesMatcher.start();    //the starting position of the insides of this measure in the root string Score.ROOT_STRING

                if (measureStringList.size()<measureCount) {
                    measureStringList.add(new ArrayList<>());
                    measurePositionsList.add(new ArrayList<>());
                    measureNamesList.add(new ArrayList<>());
                }

                //get the particular measure we are interested in and add this line to its list of lines
                List<String> measureLines = measureStringList.get(measureCount-1);  //-1 cuz of zero indexing
                List<Integer> measurePositions = measurePositionsList.get(measureCount-1);
                List<String[]> measureNames = measureNamesList.get(measureCount-1);
                measureLines.add(measureLineString);
                measurePositions.add(measurePosition);
                measureNames.add(lineName);
            }
        }
        boolean isFirstMeasureInGroup = true;
        for (int i=0; i<measureStringList.size(); i++) {
            List<String> measureLineList = measureStringList.get(i);
            List<Integer> measureLinePositionList = measurePositionsList.get(i);
            List<String[]> measureLineNameList = measureNamesList.get(i);

            measureList.add(Measure.from(measureLineList, measureLineNameList, measureLinePositionList, isFirstMeasureInGroup));
            isFirstMeasureInGroup = false;
        }
        return measureList;
    }

    /**
     * Validates if all Measure objects aggregated in this MeasureGroup have the same number of measure lines. It also
     * validates that all its aggregate Measure objects are an instance of the same type of Measure class (i.e they're all
     * GuitarMeasure objects or all DrumMeasure objects). Finally, it validates all its aggregates i.e all Measure objects
     * and Instruction objects that it aggregates. It stops evaluation at the first aggregated object which fails validation.
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

        //--------------Validating yourself--------------------------
        //making sure all measures in this measure group have the same number of lines
        boolean hasEqualMeasureLineCount = true;
        StringBuilder failPoints = new StringBuilder();
        int measureLineCount = 0;
        for (Measure measure : this.measureList) {
            if (measureLineCount==0)
                measureLineCount = measure.lineCount;
            else if(measure.lineCount!=measureLineCount) {
                hasEqualMeasureLineCount = false;
                if (!failPoints.isEmpty())
                    failPoints.append(";");
                failPoints.append(measure.getLinePositions());
            }
        }

        if (!hasEqualMeasureLineCount) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "All measures in a measure group must have the same number of lines");
            response.put("positions", failPoints.toString());
            response.put("priority", "2");
            result.add(response);
        }

        boolean hasGuitarMeasures = true;
        boolean hasDrumMeasures = true;
        for (Measure measure : this.measureList) {
            hasGuitarMeasures &= measure instanceof GuitarMeasure;
            hasDrumMeasures &= measure instanceof DrumMeasure;
        }
        if (!(hasGuitarMeasures || hasDrumMeasures)) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "All measures in a measure group must be of the same type (i.e. all guitar measures or all drum measures)");
            response.put("positions", this.getLinePositions());
            response.put("priority", "2");
            result.add(response);
        }

        //--------------Validate your aggregates (only if you're valid)-------------------
        if (!result.isEmpty()) return result;

        for (Measure measure : this.measureList) {
            result.addAll(measure.validate());
        }
        for (Instruction instruction : this.instructionList) {
            result.addAll(instruction.validate());
        }

        return result;
    }

    /**
     * Creates a string representation of the index position range of each line making up this MeasureGroup instance,
     * where each index position range describes the location where the lines of this MeasureGroup can be found in the
     * root string from which it was derived (i.e Score.ROOT_STRING)
     * @return a String representing the index range of each line in this MeasureGroup, formatted as follows:
     * "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    private String getLinePositions() {
        StringBuilder linePositions = new StringBuilder();
        for (int i=0; i<this.lines.size(); i++) {
            int startIdx = this.positions.get(i);
            int endIdx = startIdx+this.lines.get(i).length();
            if (!linePositions.isEmpty())
                linePositions.append(";");
            linePositions.append("["+startIdx+","+endIdx+"]");
        }
        return linePositions.toString();
    }

    public List<models.measure.Measure> getMeasureModels() {
        List<models.measure.Measure> measureModels = new ArrayList<>();
        for (Measure measure : this.measureList) {
            measureModels.add(measure.getModel());
        }
        return measureModels;
    }

    public boolean isGuitar(boolean strictCheck) {
        for (Measure measure : this.measureList) {
            if (!measure.isGuitar(strictCheck))
                return false;
        }
        return true;
    }

    public boolean isDrum(boolean strictCheck) {
        for (Measure measure : this.measureList) {
            if (!measure.isDrum(strictCheck))
                return false;
        }
        return true;
    }

    public int getDivisions() {
        int divisions = 0;
        for (Measure measure : this.measureList) {
            divisions = Math.max(divisions,  measure.getDivisions());
        }

        return divisions;
    }

    public void setDurations() {
        for (Measure measure : this.measureList) {
            measure.setDurations();
        }
    }

    public List<Measure> getMeasureList() {
        return this.measureList;
    }

    public Range getRelativeRange() {
        if (this.lines.isEmpty()) return null;
        int position = this.positions.get(0);
        int relStartPos = position-Score.ROOT_STRING.substring(0,position).lastIndexOf("\n");
        int relEndPos = relStartPos + this.lines.get(0).length();
        return new Range(relStartPos, relEndPos);
    }
}