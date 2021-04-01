package converter.measure;

import GUI.TabInput;
import converter.measure_line.BassMeasureLine;
import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BassMeasure extends GuitarMeasure {
    public static final int MIN_LINE_COUNT = 4;
    public static final int MAX_LINE_COUNT = 4;

    public BassMeasure(List<String> lines, List<String[]> lineNamesAndPositions, List<Integer> linePositions, boolean isFirstMeasure) {
        super(lines, lineNamesAndPositions, linePositions, isFirstMeasure);
    }
    @Override
    public List<HashMap<String, String>> validate() {
        //-----------------Validate yourself-------------------------
        List<HashMap<String,String>> result = new ArrayList<>(super.validate()); //this validates if all MeasureLine objects in this measure are of the same type

        // Now, all we need to do is check if they are actually guitar measures
        if (!(this.measureLineList.get(0) instanceof BassMeasureLine)) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "All measure lines in this measure must be Bass measure lines.");
            response.put("positions", this.getLinePositions());
            int priority = 1;
            response.put("priority", ""+priority);
            if (TabInput.ERROR_SENSITIVITY>=priority)
                result.add(response);
        }

        if (this.measureLineList.size()<MIN_LINE_COUNT || this.measureLineList.size()>MAX_LINE_COUNT) {
            HashMap<String, String> response = new HashMap<>();
            String rangeMsg;
            if (MIN_LINE_COUNT==MAX_LINE_COUNT)
                rangeMsg = ""+MIN_LINE_COUNT;
            else
                rangeMsg = "between "+MIN_LINE_COUNT+" and "+MAX_LINE_COUNT;
            response.put("message", "A Bass measure should have "+rangeMsg+" lines.");
            response.put("positions", this.getLinePositions());
            int priority = 2;
            response.put("priority", ""+priority);
            if (TabInput.ERROR_SENSITIVITY>=priority)
                result.add(response);
        }


        //-----------------Validate Aggregates (only if you are valid)------------------
        if (!result.isEmpty()) return result;

        for (MeasureLine measureLine : this.measureLineList) {
            result.addAll(measureLine.validate());
        }

        return result;
    }
}
