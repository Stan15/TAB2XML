package converter.GuitarConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

public class GuitarNote {
    public static String makeNoteScript(int stringNumber, String notation){

        return "<note>\n" + scripting(stringNumber, notation) + "</note>\n";
    }

    public static String makeRestNoteScript(){
        return "<note>\n" +
                "<rest/>\n" +
                "<duration>1</duration>\n" +
                "<voice>1</voice>\n" +
                "<type>16th</type>\n" +
                "</note>\n";
    }


    public static String makeChordNoteScript(HashMap<Integer, String> notations){

        Iterator<Integer> iter = notations.keySet().iterator();
        ArrayList<Integer> sortedKey = new ArrayList<>(notations.keySet());
        Collections.sort(sortedKey);

        String result = "";
        for(int i = sortedKey.size() - 1; i >= 0; i--){
            String notation = notations.get(sortedKey.get(i));
            if(i == sortedKey.size() - 1){
                result = result + "<note>\n" + scripting(sortedKey.get(i), notation) + "</note>\n";
            }
            else{
                result += "<note>\n" +
                        "<chord/>\n";
                result = result + scripting(sortedKey.get(i), notation) + "</note>\n";
            }
        }
        return result;
    }

    //https://guitargearfinder.com/lessons/how-to-read-guitar-tab/
    private static String scripting(int stringNumber, String notation){

        String result = "";

        if(Pattern.matches(("^[0-9]*$"), notation)){
            int fretNum = Integer.parseInt(notation);
            result += GuitarNote.pitchScript(octave(stringNumber, fretNum),key(stringNumber, fretNum));
            result += "<duration>1</duration>\n" +
                    "<voice>1</voice>\n" +
                    "<type>16th</type>\n";
            result += "<notations>\n" +
                    "<technical>\n" +
                    "<string>" + stringNumber + "</string>\n" +
                    "<fret>" + fretNum + "</fret>\n" +
                    "</technical>\n" +
                    "</notations>\n";
        }
        else if(notation.equals("x") || notation.equals("X")){
        }
        else if(Pattern.matches(("^[[0-9]*[phPH][0-9]]*"), notation)){
        }
        else if(Pattern.matches(("^[[(][0-9]*[)]]"), notation)){
        }
        else{
        }
        return result;
    }

    private static String pitchScript(int octave, String key) {

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

    private static String key(int stringNumber, int fret) {
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
}
