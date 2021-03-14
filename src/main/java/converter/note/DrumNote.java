package converter.note;

import models.measure.note.Instrument;
import models.measure.note.Unpitched;

public class DrumNote extends Note{

    public static String COMPONENT_PATTERN = createComponentPattern();

    public DrumNote (String line, String lineName, int distanceFromStart, int position){
        super(line, lineName, distanceFromStart, position);
    }
    // line is line
    public String getDrumID(String lineName){
        lineName = lineName.strip();
        if (lineName.equalsIgnoreCase("C")) //crash Cymbal
            return "P1-I50";
        else if (lineName.equalsIgnoreCase("R")) // ride Cymbal
            return "P1-I52";
        else if (lineName.equals("T")) //High Tom
            return "P1-I51";
        else if (lineName.equals("t")) // medium Tom ??? there are two; low-mid Tom and high-mid Tom
            return "P1-I48";
        else if (lineName.equals("FT")) // floor Tom ; there are two ; high and low floor tom
            return "P1-I42";
        else if (lineName.equals("HH")) // High Hat
            return "P1-I45";
        else if (lineName.equals("SD")) // Snare Drum
            return "P1-I39";
        else if (lineName.equals("B")) // Base Drum 1 id; there are two base drum ids.
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
        noteModel.setInstrument(new Instrument(getDrumID(this.lineName)));
        noteModel.setVoice(1);
        //noteModel.setUnpitched(new Unpitched());


        return noteModel;

    }



    private static String createComponentPattern() {
        return "[xoXOdDfF]";
    }
}
