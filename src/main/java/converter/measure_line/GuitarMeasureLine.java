package converter.measure_line;

import converter.note.Note;

import java.util.*;

public class GuitarMeasureLine extends MeasureLine {
    public static Set<String> NAME_SET = createLineNameSet();

    public GuitarMeasureLine(String line, String[] nameAndPosition, int position) {
        super(line, nameAndPosition, position);
    }

    protected static Set<String> createLineNameSet() {
        String[] names = {"E", "A", "D", "G", "B", "e", "a", "d", "g", "b"};
        HashSet<String> nameSet = new HashSet<>();
        nameSet.addAll(Arrays.asList(names));
        return nameSet;
    }


    public List<HashMap<String,String>> validate() {
        List<HashMap<String,String>> result = new ArrayList<>();
        result.addAll(super.validate());

        if (!isGuitarName(this.name)) {
            HashMap<String, String> response = new HashMap<>();
            if (isDrumName(this.name))
                response.put("message", "A guitar string name is expected here.");
            else
                response.put("message", "Invalid measure line name.");
            response.put("positions", "["+this.namePosition+","+(this.namePosition+this.name.length())+"]");
            response.put("priority", "1");
            result.add(response);
        }

        for (Note note : this.noteList)
            result.addAll(note.validate());

        return result;
    }
}
