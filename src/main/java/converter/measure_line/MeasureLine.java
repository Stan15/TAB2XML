package converter.measure_line;

import converter.note.Note;
import converter.Patterns;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MeasureLine {
    String line;
    String name;
    int position;
    public List<Note> noteList;

    protected MeasureLine(String line, String name, int position) {
        this.line = line;
        this.name = name;
        this.position = position;
        this.noteList = this.createNoteList(line, name, position);
    }


    /**
     * Creates a MeasureLine object from the provided string representation of the MeasureLine. The MeasureLine object
     * is either of type GuitarMeasureLine or DrumMeasureLine depending on if the features of the input Strings resembles
     * a guitar measure line or a drum measure line (this is determined by the MeasureLine.isGuitar() and MeasureLine.isDrum())
     * If it has features that neither belongs to GuitarMeasure nor DrumMeasure or has features shared by both, it defaults
     * to creating a GuitarMeasureLine object, and further error checking can be done by calling GuitarMeasureLine().validate()
     * on the object returned.
     * @param line the contents of the MeasureLine
     * @param name the name of the MeasureLine
     * @param position the index at which the contents of the measure line can be found in the root string from which it
     *                 was derived (i.e Score.ROOT_STRING)
     * @return a MeasureLine object derived from the information in the input Strings. Either of type GuitarMeasureLine
     * or DrumMeasureLine
     */
    public static MeasureLine from(String line, String name, int position) {
        boolean isGuitarLine = MeasureLine.isGuitar(line, name);
        boolean isDrumLine = MeasureLine.isDrum(line, name);
        if (isDrumLine && !isGuitarLine)
            return new DrumMeasureLine(line, name, position);
        else if(isGuitarLine && !isDrumLine)
            return new GuitarMeasureLine(line, name, position);
        else
            return new GuitarMeasureLine(line, name, position); //default value if any of the above is not true (i.e when the measure type can't be understood or has components belonging to both instruments)return null;
    }


    private List<Note> createNoteList(String line, String name, int position) {
        List<Note> noteList = new ArrayList<>();

        int dashCounter = 0;
        StringBuilder noteStrCollector = new StringBuilder();
        int startIdx = 0;
        for (int i = 0; i < this.line.length(); i++) {
            dashCounter++;
            if (line.charAt(i) == '-') { //accounts for each instance of dash
                if(!noteStrCollector.isEmpty()){
                    noteList.addAll(Note.from(noteStrCollector.toString().strip(), name, dashCounter, position+i));
                    noteStrCollector.setLength(0);
                }
                startIdx = 0;
            } else if (line.charAt(i) == '|') { //accounts for each instance of vertical bar
                if(!noteStrCollector.isEmpty()){
                    noteList.addAll(Note.from(noteStrCollector.toString().strip(), name, dashCounter, position+i));
                    noteStrCollector.setLength(0);
                }
                dashCounter = 0; //reset dash counter
                startIdx = 0;
            } else {
                if (startIdx==0)
                    startIdx = position+i;
                noteStrCollector.append(line.charAt(i));
            }
        }
        if(!noteStrCollector.isEmpty()){
            noteList.addAll(Note.from(noteStrCollector.toString().strip(), name, dashCounter, startIdx));
            noteStrCollector.setLength(0);
        }
        return noteList;
    }

    /**
     * TODO Validates this MeasureLine object by ensuring if the amount of whitespace contained in this measureline is not
     * above a certain percentage of the total length of the line, as this can lead to the program interpreting chords
     * and timings vastly different than the user expects. This method does not validate its aggregated Note objects.
     * That job is left up to its concrete GuitarMeasureLine and DrumMeasureLine classes.
     * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
     * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
     * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
     * found in the root string from which it was derived (i.e Score.ROOT_STRING).
     * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public HashMap<String, String> validate() {
        HashMap<String, String> result = new HashMap<>();
        result.put("success", "true");
        return result;
    }

    /**
     * TODO Make a more comprehensive test where you check if the line components belong go a drum
     * @param line
     * @param name
     * @return
     */
    public static boolean isDrum(String line, String name) {
        if (!DrumMeasureLine.NAME_SET.contains(name)) return false;
        return true;
    }

    /**
     * TODO Make a more comprehensive test where you check if the line components belong go a guitar
     * @param line
     * @param name
     * @return
     */
    public static boolean isGuitar(String line, String name) {
        if (!GuitarMeasureLine.NAME_SET.contains(name)) return false;
        return true;
    }



    //---------------------------------------------regex stuff----------------------------------------------------------

    public static String INSIDES_PATTERN = createInsidesPattern();
    // e------------ or |e---------------- or |e|-------------------- when it is the first measure of the measure group (start of line, SOL)
    public static String PATTERN_SOL = createMeasureNameSOLPattern() + createInsidesPattern();
    //|--------------------- when it is in between other measures (middle of line, MIDL)
    public static String PATTERN_MIDL = "("+"(\\|)+" + createInsidesPattern()+")";
    public static Set<String> NAME_SET = createLineNameSet();
    public static String COMPONENT_PATTERN = createLineComponentPattern();

    public static String nameOf(String measureLineStr) {
        Pattern measureLineNamePttrn = Pattern.compile(createMeasureNameExtractPattern());
        Matcher measureLineNameMatcher = measureLineNamePttrn.matcher(measureLineStr);
        measureLineNameMatcher.find();
        return measureLineNameMatcher.group().strip();
    }

    /**
     * a very general, very vague "inside a measure line" pattern. We want to be as general and vage as possible so that
     * we delay catching erroneous user input until we are able to pinpoint where the error is exactly. e.g. if this
     * pattern directly detects a wrong note here, a Note object will never be created. It will just tell the user the
     * measure line where the error is, not the precise note which caused the error.
     * This regex pattern does not capture the |'s surrounding the measure insides, but it may verify if it is surrounded
     * by |'s
     * @return the bracket-enclosed String regex pattern.
     */
    private static String createInsidesPattern() {
        // stuff like (2h7) or 8s3 or 12 or 4/2, etc. All the stuff that you find inside a measure line. Make this very
        // vague, so that it captures even erroneous component, as long as it generally "looks like" a proper measure
        // component. don't worry about balanced brackets and stuff like that here. read javadoc for this method for
        // explanation on why we are delaying failure
        String component = "[0-9./\\\\(\\)]";

        //                     behind it is (space or newline, followed by a measure name) or ("|")     then the line either starts with a -, or starts with a component followed by a -  then repeated zero or more times, (- or space, followed by a component)        then the rest of the un-captured spaces or -
        //                                                      |                                                                         |                                                                                                                                      |
        String measureInsides = "("  +  "(?<="+"([ \\n]"+createMeasureNamePattern()+")|"+"\\|)"        +                   "(([ ]*-)|("+component+"[ ]*-))"                         +                  "([ -]*"+component+")*"                                      +             "[ -]*" + ")";
        return measureInsides;
    }

    private static String createMeasureNameExtractPattern() {
        StringBuilder pattern = new StringBuilder();
        pattern.append("(?<=^\\|*)");
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append(createMeasureNamePattern());
        pattern.append("(?=-|\\|)");

        return pattern.toString();
    }

    private static String createMeasureNameSOLPattern() {
        StringBuilder pattern = new StringBuilder();
        pattern.append("(");
        pattern.append("(?<=(^|\\n))"+Patterns.WHITESPACE+"*\\|*");
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append(createMeasureNamePattern());
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append("((?=-)|(\\|+))");
        pattern.append(")");

        return pattern.toString();
    }

    public static String createMeasureNamePattern() {
        Iterator<String> measureLineNames = MeasureLine.createLineNameSet().iterator();
        StringBuilder pattern = new StringBuilder();
        pattern.append("("+measureLineNames.next());
        while(measureLineNames.hasNext()) {
            pattern.append("|"+measureLineNames.next());
        }
        pattern.append(")");
        return pattern.toString();
    }


    private static Set<String> createLineNameSet() {
        HashSet<String> nameSet = new HashSet<>();
        nameSet.addAll(GuitarMeasureLine.NAME_SET);
        nameSet.addAll(DrumMeasureLine.NAME_SET);
        return nameSet;
    }

    private static String createLineComponentPattern() {
        return "(" + GuitarMeasureLine.COMPONENT_PATTERN + "|" + GuitarMeasureLine.COMPONENT_PATTERN + ")";
    }
}
