package converter.GuitarConverter;


import java.util.ArrayList;
import java.util.Collections;

public class GNotes extends GMeasure{
    protected int totalDurationPerMeasrue;
    protected int totalMeasureLength;
    protected ArrayList<Notation> notes;
    protected String[][] noteBox;
    protected static int frontRest;
    protected ArrayList<String> completedNoteScript;

    public GNotes(ArrayList<String> eachMeasureInfo){
        this.totalDurationPerMeasrue = BEATS * DIVISION;
        this.totalMeasureLength = eachMeasureInfo.get(0).length();
        this.notes = new ArrayList<>();
        this.frontRest = Integer.MAX_VALUE;

        this.noteBox = new String[eachMeasureInfo.size()][];
        for(int i = 0; i < this.noteBox.length; i++){
            this.noteBox[i] = new String[totalDurationPerMeasrue];
        }

        for(int i = 0; i < eachMeasureInfo.size(); i++){
            putNotes(eachMeasureInfo.get(i), i);
        }

        int frontRestDuration = (int) ((double)frontRest / (double)totalMeasureLength + 0.5);
        if(frontRestDuration >= 1){
            Notation restNote = new Notation(null,0, 0);
            notes.add(0, restNote);
        }

        Collections.sort(notes);
        GNoteScript noteScrt  = new GNoteScript(notes, totalMeasureLength, totalDurationPerMeasrue);
        this.completedNoteScript = noteScrt.getEachNoteScript();
    }

    public ArrayList<String> getCompletedNoteScript(){
        return this.completedNoteScript;
    }

    private void putNotes(String lines, int stringNum){
        String temp = lines;
        int totalLength = temp.length();
        String[] splitChar = temp.split("[-]");
        ArrayList<String> notations = new ArrayList<>();
        for(String notation : splitChar){
            if(!notation.equals("") && !notation.equals(" ")){
                notations.add(notation);
            }
        }

        for(int i = 0; i < notations.size(); i++){
            String notation = notations.get(i);
            int index = temp.indexOf(notation);
            if(i == 0){
                if(index < frontRest){
                    frontRest = index;
                }
            }
            Notation newNote = new Notation(notation, index, stringNum);
            notes.add(newNote);

            StringBuilder builder = new StringBuilder(temp);
            for(int j = index; j < index + notation.length(); j++){
                builder.setCharAt(j,'-');
            }

            temp = builder.toString();
        }
    }

    private int calculateDuration(int noteLen){
        int duration = -1;
        double boxIndex = (double)noteLen / (double)(this.totalMeasureLength);
        for(int i = 1; i <= totalDurationPerMeasrue; i++){
            if(boxIndex <= i / totalDurationPerMeasrue){
                duration = i - 1;
                break;
            }
        }
        return duration;
    }
}

class Notation implements Comparable{
    String notation;
    int index;
    int duration;
    int stringNum;
    public Notation(String notation, int index, int stringNum){
        this.notation = notation;
        this.index = index;
        this.stringNum = stringNum;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getNotation(){
        return this.notation;
    }

    public int getIndex(){
        return this.index;
    }

    public int getStringNum(){return this.stringNum; }


    @Override
    public int compareTo(Object o) {
        return this.index - ((Notation)o).getIndex();
    }
}
