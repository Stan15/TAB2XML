package converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class MeasureGroupTest {
    public MeasureGroup measureGroupInstance;

    @BeforeEach
    void init() {
        List<String> lines = Arrays.asList(
                "[10]E |--------------------||-------------------|{-------------3-----|---------------8---|---------5---------}|-------------------|",
                "[20]B |---0-----0---0------||---0-----3---6-----|{-------------------|-------------------|-------------------}|-----3---------2---|",
                "[40]G |-------0-------0---.||-------0-------0---|{----4--------------|-------------------|----3--------------}|-------------------|",
                "[60]D |-------------------.||-------------------|{-------------9-----|-------------------|-------------------}|-----------6-------|",
                "[80]A |--------------------||-------------------|{-------------------|--------4----------|---------------8---}|-------------------|",
                "[97]E |---3-------3--------||---3-------3-------|{-------------------|----3--------------|--------5----------}|-----------1-------|"
        );
        this.measureGroupInstance = new MeasureGroup(lines);
    }

    @Test
    void measureCountValidation() {
        assertEquals(6, measureGroupInstance.measureList.size());
    }

    @Test
    void positions() {
        List<Integer> expected = Arrays.asList(10,20,40,60,80,97);
        assertTrue(expected.equals(this.measureGroupInstance.positions));
    }
}
