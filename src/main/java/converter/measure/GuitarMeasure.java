package converter.measure;

import GUI.TabInput;
import converter.Score;
import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;
import converter.note.Note;
import models.measure.Backup;
import models.measure.attributes.*;
import models.measure.barline.BarLine;
import models.measure.barline.Repeat;
import models.measure.direction.Direction;
import models.measure.direction.DirectionType;
import models.measure.direction.Words;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuitarMeasure extends Measure{
    private static final int MIN_LINE_COUNT = 6;
    private static final int MAX_LINE_COUNT = 6;

    public GuitarMeasure(List<String> lines, List<String[]> lineNamesAndPositions, List<Integer> linePositions, boolean isFirstMeasure) {
        super(lines, lineNamesAndPositions, linePositions, isFirstMeasure);
        this.lineNamesAndPositions = this.fixNamingOfE(lineNamesAndPositions);
        this.measureLineList = this.createMeasureLineList(this.lines, this.lineNamesAndPositions, this.positions);
        this.sortedNoteList = this.getSortedNoteList();
        this.voiceSortedNoteList = this.getVoiceSortedNoteList();
        setChords();
        calcDurationRatios();
    }


    private List<String[]>  fixNamingOfE(List<String[]> lineNamesAndPositions) {
        int lowerEcount = 0;
        int upperEcount = 0;
        StringBuilder order = new StringBuilder();
        for (String[] nameAndPosition : lineNamesAndPositions) {
            String strippedName = nameAndPosition[0].strip();
            order.append(strippedName.toLowerCase());
            if (strippedName.equals("E"))
                upperEcount++;
            else if (strippedName.equals("e"))
                lowerEcount++;
        }

        //if it's not in order, we have no certainty as to what E tuning they are referring to. leave it as it is
        if (!order.toString().equals("ebgdae") && !order.toString().equals("eadgbe")) return lineNamesAndPositions;

        //if there are not multiple Es (multiple lower case e or multiple upper case E) then there's nothing to decipher
        if (!(lowerEcount>1 || upperEcount>1)) return lineNamesAndPositions;

        String prevName = null;
        for (int i=0; i<lineNamesAndPositions.size(); i++) {
            String name = lineNamesAndPositions.get(i)[0];
            if (!name.strip().equalsIgnoreCase("e")) {
                prevName = name;
                continue;
            }
            ArrayList<String> surroundingNames = new ArrayList<>();
            if (prevName!=null)
                surroundingNames.add(prevName.strip().toLowerCase());
            if (i+1<lineNamesAndPositions.size()) {
                surroundingNames.add(lineNamesAndPositions.get(i+1)[0].strip().toLowerCase());
            }

            if (surroundingNames.contains("b"))
                lineNamesAndPositions.get(i)[0] = lineNamesAndPositions.get(i)[0].toLowerCase();  //lower case e
            else lineNamesAndPositions.get(i)[0] = lineNamesAndPositions.get(i)[0].toUpperCase();  //upper case E

            prevName = name;
        }

        return lineNamesAndPositions;
    }

    /**
     * Validates that all MeasureLine objects in this GuitarMeasure are GuitarMeasureLine objects, and validates its
     * aggregate MeasureLine objects. It stops evaluation at the first aggregated object which fails validation.
     * TODO it might be better to not have it stop when one aggregated object fails validation, but instead have it
     *      validate all of them and return a List of all aggregated objects that failed validation, so the user knows
     *      all what is wrong with their tablature file, instead of having to fix one problem before being able to see
     *      what the other problems with their text file is.
     * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
     * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
     * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
     * found in the root string from which it was derived (i.e Score.ROOT_STRING).
     * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public List<HashMap<String, String>> validate() {
        //-----------------Validate yourself-------------------------
        List<HashMap<String,String>> result = new ArrayList<>(super.validate()); //this validates if all MeasureLine objects in this measure are of the same type

        // Now, all we need to do is check if they are actually guitar measures
        if (!(this.measureLineList.get(0) instanceof GuitarMeasureLine)) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "All measure lines in this measure must be Guitar measure lines.");
            response.put("positions", this.getLinePositions());
            int priority = 1;
            response.put("priority", ""+priority);
            if (TabInput.ERROR_SENSITIVITY>=priority)
                result.add(response);
        }else if (this.measureLineList.size()<MIN_LINE_COUNT || this.measureLineList.size()>MAX_LINE_COUNT) {
            HashMap<String, String> response = new HashMap<>();
            String rangeMsg;
            if (MIN_LINE_COUNT==MAX_LINE_COUNT)
                rangeMsg = ""+MIN_LINE_COUNT;
            else
                rangeMsg = "between "+MIN_LINE_COUNT+" and "+MAX_LINE_COUNT;
            response.put("message", "A Guitar measure should have "+rangeMsg+" lines.");
            response.put("positions", this.getLinePositions());
            int priority = 2;
            response.put("priority", ""+priority);
            if (TabInput.ERROR_SENSITIVITY>=priority)
                result.add(response);
        }


        //-----------------Validate Aggregates (only if you are valid)------------------
        if (!result.isEmpty()) return result;

        for (MeasureLine measureLine : this.measureLineList) {
            result.addAll(measureLine.validate());
        }

        return result;
    }

    private Attributes getAttributesModel() {
        Attributes attributes = new Attributes();
        attributes.setKey(new Key(0));
        if (this.isTimeSigOverridden())
            attributes.setTime(new Time(this.beatCount, this.beatType));

        if (this.measureCount == 1) {
            attributes.setClef(new Clef("TAB", 5));
            attributes.setDivisions(Score.GLOBAL_DIVISIONS);
            List<StaffTuning> staffTunings = new ArrayList<>();
            staffTunings.add(new StaffTuning(1, "E", 2));
            staffTunings.add(new StaffTuning(2, "A", 2));
            staffTunings.add(new StaffTuning(3, "D", 3));
            staffTunings.add(new StaffTuning(4, "G", 3));
            staffTunings.add(new StaffTuning(5, "B", 3));
            staffTunings.add(new StaffTuning(6, "E", 4));

            attributes.setStaffDetails(new StaffDetails(6, staffTunings));
        }


        return attributes;
    }

    public models.measure.Measure getModel() {
        models.measure.Measure measureModel = new models.measure.Measure();
        measureModel.setNumber(this.measureCount);
        measureModel.setAttributes(this.getAttributesModel());

        List<models.measure.note.Note> noteBeforeBackupModels = new ArrayList<>();
        List<models.measure.note.Note> noteAfterBackupModels = new ArrayList<>();
        for (int i=0; i<this.voiceSortedNoteList.size(); i++) {
            List<Note> voice = this.voiceSortedNoteList.get(i);
            double backupDuration = 0;
            for (Note note : voice) {
                if (note.voice==1)
                    noteBeforeBackupModels.add(note.getModel());
                if (note.voice==2)
                    noteAfterBackupModels.add(note.getModel());
                backupDuration += note.duration;
            }
            if (voice.get(0).voice==1)
                measureModel.setNotesBeforeBackup(noteBeforeBackupModels);
            if (voice.get(0).voice==2)
                measureModel.setNotesBeforeBackup(noteAfterBackupModels);
            if (i+1<this.voiceSortedNoteList.size()) {
                measureModel.getBackup().add(new Backup((int)backupDuration));
            }
        }

        List<BarLine> barLines = new ArrayList<>();
        if (this.isRepeatStart()) {
            BarLine barLine = new BarLine();
            barLines.add(barLine);
            barLine.setLocation("left");
            barLine.setBarStyle("heavy-light");

            Repeat repeat = new Repeat();
            repeat.setDirection("forward");
            barLine.setRepeat(repeat);

            Direction direction = new Direction();
            direction.setPlacement("above");
            measureModel.setDirection(direction);

            DirectionType directionType = new DirectionType();
            direction.setDirectionType(directionType);

            Words words = new Words();
            words.setRelativeX(256.17);
            words.setRelativeX(16.01);
            words.setRepeatText("Repeat "+this.repeatCount+" times");
            directionType.setWords(words);
        }

        if (this.isRepeatEnd()) {
            BarLine barLine = new BarLine();
            barLines.add(barLine);
            barLine.setLocation("right");
            barLine.setBarStyle("light-heavy");

            Repeat repeat = new Repeat();
            repeat.setDirection("backward");
            barLine.setRepeat(repeat);
        }

        if (!barLines.isEmpty())
            measureModel.setBarlines(barLines);
        return measureModel;
    }


}
