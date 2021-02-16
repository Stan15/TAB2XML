package converter;

public class Patterns {
    public static final String WHITESPACE = "[^\\S\\n\\r]";
    public static final String COMMENT = "^[^\\S\\n\\r]*#.+(?=\\n)";
    public Patterns() {}
}
