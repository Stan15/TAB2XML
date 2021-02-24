package converter.note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class Note implements Comparable<Note>{
    public boolean startWithPrevious;
    public String line;
    public String name;
    int stringNumber;
    public int distance;
    int position;
    public int duration;
    public boolean isValid;

    // A pattern that matches the note components of a measure line, like (2h7) or 8s3 or 12 or 4/2, etc.
    // It doesn't have to match the correct notation. It should be as vague as possible, so it matches anything that "looks"
    //like a note component (e.g it should match something like e|-------h3(-----|, even though it is invalid ) this makes it so that
    //even though incorrect, we still recognise the whole thing as a measure, and we get to the stage where we are trying to convert this
    //particular note. We thus will know the exact place where the problem is instead of the whole measure not being recognised as an
    // actual measure just because of that error and we flag the whole measure as an error instead of this one, small, specific
    // area of hte measure (the pattern for detecting measure groups uses this pattern)
    public static String CHARACTER_SET_PATTERN = "[0-9./\\\\~\\(\\)]";

    public Note(String line, String lineName, int distanceFromMeasureStart, int position) {
        this.line = line;
        this.name = lineName;
        this.position = position;
        this.stringNumber = this.convertNameToNumber(this.name);
        this.duration = 1;
        this.distance = distanceFromMeasureStart;

        this.isValid = this.isValid();
    }

    public List<HashMap<String,String>> validate() {
        return new ArrayList<>();
    }

    public boolean isValid() {
        int cut_off_priority_level = 2;
        for (HashMap<String,String> response : this.validate()) {
            if (Integer.parseInt(response.get("priority")) <= cut_off_priority_level)
                return false;
        }
        return true;
    }

    /**
     * TODO REMOVE THE TRY CATCH AND HANDLE THIS PROPERLY
     * @param line
     * @param lineName
     * @param distanceFromMeasureStart
     * @param position
     * @return
     */
    public static List<Note> from(String line, String lineName, int distanceFromMeasureStart, int position) {
        List<Note> noteList = new ArrayList<>();
        try {
            noteList.add(new GuitarNote(line, lineName, distanceFromMeasureStart, position));
        }catch (Exception e) {
            e.printStackTrace();
        }

        return noteList;
    }

    public int convertNameToNumber(String lineName) {
        if (lineName.equals("e")) {
            return 1;
        } else if (lineName.equals("B")) {
            return 2;
        } else if (lineName.equals("G")) {
            return 3;
        } else if (lineName.equals("D")) {
            return 4;
        } else if (lineName.equals("A")) {
            return 5;
        } else if (lineName.equals("E")) {
            return 6;
        }
        return 0;
    }

    //I made only pitch part for now.
    //reference: https://theacousticguitarist.com/all-notes-on-guitar/
    //make script
    public String pitchScript() {
        int fret = Integer.parseInt(this.line);
        String key = Note.key(this.stringNumber, fret);
        int octave = octave(this.stringNumber,fret);

        String octaveString = "<octave>" + octave + "</octave>\n";
        String stepString;
        if(!key.contains("#")) {
            stepString = "<step>" + key + "</step>\n";
        }
        else {
            stepString = "<step>" + key.charAt(0) + "</step>\n"
                    + "<alter>" + 1 + "</alter>\n";
            //In musicxml, # is expressed as <alter>1</alter>
        }

        return "<pitch>\n"
                + stepString
                + octaveString
                + "</pitch>\n";
    }

    public abstract String toXML();


    //decide octave of note
    private static int octave(int stringNumber, int fret) {
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

    //decide key of note
    public static String key(int stringNumber, int fret) {
        String[] keys = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        if(stringNumber == 6) {
            return keys[(fret + 4) % 12];
        }
        else if(stringNumber == 5) {
            return keys[(fret + 9) % 12];
        }
        else if(stringNumber == 4) {
            return keys[(fret + 2) % 12];
        }
        else if(stringNumber == 3) {
            return keys[(fret + 7) % 12];
        }
        else if(stringNumber == 2) {
            return keys[(fret + 11) % 12];
        }
        else {
            return keys[(fret + 4) % 12];
        }
    }

    @Override
    public int compareTo(Note o) {
        return this.distance-o.distance;
    }
}
