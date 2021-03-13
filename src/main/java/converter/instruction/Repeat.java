package converter.instruction;

import converter.ScoreComponent;

public class Repeat extends Instruction {
    Repeat(String line, int position, RelativePosition relativePosition) {
        super(line, position, relativePosition);
    }

    @Override
    public void apply(ScoreComponent scoreComponent) {
        
    }
}
