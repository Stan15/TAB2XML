package converter.measure_line;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GuitarMeasureLine extends MeasureLine {
    public static Set<String> NAME_SET = createLineNameSet();
    public static String COMPONENT_PATTERN = createLineComponentPattern();

    public GuitarMeasureLine(String line, String name, int position) {
        super(line, name, position);
    }

    private static Set<String> createLineNameSet() {
        String[] names = {"E", "A", "D", "G", "B", "e", "a", "d", "g", "b"};
        HashSet<String> nameSet = new HashSet<>();
        nameSet.addAll(Arrays.asList(names));
        return nameSet;
    }

    private static String createLineComponentPattern() {
        return "[0-9]+|h|p|/|\\";
    }

}
