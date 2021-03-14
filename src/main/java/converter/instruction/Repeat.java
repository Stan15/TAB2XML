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

public class Repeat extends Instruction {
    public static String PATTERN = getPattern();

    private int repeatCount;
    private boolean startApplied = false;
    private boolean endApplied = false;
    Repeat(String content, int position, RelativePosition relativePosition) {
        super(content, position, relativePosition);
        Matcher matcher = Pattern.compile("[0-9]+").matcher(content);
        if (matcher.find())
            this.repeatCount = Integer.parseInt(matcher.group());
    }

    public <E extends ScoreComponent> void applyTo(E scoreComponent) {
        if (!validateSelf().isEmpty() || this.getHasBeenApplied() || this.repeatCount==0) {
            this.setHasBeenApplied(true);
            return;
        }

        if (scoreComponent instanceof MeasureCollection) {
            MeasureCollection measureCollection = (MeasureCollection) scoreComponent;
            Measure firstMeasure = null;
            Measure lastMeasure = null;
            for (MeasureGroup measureGroup : measureCollection.getMeasureGroupList()) {
                Range measureGroupRange = measureGroup.getRelativeRange();
                if (measureGroupRange==null) continue;
                if (!this.getRelativeRange().overlaps(measureGroupRange)) continue;
                for (Measure measure : measureGroup.getMeasureList()) {
                    Range measureRange = measure.getRelativeRange();
                    if (measureRange==null || !this.getRelativeRange().overlaps(measureRange)) continue;
                    if (firstMeasure==null && !this.startApplied)
                        firstMeasure = measure;
                    if (!this.endApplied)
                        lastMeasure = measure;
                }
            }
            if (firstMeasure!=null)
                this.startApplied = firstMeasure.setRepeat(this.repeatCount, "start");
            if (lastMeasure!=null)
                this.endApplied = lastMeasure.setRepeat(this.repeatCount, "end");
        }
        this.setHasBeenApplied(this.startApplied && this.endApplied);
    }

    public List<HashMap<String, String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>(super.validate());
        result.addAll(validateSelf());
        if ((!this.startApplied && this.endApplied)) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "This repeat was only partially applied for some reason.");
            response.put("positions", "["+this.getPosition()+","+(this.getPosition()+this.getContent().length())+"]");
            response.put("priority", "2");
            result.add(response);
        }
        return result;
    }

    private List<HashMap<String, String>> validateSelf() {
        List<HashMap<String, String>> result = new ArrayList<>();
        if (!(this.getRelativeRange() instanceof Top)) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Repeats should only be applied to the top of measures.");
            response.put("positions", "["+this.getPosition()+","+(this.getPosition()+this.getContent().length())+"]");
            response.put("priority", "3");
            result.add(response);
        }
        if (this.repeatCount>10) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "only up to 10 repeats are allowed.");
            response.put("positions", "["+this.getPosition()+","+(this.getPosition()+this.getContent().length())+"]");
            response.put("priority", "3");
            result.add(response);
        }
        return result;
    }

    private static String getPattern() {
        String repeatTextPattern = "[Rr][Ee][Pp][Ee][Aa][Tt]"+"([ -]{0,7}|[ \t]{0,2})"+"[xX*]?[0-9][0-9]?[xX*]?";
        return "(|[ -]*)?"+repeatTextPattern+"([ -]*(?=|))?";
    }
}
