package converter.note;

import GUI.TabInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvalidNote extends Note {

    public InvalidNote(String origin, int position, String lineName, int distanceFromMeasureStart) {
        super(origin, position, lineName, distanceFromMeasureStart);
    }

    @Override
    public models.measure.note.Note getModel() {
        return null;
    }

    public List<HashMap<String, String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "This annotation is either unsupported or invalid.");
        response.put("positions", "[" + this.position + "," + (this.position + this.origin.length()) + "]");
        int priority = 1;
        response.put("priority", "" + priority);
        if (TabInput.ERROR_SENSITIVITY >= priority)
            result.add(response);
        return result;
    }
}
