package converter.instruction;

import converter.Patterns;

import java.util.HashMap;

public class Instruction {
    public static String LINE_PATTERN = "([\\n\\r]"+ Patterns.WHITESPACE+"*"+"#[^\\n\\r]+)";
    public HashMap<String, String> validate() {
        return null;
    }
}
