package converter.measure;

import converter.measure_line.DrumMeasureLine;
import converter.measure_line.MeasureLine;

import java.util.HashMap;
import java.util.List;

public class DrumMeasure extends Measure {

    public DrumMeasure(List<String> lines, List<String> lineNames, List<Integer> linePositions, boolean isFirstMeasure) {
        super(lines, lineNames, linePositions, isFirstMeasure);
        this.measureLineList = this.createMeasureLineList(this.lines, this.lineNames, this.positions);
    }
    /**
     * Validates that all MeasureLine objects in this GuitarMeasure are GuitarMeasureLine objects, and validates its
     * aggregated MeasureLine objects. It stops evaluation at the first aggregated object which fails validation.
     * TODO it might be better to not have it stop when one aggregated object fails validation, but instead have it
     *      validate all of them and return a List of all aggregated objects that failed validation, so the user knows
     *      all what is wrong with their tablature file, instead of having to fix one problem before being able to see
     *      what the other problems with their text file is.
     * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
     * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
     * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
     * found in the root string from which it was derived (i.e Score.ROOT_STRING).
     * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    @Override
    public HashMap<String, String> validate() {
        //-----------------Validate yourself-------------------------
        HashMap<String, String> superResult = super.validate(); //this validates if all MeasureLine objects in this measure are of the same type
        if (superResult.get("success").equals("false"))
            return superResult;

        HashMap<String, String> result = new HashMap<>();
        //if we are here, all MeasureLine objects are of the same type. Now, all we need to do is check if they are actually guitar measures
        if (!(this.measureLineList.get(0) instanceof DrumMeasureLine)) {
            result.put("success", "false");
            result.put("message", "All measure lines in this measure must be Guitar measure lines.");
            result.put("positions", this.getLinePositions());
            result.put("priority", "1");
            return result;
        }

        //-----------------Validate Aggregates------------------
        for (MeasureLine measureLine : this.measureLineList) {
            HashMap<String,String> response = measureLine.validate();
            if (response.get("success").equals("false"))
                return response;
        }

        result.put("success", "true");
        return result;
    }
}
