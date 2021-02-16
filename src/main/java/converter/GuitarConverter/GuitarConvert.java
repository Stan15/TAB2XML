package converter.GuitarConverter;

import java.util.ArrayList;
import java.util.HashMap;

public class GuitarConvert {
    protected int measureNumber;
    protected static int lastMeasureNumber;
    protected String instrumentName;
    protected HashMap<Integer, ArrayList<String>> measureInfoWithNumber;
    protected ArrayList<GuitarMeasure> measures;

    protected final int INSTRUMENT_STRING_NUM = 6;
    protected ArrayList<String> barsPerline;

    public GuitarConvert(){}

    public static void main(String[] args) {
        ArrayList<String> o = new ArrayList<>();
        o.add("|---------------------------------|------------------------------------|\n" +
                "|-----1---------1-----1-----------|-------1---------------1----------1-|\n" +
                "|-----------0------------0--------|-------------0----------------0-----|\n" +
                "|-----0-------2-------2-----------|-------2-------2-------0--------0---|\n" +
                "|-3-------3-------3-------3-------|------------------------------------|\n" +
                "|-----------------------------0---|---1-------1-------3-------3--------|\n");
        o.add("|------------------------------------------------------------------|\n" +
                "|-----1---------1-----0---------0-----1---------1-----3---------3--|\n" +
                "|-----------0---------------0---------------0---------------0------|\n" +
                "|-----2-------2-------2-------2-------2-------2-------0-------0----|\n" +
                "|-0-------0--------------------------------------------------------|\n" +
                "|-----------------0-------0--------1------1-------3-------3--------|\n");
        GuitarConvert ex = new GuitarConvert(o);
        String aa = ex.makeScript();
        System.out.println(aa);
    }// example

    //I assumed this class receives ArrayList has whole bar per line as String.
    //e.g) bars.get(0) = first whole bar line, like this. Whole one line is one element.
    //|---------|---3------2--|---2-8--------|\n
    //|---------|---0---------|--------------|\n
    //|---------|-------------|---7----------|\n
    //|---------|----------6--|------------0-|\n
    //|-------0-|-------------|-1------------|\n
    //|-------0-|---3---------|--10----------|\n

    //pre condition: each Whole bar should has the same number of measures.

    public GuitarConvert(ArrayList<String> WholeBars){

        this.measureNumber = 0;
        this.instrumentName = "Acoustic Guitar";
        this.barsPerline = WholeBars;
        this.measureInfoWithNumber = new HashMap<>();
        this.measures = new ArrayList<>();


        for(int i = 0; i < barsPerline.size(); i++){

            String temp = barsPerline.get(i);
            ArrayList<String> storedLines = splitByline(temp);
            //.get(0) = |---------|---3------2--|---2-8--------|
            //.get(1) = |---------|---0---------|--------------|
            //..

            String[][] measure2Dinfo = make2DarrInfo(storedLines);
            // [0][0] = ---------, [0][1] = ---3------2--, [0][2] = ---2-8--------
            // [1][0] = ---------, [1][1] = ---0---------, [1][2] = --------------
            // ..

            putColumnOf2DArr(measure2Dinfo, this.measureInfoWithNumber);
            //measureInfoWithNumber.get(1) = {"---------","---------","---------","---------","-------0-","-------0-"}
            //.get(2) = {"---3------2--","---0---------","-------------","----------6--","-------------","---3---------"}
            //..
        }

        for(int i = 0; i < this.measureInfoWithNumber.size(); i++){
            GuitarMeasure measureClass = new GuitarMeasure(i + 1, this.measureInfoWithNumber.get(i + 1));
            this.measures.add(measureClass);
        }

        this.lastMeasureNumber = measures.size();

    }
    private ArrayList<String> splitByline(String wholeLine){
        ArrayList<String> storedLines = new ArrayList<>();
        int splitIndex = wholeLine.indexOf("\n");
        for(int j = 0; splitIndex != -1; j++){
            if(!wholeLine.substring(0, splitIndex).equals("")){
                storedLines.add(wholeLine.substring(0, splitIndex));
                wholeLine = wholeLine.substring(splitIndex + 1);
                splitIndex = wholeLine.indexOf("\n");
            }
        }
        return storedLines;
    }

    private String[][] make2DarrInfo(ArrayList<String> storedLines){

        String[][] to2DArr = new String[storedLines.size()][];
        for(int i = 0; i < storedLines.size(); i++){
            String[] measures = storedLines.get(i).split("[|]");
            ArrayList<String> temp = new ArrayList<>();
            for(int j = 0; j < measures.length; j++){
                if(!measures[j].equals("") && !measures[j].equals(" ")){
                    temp.add(measures[j]);
                }
            }
            to2DArr[i] = new String[temp.size()];
            for(int j = 0; j < temp.size(); j++){
                to2DArr[i][j] = temp.get(j);
            }
        }
        return to2DArr;
    }

    private void putColumnOf2DArr(String[][] measure2Dinfo, HashMap<Integer, ArrayList<String>> measureInfoWithNumber){
        for(int j = 0; j < measure2Dinfo[j].length; j++){
            ArrayList<String> columnOf2D = new ArrayList<>();
            for(int k = 0; k < measure2Dinfo.length; k++){
                columnOf2D.add(measure2Dinfo[k][j]);
            }
            this.measureNumber++;
            measureInfoWithNumber.put(this.measureNumber, columnOf2D);
        }
    }

    public String makeScript(){
        String script = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE score-partwise PUBLIC \"-//Recordare//DTD MusicXML 3.1 Partwise//EN\" \"http://www.musicxml.org/dtds/partwise.dtd\">\n" +
                "<score-partwise version=\"3.1\">\n" +
                "<part-list>\n" +
                "<score-part id=\"P1\">\n" +
                "<part-name>" + instrumentName + "</part-name>\n" +
                "</score-part>\n" +
                "</part-list>\n" +
                "<part id=\"P1\">\n";

        for(int i = 0; i < measures.size(); i++){
            String measureScript = measures.get(i).makeScript();
            script += measureScript;
        }

        script += "</part>\n" +
                "</score-partwise>\n";

        return script;
    }
}
