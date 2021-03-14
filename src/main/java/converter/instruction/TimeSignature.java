package converter.instruction;

import converter.MeasureCollection;
import converter.MeasureGroup;
import converter.ScoreComponent;
import converter.measure.Measure;
import utility.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeSignature extends Instruction {
    public static String PATTERN = "(?<=\s|\n|\r|^)[0-9][0-9]?\\/[0-9][0-9]?(?=\s|\n|\r|$)";
    private int beatType;
    private int beatCount;
    TimeSignature(String content, int position, RelativePosition relativePosition) {
        super(content, position, relativePosition);
        Matcher beatCountMatcher = Pattern.compile("[0-9]+(?=[/\\\\])").matcher(content);
        Matcher beatTypeMatcher = Pattern.compile("(?<=[/\\\\])[0-9]+").matcher(content);
        if (beatCountMatcher.find())
            this.beatCount = Integer.parseInt(beatCountMatcher.group());
        if (beatTypeMatcher.find())
            this.beatType = Integer.parseInt(beatTypeMatcher.group());
    }

    @Override
    public <E extends ScoreComponent> void applyTo(E scoreComponent) {
        if (!validateSelf().isEmpty() || this.getHasBeenApplied()) {
            this.setHasBeenApplied(true);
            return;
        }

        if (scoreComponent instanceof MeasureCollection) {
            MeasureCollection measureCollection = (MeasureCollection) scoreComponent;
            for (MeasureGroup measureGroup : measureCollection.getMeasureGroupList()) {
                Range measureGroupRange = measureGroup.getRelativeRange();
                if (measureGroupRange==null) continue;
                if (!measureGroupRange.contains(this.getRelativeRange())) continue;
                for (Measure measure : measureGroup.getMeasureList()) {
                    Range measureRange = measure.getRelativeRange();
                    if (measureRange==null || !measureRange.contains(this.getRelativeRange())) continue;
                    this.setHasBeenApplied(measure.setTimeSignature(this.beatCount, this.beatType));
                }
            }
        }
    }

    public List<HashMap<String, String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>(super.validate());
        result.addAll(validateSelf());
        return result;
    }

    private List<HashMap<String, String>> validateSelf() {
        List<HashMap<String, String>> result = new ArrayList<>();
        if (beatCount<=0 || beatType<=0) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Invalid beat " + (this.beatCount<=0?"count" : "type") + " value.");
            response.put("positions", "["+this.getPosition()+","+(this.getPosition()+this.getContent().length())+"]");
            response.put("priority", "2");
            result.add(response);
        }else if (!isValid(this.beatCount, this.beatType)) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Unsupported time signature.");
            response.put("positions", "["+this.getPosition()+","+(this.getPosition()+this.getContent().length())+"]");
            response.put("priority", "2");
            result.add(response);
        }
        return result;
    }

    public static boolean isValid(int beatCount, int beatType) {
        if (beatCount<=0 || beatType<=0)
            return false;
        // check for any unacceptable time signatures here and return false
        return true;
    }
}
