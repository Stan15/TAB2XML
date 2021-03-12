package converter.GuitarConverter;


import java.util.ArrayList;

public class GNotes extends GMeasure{
    protected int totalDurationPerMeasrue;
    protected int totalMeasureLength;
    protected ArrayList<Notation> notes;
    protected char[][] noteBox;

    public GNotes(ArrayList<String> eachMeasureInfo){
        this.totalDurationPerMeasrue = BEATS * DIVISION;
        this.totalMeasureLength = eachMeasureInfo.get(0).length();

        this.noteBox = new char[eachMeasureInfo.size()][];
        for(int i = 0; i < this.noteBox.length; i++){
            this.noteBox[i] = eachMeasureInfo.get(i).toCharArray();
        }

        for(int i = 0; i < totalMeasureLength; i++){
            for(int j = 0; j < noteBox.length; j++){
                if(noteBox[j][i] != '\000'){

                }
            }
        }

    }

    private void putNotes(String lines, int stringNum){
        String temp = lines;
        String[] splitChar = temp.split("[-]");
        ArrayList<String> notations = new ArrayList<>();
        for(String notation : splitChar){
            if(!notation.equals("") && !notation.equals(" ")){
                notations.add(notation);
            }
        }

        for(String notation : notations){
            int index = temp.indexOf(notation);
            Notation newNote = new Notation(notation, index, stringNum);
            notes.add(newNote);
        }
    }

    private int setPosition(int index){
        int resultIndex = -1;
        double boxIndex = (double)index / (double)(this.totalMeasureLength);
        for(int i = 1; i <= totalDurationPerMeasrue; i++){
            if(boxIndex <= i / totalDurationPerMeasrue){
                resultIndex = i - 1;
                break;
            }
        }
        return resultIndex;
    }
}
class Notation{
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
}
