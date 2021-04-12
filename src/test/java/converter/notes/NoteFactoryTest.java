package converter.notes;

import converter.Instrument;
import converter.note.Note;
import converter.note.NoteFactory;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class NoteFactoryTest {
    HashMap<String, Integer> correctGraceGuitarNoteSamples = new HashMap<>() {
        {
            //hammer-ons
            put("g3h7", 2);
            put("g3h17", 2);
            put("g17h3", 2);
            put("g17h13", 2);
            //pull-offs
            put("g3p7", 2);
            put("g3p17", 2);
            put("g17p3", 2);
            put("g17p13", 2);
        }
    };

    HashMap<String, Integer> correctNonGraceGuitarNoteSamples = new HashMap<>() {
        {
            //notes without connectors
                //frets
            put("1",1);
            put("19",1);
                //harmonics
            put("[1]",1);
            put("[19]",1);

            //notes with connectors, no dashes
            put("8p3",2);
            put("5p13",2);
            put("12p7",2);
            put("16p19",2);

            put("8h3",2);
            put("5h13",2);
            put("12h7",2);
            put("16h19",2);

            put("8s3",2);
            put("5s13",2);
            put("12s7",2);
            put("16s19",2);

            put("8/3",2);
            put("5/13",2);
            put("12/7",2);
            put("16/19",2);

            put("8\\3",2);
            put("5\\13",2);
            put("12\\7",2);
            put("16\\19",2);

            //notes with connectors, dashes present
            put("8---p3",2);
            put("5---p13",2);
            put("12---p7",2);
            put("16---p19",2);

            put("8---h3",2);
            put("5---h13",2);
            put("12---h7",2);
            put("16---h19",2);

            put("8---s3",2);
            put("5---s13",2);
            put("12---s7",2);
            put("16---s19",2);

            put("8---/3",2);
            put("5---/13",2);
            put("12---/7",2);
            put("16---/19",2);

            put("8---\\3",2);
            put("5---\\13",2);
            put("12---\\7",2);
            put("16---\\19",2);
        }
    };

    Pattern guitarNotePattern = Pattern.compile(NoteFactory.GUITAR_NOTE_PATTERN);

    @Test
    void guitarNotePatternValidTest() {
        HashMap<String, Integer> samples = new HashMap<>();
        samples.putAll(correctGraceGuitarNoteSamples);
        samples.putAll(correctNonGraceGuitarNoteSamples);

        List<Note> notes;
        for (String sample : samples.keySet()) {
            try {
                notes = Note.from(sample, 1, Instrument.GUITAR, "C", 0);
            }catch (IllegalStateException e) {
                fail("A guitar note object could not be created from the following sample:\n\t\""+sample+"\"\n");
                return;
            }

            String matchesString = "";
            for (int i=0; i<notes.size(); i++)
                matchesString += (i==0 ? "" : ", ") + "\""+notes.get(i).origin.strip()+"\"";

            assertNotEquals(0, notes.size(), "the notes in the following sample were not detected:\n\tSample: \""+sample+"\"\n");

            assertEquals(samples.get(sample), notes.size(), notes.size() + " notes were extracted from the following sample where only "+samples.get(sample)+" were expected:\n\tSample: \""+sample+"\"\n\t"
                                                        + "Expected number of notes: "+samples.get(sample)+"\n\tExtracted notes: "+matchesString+"\n");
        }
    }
}
