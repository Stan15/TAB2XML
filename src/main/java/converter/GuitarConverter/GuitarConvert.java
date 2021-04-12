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
    //aaaa
    public GuitarConvert(){}

    //I assumed this class receives ArrayList has whole bar per line as String.
    //e.g) bars.get(0) = first whole bar line, like this. Whole one line is one element.
    //|---------|---3------2--|---2-8--------|\n
    //|---------|---0---------|--------------|\n
    //|---------|-------------|---7----------|\n
    //|---------|----------6--|------------0-|\n
    //|-------0-|-------------|-1------------|\n
    //|-------0-|---3---------|--10----------|\n


    public static ArrayList<String> tempConvert(String whole){
        String[] everyLines = whole.split("\n");
        ArrayList<String> result = new ArrayList<>();
        int i = 0;
        while(i < everyLines.length){
            if(everyLines[i].contains("-") && everyLines[i].contains("|") && !everyLines[i].contains(" ") && !everyLines[i].equals("")){
                StringBuilder temp = new StringBuilder("");
                int j = 0;
                while(j < 6){
                    temp.append(everyLines[i]);
                    temp.append("\n");
                    j++;
                    i++;
                }
                result.add(temp.toString());
            }
            i++;
        }

        return result;
    }
    public GuitarConvert(ArrayList<String> WholeBars){

        this.measureNumber = 0;
        this.instrumentName = "Acoustic Guitar";
        this.barsPerline = WholeBars;
        this.measureInfoWithNumber = new HashMap<>();
        this.measures = new ArrayList<>();
        //initializing

        for(int i = 0; i < barsPerline.size(); i++){

            String temp = barsPerline.get(i);

            if(temp.charAt(temp.length() - 1) != '\n'){
                temp += "\n";
            }//for the case that last line doesn't have '\n' at the end of the string(e.g the last line of the last bar)

            ArrayList<String> storedLines = splitByline(temp);
            //.get(0) = |---------|---3------2--|---2-8--------|
            //.get(1) = |---------|---0---------|--------------|
            //..

            String[][] measure2Dinfo = make2DarrInfo(storedLines);
            // [0][0] = ---------, [0][1] = ---3------2--, [0][2] = ---2-8--------
            // [1][0] = ---------, [1][1] = ---0---------, [1][2] = --------------
            // first measure       second measure          third measure....

            putColumnOf2DArr(measure2Dinfo, this.measureInfoWithNumber);
            //measureInfoWithNumber.get(1) = {"---------","---------","---------","---------","-------0-","-------0-"}
            //.get(2) = {"---3------2--","---0---------","-------------","----------6--","-------------","---3---------"}
            //the number of measure(key) each 6 lines(value) (hash map)
        }

        for(int i = 0; i < this.measureInfoWithNumber.size(); i++){
            GuitarMeasure measureClass = new GuitarMeasure(i + 1, this.measureInfoWithNumber.get(i + 1));
            //measure number starts from 1

            this.measures.add(measureClass);
            //Construct measureClass list
        }

        this.lastMeasureNumber = measures.size();
        //Store last measureNumber to change the last bar line symbol in score

    }
    private ArrayList<String> splitByline(String wholeLine){
        ArrayList<String> storedLines = new ArrayList<>();
        int splitIndex = wholeLine.indexOf("\n");
        //Split the wholeline by '\n'.

        for(int j = 0; splitIndex != -1; j++){
            if(!wholeLine.substring(0, splitIndex).equals("") && !wholeLine.substring(0, splitIndex).equals(" ")) {
                storedLines.add(wholeLine.substring(0, splitIndex));
                wholeLine = wholeLine.substring(splitIndex + 1);
                splitIndex = wholeLine.indexOf("\n");
            }
            // Store lines when it has information
            // Repeat it untill there is no more '\n' to split
        }
        return storedLines;
    }

    //make 2D array to collect notes information by each measure.
    private String[][] make2DarrInfo(ArrayList<String> storedLines){

        String[][] to2DArr = new String[storedLines.size()][];
        //storedLines's size = the string number of instrument
        //Since it's a guitar converter, it's 6.

        for(int i = 0; i < storedLines.size(); i++){
            String[] measures = storedLines.get(i).split("[|]");
            //Split information by '|'. Therefore, length of it = the number of measures

            ArrayList<String> temp = new ArrayList<>();
            for(int j = 0; j < measures.length; j++){
                if(!measures[j].equals("") && !measures[j].equals(" ") && measures[j].contains("-")){
                    temp.add(measures[j]);
                }// Store measures when it has information
            }
            to2DArr[i] = new String[temp.size()];
            //initialize column size of 2D array for each row

            for(int j = 0; j < temp.size(); j++){
                to2DArr[i][j] = temp.get(j);
            }//make each row of 2D array(= each measure's 'i+1'th string information)
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
        }//Collect each measure information from 2D array and make hash map with measure number(key)
        // (value = information of string 1 to 6 for each one measure)
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
        // basic mandatory information

        for(int i = 0; i < measures.size(); i++){
            String measureScript = measures.get(i).makeScript();
            script += measureScript;
        }// make each measure script from measure number 1 and add it

        script += "</part>\n" +
                "</score-partwise>\n";
        // close xml script

        return script;
    }
}

