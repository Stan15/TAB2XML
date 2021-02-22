package converter.GuitarConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GuitarMeasure extends GuitarConvert{
    private int measureNum;
    private ArrayList<String> measureInfo;
    private ArrayList<GuitarNote> notes;
    private String[][] notesBox;
    private int totalDurationPerMeasure;
    private final int DEFAULT_DIVISION = 4;
    private final int DEFAULT_BEATS = 4;
    private final int DEFAULT_BEAT_TYPE = 4;
    private final int NUM_OF_GUITAR_STRING = 6;
    // set default division 4, beats 4, beat type 4.
    // it means total 16 beats are available in one measure
    // We can change if it is not enough or it is too much or can change it not to final value (depending on measures)

    public GuitarMeasure(int measureNum, ArrayList<String> measureInfo){

        this.measureNum = measureNum;
        this.measureInfo = measureInfo;

        if(DEFAULT_BEAT_TYPE == 4){
            this.totalDurationPerMeasure = DEFAULT_BEATS * DEFAULT_DIVISION;
        }
        else if(DEFAULT_BEAT_TYPE == 8){
            this.totalDurationPerMeasure = DEFAULT_BEATS * (DEFAULT_DIVISION / 2);
        }
        else{
            this.totalDurationPerMeasure = DEFAULT_BEATS * (DEFAULT_DIVISION * 2);
        }//in the case of beat type = 2

        this.notesBox = new String[NUM_OF_GUITAR_STRING][totalDurationPerMeasure];
        this.notes = new ArrayList<>();
        for(int i = 0; i < measureInfo.size(); i++){
            String eachStringLine = measureInfo.get(i);
            storeNotes(eachStringLine, i);
        }
    }

    //It takes each line of string (from 1 to 6) and extract notation
    private void storeNotes(String eachStringLine, int stringNum){
        String temp = eachStringLine;
        int totalLength = temp.length();
        String[] splitChar = temp.split("[-]");
        ArrayList<String> notations = new ArrayList<>();
        for(String notation : splitChar){
            if(!notation.equals("") && !notation.equals(" ")){
                notations.add(notation);
            }// Store lines when it has information
        }

        for(String notation : notations){
            int index = temp.indexOf(notation);
            double position = (double) (index + 1) / (double) totalLength;
            for(int i = 0; i < totalDurationPerMeasure; i++){
                if(position <= (double) (i + 1) / totalDurationPerMeasure) {
                    if(notesBox[stringNum][i] != null){
                        notesBox[stringNum][i + 1] = notation;
                        break;
                    }
                    else if(notesBox[stringNum][i] != null && notesBox[stringNum][i + 1] != null){
                        notesBox[stringNum][i - 1] = notation;
                        break;
                    }
                    else{
                        notesBox[stringNum][i] = notation;
                        break;
                    }
                }
            }

            StringBuilder builder = new StringBuilder(temp);
            for(int i = index; i < index + notation.length(); i++){
                builder.setCharAt(i,'-');
            }

            temp = builder.toString();
        }
    }

    private String makeAttributes(){
        String attributes = "<attributes>\n" +
                "<divisions>" + DEFAULT_DIVISION + "</divisions>\n" +
                "<key>\n" +
                "<fifths>0</fifths>\n" +
                "</key>\n" +
                "<time>\n" +
                "<beats>" + DEFAULT_BEATS + "</beats>\n" +
                "<beat-type>" + DEFAULT_BEAT_TYPE + "</beat-type>\n" +
                "</time>\n" +
                "<clef>\n" +
                "<sign>TAB</sign>\n" +
                "<line>5</line>\n" +
                "</clef>\n" +
                "<staff-details>\n" +
                "<staff-lines>6</staff-lines>\n" +
                "<staff-tuning line=\"1\">\n" +
                "<tuning-step>E</tuning-step>\n" +
                "<tuning-octave>2</tuning-octave>\n" +
                "</staff-tuning>\n" +
                "<staff-tuning line=\"2\">\n" +
                "<tuning-step>A</tuning-step>\n" +
                "<tuning-octave>2</tuning-octave>\n" +
                "</staff-tuning>\n" +
                "<staff-tuning line=\"3\">\n" +
                "<tuning-step>D</tuning-step>\n" +
                "<tuning-octave>3</tuning-octave>\n" +
                "</staff-tuning>\n" +
                "<staff-tuning line=\"4\">\n" +
                "<tuning-step>G</tuning-step>\n" +
                "<tuning-octave>3</tuning-octave>\n" +
                "</staff-tuning>\n" +
                "<staff-tuning line=\"5\">\n" +
                "<tuning-step>B</tuning-step>\n" +
                "<tuning-octave>3</tuning-octave>\n" +
                "</staff-tuning>\n" +
                "<staff-tuning line=\"6\">\n" +
                "<tuning-step>E</tuning-step>\n" +
                "<tuning-octave>4</tuning-octave>\n" +
                "</staff-tuning>\n" +
                "</staff-details>\n" +
                "</attributes>\n";
        return attributes;
    }

    public String makeScript(){
        String script = "<measure number=\"" + measureNum + "\">\n";
        if(measureNum == 1){
            script += makeAttributes();
        }

        for(int i = 0; i < totalDurationPerMeasure; i++){
            HashMap<Integer, String> notations = new HashMap<>();
            for(int j = 0; j < NUM_OF_GUITAR_STRING; j++){
                if(notesBox[j][i] != null){
                    notations.put(j + 1, notesBox[j][i]);
                } //string num, notation
            }

            if(notations.isEmpty()){
                script += GuitarNote.makeRestNoteScript();
            }

            else if(notations.size() == 1){
                Iterator iter = notations.keySet().iterator();
                int key = (int) iter.next();
                script += GuitarNote.makeNoteScript(key, notations.get(key));
            }

            else{
                script += GuitarNote.makeChordNoteScript(notations);
            }
        }

        if(measureNum == lastMeasureNumber){
            script += "<barline location=\"right\">\n" +
                    "<bar-style>light-heavy</bar-style>\n" +
                    "</barline>\n";
        }
        script += "</measure>\n";
        return script;
    }
}
