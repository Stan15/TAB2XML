package converter.GuitarConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class GMeasure extends GWholeScript{
    protected String measureInfo;
    protected int repeatInfo;
    protected String[][] lines2Darr;
    protected ArrayList<GNotes> eachMeasrueNotes;

    public GMeasure(){}
    public GMeasure(String measureInfo, int repeatInfo){
        this.measureInfo = measureInfo;
        this.repeatInfo = repeatInfo;
        this.eachMeasrueNotes = new ArrayList<>();

        ArrayList<String> storedLines = splitByLines(this.measureInfo);
        this.lines2Darr = make2Darr(storedLines);

        setEachMeasrueNotes();



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
            String[] split = storedLines.get(i).split("|");
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

    private void setEachMeasrueNotes(){
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
