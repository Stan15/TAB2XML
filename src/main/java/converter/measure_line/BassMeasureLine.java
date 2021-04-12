package converter.measure_line;

import GUI.TabInput;
import converter.Instrument;
import converter.Score;
import converter.note.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BassMeasureLine extends GuitarMeasureLine{
    public BassMeasureLine(String line, String[] nameAndPosition, int position) {
        super(line, nameAndPosition, position);
        this.instrument = Instrument.BASS;
        this.noteList = this.createNoteList(this.line, position);
    }

    @Override
    public List<HashMap<String,String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>(super.validate());

        if (!isGuitarName(this.name)) {
            HashMap<String, String> response = new HashMap<>();
            if (isDrumName(this.name))
                response.put("message", "A Bass string name is expected here.");
            else
                response.put("message", "Invalid measure line name.");
            response.put("positions", "["+this.namePosition+","+(this.namePosition+this.name.length())+"]");
            int priority = 1;
            response.put("priority", ""+priority);
            if (TabInput.ERROR_SENSITIVITY>=priority)
                result.add(response);
        }


        for (HashMap<String, String> error : result) {
            if (Integer.parseInt(error.get("priority")) <= Score.CRITICAL_ERROR_CUTOFF) {
                return result;
            }
        }

        for (Note note : this.noteList)
            result.addAll(note.validate());

        return result;
    }
}
