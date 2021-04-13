package converter.measure;

import GUI.TabInput;
import converter.Instrument;
import converter.Score;
import converter.ScoreComponent;
import converter.instruction.RepeatType;
import converter.instruction.TimeSignature;
import converter.measure_line.DrumMeasureLine;
import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;
import converter.note.Note;
import utility.Patterns;
import utility.Range;
import utility.ValidationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Measure implements ScoreComponent {
    public static int GLOBAL_MEASURE_COUNT;
    public static Instrument PREV_MEASURE_TYPE;
    private static double FOLLOW_PREV_MEASURE_WEIGHT = 0.3;
    protected int measureCount;
    int beatCount = Score.DEFAULT_BEAT_COUNT;
    int beatType = Score.DEFAULT_BEAT_TYPE;
    List<String> lines;
    List<String[]> lineNamesAndPositions;
    public int lineCount;
    List<Integer> positions;
    public List<MeasureLine> measureLineList;
    boolean isFirstMeasureInGroup;
    List<List<Note>> voiceSortedNoteList;   // a list of voices where each voice is a sorted list of notes

    boolean repeatStart = false;
    boolean repeatEnd = false;
    int repeatCount = 0;
    private boolean timeSigOverridden;
    public boolean isTimeSigOverridden() {
        return this.timeSigOverridden;
    }

    public int getBeatCount() {
        return this.beatCount;
    }
    public int getBeatType() {
        return this.beatType;
    }

    public Measure(List<String> lines, List<String[]> lineNamesAndPositions, List<Integer> linePositions, boolean isFirstMeasureInGroup) {
        this.measureCount = ++GLOBAL_MEASURE_COUNT;
        this.lines = lines;
        this.lineCount = this.lines.size();
        this.lineNamesAndPositions = lineNamesAndPositions;
        this.positions = linePositions;
        this.isFirstMeasureInGroup = isFirstMeasureInGroup;
    }

    /**
     * Creates a List of MeasureLine objects from the provided string representation of a Measure.
     * These MeasureLine objects are not guaranteed to be valid. you can find out if all the Measure
     * objects in this MeasureGroup are actually valid by calling the Measure().validate() method.
     * @param lines a List of Strings where each String represents a line of the measure. It is a parallel list with lineNames and linePositions
     * @param namesAndPosition a List of Strings where each String represents the name of a line of the measure. It is a parallel list with lines and linePositions
     * @param linePositions a List of Strings where each String represents the starting index of a line of the measure,
     *                      where a starting index of a line is the index where the line can be found in the root string,
     *                      Score.ROOT_STRING, from where it was derived. It is a parallel list with lineNames and lines
     * @return A list of MeasureLine objects. The concrete class type of these MeasureLine objects is determined
     * from the input String lists(lines and lineNames), and they are not guaranteed to all be of the same type.
     */
    protected List<MeasureLine> createMeasureLineList(List<String> lines, List<String[]> namesAndPosition, List<Integer> linePositions) {
        List<MeasureLine> measureLineList = new ArrayList<>();
        for (int i=0; i<lines.size(); i++) {
            String line = lines.get(i);
            String[] nameAndPosition = namesAndPosition.get(i);
            int position = linePositions.get(i);
            Instrument instrumentBias = this instanceof BassMeasure ? Instrument.BASS : this instanceof DrumMeasure ? Instrument.DRUM : this instanceof GuitarMeasure ? Instrument.GUITAR : Instrument.AUTO;
            measureLineList.add(MeasureLine.from(line, nameAndPosition, position, instrumentBias, this instanceof BassMeasure ? true : false));
        }
        return measureLineList;
    }

    /**
     * Creates an instance of the abstract Measure class whose concrete type is either GuitarMeasure or DrumMeasure, depending
     * on if the features of the input String Lists resemble a drum measure or a Guitar measure(this is determined by the
     * MeasureLine.isGuitar() and MeasureLine.isDrum() methods). If its features could not be deciphered or it has features
     * of both guitar and drum features, it defaults to creating a GuitarMeasure object and further error checking can
     * be done by calling GuitarMeasure().validate() on the object.
     * @param lineList A list of the insides of each measure lines that makes up this measure (without the line names) (parallel list with the other two List parameters)
     * @param lineNameList A list of the names of each the measure lines that makes up this measure (parallel list with the other two List parameters)
     * @param linePositionList A list of the positions of the insides of each of the measure lines that make up this (parallel list with the other two List parameters)
     *                         measure, where a line's position is the index at which the line is located in the root
     *                         String from which it was derived (Score.ROOT_STRING)
     * @param isFirstMeasureInGroup specifies weather this measure is the first one in its measure group. (useful to know, so we only add the xml measure attributes to the first measure)
     *
     * @return A Measure object which is either of type GuitarMeasure if the measure was understood to be a guitar
     * measure, or of type DrumMeasure if the measure was understood to be of type DrumMeasure
     */
    public static Measure from(List<String> lineList, List<String[]> lineNameList, List<Integer> linePositionList, boolean isFirstMeasureInGroup) {
        boolean repeatStart = checkRepeatStart(lineList);
        boolean repeatEnd = checkRepeatEnd(lineList);
        String repeatCountStr = extractRepeatCount(lineList);
        removeRepeatMarkings(lineList, linePositionList, repeatStart, repeatEnd, repeatCountStr);
        int repeatCount = 1;
        if (!repeatCountStr.isEmpty()) {
            Matcher numMatcher = Pattern.compile("(?<=\\])[0-9]+").matcher(repeatCountStr);
            numMatcher.find();
            repeatCountStr = numMatcher.group();
            repeatCount = Integer.parseInt(repeatCountStr);
        }

        Measure measure;
        if (Score.INSTRUMENT_MODE!=Instrument.AUTO) {
            measure = switch (Score.INSTRUMENT_MODE) {
                case GUITAR -> new GuitarMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
                case BASS -> new BassMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
                case DRUM -> new DrumMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
                case AUTO -> null;
            };
        }else {
            double guitarLikelihood = isGuitarMeasureLikelihood(lineList, lineNameList);
            double drumLikelihood = isDrumMeasureLikelihood(lineList, lineNameList);
            double bassLikelihood = isBassMeasureLikelihood(lineList, lineNameList);

            //adjusting values
            double guitarLikelihoodAdj = guitarLikelihood*(1-FOLLOW_PREV_MEASURE_WEIGHT) + (PREV_MEASURE_TYPE==Instrument.GUITAR ? FOLLOW_PREV_MEASURE_WEIGHT : 0);
            double drumLikelihoodAdj = drumLikelihood*(1-FOLLOW_PREV_MEASURE_WEIGHT) + (PREV_MEASURE_TYPE==Instrument.DRUM ? FOLLOW_PREV_MEASURE_WEIGHT : 0);
            double bassLikelihoodAdj = bassLikelihood*(1-FOLLOW_PREV_MEASURE_WEIGHT) + (PREV_MEASURE_TYPE==Instrument.BASS ? FOLLOW_PREV_MEASURE_WEIGHT : 0);

            if (guitarLikelihoodAdj >= drumLikelihoodAdj && guitarLikelihoodAdj >= bassLikelihoodAdj) {
                measure = new GuitarMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
                PREV_MEASURE_TYPE = Instrument.GUITAR;
                // the more confident we are about what type of measure this is, the more we want the next measure to be likely to follow it.
                //dont use the guitarLikelihoodAdj "Adj" score to calculate confidence or else the effect will build on itself everytime we adjust the FOLLOW_PREV_MEASURE_WEIGHT value
                double confidenceScore = guitarLikelihood-Math.min(Math.max(drumLikelihood, bassLikelihood), guitarLikelihood);

                //FOLLOW_PREV_MEASURE_WEIGHT = FOLLOW_PREV_MEASURE_WEIGHT * adjustRawConfidenceScore(confidenceScore);;
            }else if (bassLikelihoodAdj >= drumLikelihoodAdj){
                measure = new BassMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
                PREV_MEASURE_TYPE = Instrument.BASS;
                double confidenceScore = bassLikelihood-Math.min(Math.max(drumLikelihood, guitarLikelihood)*2, bassLikelihood);
                //FOLLOW_PREV_MEASURE_WEIGHT = FOLLOW_PREV_MEASURE_WEIGHT * adjustRawConfidenceScore(confidenceScore);;
            }else {
                measure = new DrumMeasure(lineList, lineNameList, linePositionList, isFirstMeasureInGroup);
                PREV_MEASURE_TYPE = Instrument.DRUM;
                double confidenceScore = drumLikelihood-Math.min(Math.max(bassLikelihood, guitarLikelihood)*2, drumLikelihood);
                //FOLLOW_PREV_MEASURE_WEIGHT = FOLLOW_PREV_MEASURE_WEIGHT * adjustRawConfidenceScore(confidenceScore);;
            }
        }
        if (repeatStart)
            measure.setRepeat(repeatCount, RepeatType.START);
        if (repeatEnd)
            measure.setRepeat(repeatCount, RepeatType.END);
        return measure;
    }

    private static double adjustRawConfidenceScore(double confidence) {
        //Exponential Decay (increasing form)
        //https://people.richland.edu/james/lecture/m116/logs/models.html
        double lowerLimit = 0.5;
        double size = 0.5;
        return lowerLimit + 0.5*(1-Math.exp(-5*confidence));
    }

    private static double isGuitarMeasureLikelihood(List<String> lineList, List<String[]> lineNameList) {
        double score = 0;
        int lineCount = lineList.size();
        for (int i=0; i<lineCount; i++) {
            score += MeasureLine.isGuitarLineLikelihood(lineNameList.get(i)[0], lineList.get(i), Instrument.AUTO);
        }
        if (lineCount==0)
            score += 1; //if there is risk of zero division error, assign the full weight
        else
            score += (score/lineCount);

        return score;
    }

    private static double isDrumMeasureLikelihood(List<String> lineList, List<String[]> lineNameList) {
        double score = 0;
        int lineCount = lineList.size();
        for (int i=0; i<lineCount; i++) {
            score += MeasureLine.isDrumLineLikelihood(lineNameList.get(i)[0], lineList.get(i), Instrument.AUTO);
        }
        if (lineCount==0)
            score += 1; //if there is risk of zero division error, assign the full weight
        else
            score += (score/lineCount);

        return score;
    }

    private static double isBassMeasureLikelihood(List<String> lineList, List<String[]> lineNameList) {
        double withinSizeBias = 0.1;  //weight for if the number of lines in this measure is within the size cap of Bass measures

        //---------this code block must be the exact same as isGuitarMeasureLikelihood (except the PREV_MEASURE_TYPE part)
        double guitarScore = isGuitarMeasureLikelihood(lineList, lineNameList);
        double bassScore = guitarScore;
        if (lineList.size()>=BassMeasure.MIN_LINE_COUNT && lineList.size()<=BassMeasure.MAX_LINE_COUNT)
            bassScore += withinSizeBias;
        return bassScore;
    }

    private static boolean checkRepeatStart(List<String> lines) {
        boolean repeatStart = true;
        int repeatStartMarkCount = 0;
        for (String line : lines) {
            repeatStart &= line.strip().startsWith("|");
            if (line.strip().startsWith("|*")) repeatStartMarkCount++;
        }
        repeatStart &= repeatStartMarkCount>=2;
        return repeatStart;
    }
    private static boolean checkRepeatEnd(List<String> lines) {
        boolean repeatEnd = true;
        int repeatEndMarkCount = 0;
        for (int i=1; i<lines.size(); i++) {
            String line = lines.get(i);
            repeatEnd &= line.strip().endsWith("|");
            if (line.strip().endsWith("*|")) repeatEndMarkCount++;
        }
        repeatEnd &= repeatEndMarkCount>=2;
        return repeatEnd;
    }
    private static String extractRepeatCount(List<String> lines) {
        if (!checkRepeatEnd(lines)) return "";
        Matcher numMatcher = Pattern.compile("(?<=[^0-9])[0-9]+(?=[ ]|"+ Patterns.DIVIDER+"|$)").matcher(lines.get(0));
        if (!numMatcher.find()) return "";
        return "["+numMatcher.start()+"]"+numMatcher.group();
    }

    private static void removeRepeatMarkings(List<String> lines, List<Integer> linePositions, boolean repeatStart, boolean repeatEnd, String repeatCountStr) {
        if (!repeatCountStr.isEmpty()){
            Matcher posMatcher = Pattern.compile("(?<=\\[)[0-9]+(?=\\])").matcher(repeatCountStr);
            Matcher numMatcher = Pattern.compile("(?<=\\])[0-9]+").matcher(repeatCountStr);
            posMatcher.find();
            numMatcher.find();
            int position = Integer.parseInt(posMatcher.group());
            int numLen = numMatcher.group().length();
            String line = lines.get(0);
            line = line.substring(0, position)+"-".repeat(Math.max(numLen-1, 0))+line.substring(position+numLen);
            //remove extra - which overlaps with the |'s
            /*
            -----------4|
            -----------||
            ----------*||   we wanna remove the -'s on the same column as the *'s. we do that for the first measure line in the code right below. the code lower handles the case for the rest of the lines.
            ----------*||
            -----------||
            -----------||
             */
            String tmp1 = line.substring(0, position-1);
            String tmp2 = position>=line.length() ? "" : line.substring(position);
            line = tmp1+tmp2;
            lines.set(0, line);
        }
        for(int i=0; i<lines.size(); i++) {
            String line = lines.get(i);
            int linePosition = linePositions.get(i);
            if (line.startsWith("|*")){
                linePosition+=2;
                line = line.substring(2);
            }else if(line.startsWith("|")) {
                int offset;
                if (repeatStart) offset = 2;
                else offset = 1;
                linePosition += offset;
                line = line.substring(offset);
            }
            if (line.endsWith("*|"))
                line = line.substring(0, line.length()-2);
            else if (line.endsWith("|")) {
                int offset;
                if (repeatEnd) offset = 2;
                else offset = 1;
                line = line.substring(0, line.length() - offset);
            }
            lines.set(i, line);
            linePositions.set(i, linePosition);
        }
    }

    public void calcDurationRatios() {
        for (List<List<Note>> chordList : getVoiceSortedChordList()) {
            calcDurationRatios(chordList);
        }
    }
    private void calcDurationRatios(List<List<Note>> chordList) {
        int maxMeasureLineLen = getMaxMeasureLineLength();

        // handle all but last chord
        for (int i=0; i<chordList.size()-1; i++) {
            List<Note> chord = chordList.get(i);
            int currentChordDistance = chord.get(0).distance;
            int nextChordDistance = chordList.get(i+1).get(0).distance;

            double durationRatio = ((double)(nextChordDistance-currentChordDistance))/maxMeasureLineLen;
            for (Note note : chord) {
                note.setDurationRatio(durationRatio);
            }
            //0.11..., 0.5882
        }
        //handle last chord, as it is a special case (it has no next chord)
        if (!chordList.isEmpty()) {
            List<Note> chord = chordList.get(chordList.size()-1);
            int currentChordDistance = chord.get(0).distance;

            double durationRatio = ((double)(maxMeasureLineLen-currentChordDistance))/maxMeasureLineLen;
            for (Note note : chord) {
                note.setDurationRatio(durationRatio);
            }
            //0.176,
        }
    }
    public List<List<List<Note>>> getVoiceSortedChordList() {
        List<List<List<Note>>> voiceSortedChordList = new ArrayList<>();
        for (List<Note> voice : this.voiceSortedNoteList) {
            List<List<Note>> voiceChordList = new ArrayList<>();
            List<Note> currentChord = new ArrayList<>();
            for (Note note : voice) {
                if (note.startsWithPreviousNote)
                    currentChord.add(note);
                else {
                    currentChord = new ArrayList<>();
                    currentChord.add(note);
                    voiceChordList.add(currentChord);
                }
            }
            voiceSortedChordList.add(voiceChordList);
        }
        return voiceSortedChordList;
    }

    public int getDivisions() {
        double totalMeasureDuration = (double)beatCount/(double)beatType;
        double minDurationRatio = 0;
        for (List<Note> voice : this.voiceSortedNoteList) {
            for (Note note : voice) {
                double noteDurationRatio = note.durationRatio;
                if (noteDurationRatio == 0)
                    continue;
                if (minDurationRatio == 0)
                    minDurationRatio = noteDurationRatio;
                minDurationRatio = Math.min(minDurationRatio, note.durationRatio);
            }
        }
        if (minDurationRatio==0)
            minDurationRatio = 1;

        //the number of times you have to divide a whole note to get the note (minDurationRatio*total...) with the shortest duration (i.e a quarter note is 4, an eighth note is 8, 1/16th note is 16, etc)
        double inverseStandardDuration = 1/(minDurationRatio*totalMeasureDuration);

        //we might not get the exact value we want though (e.g we get a 17th note, but that doesn't exist. we want either a 16th note that is dotted, or a 32th note, cuz those are the standard note types.)
        double roundedUpInverseStandardDuration = Math.pow(2, Math.ceil(Math.log(inverseStandardDuration)/Math.log(2)));      //we get the shortest nearest note to this note (e.g, if we have a 17th note, this gives us the number 32 as the note 1/32 is the shortest nearest note to 1/17)

        //find out how many times we need to divide a 4th note to get our smallest duration note (e.g say the smallest duration note in our score is a 1/64th note (wholeNOteDuration = 64) then we need to divide a 4th note 64/4 times to get our smallest duration note)
        double divisions = roundedUpInverseStandardDuration*0.25;
        return (int) Math.ceil(divisions);
    }


    public void setDurations() {
                        //total duration in unit of quarter notes
        double totalMeasureDuration = (double)beatCount/(double)beatType;
        for (MeasureLine measureLine : this.measureLineList) {
            for (Note note : measureLine.noteList) {

                //the number of times you have to divide a whole note to get the note note.durationRatio*total... (i.e a quarter note is 4, an eighth note is 8, 1/16th note is 16, etc)
                // 16 durations give you one whole note, x durations give you x/16 whole notes
                double inverseStandardDuration = 1/(note.durationRatio*totalMeasureDuration);

                //we might not get the exact value we want though (e.g we get a 17th note, but that doesn't exist. we want either a 16th note that is dotted, or a 32th note, cuz those are the standard note types.)
                double temp = Math.log(inverseStandardDuration)/Math.log(2);
                double roundedUpInverseStandardDuration = Math.pow(2, Math.ceil(temp));      //we get the nearest shortest note to this note (e.g, if we have a 17th note, this gives us the number 32 as the note 1/32 is the shortest nearest note to 1/17)
                double roundedDownInverseStandardDuration = Math.pow(2, Math.floor(temp));   //we get the nearest longest note to this note (e.g, if we have a 17th note, this gives us the number 16 as the note 1/16 is the longest nearest note to 1/17)

                // TODO The below if statement is here so we don't have excessive dots on notes. It is meant to round up
                //  the duration of the note if it is "close enough" to the next, longer note. I might need to redefine
                //  what "close enough" means, because rounding up based on if it is more than halfway closer to the
                //  longer note than to the shorter note (like i'm doing in the below if statement) might not give the
                //  best results. It works pretty decent for now though
                if (inverseStandardDuration<(roundedUpInverseStandardDuration+roundedDownInverseStandardDuration)/2) {
                    inverseStandardDuration = roundedDownInverseStandardDuration;
                    double smallestUnit = 4.0*(double)Score.GLOBAL_DIVISIONS;
                    double duration = smallestUnit/inverseStandardDuration;
                    note.duration = Math.max(1, duration);
                    continue;
                }

                int dotCount = 0;
                double[] dotMultipliers = {1, 1/1.5, 1/1.75, 1/1.875, 1/1.9375};
                for (int i=0; i<dotMultipliers.length; i++) {
                    double durationWithDots = dotMultipliers[i]*roundedUpInverseStandardDuration;
                    if (durationWithDots>inverseStandardDuration)
                        break;
                    dotCount = i;
                }
                inverseStandardDuration = roundedUpInverseStandardDuration;
                //the smallest unit of duration (in terms of whole note divisions) given our divisions
                double smallestInverseDurationUnit = 4.0*(double)Score.GLOBAL_DIVISIONS;

                //if smallest unit of duration is 32 (1/32th note) and our note's duration is 8 (1/8th note) then we need 32/8 of our smallest duration note to make up our duration
                double duration = smallestInverseDurationUnit/inverseStandardDuration;
                note.dotCount = dotCount;
                note.duration = Math.max(1, duration);
            }
        }
    }

    public boolean setRepeat(int repeatCount, RepeatType repeatType) {
        if (repeatCount<0)
            return false;
        if (!(repeatType == RepeatType.START || repeatType == RepeatType.END))
            return false;
        this.repeatCount = repeatCount;
        if (repeatType == RepeatType.START)
            this.repeatStart = true;
        if (repeatType == RepeatType.END)
            this.repeatEnd = true;
        return true;
    }

    public boolean isRepeatStart() {
        return this.repeatStart;
    }

    public boolean isRepeatEnd() {
        return this.repeatEnd;
    }

    public boolean setTimeSignature(int beatCount, int beatType) {
        if (!TimeSignature.isValid(beatCount, beatType))
            return false;
        this.beatCount = beatCount;
        this.beatType = beatType;
        this.timeSigOverridden = true;
        return true;
    }

    public int getMaxMeasureLineLength() {
        int maxLen = 0;
        for (MeasureLine mLine : this.measureLineList) {
            maxLen = Math.max(maxLen, mLine.line.replace("\s", "").length());
        }
        return maxLen;
    }



    /**
     * Validates if all MeasureLine objects which this Measure object aggregates are instances of the same concrete
     * MeasureLine Class (i.e they're all GuitarMeasureLine instances or all DrumMeasureLine objects). It does not
     * validate its aggregated objects. That job is left up to its concrete classes (this is an abstract class)
     * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
     * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
     * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
     * found in the root string from which it was derived (i.e Score.ROOT_STRING).
     * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public List<ValidationError> validate() {
        List<ValidationError> result = new ArrayList<>();

        boolean hasGuitarMeasureLines = true;
        boolean hasDrumMeasureLines = true;
        boolean lineSizeEqual = true;

        int previousLineLength = -1;
        for (MeasureLine measureLine : this.measureLineList) {
            hasGuitarMeasureLines &= measureLine instanceof GuitarMeasureLine;
            hasDrumMeasureLines &= measureLine instanceof DrumMeasureLine;

            int currentLineLength = measureLine.line.replace("\s", "").length();
            lineSizeEqual &= (previousLineLength<0) || previousLineLength==currentLineLength;
            previousLineLength = currentLineLength;
        }
        if (!(hasGuitarMeasureLines || hasDrumMeasureLines)) {
            ValidationError error = new ValidationError(
                    "All measure lines in a measure must be of the same type (i.e. all guitar measure lines or all drum measure lines)",
                    1,
                    this.getLinePositions()
            );
            if (TabInput.ERROR_SENSITIVITY>=error.getPriority())
                result.add(error);
        }

        if (!lineSizeEqual) {
            ValidationError error = new ValidationError(
                    "Unequal measure line lengths may lead to incorrect note durations.",
                    2,
                    this.getLinePositions()
            );
            if (TabInput.ERROR_SENSITIVITY>=error.getPriority())
                result.add(error);
        }
        return result;
    }

    /**
     * Creates a string representation of the index position range of each line making up this Measure instance,
     * where each index position range describes the location where the lines of this Measure can be found in the
     * root string from which it was derived (i.e Score.ROOT_STRING)
     * @return a String representing the index range of each line in this Measure, formatted as follows:
     * "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public List<Integer[]> getLinePositions() {
        List<Integer[]> linePositions = new ArrayList<>();
        for (int i=0; i<this.lines.size(); i++) {
            int startIdx = this.positions.get(i);
            int endIdx = startIdx+this.lines.get(i).length();
            linePositions.add(new Integer[]{startIdx, endIdx});
        }
        return linePositions;
    }

    protected void setChords() {
        for (List<Note> voice : this.voiceSortedNoteList) {
            Note previousNote = null;
            for (Note currentNote : voice) {
                if (currentNote.isGrace) continue;
                if (previousNote != null && previousNote.distance == currentNote.distance)
                    currentNote.startsWithPreviousNote = true;
                previousNote = currentNote;
            }
        }
    }

    public List<List<Note>> getVoiceSortedNoteList() {
        List<List<Note>> voiceSortedNoteList = new ArrayList<>();
        HashMap<Integer, Integer> voiceToIndexMap = new HashMap<>();
        int currentIdx = 0;
        for (Note note : this.getSortedNoteList()) {
            if (!voiceToIndexMap.containsKey(note.voice)) {
                voiceToIndexMap.put(note.voice, currentIdx++);
                voiceSortedNoteList.add(new ArrayList<>());
            }
            int idx = voiceToIndexMap.get(note.voice);
            List<Note> voice = voiceSortedNoteList.get(idx);
            voice.add(note);
        }
        return voiceSortedNoteList;
    }

    public List<Note> getSortedNoteList() {
        List<Note> sortedNoteList = new ArrayList<>();
        for (MeasureLine measureLine : this.measureLineList) {
            sortedNoteList.addAll(measureLine.getNoteList());
        }
        Collections.sort(sortedNoteList);
        return sortedNoteList;
    }

    public boolean isGuitar(boolean strictCheck) {
        for (MeasureLine measureLine : this.measureLineList) {
            if (!measureLine.isGuitar(strictCheck))
                return false;
        }
        return true;
    }

    public boolean isDrum(boolean strictCheck) {
        for (MeasureLine measureLine : this.measureLineList) {
            if (!measureLine.isDrum(strictCheck))
                return false;
        }
        return true;
    }
    public boolean isBass(boolean strictCheck) {
        for (MeasureLine measureLine : this.measureLineList) {
            if (!measureLine.isGuitar(strictCheck))
                return false;
        }
        return this.measureLineList.size() >= BassMeasure.MIN_LINE_COUNT && this.measureLineList.size() <= BassMeasure.MAX_LINE_COUNT;
    }

    @Override
    public String toString() {
        StringBuilder stringOut = new StringBuilder();
        if (TimeSignature.isValid(this.beatCount, this.beatType))
            stringOut.append(this.beatCount+"/"+this.beatType+"\n");
        for (int i=0; i<this.measureLineList.size()-1; i++) {
            MeasureLine measureLine = this.measureLineList.get(i);
            stringOut.append(measureLine.name);
            stringOut.append("|");
            stringOut.append(measureLine.recreateLineString(getMaxMeasureLineLength()));
            stringOut.append("\n");
        }
        if (!this.measureLineList.isEmpty()) {
            MeasureLine measureLine = this.measureLineList.get(this.measureLineList.size()-1);
            stringOut.append(measureLine.name);
            stringOut.append("|");
            stringOut.append(measureLine.recreateLineString(getMaxMeasureLineLength()));
            stringOut.append("\n");
        }

        return stringOut.toString();
    }

    public Range getRelativeRange() {
        if (this.lines.isEmpty()) return null;
        int position;
        if (this.isFirstMeasureInGroup)
            position = Integer.parseInt(lineNamesAndPositions.get(0)[1]);   // use the starting position of the name instead.
        else
            position = this.positions.get(0)-1;       // use the starting position of the inside of the measure minus one, so that it also captures the starting line of that measure "|"
        int relStartPos = position-Score.ROOT_STRING.substring(0,position).lastIndexOf("\n");
        String line = this.lines.get(0);
        int lineLength = 0;
        if (line.matches("[^|]*\\|\\s*"))   //if it ends with a |
            lineLength = line.length()-1;
        else
            lineLength = line.length();
        int relEndPos = relStartPos + lineLength;
        return new Range(relStartPos, relEndPos);
    }

    public abstract models.measure.Measure getModel();

    public int getCount() {
        return this.measureCount;
    }
}