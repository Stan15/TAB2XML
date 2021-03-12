package converter.GuitarConverter;

import java.util.ArrayList;


public class GNoteScript{
    private ArrayList<String> eachNoteScript;
    private int totalMeasureLength;
    private int totalDurationPerMeasure;
    public GNoteScript(ArrayList<Notation> notes, int totalMeasureLength, int totalDurationPerMeasure){
       this.eachNoteScript = new ArrayList<>();
       this.totalMeasureLength = totalMeasureLength;
       this.totalDurationPerMeasure = totalDurationPerMeasure;

        for(int i = 0; i < notes.size(); i++){
            ArrayList<Notation> tempNotes = new ArrayList<>();
            tempNotes.add(notes.get(i));
            int index = notes.get(i).getIndex();
            int j = index;
            while(i + 1 < notes.size() && index == notes.get(i + 1).getIndex()){
                tempNotes.add(notes.get(i + 1));
                i++;
            }
            int durationLength = 0;
            if(i + 1 < notes.size()){
                durationLength = notes.get(i + 1).getIndex() - notes.get(i).getIndex();
            }
            else {
                durationLength = totalMeasureLength - notes.get(i).getIndex();
            }

            makeNote(tempNotes, durationLength);
        }
    }

    private void makeNote(ArrayList<Notation> tempNotes, int durationLen){
        if(tempNotes.size() == 1){
            putScripts(tempNotes.get(0), durationLen);
        }
        else{
            for(int i = 0; i < tempNotes.size(); i++){
                putChordScripts(tempNotes.get(i), durationLen, i);
            }
        }
    }
    private void putChordScripts(Notation note , int durationLen, int index){
        int duration = (int) (((double)durationLen / (double)this.totalMeasureLength) * (double)totalDurationPerMeasure + 0.5);
        StringBuilder result = new StringBuilder("");
        if(index == 0){
            int singleRegularNote = Integer.valueOf(note.getNotation());
            int strNum = note.getStringNum() + 1;
            result.append("<note>\n");
            String pitch = pitchScript(octave(strNum, singleRegularNote), key(strNum, singleRegularNote));
            String type = typeScript(duration);
            result.append(pitch);
            result.append("<duration>" + duration + "</duration>\n" +
                    "<voice>1</voice>\n" +
                    type +
                    "<notations>\n" +
                    "<technical>\n" +
                    "<string>" + strNum + "</string>\n" +
                    "<fret>" + singleRegularNote + "</fret>\n" +
                    "</technical>\n" +
                    "</notations>\n" +
                    "</note>\n");
            eachNoteScript.add(result.toString());
        }
        else{
            int singleRegularNote = Integer.valueOf(note.getNotation());
            int strNum = note.getStringNum() + 1;
            result.append("<note>\n");
            result.append("<chord/>\n");
            String pitch = pitchScript(octave(strNum, singleRegularNote), key(strNum, singleRegularNote));
            String type = typeScript(duration);
            result.append(pitch);
            result.append("<duration>" + duration + "</duration>\n" +
                    "<voice>1</voice>\n" +
                    type +
                    "<notations>\n" +
                    "<technical>\n" +
                    "<string>" + strNum + "</string>\n" +
                    "<fret>" + singleRegularNote + "</fret>\n" +
                    "</technical>\n" +
                    "</notations>\n" +
                    "</note>\n");
            eachNoteScript.add(result.toString());
        }
    }
    private void putScripts(Notation note , int durationLen){
        int duration = (int) (((double)durationLen / (double)this.totalMeasureLength) * (double)totalDurationPerMeasure + 0.5);
        StringBuilder result = new StringBuilder("");
        if(note.getNotation().matches("[0-9]{1,2}")){
            int singleRegularNote = Integer.valueOf(note.getNotation());
            int strNum = note.getStringNum() + 1;
            result.append("<note>\n");
            String pitch = pitchScript(octave(strNum, singleRegularNote), key(strNum, singleRegularNote));
            String type = typeScript(duration);
            result.append(pitch);
            result.append("<duration>" + duration + "</duration>\n" +
                    "<voice>1</voice>\n" +
                    type +
                    "<notations>\n" +
                    "<technical>\n" +
                    "<string>" + strNum + "</string>\n" +
                    "<fret>" + singleRegularNote + "</fret>\n" +
                    "</technical>\n" +
                    "</notations>\n" +
                    "</note>\n");
            eachNoteScript.add(result.toString());
        }
        else if(note.getNotation().matches("^[[0-9]*[phPH][0-9]*]*")){
            boolean startMark = false;
            String[] connectedNotes = note.getNotation().split("[hHpP]");
            for(int i = 0; i < connectedNotes.length; i++){
                int eachNote = Integer.valueOf(connectedNotes[i]);
                int strNum = note.getStringNum() + 1;
                result.append("<note>\n");
                String pitch = pitchScript(octave(strNum, eachNote), key(strNum, eachNote));
                String type = typeScript(duration);
                result.append(pitch);
                if(i == 0){
                    result.append("<duration>" + duration + "</duration>\n" +
                            "<voice>1</voice>\n" +
                            type +
                            "<notations>\n" +
                            "<technical>\n" +
                            "<string>" + strNum + "</string>\n" +
                            "<fret>" + eachNote + "</fret>\n" +
                            "</technical>\n" +
                            "<slur number=\"1\" placement=\"above\" type=\"start\"/>\n" +
                            "</notations>\n" +
                            "</note>\n");
                    eachNoteScript.add(result.toString());
                }
                else if(i == connectedNotes.length - 1){
                    result.append("<duration>" + duration + "</duration>\n" +
                            "<voice>1</voice>\n" +
                            type +
                            "<notations>\n" +
                            "<technical>\n" +
                            "<string>" + strNum + "</string>\n" +
                            "<fret>" + eachNote + "</fret>\n" +
                            "</technical>\n" +
                            "<slur number=\"1\" type=\"stop\"/>\n" +
                            "</notations>\n" +
                            "</note>\n");
                    eachNoteScript.add(result.toString());
                }
                else{
                    result.append("<duration>" + duration + "</duration>\n" +
                            "<voice>1</voice>\n" +
                            type +
                            "<notations>\n" +
                            "<technical>\n" +
                            "<string>" + strNum + "</string>\n" +
                            "<fret>" + eachNote + "</fret>\n" +
                            "</technical>\n" +
                            "</notations>\n" +
                            "</note>\n");
                    eachNoteScript.add(result.toString());
                }
            }
        }

        else if(note.getNotation().matches("^[[0-9]*[/\\\\][0-9]*]*")){
            String[] slideNotes = note.getNotation().split("[/\\\\]");
            for(int i = 0; i < slideNotes.length; i++){
                int eachNote = Integer.valueOf(slideNotes[i]);
                int strNum = note.getStringNum() + 1;
                result.append("<note>\n");
                String pitch = pitchScript(octave(strNum, eachNote), key(strNum, eachNote));
                String type = typeScript(duration);
                result.append(pitch);
                if(i == 0){
                    result.append("<duration>" + duration + "</duration>\n" +
                            "<voice>1</voice>\n" +
                            type +
                            "<notations>\n" +
                            "<technical>\n" +
                            "<string>" + strNum + "</string>\n" +
                            "<fret>" + eachNote + "</fret>\n" +
                            "</technical>\n" +
                            "<slide line-type=\"solid\" number=\"1\" type=\"start\"/>\n" +
                            "</notations>\n" +
                            "</note>\n");
                    eachNoteScript.add(result.toString());
                }
                else if(i == slideNotes.length - 1){
                    result.append("<duration>" + duration + "</duration>\n" +
                            "<voice>1</voice>\n" +
                            type +
                            "<notations>\n" +
                            "<technical>\n" +
                            "<string>" + strNum + "</string>\n" +
                            "<fret>" + eachNote + "</fret>\n" +
                            "</technical>\n" +
                            "<slide line-type=\"solid\" number=\"1\" type=\"stop\"/>\n" +
                            "</notations>\n" +
                            "</note>\n");
                    eachNoteScript.add(result.toString());
                }
                else {
                    result.append("<duration>" + duration + "</duration>\n" +
                            "<voice>1</voice>\n" +
                            type +
                            "<notations>\n" +
                            "<technical>\n" +
                            "<string>" + strNum + "</string>\n" +
                            "<fret>" + eachNote + "</fret>\n" +
                            "</technical>\n" +
                            "<slide line-type=\"solid\" number=\"1\" type=\"stop\"/>\n" +
                            "<slide line-type=\"solid\" number=\"1\" type=\"start\"/>\n" +
                            "</notations>\n" +
                            "</note>\n");
                    eachNoteScript.add(result.toString());
                }
            }
        }
        else if(note.getNotation() == null){
            String type = typeScript(duration);
            String restNote ="<note>\n" +
                    "<rest/>\n" +
                    "<duration>" + duration + "</duration>\n" +
                    "<voice>1</voice>\n" +
                    type +
                    "</note>\n";
            eachNoteScript.add(restNote);
        }
    }

