package converter.note;

import converter.Score;
import models.measure.note.Chord;
import models.measure.note.Pitch;
import models.measure.note.notations.Notations;
import models.measure.note.notations.Technical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuitarNote extends Note {
    public static String COMPONENT_PATTERN = createComponentPattern();

    private static String createComponentPattern() {
        return "[0-9hpg\\/\\]";
    }

    public int fret;
    public GuitarNote(String line, String lineName, int distanceFromMeasureStart, int position) {
        super(line, lineName, distanceFromMeasureStart, position);
        try {
            this.fret = Integer.parseInt(this.line.strip());
        }catch (Exception e) {
            this.fret = 0;
        }
    }

    public List<HashMap<String, String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>();
        result.addAll(super.validate());

        if (line.strip().matches("[0-9]+")) return result;
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "This annotation is either unsupported or invalid.");
        response.put("positions", "["+this.position+","+(this.position+this.line.length())+"]");
        response.put("priority", "1");
        result.add(response);
        return result;
    }

    public models.measure.note.Note getModel() {
        models.measure.note.Note noteModel = new models.measure.note.Note();
        if (this.startsWithPreviousNote)
            noteModel.setChord(new Chord());
        noteModel.setPitch(new Pitch(GuitarNote.step(this.stringNumber, this.fret), GuitarNote.alter(this.stringNumber, this.fret), Note.octave(this.stringNumber, fret)));
        noteModel.setVoice(1);
        String noteType = this.getType();
        if (!noteType.isEmpty())
            noteModel.setType(noteType);

        noteModel.setDuration((int)Math.round(this.duration));  //we are guaranteed this.duration is greater or equal ot 1. look at Measure.setDurations()

        Technical technical = new Technical();
        technical.setString(this.stringNumber);
        technical.setFret(this.fret);

        Notations notations = new Notations();
        notations.setTechnical(technical);

        noteModel.setNotations(notations);

        return noteModel;
    }

    protected String getType() {
        double noteVal = (4.0 * (double) Score.GLOBAL_DIVISIONS)/this.duration;
        if (noteVal>=1024)
            return "1024th";
        else if (noteVal>=512)
            return "512th";
        else if (noteVal>=256)
            return "256th";
        else if (noteVal>=128)
            return "128th";
        else if (noteVal>=64)
            return "64th";
        else if (noteVal>=32)
            return "32th";
        else if (noteVal>=16)
            return "16th";
        else if (noteVal>=8)
            return "eighth";
        else if (noteVal>=4)
            return "quarter";
        else if (noteVal>=2)
            return "half";
        else if (noteVal>=1)
            return "whole";
        else if (noteVal>=0.5)
            return "breve";
        else if (noteVal>=0.25)
            return "long";
        else if (noteVal>=0.125)
            return "maxima";
        return "";
    }

    private static String step(int stringNum, int fret) {
        String[] stepList = {"C", "C", "D", "D", "E", "F", "F", "G", "G", "A", "A", "B"};
        if(stringNum == 6) {
            return stepList[(fret + 4) % 12];
        }
        else if(stringNum == 5) {
            return stepList[(fret + 9) % 12];
        }
        else if(stringNum == 4) {
            return stepList[(fret + 2) % 12];
        }
        else if(stringNum == 3) {
            return stepList[(fret + 7) % 12];
        }
        else if(stringNum == 2) {
            return stepList[(fret + 11) % 12];
        }
        else {
            return stepList[(fret + 4) % 12];
        }
    }

    private static int alter(int stringNum, int fret) {
        Integer[] alterList = {0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0};
        if(stringNum == 6) {
            return alterList[(fret + 4) % 12];
        }
        else if(stringNum == 5) {
            return alterList[(fret + 9) % 12];
        }
        else if(stringNum == 4) {
            return alterList[(fret + 2) % 12];
        }
        else if(stringNum == 3) {
            return alterList[(fret + 7) % 12];
        }
        else if(stringNum == 2) {
            return alterList[(fret + 11) % 12];
        }
        else {
            return alterList[(fret + 4) % 12];
        }
    }
}
