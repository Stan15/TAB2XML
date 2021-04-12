package converter.measure_line;

import GUI.TabInput;
import converter.Instrument;
import converter.Score;
import converter.note.Note;
import utility.ValidationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BassMeasureLine extends GuitarMeasureLine{
    public BassMeasureLine(String line, String[] nameAndPosition, int position) {
        super(line, nameAndPosition, position);
        this.instrument = Instrument.BASS;
        this.noteList = this.createNoteList(this.line, position);
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> result = new ArrayList<>(super.validate());

        if (!isGuitarName(this.name)) {
            String message = isDrumName(this.name)
                    ? "A Bass string name is expected here."
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
