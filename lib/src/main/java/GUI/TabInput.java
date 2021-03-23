package GUI;

import converter.Score;
import javafx.concurrent.Task;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextArea;
import javafx.scene.text.TextFlow;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import utility.*;

import java.awt.*;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TabInput {
    private static String PREVIOUS_TEXT_INPUT = "";
    protected static TreeMap<Range, HashMap<String,String>> ACTIVE_ERRORS = new TreeMap<>();
    protected static int HOVER_DELAY = 350;   //in milliseconds
    protected static int ERROR_SENSITIVITY = 4;
    protected static boolean AUTO_HIGHLIGHT;
    protected static Score SCORE = new Score("");
    private CodeArea TEXT_AREA;
    protected static ExecutorService executor = Executors.newSingleThreadExecutor();

    public TabInput(CodeArea TEXT_AREA) {
        this.TEXT_AREA = TEXT_AREA;
    }

    public Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = TEXT_AREA.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        task.isDone();
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        TEXT_AREA.setStyleSpans(0, highlighting);
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        String strippedText = text.replaceFirst("\\s++$", "");
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        if (strippedText.equals(PREVIOUS_TEXT_INPUT.strip()) || strippedText.isBlank()) {
            spansBuilder.add(Collections.emptyList(), text.length());
            return spansBuilder.create();
        }

        TabInput.SCORE = new Score(text);
        ACTIVE_ERRORS = this.filterOverlappingRanges(this.createErrorRangeMap(TabInput.SCORE.validate()));
        if (ACTIVE_ERRORS.isEmpty()) {
            spansBuilder.add(Collections.emptyList(), text.length());
            return spansBuilder.create();
        }

        PREVIOUS_TEXT_INPUT = text;

        ArrayList<Range> errorRanges = new ArrayList<>(ACTIVE_ERRORS.keySet());
        int lastErrorEnd = 0;
        for (Range range : errorRanges) {
            int errorPriority = Integer.parseInt(ACTIVE_ERRORS.get(range).get("priority"));
            if (ERROR_SENSITIVITY<errorPriority) continue;
            String styleClass = getErrorStyleClass(errorPriority);
            spansBuilder.add(Collections.emptyList(), range.getStart() - lastErrorEnd);
            spansBuilder.add(Collections.singleton(styleClass), range.getSize());
            lastErrorEnd = range.getEnd();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastErrorEnd);
        return spansBuilder.create();
    }

    private TreeMap<Range, HashMap<String, String>> filterOverlappingRanges(TreeMap<Range, HashMap<String, String>> errors) {
        Iterator<Range> errorRanges = new ArrayList<>(errors.keySet()).iterator();
        if (!errorRanges.hasNext()) return new TreeMap<>();
        Range currentRange = errorRanges.next();
        while (errorRanges.hasNext()) {
            Range nextRange = errorRanges.next();

            while (nextRange.overlaps(currentRange)) {
                int currentErrorPriority = Integer.parseInt(errors.get(currentRange).get("priority"));
                int nextErrorPriority = Integer.parseInt(errors.get(currentRange).get("priority"));
                if (currentErrorPriority>nextErrorPriority) {
                    errors.remove(currentRange);
                    break;
                } else {
                    errors.remove(nextRange);
                    if (!errorRanges.hasNext()) break;
                    nextRange = errorRanges.next();
                }
            }
            currentRange = nextRange;
        }
        return errors;
    }

    private TreeMap<Range, HashMap<String,String>> createErrorRangeMap(List<HashMap<String, String>> errors) {
        TreeMap<Range, HashMap<String,String>> errorMap = new TreeMap<>();
        Pattern rangePattern = Pattern.compile("\\[\\d+,\\d+\\]");
        for (HashMap<String, String> error : errors) {
            Matcher rangeMatcher = rangePattern.matcher(error.get("positions"));
            while(rangeMatcher.find()) {
                Matcher startIdxMatcher = Pattern.compile("(?<=\\[)\\d+").matcher(rangeMatcher.group());
                Matcher endIdxMatcher = Pattern.compile("(?<=,)\\d+").matcher(rangeMatcher.group());
                startIdxMatcher.find();
                endIdxMatcher.find();
                int startIdx = Integer.parseInt(startIdxMatcher.group());
                int endIdx = Integer.parseInt(endIdxMatcher.group());
                errorMap.put(new Range(startIdx, endIdx), error);
            }
        }

        return errorMap;
    }

    public static String getMessageOfCharAt(int index) {
        Comparator<Range> numInRangeComparator = (r1, r2) -> {
            if (r1.contains(r2.getStart()) || r2.contains(r1.getStart()))
                return 0;
            return r1.getStart()==r1.getEnd() ? r1.getStart()-r2.getStart() : r2.getStart()- r1.getStart();
        };

        List<Range> errorRanges = new ArrayList<>(ACTIVE_ERRORS.keySet());
        Collections.sort(errorRanges, numInRangeComparator);
        //over the top, just for efficiency. search the array of ranges "errorRanges" to find the range which contains(i.e includes) the number "index"
        int rangeIdx = Collections.binarySearch(errorRanges, new Range(index, index), numInRangeComparator);
        if (rangeIdx<0)
            return "";
        Range range = errorRanges.get(rangeIdx);
        return ACTIVE_ERRORS.get(range).get("message");
    }

    private static String getErrorStyleClass(int priority) {
        switch (priority) {
            case 1: return "highPriorityError";
            case 2: return "mediumPriorityError";
            case 3: return "lowPriorityError";
            case 4: return "unimportantError";
            default:
                new Exception("TXT2XML: invalid validation error priority").printStackTrace();
                return "";
        }
    }

    public void enableHighlighting() {
        Subscription cleanupWhenDone = TEXT_AREA.multiPlainChanges()
                .successionEnds(Duration.ofMillis(350))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(TEXT_AREA.multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
    }

    public void disableHighlighting() {
        // TODO implement method stub
        return;
    }
}

