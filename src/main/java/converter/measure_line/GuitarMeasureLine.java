package converter.measure_line;

import GUI.TabInput;
import converter.note.GuitarNote;
import converter.note.Note;

import java.util.*;

public class GuitarMeasureLine extends MeasureLine {
    public static List<String> NAME_LIST = createLineNameSet();
    public static List<String> OCTAVE_LIST = createOctaveList();

    private static ArrayList<String> createOctaveList() {
        String[] names = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        ArrayList<String> nameList = new ArrayList<>();
        nameList.addAll(Arrays.asList(names));
        return nameList;
    }

    public GuitarMeasureLine(String line, String[] nameAndPosition, int position) {
        super(line, nameAndPosition, position);
    }

    protected static List<String> createLineNameSet() {
        String[] names = GuitarNote.KEY_LIST;
        ArrayList<String> nameList = new ArrayList<>();
        nameList.addAll(Arrays.asList(names));
        for (String name : names) {
            nameList.add(name.toLowerCase());
        }
        return nameList;
    }


    public List<HashMap<String,String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>(super.validate());

        if (!isGuitarName(this.name)) {
            HashMap<String, String> response = new HashMap<>();
            if (isDrumName(this.name))
                response.put("message", "A guitar string name is expected here.");
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
