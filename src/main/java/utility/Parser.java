package utility;

import converter.Score;

import java.nio.file.Files;
import java.nio.file.Path;

public class Parser {
    public static Score SCORE;

    public static void createScore(String rootString) {
        SCORE = new Score(rootString);
    }

    public Parser(Path filePath) {
        try {
            String rootString = Files.readString(filePath).replace("\r\n","\n");
            SCORE = new Score(rootString);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String parse() {
        return SCORE.toXML();
    }
}
