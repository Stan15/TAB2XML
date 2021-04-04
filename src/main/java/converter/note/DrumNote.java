package converter.note;

import models.measure.note.Beam;
import models.measure.note.Instrument;
import models.measure.note.Unpitched;

public class DrumNote extends Note{

    String DrumId;
    public static String COMPONENT_PATTERN = createComponentPattern();

    public DrumNote (String origin, int position, String lineName, int distanceFromMeasure){
        super(origin, position, lineName, distanceFromMeasure);
        this.DrumId = getDrumID(lineName);
    }
    // line is line
    public String getDrumID(String lineName){
        lineName = lineName.strip();
        if (lineName.equalsIgnoreCase("CC")) //crash Cymbal
            return "P1-I50";
        else if (lineName.equalsIgnoreCase("R")) // ride Cymbal
            return "P1-I52";
        else if (lineName.equals("HT")) // Low Mid Tom = High Tom
            return "P1-I48";
        else if (lineName.equals("MT")) // Low Tom = Medium Tom
            return "P1-I46";
        else if (lineName.equals("FT")) // Low Floor Tom = Low Tom
            return "P1-I42";
        else if (lineName.equals("HH")) { // High Hat there are two types
            /**
             * if note equals to X, closed high hat
             * return P1-143
             *
             * if note equals to O, open high hat
             * return P1-147
             */
            //return "P1-I45";
        }
        else if (lineName.equals("SD")) // Snare Drum
            return "P1-I39";
        else if (lineName.equals("BD")) // Base Drum 1 id
            return "P1-I36";
        return "";
    }
    public String toXML() {
        return null;
    }
    /**
     * gets the type of this model depending on what you need
     *  like notes, duration of notes, which drum part,
     */
    public models.measure.note.Note getModel(){ //toXML
        // linename
        // getType   of note (eighth, quarter etc)

        models.measure.note.Note noteModel = new models.measure.note.Note();

        noteModel.setUnpitched(IDtoDisplayStepAndDisplayOctave());
        noteModel.setDuration(2); //to-do
        noteModel.setInstrument(new Instrument(this.DrumId));
        noteModel.setVoice(1);
        noteModel.setType("");// to-do
        noteModel.setStem(""); // to-do
        noteModel.setNotehead("X/O"); // to do
        noteModel.setBeam(new Beam());




        return noteModel;

    }

    public Unpitched IDtoDisplayStepAndDisplayOctave(){

        if (this.DrumId == "P1-I36"){ //bass drum
            return new Unpitched("F", 4);
        }
        if (this.DrumId == "P1-I39"){ //snare
            return new Unpitched("C", 5);
        }
        if ((this.DrumId == "P1-I43") || (this.DrumId == "P1-I47")){ // high hat - closed or open
            return new Unpitched("G", 5);
        }
        if (this.DrumId == "P1-I52"){ //ride cymbal
            return new Unpitched("F", 5);
        }
        if (this.DrumId == "P1-I50"){
            return new Unpitched("A", 5);
        }
        if (this.DrumId == "P1-I48"){
            return new Unpitched("E", 5);
        }
        if (this.DrumId == "P1-I46"){
            return new Unpitched("D", 5);
        }
        if (this.DrumId == "P1-I42"){
            return new Unpitched("A", 4);
        }

        return new Unpitched("invalid", 0);
    }







    private static String createComponentPattern() {
        return "[xoXOdDfF]";
    }
}