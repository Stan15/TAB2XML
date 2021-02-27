package converter.measure;

import converter.Patterns;
import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuitarMeasure extends Measure{
    public GuitarMeasure(List<String> lines, List<String[]> lineNamesAndPositions, List<Integer> linePositions, boolean isFirstMeasure) {
        super(lines, lineNamesAndPositions, linePositions, isFirstMeasure);
        this.lineNamesAndPositions = this.fixNamingOfE(lineNamesAndPositions);
        this.measureLineList = this.createMeasureLineList(this.lines, this.lineNamesAndPositions, this.positions);
    }

    private List<String[]>  fixNamingOfE(List<String[]> lineNamesAndPositions) {
        int lowerEcount = 0;
        int upperEcount = 0;
        StringBuilder order = new StringBuilder();
        for (String[] nameAndPosition : lineNamesAndPositions) {
            String strippedName = nameAndPosition[0].strip();
            order.append(strippedName.toLowerCase());
            if (strippedName.equals("E"))
                upperEcount++;
            else if (strippedName.equals("e"))
                lowerEcount++;
        }

        //if it's not in order, we have no certainty as to what E tuning they are referring to. leave it as it is
        if (!order.toString().equals("ebgdae") && !order.toString().equals("eadgbe")) return lineNamesAndPositions;

        //if there are not multiple Es (multiple lower case e or multiple upper case E) then there's nothing to decipher
        if (!(lowerEcount>1 || upperEcount>1)) return lineNamesAndPositions;

        String prevName = null;
        for (int i=0; i<lineNamesAndPositions.size(); i++) {
            String name = lineNamesAndPositions.get(i)[0];
            if (!name.strip().equalsIgnoreCase("e")) {
                prevName = name;
                continue;
            }
            ArrayList<String> surroundingNames = new ArrayList<>();
            if (prevName!=null)
                surroundingNames.add(prevName.strip().toLowerCase());
            if (i+1<lineNamesAndPositions.size()) {
                surroundingNames.add(lineNamesAndPositions.get(i+1)[0].strip().toLowerCase());
            }

            if (surroundingNames.contains("b"))
                lineNamesAndPositions.get(i)[0] = lineNamesAndPositions.get(i)[0].toLowerCase();  //lower case e
            else lineNamesAndPositions.get(i)[0] = lineNamesAndPositions.get(i)[0].toUpperCase();  //upper case E

            prevName = name;
        }

        return lineNamesAndPositions;
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
    public List<HashMap<String, String>> validate() {
        List<HashMap<String,String>> result = new ArrayList<>();
        //-----------------Validate yourself-------------------------
        result.addAll(super.validate()); //this validates if all MeasureLine objects in this measure are of the same type

        //if we are here, all MeasureLine objects are of the same type. Now, all we need to do is check if they are actually guitar measures
        if (!(this.measureLineList.get(0) instanceof GuitarMeasureLine)) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "All measure lines in this measure must be Guitar measure lines.");
            response.put("positions", this.getLinePositions());
            response.put("priority", "1");
            result.add(response);
        }

        if (this.measureLineList.size()!=6) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "A guitar measure should have 6 lines.");
            response.put("positions", this.getLinePositions());
            response.put("priority", "2");
            result.add(response);
        }


        //-----------------Validate Aggregates (only if you are valid)------------------
        if (!result.isEmpty()) return result;

        for (MeasureLine measureLine : this.measureLineList) {
            result.addAll(measureLine.validate());
        }

        return result;
    }

//    @Override
//    protected StringBuilder addAttributesXML(StringBuilder measureXML) {
//        measureXML.append("<attributes>\n");
//        measureXML.append("<divisions>");
//        measureXML.append(this.beatType/4);
//        measureXML.append("</divisions>\n");
//
//        measureXML.append("<key>\n");
//
//        measureXML.append("<fifths>");
//        measureXML.append(0);
//        measureXML.append("</fifths>\n");
//        measureXML.append("<mode>major</mode>\n");
//        measureXML.append("</key>\n");
//
//        measureXML.append("""
//                <clef>
//                  <sign>TAB</sign>
//                  <line>5</line>
//                </clef>
//                <staff-details>
//                  <staff-lines>6</staff-lines>
//                  <staff-tuning line="1">
//                    <tuning-step>E</tuning-step>
//                    <tuning-octave>2</tuning-octave>
//                  </staff-tuning>
//                  <staff-tuning line="2">
//                    <tuning-step>A</tuning-step>
//                    <tuning-octave>2</tuning-octave>
//                  </staff-tuning>
//                  <staff-tuning line="3">
//                    <tuning-step>D</tuning-step>
//                    <tuning-octave>3</tuning-octave>
//                  </staff-tuning>
//                  <staff-tuning line="4">
//                    <tuning-step>G</tuning-step>
//                    <tuning-octave>3</tuning-octave>
//                  </staff-tuning>
//                  <staff-tuning line="5">
//                    <tuning-step>B</tuning-step>
//                    <tuning-octave>3</tuning-octave>
//                  </staff-tuning>
//                  <staff-tuning line="6">
//                    <tuning-step>E</tuning-step>
//                    <tuning-octave>4</tuning-octave>
//                  </staff-tuning>
//                </staff-details>
//                """);
//        measureXML.append("</attributes>\n");
//        return measureXML;
//    }
}
