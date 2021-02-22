package converter.measure;

import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GuitarMeasure extends Measure{
    public GuitarMeasure(List<String> lines, List<String> lineNames, List<Integer> linePositions, boolean isFirstMeasure) {
        super(lines, lineNames, linePositions, isFirstMeasure);
        this.lineNames = this.fixNamingOfE(lineNames);
        this.measureLineList = this.createMeasureLineList(this.lines, this.lineNames, this.positions);
    }

    private List<String>  fixNamingOfE(List<String> lineNames) {
        int lowerEcount = 0;
        int upperEcount = 0;
        StringBuilder order = new StringBuilder();
        for (String name : lineNames) {
            order.append(name.toLowerCase());
            if (name.equals("E"))
                upperEcount++;
            else if (name.equals("e"))
                lowerEcount++;
        }

        //if it's not in order, we have no certainty as to what E tuning they are referring to. leave it as it is
        if (!order.toString().equals("ebgdae") && !order.toString().equals("eadgbe")) return lineNames;

        //if there are not multiple Es (multiple lower case e or multiple upper case E) then there's nothing to decipher
        if (!(lowerEcount>1 || upperEcount>1)) return lineNames;

        String prevName = null;
        for (int i=0; i<lineNames.size(); i++) {
            String name = lineNames.get(i);
            if (!name.equalsIgnoreCase("e")) continue;
            ArrayList<String> surroundingNames = new ArrayList<>();
            if (prevName!=null)
                surroundingNames.add(prevName.toLowerCase());
            if (i+1!=lineNames.size())
                surroundingNames.add(lineNames.get(i+1).toLowerCase());

            if (surroundingNames.contains("b"))
                lineNames.set(i,"e");
            else lineNames.set(i,"E");  //not else if because we are guaranteed it is in either the order eadgbe or ebgdae (look ar the if statement outside this for loop)

                prevName = name;
        }
        return lineNames;
    }

    /**
     * Validates that all MeasureLine objects in this GuitarMeasure are GuitarMeasureLine objects, and validates its
     * aggregate MeasureLine objects. It stops evaluation at the first aggregated object which fails validation.
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
        if (!(this.measureLineList.get(0) instanceof GuitarMeasureLine)) {
            result.put("success", "false");
            result.put("message", "All measure lines in this measure must be Guitar measure lines.");
            result.put("positions", this.getLinePositions());
            result.put("priority", "1");
            return result;
        }

        if (this.measureLineList.size()!=6) {
            result.put("success", "false");
            result.put("message", "A guitar measure should have 6 lines.");
            result.put("positions", this.getLinePositions());
            result.put("priority", "2");
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
