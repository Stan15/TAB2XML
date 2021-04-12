package utility;

import java.util.ArrayList;

public class Error {
    int priority;
    ArrayList<ArrayList<Integer>> positions = new ArrayList<>();
    String message;

    public Error(String message, int priority, ArrayList<ArrayList<Integer>> positions) {
        this.message = message;
        this.priority = priority;
        for (ArrayList<Integer> positionGroup : positions)
            this.positions.add(new ArrayList<>(positionGroup));
    }
}
