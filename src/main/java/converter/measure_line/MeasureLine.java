package converter.measure_line;

import converter.ScoreComponent;
import converter.note.DrumNote;
import converter.note.GuitarNote;
import converter.note.Note;
import utility.Patterns;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MeasureLine implements ScoreComponent {
    public String line;
    public String name;
    int namePosition;
    int position;
    public List<Note> noteList;
    public int noteCount;
    public boolean hasRepeatSymbol;

    protected MeasureLine(String line, String[] namesAndPosition, int position) {
        this.line = line;
        this.name = namesAndPosition[0];
        this.namePosition = Integer.parseInt(namesAndPosition[1]);
        this.position = position;
        this.noteList = this.createNoteList(line, name, position);
        this.noteCount = this.noteList.size();
    }


    /**
     * Creates a MeasureLine object from the provided string representation of the MeasureLine. The MeasureLine object
     * is either of type GuitarMeasureLine or DrumMeasureLine depending on if the features of the input Strings resembles
     * a guitar measure line or a drum measure line (this is determined by the MeasureLine.isGuitar() and MeasureLine.isDrum())
     * If it has features that neither belongs to GuitarMeasure nor DrumMeasure or has features shared by both, it defaults
     * to creating a GuitarMeasureLine object, and further error checking can be done by calling GuitarMeasureLine().validate()
     * on the object returned.
     * @param line the contents of the MeasureLine
     * @param nameAndPosition the name of the MeasureLine
     * @param position  the index at which the contents of the measure line can be found in the root string from which it
     *                 was derived (i.e Score.ROOT_STRING)
     * @return a MeasureLine object derived from the information in the input Strings. Either of type GuitarMeasureLine
     * or DrumMeasureLine
     */
    public static MeasureLine from(String line, String[] nameAndPosition, int position) {
        String name = nameAndPosition[0];
        boolean isGuitarLine = MeasureLine.isGuitarName(name);
        boolean isDrumLine = MeasureLine.isDrumName(name);
        if (isDrumLine && !isGuitarLine)
            return new DrumMeasureLine(line, nameAndPosition, position);
        else if(isGuitarLine && !isDrumLine)
            return new GuitarMeasureLine(line, nameAndPosition, position);
        else
            return new GuitarMeasureLine(line, nameAndPosition, position); //default value if any of the above is not true (i.e when the measure type can't be understood or has components belonging to both instruments)return null;
    }


    private List<Note> createNoteList(String line, String name, int position) {
        List<Note> noteList = new ArrayList<>();

        StringBuilder noteStrCollector = new StringBuilder();
        int noteStrStartIdx = 0;
        int noteNonWSstartIdx = 0;
        int distance = 0;
        int nonWhitespaceDistance = 0;
        for (int i=0; i<line.length(); i++) {
            char currentChar = line.charAt(i);

            if (currentChar!='-') {
                if (noteStrCollector.isEmpty()) noteStrStartIdx = distance;
                if (noteStrCollector.toString().isBlank() && !String.valueOf(currentChar).matches("\s")) noteNonWSstartIdx = nonWhitespaceDistance;
                noteStrCollector.append(currentChar);
            }

            if ((currentChar=='-' || i==line.length()-1)) {
                if (!noteStrCollector.toString().isBlank())
                    noteList.addAll(Note.from(noteStrCollector.toString(), name, noteNonWSstartIdx, position+noteStrStartIdx));
                noteStrCollector.delete(0, noteStrCollector.length());
            }
            distance++;
            if (!String.valueOf(currentChar).isBlank())
                nonWhitespaceDistance++;
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
    public List<HashMap<String, String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>();
        if (name==null) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "invalid measure line name.");
            response.put("priority", "1");
            response.put("positions", "["+this.position+","+(this.position+this.line.length())+"]");
            result.add(response);
        }
        Matcher matcher = Pattern.compile(MeasureLine.INSIDES_PATTERN).matcher("|"+line);
        if (!matcher.find() || !matcher.group().equals(this.line)) {     // "|"+name because the MeasureLine.INSIDES_PATTERN expects a newline, space, or | to come before
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "invalid measure line.");
            response.put("priority", "1");
            response.put("positions", "["+this.position+","+(this.position+this.line.length())+"]");
            result.add(response);
        }

        if (this.line.length()-this.line.replaceAll("\s", "").length() != 0) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Adding whitespace might result in different timing than you expect.");
            response.put("priority", "3");
            response.put("positions", "["+this.position+","+(this.position+this.line.length())+"]");
            result.add(response);
        }

        return result;
    }

    /**
     * TODO Make a more comprehensive test where you check if the line components belong go a drum
     * @param name
     * @return
     */
    public static boolean isDrumName(String name) {
        if (!DrumMeasureLine.NAME_SET.contains(name.strip())) return false;
        return true;
    }

    /**
     * TODO Make a more comprehensive test where you check if the line components belong go a guitar
     * @param name
     * @return
     */
    public static boolean isGuitarName(String name) {
        if (!GuitarMeasureLine.NAME_SET.contains(name.strip())) return false;
        return true;
    }

    public boolean isGuitar(boolean strictCheck) {
        if (!isGuitarName(this.name)) return false;
        if (!strictCheck) return true;
        for (Note note : this.noteList) {
            if (!note.isGuitar())
                return false;
        }
        return true;
    }

    public boolean isDrum(boolean strictCheck) {
        if (!isDrumName(this.name)) return false;
        if (!strictCheck) return true;
        for (Note note : this.noteList) {
            if (!note.isDrum())
                return false;
        }
        return true;
    }



    //---------------------------------------------regex stuff----------------------------------------------------------

    public static String INSIDES_PATTERN = createInsidesPattern();
    // e------------ or |e---------------- or |e|-------------------- when it is the first measure of the measure group (start of line, SOL)
    public static String PATTERN_SOL = "(" + createMeasureNameSOLPattern() + createInsidesPattern() + ")";
    //|--------------------- when it is in between other measures (middle of line, MIDL)
    public static String PATTERN_MIDL = "("+Patterns.DIVIDER+"+" + createInsidesPattern()+")";
    public static Set<String> NAME_SET = createLineNameSet();
    public static String COMPONENT_PATTERN = createLineComponentPattern();

    public static String[] nameOf(String measureLineStr, int lineStartIdx) {
        Pattern measureLineNamePttrn = Pattern.compile(createMeasureNameExtractPattern());
        Matcher measureLineNameMatcher = measureLineNamePttrn.matcher(measureLineStr);
        if (measureLineNameMatcher.find())
            return new String[] {measureLineNameMatcher.group(), ""+(lineStartIdx+measureLineNameMatcher.start())};
        else
            return null;
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

        //                     behind it is (space or newline, followed by a measure name) or ("|")     then the line either starts with a -, or starts with a component followed by a -  then repeated zero or more times, (- or space, followed by a component)        then the rest of the un-captured spaces or -
        //                                                      |                                                                         |                                                                                                                                      |
        String measureInsides = "("  +  "(?<="+"([ \\n]"+ createGenericMeasureNamePattern()+")|"+Patterns.DIVIDER+"+"+")"        +       "(([ ]*-)|("+Note.COMPONENT_PATTERN+"[ ]*-))"                         +                  "([ -]*"+Note.COMPONENT_PATTERN+")*"                                      +             "[ -]*" + ")";
        return measureInsides;
    }

    private static String createMeasureNameExtractPattern() {
        StringBuilder pattern = new StringBuilder();
        pattern.append("(?<=^"+Patterns.DIVIDER+"*"+")");
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append(createGenericMeasureNamePattern());
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append("(?="+"-" + "|" +Patterns.DIVIDER+")");  // what's ahead is a dash or a divider

        return pattern.toString();
    }

    private static String createMeasureNameSOLPattern() {
        StringBuilder pattern = new StringBuilder();
        pattern.append("(");
        pattern.append(Patterns.WHITESPACE+"*"+Patterns.DIVIDER+"*");
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append(createGenericMeasureNamePattern());
        pattern.append(Patterns.WHITESPACE+"*");
        pattern.append("((?=-)|("+Patterns.DIVIDER+"+))");
        pattern.append(")");

        return pattern.toString();
    }

    public static String createGenericMeasureNamePattern() {
        Iterator<String> measureLineNames = MeasureLine.createLineNameSet().iterator();
        StringBuilder pattern = new StringBuilder();
        pattern.append("([a-zA-Z]+|("+measureLineNames.next());
        while(measureLineNames.hasNext()) {
            pattern.append("|"+measureLineNames.next());
        }
        pattern.append("))");
        return pattern.toString();
    }


    private static Set<String> createLineNameSet() {
        HashSet<String> nameSet = new HashSet<>();
        nameSet.addAll(GuitarMeasureLine.createLineNameSet());
        nameSet.addAll(DrumMeasureLine.createLineNameSet());
        return nameSet;
    }

    private static String createLineComponentPattern() {
        return "(" + GuitarNote.COMPONENT_PATTERN + "|" + DrumNote.COMPONENT_PATTERN + ")";
    }

    @Override
    public String toString() {
        return this.name.strip()+"|"+this.recreateLineString(this.line.length())+"|";
    }

    public String recreateLineString(int maxMeasureLineLength) {
        StringBuilder outStr = new StringBuilder();
        if (this.noteList.isEmpty()) {
            for (int i=0; i<this.line.length(); i++) {
                String str = String.valueOf(this.line.charAt(i));
                if (str.matches("\s")) continue;
                outStr.append(str);
            }
            outStr.append("|");
            return outStr.toString();
        }

        double maxRatio = 0;
        for (Note note : this.noteList) {
            maxRatio = Math.max(maxRatio, note.durationRatio);
        }
        int actualLineDistance = maxMeasureLineLength;


        int prevNoteEndDist = 0;
        for (Note note : this.noteList) {
            if (!note.validate().isEmpty()) continue;
            int dashCount = note.distance-prevNoteEndDist;
            outStr.append("-".repeat(Math.max(0, dashCount)));
            outStr.append(note.sign);
            prevNoteEndDist = note.distance + note.sign.length();
        }
        outStr.append("-".repeat(Math.max(0, actualLineDistance - prevNoteEndDist)));
        outStr.append("|");
        return outStr.toString();
    }

}
