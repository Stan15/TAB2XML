package org.openjfx;

import converter.Score;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import utility.Parser;
import utility.Range;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TabInput {
    @FXML
    public static CodeArea TEXT_AREA;
    protected static int HOVER_DELAY = 350;   //in milliseconds
    public static Map<Range,String> ACTIVE_ERROR_MESSAGES = new HashMap<>();
    public static int ERROR_SENSITIVITY;
    public Score score;

    static Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = TEXT_AREA.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        MainApp.executor.execute(task);
        return task;
    }

    public static void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        TEXT_AREA.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass = getErrorStyleClass(ERROR_SENSITIVITY);
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public static String getMessageOfCharAt(int index) {
        for (Range range : ACTIVE_ERROR_MESSAGES.keySet()) {
            if (range.contains(index)) {
                return ACTIVE_ERROR_MESSAGES.get(range);
            }
        }
        return "";
    }

    private static String getErrorStyleClass(int priority) {
        switch (priority) {
            case 1: return "redHighlight";
            case 2: return "orangeHighlight";
            case 3: return "blueHighlight";
            default:
                new Exception("TXT2XML: invalid validation error priority").printStackTrace();
                return "";
        }
    }
}

