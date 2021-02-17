package converter.note;

public class GuitarNote extends Note {
    public GuitarNote(String line, String lineName, int distanceFromMeasureStart, int position) {
        super(line, lineName, distanceFromMeasureStart, position);
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
