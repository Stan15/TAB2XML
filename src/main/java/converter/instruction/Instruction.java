package converter.instruction;

import converter.Score;
import converter.ScoreComponent;
import utility.Patterns;
import utility.Range;
import utility.ValidationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Instruction {
    public static Top TOP = new Top();
    public static Bottom BOTTOM = new Bottom();
    public static String LINE_PATTERN = getLinePattern();

    private String content;
    private int position;
    private RelativePosition relativePosition;
    private boolean hasBeenApplied;

    Instruction(String content, int position, RelativePosition topOrBottom) {
        this.content = content;
        this.position = position;

        int relStartPos = position-Score.ROOT_STRING.substring(0,position).lastIndexOf("\n");
        int relEndPos = relStartPos + content.length();

        if (topOrBottom instanceof Top)
            this.relativePosition = new Top(relStartPos, relEndPos);
        else
            this.relativePosition = new Bottom(relStartPos, relEndPos);
    }

    public abstract <E extends ScoreComponent> void applyTo(E scoreComponent);

    RelativePosition getRelativeRange() {
        return this.relativePosition;
    }

    public static List<Instruction> from(String line, int position, RelativePosition topOrBottom) {
        List<Instruction> instructionList = new ArrayList<>();
        Matcher repeatMatcher = Pattern.compile(Repeat.PATTERN).matcher(line);
        while(repeatMatcher.find()) {
            instructionList.add(new Repeat(repeatMatcher.group(), position+repeatMatcher.start(), topOrBottom));
        }

        Matcher timeSigMatcher = Pattern.compile(TimeSignature.PATTERN).matcher(line);
        while(timeSigMatcher.find()) {
            instructionList.add(new TimeSignature(timeSigMatcher.group(), position+timeSigMatcher.start(), topOrBottom));
        }

        return instructionList;
    }

    String getContent() {
        return this.content;
    }
    int getPosition() {
        return this.position;
    }
    void setHasBeenApplied(boolean bool) {
        this.hasBeenApplied = bool;
    }
    boolean getHasBeenApplied() {
        return this.hasBeenApplied;
    }


    public List<ValidationError> validate() {
        List<ValidationError> result = new ArrayList<>();
        if (!this.hasBeenApplied) {
            result.add(new ValidationError(
                    "This instruction could not be applied to any measure or note.",
                    3,
                    new ArrayList<>(Collections.singleton(
                            new Integer[]{this.position, this.position + this.content.length()}
                            ))
            ));
        }
        return result;
    }

    private static String getLinePattern() {
        String instruction = "(("+TimeSignature.PATTERN+")|("+Repeat.PATTERN+"))";
        return "("+Patterns.WHITESPACE+"*" + instruction + Patterns.WHITESPACE+"*" + ")+";
    }
}

abstract class RelativePosition extends Range {
    RelativePosition(int relStart, int relEnd) {
        super(relStart, relEnd);
    }
}
class Top extends RelativePosition {
    Top() {
        super(0,0);
    }
    Top(int relStart, int relEnd) {
        super(relStart, relEnd);
    }
}
class Bottom extends RelativePosition {
    Bottom() {
        super(0,0);
    }
    Bottom(int relStart, int relEnd) {
        super(relStart, relEnd);
    }
}
