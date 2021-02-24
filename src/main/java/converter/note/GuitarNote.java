package converter.note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuitarNote extends Note {
    public GuitarNote(String line, String lineName, int distanceFromMeasureStart, int position) {
        super(line, lineName, distanceFromMeasureStart, position);
    }

    public List<HashMap<String, String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>();

        if (line.matches("[0-9]+")) return result;
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "This note type is either unsupported or invalid");
        response.put("positions", "["+this.position+","+(this.position+this.line.length())+"]");
        response.put("priority", "2");
        result.add(response);
        return result;
    }

    @Override
    public String toXML() {
        StringBuilder noteXML = new StringBuilder();
        noteXML.append("<note>\n");

        if (this.startWithPrevious)
            noteXML.append("<chord/>\n");
        noteXML.append(pitchScript());
        noteXML.append("<duration>");
        noteXML.append(this.duration);
        noteXML.append("</duration>\n");
        noteXML.append("<notations>\n");
        noteXML.append("<technical>\n");
        noteXML.append("<string>");
        noteXML.append(this.stringNumber);
        noteXML.append("</string>\n");
        noteXML.append("<fret>");
        noteXML.append(this.fret);
        noteXML.append("</fret>\n");
        noteXML.append("</technical>\n");
        noteXML.append("</notations>\n");

        noteXML.append("</note>\n");

        return noteXML.toString();
    }
}
