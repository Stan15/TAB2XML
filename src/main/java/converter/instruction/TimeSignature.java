package converter.instruction;

import GUI.TabInput;
import converter.MeasureCollection;
import converter.MeasureGroup;
import converter.ScoreComponent;
import converter.measure.Measure;
import utility.Range;
import utility.ValidationError;

import java.util.ArrayList;
import java.util.Collections;
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

    public List<ValidationError> validate() {
        List<ValidationError> result = new ArrayList<>(super.validate());
        result.addAll(validateSelf());
        return result;
    }

    private List<ValidationError> validateSelf() {
        List<ValidationError> result = new ArrayList<>();
        if (!(this.getRelativeRange() instanceof Top)) {
            ValidationError error = new ValidationError(
                    "Time signatures should only be applied to the top of measures.",
                    3,
                    new ArrayList<>(Collections.singleton(new Integer[]{
                            this.getPosition(),
                            this.getPosition()+this.getContent().length()
                    }))
            );
            if (TabInput.ERROR_SENSITIVITY>= error.getPriority())
                result.add(error);
            return result;
        }
        if (beatCount<=0 || beatType<=0) {
            ValidationError error = new ValidationError(
                    "Invalid beat " + (this.beatCount<=0?"count" : "type") + " value.",
                    2,
                    new ArrayList<>(Collections.singleton(new Integer[]{
                            this.getPosition(),
                            this.getPosition()+this.getContent().length()
                    }))
            );
            if (TabInput.ERROR_SENSITIVITY>= error.getPriority())
                result.add(error);
        }else if (!isValid(this.beatCount, this.beatType)) {
            ValidationError error = new ValidationError(
                    "Unsupported time signature.",
                    2,
                    new ArrayList<>(Collections.singleton(new Integer[]{
                            this.getPosition(),
                            this.getPosition()+this.getContent().length()
                    }))
            );
            if (TabInput.ERROR_SENSITIVITY>= error.getPriority())
                result.add(error);
        }
        return result;
    }

    public static boolean isValid(int beatCount, int beatType) {
        return switch (beatCount + "/" + beatType) {
            case "2/4", "2/2", "3/8", "3/4", "3/2", "4/8", "4/4", "4/2", "6/8", "6/4", "9/8", "9/4", "12/8", "12/4" -> true;
            default -> false;
        };
    }
}
