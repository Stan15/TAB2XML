package converter.measure_line;

import GUI.TabInput;
import converter.note.Note;
import converter.note.NoteFactory;

import java.util.*;

public class DrumMeasureLine extends MeasureLine {
    public static Set<String> NAME_SET = createLineNameSet();
    public static String COMPONENT = "[xXoOdDfF]";
    public static String INSIDES_PATTERN_SPECIAL_CASE = "$a";

    protected DrumMeasureLine(String line, String[] nameAndPosition, int position) {
        super(line, nameAndPosition, position);
    }

    protected static Set<String> createLineNameSet() {
        String[] names = {"CC", "HH", "SD", "HT", "MT", "BD"};
        HashSet<String> nameSet = new HashSet<>(Arrays.asList(names));
        return nameSet;
    }

    /**
     * TODO validate that the symbols in the measure line correspond to the measure line name.
     *      look at this wikipedia page. If the measure line is a Cymbal measure line,
     *      only certain types of symbols, or "notes" can be in that measure line
     *      https://en.wikipedia.org/wiki/Drum_tablature#:~:text=Drum%20tablature,%20commonly%20known%20as%20a%20drum%20tab,,to%20stroke.%20Drum%20tabs%20frequently%20depict%20drum%20patterns.
     * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
     * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
     * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
     * found in the root string from which it was derived (i.e Score.ROOT_STRING).
     * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public List<HashMap<String, String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>(super.validate());

        if (!isDrumName(this.name)) {
            HashMap<String, String> response = new HashMap<>();
            if (isGuitarName(this.name))
                response.put("message", "A Drum name is expected here.");
            else
                response.put("message", "Invalid measure line name.");
            response.put("positions", "["+this.namePosition+","+(this.namePosition+this.name.length())+"]");
            int priority = 1;
            response.put("priority", ""+priority);
            if (TabInput.ERROR_SENSITIVITY>=priority)
                result.add(response);
        }

        for (Note note : this.noteList)
            result.addAll(note.validate());

        return result;
    }
}
