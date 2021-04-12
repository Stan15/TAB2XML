package converter.note;

import GUI.TabInput;
import converter.Instrument;
import converter.Score;
import converter.ScoreComponent;
import utility.Patterns;
import utility.ValidationError;

import java.util.*;

public abstract class Note implements Comparable<Note>, ScoreComponent {
    public boolean startsWithPreviousNote;
    public String origin;
    public String lineName;
    public int dotCount;
    public Instrument instrument;
    int stringNumber;
    public int distance;
    int position;
    public double duration;
    public double durationRatio;
    public String sign;
    public int voice;
    public boolean isGrace;
    public static boolean SLASHED_GRACE = true;
    protected Map<NoteFactory.NoteDecor, String> noteDecorMap = new LinkedHashMap<>();


    // A pattern that matches the note components of a measure line, like (2h7) or 8s3 or 12 or 4/2, etc.
    // It doesn't have to match the correct notation. It should be as vague as possible, so it matches anything that "looks"
    //like a note component (e.g it should match something like e|-------h3(-----|, even though it is invalid ) this makes it so that
    //even though incorrect, we still recognise the whole thing as a measure, and we get to the stage where we are trying to convert this
    //particular note. We thus will know the exact place where the problem is instead of the whole measure not being recognised as an
    // actual measure just because of that error and we flag the whole measure as an error instead of this one, small, specific
    // area of hte measure (the pattern for detecting measure groups uses this pattern)
    public static String COMPONENT_PATTERN = "[^-\\n\\r"+Patterns.DIVIDER_COMPONENTS+"]";
    public static String PATTERN = getNotePattern();

    private static String getNotePattern() {
        return "(" + NoteFactory.GUITAR_NOTE_GROUP_PATTERN + "|" + NoteFactory.DRUM_NOTE_GROUP_PATTERN + "|" + COMPONENT_PATTERN+"+" + ")";
    }

    public Note(String origin, int position, String lineName, int distanceFromMeasureStart) {
        this.origin = origin;
        this.lineName = lineName;
        this.position = position;
        this.stringNumber = this.convertNameToNumber(this.lineName);
        this.duration = 1;
        this.distance = distanceFromMeasureStart;
        this.voice = 1;
    }
    public Note(String origin, int position, String lineName, int distanceFromMeasureStart, int voice) {
        this(origin, position, lineName, distanceFromMeasureStart);
        this.voice = voice;
    }

    public List<ValidationError> validate() {
        List<ValidationError> result = new ArrayList<>();
        if (!this.origin.equals(this.origin.strip())) {
            ValidationError error = new ValidationError(
                    "Adding whitespace might result in different timing than you expect.",
                    3,
                    new ArrayList<>(Collections.singleton(new Integer[]{
                            this.position,
                            this.position+this.origin.length()
                    }))
            );
            if (TabInput.ERROR_SENSITIVITY>= error.getPriority())
                result.add(error);
        }
        return result;
    }


    /**
     * TODO REMOVE THE TRY CATCH AND HANDLE THIS PROPERLY
     * @param origin
     * @param lineName
     * @param distanceFromMeasureStart
     * @param position
     * @return
     */
    public static List<Note> from(String origin, int position, Instrument instrument, String lineName, int distanceFromMeasureStart) {
        NoteFactory nf = new NoteFactory(origin, position, instrument, lineName, distanceFromMeasureStart);
        return nf.getNotes();
    }

    public boolean addDecor(NoteFactory.NoteDecor noteDecor, String message) {
        this.noteDecorMap.put(noteDecor, message);
        return true;
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
            return "32nd";
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

    public int convertNameToNumber(String lineName) {
        lineName = lineName.strip();
        if (lineName.equals("e")) {
            return 1;
        } else if (lineName.equalsIgnoreCase("B")) {
            return 2;
        } else if (lineName.equalsIgnoreCase("G")) {
            return 3;
        } else if (lineName.equalsIgnoreCase("D")) {
            return 4;
        } else if (lineName.equalsIgnoreCase("A")) {
            return 5;
        } else if (lineName.equalsIgnoreCase("E")) {
            return 6;
        }
        return 0;
    }

    public boolean isGuitar() {
        // remember, invalid notes are still accepted but are created as GuitarNote objects. we want to be able to still convert despite having invalid notes, as long as we warn the user that they have invalid input. We might want to create a new concrete class, InvalidNote, that extends Note to take care of this so that we have the guarantee that this is valid.
        return this.origin.strip().matches(NoteFactory.FRET);
    }

    public boolean isDrum() {
        // remember, invalid notes are still accepted but are created as GuitarNote objects. we want to be able to still convert despite having invalid notes, as long as we warn the user that they have invalid input. We might want to create a new concrete class, InvalidNote, that extends Note to take care of this so that we have the guarantee that this is valid.
        return this.origin.strip().matches(NoteFactory.DRUM_NOTE_PATTERN);
    }

    public abstract models.measure.note.Note getModel();

    public int compareTo(Note other) {
        return this.distance-other.distance;
    }
}
