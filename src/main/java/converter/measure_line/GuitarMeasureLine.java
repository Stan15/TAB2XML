package converter.measure_line;

import converter.note.Note;

import java.util.*;

public class GuitarMeasureLine extends MeasureLine {
    public static Set<String> NAME_SET = createLineNameSet();
    public static String COMPONENT_PATTERN = createLineComponentPattern();

    public GuitarMeasureLine(String line, String name, int position) {
        super(line, name, position);
    }

    protected static Set<String> createLineNameSet() {
        String[] names = {"E", "A", "D", "G", "B", "e", "a", "d", "g", "b"};
        HashSet<String> nameSet = new HashSet<>();
        nameSet.addAll(Arrays.asList(names));
        return nameSet;
    }

    private static String createLineComponentPattern() {
        return "[0-9]+|h|p|/|\\";
    }

    public List<HashMap<String,String>> validate() {
        List<HashMap<String,String>> result = new ArrayList<>();
        result.addAll(super.validate());

        for (Note note : this.noteList)
            result.addAll(note.validate());

        return result;
    }
}
