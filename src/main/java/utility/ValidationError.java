package utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    
    int getPriority() {
    	return this.priority;
    }
    
    List<Integer[]> getPositions() {
    	List<Integer[]> positions= new ArrayList<>();
    	for (Integer[] position : this.positions)
            positions.add(Arrays.copyOf(position, position.length));
    	return positions;
    }
    
    String getMessage() {
    	return this.message;
    }
}
