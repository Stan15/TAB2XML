package utility;

import java.util.ArrayList;
import java.util.Arrays;

public class ValidationError {
    int priority;
    ArrayList<Integer[]> positions = new ArrayList<>();
    String message;

    public ValidationError(String message, int priority, ArrayList<Integer[]> positions) {
        this.message = message;
        this.priority = priority;
        for (Integer[] position : positions)
            this.positions.add(Arrays.copyOf(position, position.length));
    }
}
