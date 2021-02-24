package org.openjfx;

import converter.Score;
import org.fxmisc.richtext.StyleClassedTextArea;
import parser.Parser;
import utility.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputField {
    StyleClassedTextArea TEXT_AREA;
    protected static int HOVER_DELAY = 350;   //in milliseconds
    public static String PREV_ROOT_STRING = "";
    public static Map<Range,String> ACTIVE_ERROR_MESSAGES = new HashMap<>();

    public InputField(StyleClassedTextArea TEXT_AREA) {
        this.TEXT_AREA = TEXT_AREA;
    }

    public void errorHighlight(int errorSensitivity) {
        String rootString = TEXT_AREA.getText();
        if (rootString.strip().equals(PREV_ROOT_STRING.strip())) return;
        PREV_ROOT_STRING = rootString;
        if (rootString.strip().isEmpty()) return;

        TEXT_AREA.clearStyle(0,rootString.length());
        ACTIVE_ERROR_MESSAGES.clear();

        Parser.createScore(rootString);
        Score score = Parser.SCORE;

        for (HashMap<String, String> error : score.validate()) {
            int priority = Integer.parseInt(error.get("priority"));
            if (errorSensitivity<priority) continue;

            String rangePttrn = "\\[[0-9]+,[0-9]+\\]" ;

            String ranges = error.get("positions");
            if (!ranges.matches(rangePttrn + "(;"+rangePttrn+")*")) {
                new Exception("TXT2XML: error position range format is incorrect").printStackTrace();
                continue;
            }

            for (String range : ranges.split(";")) {
                if (range.strip().equals("")) continue;

                Matcher tagMatcher = Pattern.compile("(?<=\\[)[0-9]+").matcher(range);
                tagMatcher.find();
                int startIdx = Integer.parseInt(tagMatcher.group());

                tagMatcher = Pattern.compile("(?<=,)[0-9]+").matcher(range);
                tagMatcher.find();
                int endIdx = Integer.parseInt(tagMatcher.group());

                String styleClass = this.getErrorStyleClass(priority);
                if (!styleClass.isEmpty()) {
                    TEXT_AREA.setStyleClass(startIdx, endIdx, styleClass);

                    ACTIVE_ERROR_MESSAGES.put(new Range(startIdx, endIdx), error.get("message"));
                }
            }
        }
    }

    public static String getMessageOfCharAt(int index) {
        for (Range range : ACTIVE_ERROR_MESSAGES.keySet()) {
            if (range.contains(index)) {
                return ACTIVE_ERROR_MESSAGES.get(range);
            }
        }
        return "";
    }

    private String getErrorStyleClass(int priority) {
        switch (priority) {
            case 1: return "redHighlight";
            case 2: return "yellowHighlight";
            case 3: return "blueHighlight";
            default:
                new Exception("TXT2XML: invalid validation error priority").printStackTrace();
                return "";
        }
    }
}

