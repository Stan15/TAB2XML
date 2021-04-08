package converter.note;

import models.measure.note.Instrument;
import models.measure.note.Unpitched;

public class DrumNote extends Note{

    String DrumId;
    public static String COMPONENT_PATTERN = createComponentPattern();

    public DrumNote (String origin, int position, String lineName, int distanceFromMeasure){
        super(origin, position, lineName, distanceFromMeasure);
        this.DrumId = getDrumID(lineName);
        if (lineName.strip().equalsIgnoreCase("BD"))
            this.voice = 2;
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
            if (origin.strip().equalsIgnoreCase("X"))
                return "P1-I43";
            else if (origin.strip().equalsIgnoreCase("O"))
                return "P1-I47";
        } else if (lineName.equals("SD")) // Snare Drum
            return "P1-I39";
        else if (lineName.equals("BD")) // Base Drum 1 id
            return "P1-I36";
        return "";
    }

    /**
     * gets the type of this model depending on what you need
     *  like notes, duration of notes, which drum part,
     */
    public models.measure.note.Note getModel(){ //toXML
        models.measure.note.Note noteModel = new models.measure.note.Note();

        noteModel.setUnpitched(IDtoDisplayStepAndDisplayOctave());
        noteModel.setDuration((int) this.duration);
        noteModel.setInstrument(new Instrument(this.DrumId));
        noteModel.setVoice(this.voice);
        String noteType = this.getType();
        if (!noteType.isEmpty())
            noteModel.setType(noteType);
        noteModel.setStem(this.lineName.equalsIgnoreCase("BD") ? "down" : "up");
        noteModel.setNotehead(this.origin.strip());
        for (NoteFactory.NoteDecor noteDecor : this.noteDecorMap.keySet()) {
            if (noteDecorMap.get(noteDecor).equals("success"))
                noteDecor.applyTo(noteModel);
        }
        return noteModel;
    }

    public Unpitched IDtoDisplayStepAndDisplayOctave(){

        if (this.DrumId.equals("P1-I36")) //bass drum
            return new Unpitched("F", 4);
        else if (this.DrumId.equals("P1-I39")) //snare
            return new Unpitched("C", 5);
        else if ((this.DrumId.equals("P1-I43")) || (this.DrumId.equals("P1-I47"))) // high hat - closed or open
            return new Unpitched("G", 5);
        else if (this.DrumId.equals("P1-I52")) //ride cymbal
            return new Unpitched("F", 5);
        else if (this.DrumId.equals("P1-I50"))
            return new Unpitched("A", 5);
        else if (this.DrumId.equals("P1-I48"))
            return new Unpitched("E", 5);
        else if (this.DrumId.equals("P1-I46"))
            return new Unpitched("D", 5);
        else if (this.DrumId.equals("P1-I42"))
            return new Unpitched("A", 4);
        return null;
    }




    private static String createComponentPattern() {
        return "[xoXOdDfF]";
    }
}