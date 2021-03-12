package converter.measure;

import converter.measure_line.DrumMeasureLine;
import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;
import converter.note.Note;

import java.util.*;

public abstract class Measure {
    public static int GLOBAL_MEASURE_COUNT;
    protected int measureCount;
    int beatCount = 4;
    int beatType = 4;
    double divisions = 1;
    List<String> lines;
    List<String[]> lineNamesAndPositions;
    public int lineCount;
    List<Integer> positions;
    public List<MeasureLine> measureLineList;
    boolean isFirstMeasure;
    boolean isLastMeasure;
    List<Note> sortedNoteList;

    public Measure(List<String> lines, List<String[]> lineNamesAndPositions, List<Integer> linePositions, boolean isFirstMeasure) {
        this.measureCount = GLOBAL_MEASURE_COUNT;
        GLOBAL_MEASURE_COUNT++;
        this.lines = lines;
        this.lineCount = this.lines.size();
        this.lineNamesAndPositions = lineNamesAndPositions;
        this.positions = linePositions;
        this.isFirstMeasure = isFirstMeasure;
    }

    /**
     * Creates a List of MeasureLine objects from the provided string representation of a Measure.
     * These MeasureLine objects are not guaranteed to be valid. you can find out if all the Measure
     * objects in this MeasureGroup are actually valid by calling the Measure().validate() method.
     * @param lines a List of Strings where each String represents a line of the measure. It is a parallel list with lineNames and linePositions
     * @param namesAndPosition a List of Strings where each String represents the name of a line of the measure. It is a parallel list with lines and linePositions
     * @param linePositions a List of Strings where each String represents the starting index of a line of the measure,
     *                      where a starting index of a line is the index where the line can be found in the root string,
     *                      Score.ROOT_STRING, from where it was derived. It is a parallel list with lineNames and lines
     * @return A list of MeasureLine objects. The concrete class type of these MeasureLine objects is determined
     * from the input String lists(lines and lineNames), and they are not guaranteed to all be of the same type.
     */
    protected List<MeasureLine> createMeasureLineList(List<String> lines, List<String[]> namesAndPosition, List<Integer> linePositions) {
        List<MeasureLine> measureLineList = new ArrayList<>();
        for (int i=0; i<lines.size(); i++) {
            String line = lines.get(i);
            String[] nameAndPosition = namesAndPosition.get(i);
            int position = linePositions.get(i);
            measureLineList.add(MeasureLine.from(line, nameAndPosition, position));
        }
        return measureLineList;
    }

    /**
     * Creates an instance of the abstract Measure class whose concrete type is either GuitarMeasure or DrumMeasure, depending
     * on if the features of the input String Lists resemble a drum measure or a Guitar measure(this is determined by the
     * MeasureLine.isGuitar() and MeasureLine.isDrum() methods). If its features could not be deciphered or it has features
     * of both guitar and drum features, it defaults to creating a GuitarMeasure object and further error checking can
     * be done by calling GuitarMeasure().validate() on the object.
     * @param lineList A list of the insides of each measure lines that makes up this measure (without the line names) (parallel list with the other two List parameters)
     * @param lineNameList A list of the names of each the measure lines that makes up this measure (parallel list with the other two List parameters)
     * @param linePositionList A list of the positions of the insides of each of the measure lines that make up this (parallel list with the other two List parameters)
     *                         measure, where a line's position is the index at which the line is located in the root
     *                         String from which it was derived (Score.ROOT_STRING)
     * @param isFirstMeasure specifies weather this measure is the first one in its measure group. (useful to know, so we only add the xml measure attributes to the first measure)
     *
     * @return A Measure object which is either of type GuitarMeasure if the measure was understood to be a guitar
     * measure, or of type DrumMeasure if the measure was understood to be of type DrumMeasure
     */
    public static Measure from(List<String> lineList, List<String[]> lineNameList, List<Integer> linePositionList, boolean isFirstMeasure) {
        boolean isGuitarMeasure = true;
        boolean isDrumMeasure = true;
        for (int i=0; i<lineList.size(); i++) {
            String line = lineList.get(i);
            String[] nameAndPosition = lineNameList.get(i);
            isGuitarMeasure &= MeasureLine.isGuitarName(nameAndPosition[0]);
            isDrumMeasure &= MeasureLine.isDrumName(nameAndPosition[0]);
        }
        if (isDrumMeasure && !isGuitarMeasure)
            return new DrumMeasure(lineList, lineNameList, linePositionList, isFirstMeasure);
        else if(isGuitarMeasure && !isDrumMeasure)
            return new GuitarMeasure(lineList, lineNameList, linePositionList, isFirstMeasure);
        else
            return new GuitarMeasure(lineList, lineNameList, linePositionList, isFirstMeasure); //default value if any of the above is not true (i.e when the measure type can't be understood or has components belonging to both instruments)
    }



