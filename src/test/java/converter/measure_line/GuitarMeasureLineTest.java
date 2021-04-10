package converter.measure_line;

import converter.note.GuitarNote;
import converter.note.Note;
import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GuitarMeasureLineTest {

    /* Testing individual bars for valid and invalid inputs */
    @Test
    void testValidity1(){
        String s1 = ("---12---6-4-5-8-1-----------2-");
        MeasureLine measureLine = new GuitarMeasureLine(s1, new String[]{"e", "1"}, 4);
        Integer[] expected = {12, 6, 4, 5, 8, 1, 2};
        for(int i = 0; i < measureLine.noteList.size(); i++) {
            GuitarNote note = (GuitarNote) measureLine.noteList.get(i);
            assertEquals(expected[i], note.getFret());
        }
    }
    @Test
    void testValidity2(){
        String s2 = ("-1-----1-1-1-1--");
        MeasureLine measureLine = new GuitarMeasureLine(s2, new String[]{"B", "1"}, 4);
        Integer[] expected = {1, 1, 1, 1, 1};
        for(int i = 0; i < measureLine.noteList.size(); i++){
            GuitarNote note = (GuitarNote) measureLine.noteList.get(i);
            assertEquals(expected[i], note.getFret());
        }
    }
    @Test
    void testValidity3(){
        String s3 = ("|-----------------------------28-|");
        MeasureLine measureLine = new GuitarMeasureLine(s3, new String[]{"D", "1"}, 4);
        Integer[] expected = {28};
        for(int i = 0; i < measureLine.noteList.size(); i++){
            GuitarNote note = (GuitarNote) measureLine.noteList.get(i);
            assertEquals(expected[i], note.getFret());
        }
    }
    @Test
    void testValidity4(){
        String s6 = ("|----2--4--7-7----6---|");
        MeasureLine measureLine = new GuitarMeasureLine(s6, new String[]{"G", "1"}, 4);
        assertTrue(measureLine.validate().isEmpty());
    }
    @Test
    void testInvalidity1(){
        String s4 = ("----------|----------");
        MeasureLine measureLine = new GuitarMeasureLine(s4, new String[]{"B", "1"}, 4);
        assertFalse(measureLine.validate().isEmpty());
    }
    @Test
    void testInvalidity2(){
        String s5 = ("----2----|---4----8--|");
        MeasureLine measureLine = new GuitarMeasureLine(s5, new String[]{"g", "1"}, 4);
        assertFalse(measureLine.validate().isEmpty());
    }
    @Test
    void testInvalidNaming(){
        String[] names = {"x", "z", "v", "q", "w", "u", "y", "i", "o", "p", "X", "Z", "V", "Q", "W", "U", "Y", "I", "O", "P"};
        String s6 = ("----2--4--7-7----6---");
        Integer[] expected = {};
        for(int i = 0; i < names.length; i++){
            MeasureLine measureLine = new GuitarMeasureLine(s6, new String[]{names[i], "1"}, 4);
            assertFalse(measureLine.validate().isEmpty());
        }
    }
//    @Test
//    void testInvalid

}
