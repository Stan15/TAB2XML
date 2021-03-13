package converter.instruction;

import converter.Patterns;
import converter.Score;
import converter.ScoreComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Instruction {
    public static Top TOP = new Top();
    public static Bottom BOTTOM = new Bottom();
    public static String LINE_PATTERN = "([\\n\\r]"+ Patterns.WHITESPACE+"*"+"#[^\\n\\r]+)";
    private String line;
    private int position;
    private RelativePosition relativePosition;
    private boolean isApplied;

    Instruction(String line, int position, RelativePosition relativePosition) {
        this.line = line;
        this.position = position;
        this.relativePosition = relativePosition;
        this.relativePosition.setPositionFromIndex(position, position+line.length());
    }

    public abstract void apply(ScoreComponent scoreComponent);

    /**
     *
     * @param line
     * @param position is this instruction positioned at the top or at the bottom of a measure/measure collection?
     * @return
     */
    public static List<Instruction> from(String line, int index, RelativePosition position) {
        return new ArrayList<>();
    }

    public List<HashMap<String, String>> validate() {
        List<HashMap<String,String>> result = new ArrayList<>();
        if (!this.isApplied) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "This instruction could not be applied to a measure or note.");
            response.put("positions", "["+this.position+","+(this.position+this.line.length())+"]");
            response.put("priority", "3");
            result.add(response);
        }
        return new ArrayList<>();

    }
}

abstract class RelativePosition {
    int startDistFromNewline;
    int endDistFromNewline;

    public int setPositionFromIndex(int startIdx, int endIdx) {

    }
}
class Top extends RelativePosition {}
class Bottom extends RelativePosition {}
