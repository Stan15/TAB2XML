package converter.measure;

import converter.note.Note;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

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

        List<String> lineNames = Arrays.asList("e", "B", "G", "D", "A", "E");

        //doesn't really matter what values the position has.
        List<Integer> positions = Arrays.asList(1, 5, 7, 9, 20, 43);

        this.measureInstance = Measure.from(lines, lineNames, positions, false);
    }

    /**
     * Ensures all the notes are detected in the correct order, and that chords are detected also as having the same distance from the start of the measure.
     */
    @Test
    void getNoteQueueTest() {
        String[] expected = {"35", "2", "34", "6", "85", "7", "9", "6", "5", "0"};
        PriorityQueue<Note> noteQueue = this.measureInstance.getNoteQueue();
        int idx = 0;
        int currentChordSize = 0;
        while (!noteQueue.isEmpty()) {
            Note currentNote = noteQueue.poll();
            currentChordSize++;

            assertTrue(expected[idx].contains(currentNote.line), "The notes are not being detected in the correct order (the distance of the note from the start of the measure may not be accurate)");

            if (noteQueue.isEmpty() || noteQueue.peek().distance!=currentNote.distance) {
                assertEquals(expected[idx].length(), currentChordSize, "not all notes in the chord (or more notes than there exists in the chord) are being detected");
                currentChordSize = 0;;
                idx++;
            }
        }
        assertEquals(expected.length, idx, "not all chords in this measure were detected.");
    }

    @Test
    void fromTest() {
        assertTrue(this.measureInstance instanceof GuitarMeasure, "incorrect detection of measure type");
    }
}
