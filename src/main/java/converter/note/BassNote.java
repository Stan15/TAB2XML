package converter.note;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BassNote extends GuitarNote {

    public BassNote(String origin, int position, String lineName, int distanceFromMeasureStart) {
        super(origin, position, lineName, distanceFromMeasureStart);
        this.fret = Integer.parseInt(origin);
        String noteDetails = BassNote.noteDetails(this.name, this.fret);
        this.step = GuitarNote.step(noteDetails);
        this.alter = GuitarNote.alter(noteDetails);
        this.octave = GuitarNote.octave(noteDetails);
        this.sign = this.fret+"";
    }

    protected static String noteDetails(String lineName, int fret) {
        String noteDetails = "";
        String name = lineName.strip();
        String[] nameList = GuitarNote.KEY_LIST;

        int currentOctave;
        Matcher lineOctaveMatcher = Pattern.compile("(?<=[^0-9])[0-9]+$").matcher(name);
        if (lineOctaveMatcher.find()) {
            name = name.substring(0, lineOctaveMatcher.start());
            currentOctave = Integer.parseInt(lineOctaveMatcher.group());
        }else
            currentOctave = GuitarNote.getDefaultOctave(name, -1);

        boolean nameFound = false;
        for (int i=0; i< nameList.length*2; i++){
            int idx = i%nameList.length;
            if (nameFound) {
                fret--;
            }
            if (nameList[idx].equalsIgnoreCase(name))
                nameFound = true;
            if (nameFound) {
                if (idx == 0)
                    currentOctave++;
                if (fret==0) {
                    noteDetails = nameList[idx];
                    break;
                }
            }
        }
        return noteDetails+currentOctave;
    }
}
