package converter.note;

import GUI.TabInput;
import models.measure.note.Chord;
import models.measure.note.Dot;
import models.measure.note.Grace;
import models.measure.note.Pitch;
import models.measure.note.notations.Notations;
import models.measure.note.notations.Slur;
import models.measure.note.notations.technical.Technical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuitarNote extends Note {
    public static String COMPONENT_PATTERN = createComponentPattern();
    public static String FRET_PATTERN = "([0-9]{1,2})";
    public static String GRACE_PATTERN = getGracePattern();
    public static String PATTERN = getPattern();


    private static String getPattern() {
        return "("+ FRET_PATTERN +"|"+ GRACE_PATTERN +")";
    }

    private String step;
    private int alter;
    private int octave;
    boolean isGrace;
    boolean isGracePair;
    public static String[] KEY_LIST = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    private static String getGracePattern() {
        return "(g"+ FRET_PATTERN +"[hp]"+ FRET_PATTERN +")";
    }
    private static String createComponentPattern() {
        return "[0-9hpg\\/\\]";
    }

    public int fret;
    public GuitarNote(String origin, int position, int fret, int fretPosition, String lineName, int distanceFromMeasureStart) {
        super(origin, position, lineName, distanceFromMeasureStart);
        this.fret = fret;
        String noteDetails = noteDetails(this.name, this.fret);
        this.step = GuitarNote.step(noteDetails);
        this.alter = GuitarNote.alter(noteDetails);
        this.octave = GuitarNote.octave(noteDetails);
        this.sign = this.fret+"";
    }

    public List<HashMap<String, String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>(super.validate());

        for (NoteFactory.NoteDecor noteDecor : this.noteDecorMap.keySet()) {
            String resp = noteDecorMap.get(noteDecor);
            if (resp.equals("success")) continue;
            Matcher matcher = Pattern.compile("(?<=\\[)[0-9](?=\\])").matcher(resp);
            matcher.find();
            int priority = Integer.parseInt(matcher.group());
            matcher = Pattern.compile("(?<=\\]).*").matcher(resp);
            matcher.find();
            String message = matcher.group();

            HashMap<String, String> response = new HashMap<>();
            response.put("message", message);
            response.put("positions", "["+this.position+","+(this.position+this.origin.length())+"]");
            response.put("priority", ""+priority);
            if (TabInput.ERROR_SENSITIVITY>=priority)
                result.add(response);
        }
        return result;
    }

    public int getFret() {
        return this.fret;
    }

    public models.measure.note.Note getModel() {
        models.measure.note.Note noteModel = new models.measure.note.Note();
        if (this.startsWithPreviousNote)
            noteModel.setChord(new Chord());
        noteModel.setPitch(new Pitch(this.step, this.alter, this.octave));
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
        //dot's don't work for some reason
        List<Dot> dots = new ArrayList<>();
//        for (int i=0; i<this.dotCount; i++){
//            dots.add(new Dot());
//        }
//        if (!dots.isEmpty())
//            noteModel.setDots(dots);
        for (NoteFactory.NoteDecor noteDecor : this.noteDecorMap.keySet()) {
            if (noteDecorMap.get(noteDecor).equals("success"))
                noteDecor.applyTo(noteModel);
        }
        return noteModel;
    }

    private static String noteDetails(String lineName, int fret) {
        String noteDetails = "";
        String name = lineName.strip();
        String[] nameList = GuitarNote.KEY_LIST;

        int currentOctave;
        Matcher lineOctaveMatcher = Pattern.compile("(?<=[^0-9])[0-9]+$").matcher(name);
        if (lineOctaveMatcher.find()) {
            name = name.substring(0, lineOctaveMatcher.start());
            currentOctave = Integer.parseInt(lineOctaveMatcher.group());
        }else
            currentOctave = GuitarNote.getDefaultOctave(name);

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

    private static int getDefaultOctave(String name) {
        if (name.equals("e"))
            return 4;
        else if (name.equalsIgnoreCase("B"))
            return 3;
        else if (name.equalsIgnoreCase("G"))
            return 3;
        else if (name.equalsIgnoreCase("D"))
            return 3;
        else if (name.equalsIgnoreCase("A"))
            return 2;
        else if (name.equalsIgnoreCase("E"))
            return 2;
        return -1;
    }

    private static String step(String noteDetails) {
        Matcher matcher = Pattern.compile("^[a-zA-Z]+").matcher(noteDetails);
        if (matcher.find())
            return matcher.group().toUpperCase();
        return "";
    }

    //decide octave of note
    protected static int octave(int stringNumber, int fret) {
        int octave;
        if(stringNumber == 6) {
            if(fret >= 0 && fret <= 7) {
                octave = 2;
            }
            else {
                octave = 3;
            }
        }
        else if(stringNumber == 5) {
            if(fret >= 0 && fret <= 2) {
                octave = 2;
            }
            else if(fret >= 3 && fret <= 14) {
                octave = 3;
            }
            else {
                octave = 4;
            }
        }
        else if(stringNumber == 4) {
            if(fret >=0 && fret <= 9) {
                octave = 3;
            }
            else {
                octave = 4;
            }
        }
        else if(stringNumber == 3) {
            if(fret >= 0 && fret <= 4) {
                octave = 3;
            }
            else if(fret >= 5 && fret <= 16) {
                octave = 4;
            }
            else {
                octave = 5;
            }
        }
        else if(stringNumber == 2) {
            if(fret == 0) {
                octave = 3;
            }
            else if(fret >= 1 && fret <= 12) {
                octave = 4;
            }
            else {
                octave = 5;
            }
        }
        else {
            if(fret >= 0 && fret <= 7) {
                octave = 4;
            }
            else {
                octave = 5;
            }
        }
        return octave;
    }

    private static int alter(String noteDetails) {
        if (noteDetails.contains("#"))
            return 1;
        return 0;
    }

    //decide octave of note
    private static int octave(String noteDetails) {
        Matcher lineOctaveMatcher = Pattern.compile("(?<=[^0-9])[0-9]+$").matcher(noteDetails);
        lineOctaveMatcher.find();
        return Integer.parseInt(lineOctaveMatcher.group());
    }
}
