package converter.note;

import GUI.TabInput;
import models.measure.note.Chord;
import models.measure.note.Pitch;
import models.measure.note.notations.Notations;
import models.measure.note.notations.technical.Technical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuitarNote extends Note {
    public static String FRET_PATTERN = "([0-9]{1,2})";
    public static String GRACE_PATTERN = getGracePattern();
    public static String PATTERN = getPattern();


    private static String getPattern() {
        return "("+ FRET_PATTERN +"|"+ GRACE_PATTERN +")";
    }

    protected String step;
    protected int alter;
    protected int octave;
    public static String[] KEY_LIST = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    private static String getGracePattern() {
        return "(g"+ FRET_PATTERN +"[hp]"+ FRET_PATTERN +")";
    }

    protected int fret;
    public GuitarNote(String origin, int position, String lineName, int distanceFromMeasureStart) {
        super(origin, position, lineName, distanceFromMeasureStart);
        this.fret = Integer.parseInt(origin);
        String noteDetails = noteDetails(this.lineName, this.fret);
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
            Matcher matcher = Pattern.compile("(?<=^\\[)[0-9](?=\\])").matcher(resp);
            matcher.find();
            int priority = Integer.parseInt(matcher.group());
            String message = resp.substring(matcher.end()+1);;
            int startIdx = this.position;
            int endIdx = this.position+this.origin.length();


            matcher = Pattern.compile("(?<=^\\[)[0-9]+,[0-9]+(?=\\])").matcher(message);
            if (matcher.find()) {
                String positions = matcher.group();
                matcher = Pattern.compile("[0-9]+").matcher(positions); matcher.find();
                startIdx = Integer.parseInt(matcher.group()); matcher.find();
                endIdx = Integer.parseInt(matcher.group());
                message = message.substring(matcher.end()+2);
            }

            HashMap<String, String> response = new HashMap<>();
            response.put("message", message);
            response.put("positions", "["+startIdx+","+endIdx+"]");
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
        noteModel.setVoice(this.voice);
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

//        //dot's don't work for some reason
//        List<Dot> dots = new ArrayList<>();
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
            currentOctave = GuitarNote.getDefaultOctave(name, 0);

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

    protected static int getDefaultOctave(String name, int offset) {
        if (name.equals("e"))
            return 4+offset;
        else if (name.equalsIgnoreCase("B"))
            return 3+offset;
        else if (name.equalsIgnoreCase("G"))
            return 3+offset;
        else if (name.equalsIgnoreCase("D"))
            return 3+offset;
        else if (name.equalsIgnoreCase("A"))
            return 2+offset;
        else if (name.equalsIgnoreCase("E"))
            return 2+offset;
        return -1;
    }

    protected static String step(String noteDetails) {
        Matcher matcher = Pattern.compile("^[a-zA-Z]+").matcher(noteDetails);
        if (matcher.find())
            return matcher.group().toUpperCase();
        return "";
    }

    protected static int alter(String noteDetails) {
        if (noteDetails.contains("#"))
            return 1;
        return 0;
    }

    //decide octave of note
    protected static int octave(String noteDetails) {
        Matcher lineOctaveMatcher = Pattern.compile("(?<=[^0-9])[0-9]+$").matcher(noteDetails);
        lineOctaveMatcher.find();
        return Integer.parseInt(lineOctaveMatcher.group());
    }
}
