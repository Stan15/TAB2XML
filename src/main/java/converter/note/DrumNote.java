package converter.note;

import GUI.TabInput;
import converter.Instrument;
import models.measure.note.Chord;
import models.measure.note.Unpitched;
import utility.DrumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrumNote extends Note{

    String partID;
    public static String COMPONENT_PATTERN = createComponentPattern();

    public DrumNote (String origin, int position, String lineName, int distanceFromMeasure){
        super(origin, position, lineName, distanceFromMeasure);
        this.instrument = Instrument.DRUM;
        this.partID = DrumUtils.getPartID(lineName, origin);
        if (lineName.strip().equalsIgnoreCase("BD"))
            this.voice = 2;
    }

    /**
     * gets the type of this model depending on what you need
     *  like notes, duration of notes, which drum part,
     */
    public models.measure.note.Note getModel(){ //toXML
        models.measure.note.Note noteModel = new models.measure.note.Note();
        if (this.startsWithPreviousNote)
            noteModel.setChord(new Chord());

        noteModel.setUnpitched(IDtoDisplayStepAndDisplayOctave());
        noteModel.setDuration((int) this.duration);
        noteModel.setInstrument(new models.measure.note.Instrument(this.partID));
        noteModel.setVoice(this.voice);
        String noteType = this.getType();
        if (!noteType.isEmpty())
            noteModel.setType(noteType);
        noteModel.setStem(this.lineName.strip().equalsIgnoreCase("BD") ? "down" : "up");
        String noteHead = this.origin.strip();
        if (!(noteHead.equalsIgnoreCase("f") || noteHead.equalsIgnoreCase("d")))
            noteModel.setNotehead(noteHead);
        for (NoteFactory.NoteDecor noteDecor : this.noteDecorMap.keySet()) {
            if (noteDecorMap.get(noteDecor).equals("success"))
                noteDecor.applyTo(noteModel);
        }
        return noteModel;
    }

    public Unpitched IDtoDisplayStepAndDisplayOctave(){
        return this.partID==null ? null : new Unpitched(DrumUtils.getDisplayStep(this.partID), DrumUtils.getDisplayOctave(this.partID));
    }

    public List<HashMap<String, String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>(super.validate());

        for (NoteFactory.NoteDecor noteDecor : this.noteDecorMap.keySet()) {
            String resp = noteDecorMap.get(noteDecor);
            if (resp.equals("success")) continue;
            Matcher matcher = Pattern.compile("(?<=^\\[)[0-9](?=\\])").matcher(resp);
            matcher.find();
            int priority = Integer.parseInt(matcher.group());
            String message = resp.substring(matcher.end()+1);;
            int startIdx = this.position;
            int endIdx = this.position+this.origin.length();


            matcher = Pattern.compile("(?<=^\\[)[0-9]+,[0-9]+(?=\\])").matcher(message);
            if (matcher.find()) {
                String positions = matcher.group();
                matcher = Pattern.compile("[0-9]+").matcher(positions); matcher.find();
                startIdx = Integer.parseInt(matcher.group()); matcher.find();
                endIdx = Integer.parseInt(matcher.group());
                message = message.substring(matcher.end()+2);
            }

            HashMap<String, String> response = new HashMap<>();
            response.put("message", message);
            response.put("positions", "["+startIdx+","+endIdx+"]");
            response.put("priority", ""+priority);
            if (TabInput.ERROR_SENSITIVITY>=priority)
                result.add(response);
        }

        return result;
    }



    private static String createComponentPattern() {
        return "[xoXOdDfF]";
    }
}