    /**
     * Validates if all MeasureLine objects which this Measure object aggregates are instances of the same concrete
     * MeasureLine Class (i.e they're all GuitarMeasureLine instances or all DrumMeasureLine objects). It does not
     * validate its aggregated objects. That job is left up to its concrete classes (this is an abstract class)
     * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
     * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
     * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
     * found in the root string from which it was derived (i.e Score.ROOT_STRING).
     * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public List<HashMap<String, String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>();

        boolean hasGuitarMeasureLines = true;
        boolean hasDrumMeasureLines = true;
        for (MeasureLine measureLine : this.measureLineList) {
            hasGuitarMeasureLines &= measureLine instanceof GuitarMeasureLine;
            hasDrumMeasureLines &= measureLine instanceof DrumMeasureLine;
        }
        if (!(hasGuitarMeasureLines || hasDrumMeasureLines)) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "All measure lines in a measure must be of the same type (i.e. all guitar measure lines or all drum measure lines)");
            response.put("positions", this.getLinePositions());
            response.put("priority", "1");
            result.add(response);
        }

        return result;
    }

    /**
     * Creates a string representation of the index position range of each line making up this Measure instance,
     * where each index position range describes the location where the lines of this Measure can be found in the
     * root string from which it was derived (i.e Score.ROOT_STRING)
     * @return a String representing the index range of each line in this Measure, formatted as follows:
     * "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public String getLinePositions() {
        StringBuilder linePositions = new StringBuilder();
        for (int i=0; i<this.lines.size(); i++) {
            int startIdx = this.positions.get(i);
            int endIdx = startIdx+this.lines.get(i).length();
            if (!linePositions.isEmpty())
                linePositions.append(";");
            linePositions.append("[");
            linePositions.append(startIdx);
            linePositions.append(",");
            linePositions.append(endIdx);
            linePositions.append("]");
        }
        return linePositions.toString();
    }

    protected void setChords() {
        for (int i=1; i<this.sortedNoteList.size(); i++) {
            Note previousNote = this.sortedNoteList.get(i-1);
            Note currentNote = this.sortedNoteList.get(i);
            if (previousNote.distance==currentNote.distance)
                currentNote.startsWithPreviousNote = true;
        }
    }

    public List<Note> getSortedNoteList() {
        List<Note> sortedNoteList = new ArrayList<>();
        for (MeasureLine line : this.measureLineList) {
            GuitarMeasureLine guitarMline = (GuitarMeasureLine) line;
            for (Note note : guitarMline.noteList) {
                List<HashMap<String, String>> errors = note.validate();
                boolean criticalError = false;
                for (HashMap<String, String> error : errors) {
                    if (Integer.parseInt(error.get("priority")) <= 1) {
                        criticalError = true;
                        break;
                    }
                }
                if (!criticalError)
                    sortedNoteList.add(note);
            }
        }
        Collections.sort(sortedNoteList);
        return sortedNoteList;
    }

    public boolean isGuitar(boolean strictCheck) {
        for (MeasureLine measureLine : this.measureLineList) {
            if (!measureLine.isGuitar(strictCheck))
                return false;
        }
        return true;
    }

    public boolean isDrum(boolean strictCheck) {
        for (MeasureLine measureLine : this.measureLineList) {
            if (!measureLine.isDrum(strictCheck))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder stringOut = new StringBuilder();
        for (MeasureLine measureLine : this.measureLineList) {
            stringOut.append(measureLine.toString());
            stringOut.append("\n");
        }
        return stringOut.toString();
    }

    public abstract models.measure.Measure getModel();
}
