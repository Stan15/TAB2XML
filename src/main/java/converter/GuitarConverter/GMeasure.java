package converter.GuitarConverter;

import java.util.ArrayList;

public class GMeasure {
    protected final int BEATS = 4;
    protected final int BEAT_TYPE = 4;
    protected final int DIVISION = 4;
    protected final int STRING_NUM = 6;
    protected String measureInfo;
    protected int repeatInfo;
    protected String[][] lines2Darr;
    protected ArrayList<GNotes> eachMeasrueNotes;
    protected ArrayList<String> scriptsPerMeasrue;

    public static void main(String[] args) {
        String a = "|-----------0------0-------------------------------|\r\n"
                 + "|---------0---0----0-------------------------------|\r\n"
                 + "|-------1-------1--1------4h6p3--------------------|\r\n"
                 + "|-----2------------2--------------------3/5--------|\r\n"
                 + "|---2--------------2-------------------------------|\r\n"
                 + "|-0----------------0-------------------------------|";

        GMeasure aa = new GMeasure(a, 1);
        for(String ae : aa.scriptsPerMeasrue){
            System.out.println(ae);
        }
    }
    public GMeasure(){}
    public GMeasure(String measureInfo, int repeatInfo){
        this.measureInfo = measureInfo;
        this.repeatInfo = repeatInfo;
        this.eachMeasrueNotes = new ArrayList<>();
        this.scriptsPerMeasrue = new ArrayList<String>();

        ArrayList<String> storedLines = splitByLines(this.measureInfo);
        this.lines2Darr = make2Darr(storedLines);
        setEachMeasureNotes();

        for(int i = 0; i < this.repeatInfo; i++){
            for(GNotes o : eachMeasrueNotes)
            {
                ArrayList<String> temp = o.getCompletedNoteScript();
                for(String script : temp){
                    scriptsPerMeasrue.add(script);
                }
            }
        }
    }

    public ArrayList<String> getScriptsPerMeasrue(){
        return this.scriptsPerMeasrue;
    }

    private ArrayList<String> splitByLines(String measureInfo){
        ArrayList<String> storedLines = new ArrayList<>();
        String[] split = measureInfo.split("\n");
        for(String str : split){
            if(!str.equals(" ") && !str.equals("")){
                str = str.trim();
                storedLines.add(str);
            }
        }
        return storedLines;
    }

    private String[][] make2Darr(ArrayList<String> storedLines){
        String[][] linesByMeasure = new String[STRING_NUM][];
        for(int i = 0; i < linesByMeasure.length; i++){
            String[] split = storedLines.get(i).split("[|]");
            ArrayList<String> temp = new ArrayList<>();
            for(String str : split){
                if(!str.equals("") && !str.equals(" ") && str.contains("-")){
                    str = str.trim();
                    temp.add(str);
                }
            }
            linesByMeasure[i] = new String[temp.size()];
            for(int j = 0; j < temp.size(); j++){
                linesByMeasure[i][j] = temp.get(j);
            }
        }
        return linesByMeasure;
    }

    private void setEachMeasureNotes(){
        int totalMeasureNum = lines2Darr[0].length;
        for(int i = 0; i < totalMeasureNum; i++){
            ArrayList<String> eachMeasure = new ArrayList<>();
            for(int j = 0; j < STRING_NUM; j++){
                eachMeasure.add(lines2Darr[j][i]);
            }
            GNotes notes = new GNotes(eachMeasure);
            this.eachMeasrueNotes.add(notes);
        }
    }
}
