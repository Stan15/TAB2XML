package converter.measure_line;

import converter.note.Note;
import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GuitarMeasureLineTest {

    /*Testing individual bars*/
    @Test
    void testValidity1(){
        String s1 = ("---12---6-4-5-8-1-----------2-");
        MeasureLine measureLine = new GuitarMeasureLine(s1, "e", 4);
        Integer[] expected = {12, 6, 4, 5, 8, 1, 2};
        for(int i = 0; i < measureLine.noteList.size(); i++) {
            Note note = measureLine.noteList.get(i);
            assertEquals(note.fret, expected[i]);
        }
    }
    @Test
    void testValidity2(){
        String s2 = ("-1-----1-1-1-1--");
        MeasureLine measureLine = new GuitarMeasureLine(s2, "B", 4);
        Integer[] expected = {1, 1, 1, 1, 1};
        for(int i = 0; i < measureLine.noteList.size(); i++){
            Note note = measureLine.noteList.get(i);
            assertEquals(note.fret, expected[i]);
        }
    }
    @Test
    void testValidity3(){
        String s3 = ("-----------------------------28-");
        MeasureLine measureLine = new GuitarMeasureLine(s3, "D", 4);
        Integer[] expected = {28};
        for(int i = 0; i < measureLine.noteList.size(); i++){
            Note note = measureLine.noteList.get(i);
            assertEquals(note.fret, expected[i]);
        }
    }
    @Test
    void testInvalidity1(){
        String s4 = ("----------|----------");
        MeasureLine measureLine = new GuitarMeasureLine(s4, "f", 4);
        Integer[] expected = {};
        for(int i = 0; i < measureLine.noteList.size(); i++){
            Note note = measureLine.noteList.get(i);
            assertEquals(note.fret, expected[i]);
        }
    }
    @Test
    void testInvalidity2(){
        String s5 = "----2----|---4----8-";
        MeasureLine measureLine = new GuitarMeasureLine(s5, "C", 4);
        Integer[] expected = {};
        for(int i = 0; i < measureLine.noteList.size(); i++){
            Note note = measureLine.noteList.get(i);
            assertEquals(note.fret, expected[i]);
        }
    }
    @Test
    void testInvalidity3(){
        String s6 = ("----|----");
        MeasureLine measureLine = new GuitarMeasureLine(s6, "D#", 4);
        Integer[] expected = {};
        for(int i = 0; i < measureLine.noteList.size(); i++){
            Note note = measureLine.noteList.get(i);
            assertEquals(note.fret, expected[i]);
        }
    }

}
