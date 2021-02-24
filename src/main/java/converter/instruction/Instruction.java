package converter.instruction;

import converter.Patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Instruction {
    public static String LINE_PATTERN = "([\\n\\r]"+ Patterns.WHITESPACE+"*"+"#[^\\n\\r]+)";
    public List<HashMap<String, String>> validate() {
        return new ArrayList<>();

    }
}