    public ArrayList<String> getEachNoteScript(){
        return this.eachNoteScript;
    }

    private String typeScript(int duration){
        StringBuilder builder = new StringBuilder("<type>");
        if(duration == 1){
            builder.append("16th</type>\n");
        }
        else if(duration == 2){
            builder.append("eighth</type>\n");
        }
        else if(duration == 3){
            builder.append("eighth</type>\n");
            builder.append("<dot/>\n");
        }
        else if(duration == 4 || duration == 5){
            builder.append("quarter</type>\n");
        }
        else if(duration == 6 || duration == 7){
            builder.append("quarter</type>\n");
            builder.append("<dot/>\n");
        }
        else if(duration >= 8 && duration <= 11){
            builder.append("half</type>\n");
        }
        else if(duration >= 12 && duration <= 15){
            builder.append("half</type>\n");
            builder.append("<dot/>\n");
        }
        else{
            builder.append("whole</type>\n");
        }
        return builder.toString();
    }

    public String pitchScript(int octave, String key) {

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

    public int octave(int stringNumber, int fret) {
        int octave;
        if(stringNumber == 6) {
            if(fret >= 0 && fret <= 7) {
                octave = 2;
            }
            else if(fret >=8 && fret <= 19){
                octave = 3;
            }
            else{
                octave = 4;
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
            else if(fret >= 10 && fret <= 21){
                octave = 4;
            }
            else{
                octave = 5;
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
            else if(fret >= 8 && fret <= 19){
                octave = 5;
            }
            else{
                octave = 6;
            }
        }
        return octave;
    }

    public String key(int stringNumber, int fret) {
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
