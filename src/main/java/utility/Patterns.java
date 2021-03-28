package utility;

public class Patterns {
    public static final String WHITESPACE = "[^\\S\\n\\r]";
    public static final String COMMENT = "^[^\\S\\n\\r]*#.+(?=\\n)";
    public static final String DIVIDER = getDivider();
    public static final String DIVIDER_COMPONENTS = "|{}";

ww
    private static String getDivider() {
        return "["+DIVIDER_COMPONENTS+"]";
    }
    public Patterns() {}
}
