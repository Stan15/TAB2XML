package converter.measure_line;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DrumMeasureLine extends MeasureLine {
    public static Set<String> NAME_SET = createLineNameSet();

    protected DrumMeasureLine(String line, String name, int position) {
        super(line, name, position);
    }

    private static Set<String> createLineNameSet() {
        String[] names = {"CC", "Ch", "C2", "HH", "Rd", "R", "SN", "T1", "T2", "FT", "BD", "Hf", "FH", "C", "H", "s", "S", "B", "Hh", "F", "F2", "Ht", "Mt", "f1", "f2", "Hhf"};
        HashSet<String> nameSet = new HashSet<>();
        nameSet.addAll(Arrays.asList(names));
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
    public HashMap<String, String> validate() {
        return null;
    }
}
