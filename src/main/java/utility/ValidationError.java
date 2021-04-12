package utility;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class ValidationError {
    int priority;
    List<Integer[]> positions = new ArrayList<>();
    String message;

    public ValidationError(String message, int priority, List<Integer[]> positions) {
        this.message = message;
        this.priority = priority;
        for (Integer[] position : positions)
            this.positions.add(Arrays.copyOf(position, position.length));
    }
}
