package conrverter.measure_group;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import converter.MeasureGroup;

import java.util.ArrayList;
import java.util.List;

public class MeasureGroupTest {
    @Test
    void testValidate_validInput1() {
        List<String> origin = new ArrayList<String>();
        origin.add("[0,0] e|-3-2-2-0----|");
        origin.add("[1,1] a|-3-2-2-0----|");
        origin.add("[2,2] d|-3-2-2-0----|");
        origin.add("[3,3] g|-3-2-2-0----|");
        origin.add("[4,4] b|-3-2-2-0----|");
        origin.add("[5,5] e|-3-2-2-0----|");
        MeasureGroup mg = new MeasureGroup(origin);
        assertEquals(mg.validate().get("success"), true);
    }

    @Test
    void testValidate_validInput2() {
        List<String> origin = new ArrayList<String>();
        origin.add("[0,0] E|-3-2-2-0----|");
        origin.add("[10,20] A|-3-2-2-0----|");
        origin.add("[20,30] D|-3-2-2-0----|");
        origin.add("[30,40] G|-3-2-2-0----|");
        origin.add("[40,50] B|-3-2-2-0----|");
        origin.add("[50,60] e|-3-2-2-0----|");
        MeasureGroup mg = new MeasureGroup(origin);
        assertEquals(mg.validate().get("success"), true);
    }

    @Test
    void testValidate_validInput3() {
        List<String> origin = new ArrayList<String>();
        origin.add("[0,0] E|------------1-|");
        origin.add("[10,20] A|-----1-|");
        origin.add("[20,30] D|--2-|");
        origin.add("[30,40] G|----1-|");
        origin.add("[40,50] B|-----1-|");
        origin.add("[50,60] e|-----1-|");
        MeasureGroup mg = new MeasureGroup(origin);
        assertEquals(mg.validate().get("success"), true);
    }

    @Test
    void testValidate_invalidInput1() {
        List<String> origin = new ArrayList<String>();
        origin.add("[0,d] E|------------1|");
        origin.add("[10,20] A|-----1|");
        origin.add("[20,30] D|--2|");
        origin.add("[30,40] G|----1-|");
        origin.add("[40,50] B|-----1-|");
        origin.add("[50,60] e|-----1-|");
        MeasureGroup mg = new MeasureGroup(origin);
        assertEquals(mg.validate().get("success"), false);
    }

    @Test
    void testValidate_invalidInput2() {
        List<String> origin = new ArrayList<String>();
        origin.add("[0,0] E|------------1-|");
        origin.add("[10,20] A|-----1-|");
        origin.add("[20,30] abcd|--2-|");
        origin.add("[30,40] G|----1-|");
        origin.add("[40,50] B|-----1-|");
        origin.add("[50,60] e|-----1-|");
        MeasureGroup mg = new MeasureGroup(origin);
        assertEquals(mg.validate().get("success"), false);
    }

    @Test
    void testValidate_invalidInput3() {
        List<String> origin = new ArrayList<String>();
        origin.add("[0,0] e|-3-2-2-0---a-|");
        origin.add("[1,1] a|-3-2-2-0---b-|");
        origin.add("[2,2] d|-3-2-2-0--c--|");
        origin.add("[3,3] g|-3-2-2-0---d-|");
        origin.add("[4,4] b|-3-2-2-0---e-|");
        origin.add("[5,5] e|-3-2-2-0---f-|");
        MeasureGroup mg = new MeasureGroup(origin);
        assertEquals(mg.validate().get("success"), false);
    }

    @Test
    void testToXML() {
        List<String> origin = new ArrayList<String>();
        origin.add("[0,0] e|-3-2-2-0-----0--0-0-----------------------------------------------|");
        origin.add("[10,20] B|-3-3-3-0-----0--1-0-----------------------------------------------|");
        origin.add("[20,30] G|-4-2-4-2-----0--0-1-----------------------------------------------|");
        origin.add("[30,40] D|-5-0-4-2-----2--2-2-----------------------------------------------|");
        origin.add("[40,50] A|-5---2-0-----2--3-2-----------------------------------------------|");
        origin.add("[50,60] E|-3-----------0----0-----------------------------------------------|");
        MeasureGroup mg = new MeasureGroup(origin);
        System.out.println(mg.toXML());
        assertEquals(mg.toXML(), "");
    }
}
