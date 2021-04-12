package converter.measure_line;

import GUI.TabInput;
import converter.Instrument;
import converter.Score;
import converter.note.Note;
import converter.note.NoteFactory;
import utility.DrumUtils;
import utility.ValidationError;

import java.util.*;

public class DrumMeasureLine extends MeasureLine {
    public static Set<String> USED_DRUM_PARTS = new HashSet<>();
    public static String COMPONENT = "[xXoOdDfF]";
    public static String INSIDES_PATTERN_SPECIAL_CASE = "$a"; //doesnt match anything
    private String partID;

    protected DrumMeasureLine(String line, String[] nameAndPosition, int position) {
        super(line, nameAndPosition, position);
        this.instrument = Instrument.DRUM;
        this.partID = DrumUtils.getPartID(this.name);
        if (this.partID!=null)
            USED_DRUM_PARTS.add(this.partID);
        this.noteList = this.createNoteList(this.line, position);
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
    public List<ValidationError> validate() {
        List<ValidationError> result = new ArrayList<>(super.validate());

        if (!isDrumName(this.name)) {
            String message = isGuitarName(this.name)
                    ? "A Drum measure line is expected here."
                    : "Invalid measure line name.";
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
        }else if (this.partID==null || !DrumUtils.isSupportedName(this.name)) {
            ValidationError error = new ValidationError(
                    "This drum part is unsupported.",
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
