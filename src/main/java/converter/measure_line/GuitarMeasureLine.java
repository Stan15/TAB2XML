package converter.measure_line;

import GUI.TabInput;
import converter.Instrument;
import converter.Score;
import converter.note.GuitarNote;
import converter.note.Note;
import utility.ValidationError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GuitarMeasureLine extends MeasureLine {
    public static List<String> NAME_LIST = createLineNameSet();
    public static List<String> OCTAVE_LIST = createOctaveList();
    public static String COMPONENT = "[0-9hHpPsS\\/\\\\]";
    public static String INSIDES_PATTERN_SPECIAL_CASE = "$a"; // doesnt match anything

    private static ArrayList<String> createOctaveList() {
        String[] names = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        ArrayList<String> nameList = new ArrayList<>();
        nameList.addAll(Arrays.asList(names));
        return nameList;
    }

    public GuitarMeasureLine(String line, String[] nameAndPosition, int position) {
        super(line, nameAndPosition, position);
        this.instrument = Instrument.GUITAR;
        this.noteList = this.createNoteList(this.line, position);
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


    public List<ValidationError> validate() {
        List<ValidationError> result = new ArrayList<>(super.validate());

        if (!isGuitarName(this.name)) {
            String message = isDrumName(this.name)
                    ? "A Guitar measure line is expected here."
                    : "Invalid measure line.";

            ValidationError error = new ValidationError(
                    message,
                    1,
                    new ArrayList<>(Collections.singleton(new Integer[]{
                            this.namePosition,
                            this.position+this.line.length()
                    }))
            );
            if (TabInput.ERROR_SENSITIVITY>= error.getPriority())
                result.add(error);
        }

        for (ValidationError error : result) {
            if (error.getPriority() <= Score.CRITICAL_ERROR_CUTOFF) {
                return result;
            }
        }
        for (Note note : this.noteList)
            result.addAll(note.validate());

        return result;
    }
}
