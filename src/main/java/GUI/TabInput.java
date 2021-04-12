package GUI;

import converter.Score;
import converter.measure.Measure;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextArea;
import javafx.scene.shape.MoveTo;
import javafx.scene.text.TextFlow;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import utility.*;

import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TabInput {
    protected static TreeMap<Range, ValidationError> ACTIVE_ERRORS = new TreeMap<>();
    protected static int HOVER_DELAY = 30;   //in milliseconds
    public static int ERROR_SENSITIVITY = 4;
    protected static boolean AUTO_HIGHLIGHT;
    protected static Score SCORE = new Score("");
    private CodeArea TEXT_AREA;
    private Button convertButton;
    protected static ExecutorService executor = Executors.newSingleThreadExecutor();

    public TabInput(CodeArea TEXT_AREA, Button convertButton) {
        this.TEXT_AREA = TEXT_AREA;
        this.convertButton = convertButton;
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
    public void refresh() {
        TEXT_AREA.replaceText(new IndexRange(0, TEXT_AREA.getText().length()), TEXT_AREA.getText()+" ");
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        TEXT_AREA.setStyleSpans(0, highlighting);
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        SCORE = new Score(text);
        if (SCORE.measureCollectionList.isEmpty())
            convertButton.setDisable(true);
        else
            convertButton.setDisable(false);

        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        ACTIVE_ERRORS = this.filterOverlappingRanges(this.createErrorRangeMap(TabInput.SCORE.validate()));
        if (ACTIVE_ERRORS.isEmpty()) {
            spansBuilder.add(Collections.emptyList(), text.length());
            return spansBuilder.create();
        }


        ArrayList<Range> errorRanges = new ArrayList<>(ACTIVE_ERRORS.keySet());
        int lastErrorEnd = 0;
        for (Range range : errorRanges) {
            int errorPriority = ACTIVE_ERRORS.get(range).getPriority();
            if (ERROR_SENSITIVITY<errorPriority) continue;
            String styleClass = getErrorStyleClass(errorPriority);
            spansBuilder.add(Collections.emptyList(), range.getStart() - lastErrorEnd);
            spansBuilder.add(Collections.singleton(styleClass), range.getSize());
            lastErrorEnd = range.getEnd();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastErrorEnd);
        return spansBuilder.create();
    }

    private TreeMap<Range, ValidationError> filterOverlappingRanges(TreeMap<Range, ValidationError> errors) {
        Iterator<Range> errorRanges = new ArrayList<>(errors.keySet()).iterator();
        if (!errorRanges.hasNext()) return new TreeMap<>();
        Range currentRange = errorRanges.next();
        while (errorRanges.hasNext()) {
            Range nextRange = errorRanges.next();

            while (nextRange.overlaps(currentRange)) {
                int currentErrorPriority = errors.get(currentRange).getPriority();
                int nextErrorPriority = errors.get(nextRange).getPriority();
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

    private TreeMap<Range, ValidationError> createErrorRangeMap(List<ValidationError> errors) {
        TreeMap<Range, ValidationError> errorMap = new TreeMap<>();
        for (ValidationError error : errors) {
            for (Integer[] range : error.getPositions())
                errorMap.put(new Range(range[0], range[1]), error);
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
        return ACTIVE_ERRORS.get(range).getMessage();
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

    public boolean goToMeasure(int measureCount) {
        Measure measure = new Score(TEXT_AREA.getText()).getMeasure(measureCount);
        if (measure==null) return false;
        List<Integer[]> linePositions = measure.getLinePositions();
        TEXT_AREA.moveTo(linePositions.get(0)[0]);
        TEXT_AREA.requestFollowCaret();
        TEXT_AREA.requestFocus();
        return true;
    }
}

