package converter.note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Note implements Comparable<Note>{
    String line;
    String name;
    public int distance;
    int position;
    private int octave;
    private String key;
    public int duration;
    public int fret;

    public Note(String line, String name, int distanceFromMeasureStart, int position) {
        this.line = line;
        this.name = name;
        this.position = position;
        this.fret = Integer.parseInt(line);
        int stringNumber = this.convertNameToNumber(this.name);
        this.octave = octave(stringNumber, fret);
        this.key = Note.key(stringNumber, fret);
        this.duration = 1;
        this.distance = distanceFromMeasureStart;
    }

    /**
     * TODO REMOVE THE TRY CATCH AND HANDLE THIS PROPERLY
     * @param line
     * @param name
     * @param distanceFromMeasureStart
     * @param position
     * @return
     */
    public static List<Note> from(String line, String name, int distanceFromMeasureStart, int position) {
        List<Note> noteList = new ArrayList<>();
        try {
            noteList.add(new Note(line, name, distanceFromMeasureStart, position));
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

    public String toXML(boolean startsWithPrevious) {
        StringBuilder noteXML = new StringBuilder();
        noteXML.append("<note>\n");

        if (startsWithPrevious)
            noteXML.append("<chord/>\n");
        noteXML.append(pitchScript());
        noteXML.append("<duration>");
        noteXML.append(this.duration);
        noteXML.append("</duration>\n");

        noteXML.append("</note>\n");

        return noteXML.toString();
    }

    @Override
    public int compareTo(Note o) {
        return this.distance-o.distance;
    }
}
