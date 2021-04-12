package converter.measure;

import converter.note.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MeasureTest {

    private Measure measureInstance;

    @BeforeEach
    void init() {
        List<String> lines = Arrays.asList(
                "-----------3---------------5-------",
                "---5---------------8---7-----------",
                "-------2-------6--------9------0---",
                "-----------------------------------",
                "---3-------4-------5------6--------",
                "-----------------------------------");

        List<String[]> lineNames = Arrays.asList(new String[]{"e","1"}, new String[]{"B","1"}, new String[]{"G","1"}, new String[]{"D","1"}, new String[]{"A","1"}, new String[]{"E","1"});

        //doesn't really matter what values the position has.
        List<Integer> positions = Arrays.asList(1, 5, 7, 9, 20, 43);

        this.measureInstance = Measure.from(lines, lineNames, positions, false);
    }

    /** TODO fix test. It is broken
     * Ensures all the notes are detected in the correct order, and that chords are detected also as having the same distance from the start of the measure.
     */
    @Test
    void getNoteQueueTest() {
        String[] expected = {"35", "2", "34", "6", "85", "7", "9", "6", "5", "0"};
        List<Note> noteList = this.measureInstance.getSortedNoteList();
        for (int i=1; i<noteList.size(); i++) {
            Note previousNote = noteList.get(i-1);
            Note currentNote = noteList.get(i);
            //if (previousNote.distance==currentNote.distance)

                //assertTrue();

                //assertFalse();
        }
    }

    @Test
    void fromTest() {
        assertTrue(this.measureInstance instanceof GuitarMeasure, "incorrect detection of measure type");
    }
}
