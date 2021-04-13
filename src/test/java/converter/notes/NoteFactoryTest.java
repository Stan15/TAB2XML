package converter.notes;

import converter.Instrument;
import converter.note.Note;
import converter.note.NoteFactory;
import models.measure.note.notations.Notations;
import models.measure.note.notations.Slide;
import models.measure.note.notations.technical.HammerOn;
import models.measure.note.notations.technical.PullOff;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class NoteFactoryTest {
    HashMap<String, Integer> correctGraceNoteSamples = new HashMap<>() {
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

    HashMap<String, Integer> correctFretSamples = new HashMap<>() {
        {
            put("1",1);
            put("19",1);
        }
    };

    HashMap<String, Integer> correctHarmonicSamples = new HashMap<>() {
        {
            put("[1]",1);
            put("[19]",1);
        }
    };

    HashMap<String, Integer> correctPullOffSamples = new HashMap<>() {
        {
            put("8p3",2);
            put("12p7",2);
            put("19p16",2);

            put("8---p3",2);
            put("12---p7",2);
            put("19---p16",2);
        }
    };

    HashMap<String, Integer> correctHammerOnSamples = new HashMap<>() {
        {
            put("3h8",2);
            put("5h13",2);
            put("16h19",2);

            put("3---h8",2);
            put("5---h13",2);
            put("16---h19",2);
        }
    };

    HashMap<String, Integer> correctSlideSamples = new HashMap<>() {
        {
            put("8s3",2);
            put("5s13",2);
            put("12s7",2);
            put("16s19",2);

            put("8---s3",2);
            put("5---s13",2);
            put("12---s7",2);
            put("16---s19",2);
        }
    };

    HashMap<String, Integer> correctSlideUpSamples = new HashMap<>() {
        {
            put("8/9",2);
            put("5/13",2);
            put("16/19",2);

            put("8---/9",2);
            put("5---/13",2);
            put("16---/19",2);
        }
    };

    HashMap<String, Integer> correctSlideDownSamples = new HashMap<>() {
        {
            put("8\\3",2);
            put("12\\7",2);
            put("19\\16",2);

            put("8---\\3",2);
            put("12---\\7",2);
            put("19---\\16",2);
        }
    };

    Pattern guitarNotePattern = Pattern.compile(NoteFactory.GUITAR_NOTE_PATTERN);

    @Test
    void guitarNotePatternValidTest() {
        HashMap<String, Integer> samples = new HashMap<>();
        samples.putAll(correctGraceNoteSamples);
        samples.putAll(correctFretSamples);
        samples.putAll(correctHarmonicSamples);
        samples.putAll(correctHammerOnSamples);
        samples.putAll(correctPullOffSamples);
        samples.putAll(correctSlideSamples);
        samples.putAll(correctSlideDownSamples);
        samples.putAll(correctSlideUpSamples);

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

    @Test
    void graceNoteTest() {
        for (String noteStr : correctGraceNoteSamples.keySet()) {
            List<Note> notes = new NoteFactory(noteStr, 0, Instrument.GUITAR, "C", 0).getNotes();
            Matcher matcher = Pattern.compile("[0-9]+").matcher(noteStr);
            int i=0;
            while (matcher.find()) {
                String expected = matcher.group();
                assertTrue(i<notes.size(), "Some notes in the following note group were not detected.\nNote Group: "+noteStr+"\n");
                Note note = notes.get(i++);
                String actual = note.origin;
                assertEquals(expected, actual, "wrong note detected.");
                if (i==1) { //it is the grace note
                    assertNotNull(note.getModel().getGrace(), "The note "+note.origin+" in the following note group is expected to be a grace note.\nNote Group: "+noteStr+"\n");
                }else { //it is a grace pair
                    assertNull(note.getModel().getGrace(), "The note "+note.origin+" in the following note group is expected NOT to be a grace note.\nNote Group: "+noteStr+"\n");
                    assertNull(note.getModel().getChord(), "A grace pair should not be chorded to its grace note, but it was in the following note group.\nNote Group: "+noteStr);
                }
            }
            assertEquals(i,notes.size(), "More notes were detected in the following note group than was expected.\nNote Group: " + noteStr+"\n");
        }
    }

    @Test
    void fretTest() {
        for (String noteStr : correctFretSamples.keySet()) {
            List<Note> notes = new NoteFactory(noteStr, 0, Instrument.GUITAR, "C", 0).getNotes();
            Matcher matcher = Pattern.compile("[0-9]+").matcher(noteStr);
            int i=0;
            while (matcher.find()) {
                String expected = matcher.group();
                assertTrue(i<notes.size(), "Some notes in the following note group were not detected.\nNote Group: "+noteStr+"\n");
                Note note = notes.get(i++);
                String actual = note.origin;
                assertEquals(expected, actual, "wrong note detected in the following note group.\nNote Group: "+noteStr+"\n");
            }
            assertEquals(i,notes.size(), "More notes were detected in the following note group than was expected.\nNote Group: " + noteStr+"\n");
        }
    }

    @Test
    void harmonicTest() {
        for (String noteStr : correctHarmonicSamples.keySet()) {
            List<Note> notes = new NoteFactory(noteStr, 0, Instrument.GUITAR, "C", 0).getNotes();
            Matcher matcher = Pattern.compile("[0-9]+").matcher(noteStr);
            int i=0;
            while (matcher.find()) {
                String expected = matcher.group();
                assertTrue(i<notes.size(), "Some notes in the following note group were not detected.\nNote Group: "+noteStr+"\n");
                Note note = notes.get(i++);
                String actual = note.origin;
                assertEquals(expected, actual, "wrong note detected in the following note group.\nNote Group: "+noteStr+"\n");

                Notations notations = note.getModel().getNotations();
                assertNotNull(notations, "Expected a harmonic in the following note group but none was found.\nNote Group: "+noteStr+"\n");
                assertNotNull(notations.getTechnical(), "Expected a harmonic in the following note group but none was found.\nNote Group: "+noteStr+"\n");
                assertNotNull(notations.getTechnical().getHarmonic(), "Expected a harmonic in the following note group but none was found.\nNote Group: "+noteStr+"\n");
            }
            assertEquals(i,notes.size(), "More notes were detected in the following note group than was expected.\nNote Group: " + noteStr+"\n");
        }
    }

    @Test
    void hammerOnTest() {
        for (String noteStr : correctHammerOnSamples.keySet()) {
            List<Note> notes = new NoteFactory(noteStr, 0, Instrument.GUITAR, "C", 0).getNotes();
            Matcher matcher = Pattern.compile("[0-9]+").matcher(noteStr);
            int matchCount = 0;
            while (matcher.find()) matchCount++;

            matcher = Pattern.compile("[0-9]+").matcher(noteStr);
            int i=0;
            while (matcher.find()) {
                String expected = matcher.group();
                assertTrue(i<notes.size(), "Some notes in the following note group were not detected.\nNote Group: "+noteStr+"\n");
                Note note = notes.get(i++);
                String actual = note.origin;
                assertEquals(expected, actual, "wrong note detected in the following note group.\nNote Group: "+noteStr+"\n");

                Notations notations = note.getModel().getNotations();
                assertNotNull(notations, "Expected a hammer-on in the following note group but none were found.\nNote Group: "+noteStr+"\n");
                assertNotNull(notations.getTechnical(), "Expected a hammer-on in the following note group but none were found.\nNote Group: "+noteStr+"\n");
                assertNotNull(notations.getTechnical().getHammerOns(), "Expected a hammer-on in the following note group but none were found.\nNote Group: "+noteStr+"\n");

                List<HammerOn> hammerOns = notations.getTechnical().getHammerOns();
                assertTrue(hammerOns.size() <= 2 || (i == 1 && hammerOns.size()>=2), "More hammer-on tags were detected than was expected in the JacksonXML model for the note \""+note.origin+"\" in the following note group.\nNote Group: "+noteStr+"\n");

                Set<String> hammerStartStopSet = new HashSet<>();
                for(int j=0; j<hammerOns.size(); j++) {
                    if (i==1)
                        assertTrue(hammerOns.get(j).getType().equalsIgnoreCase("start"), "Expected the first note in the following note group to have a \"start\" hammer-on type.\nNote Group: "+noteStr);
                    if (i==matchCount)
                        assertTrue(hammerOns.get(j).getType().equalsIgnoreCase("stop"), "Expected the last note in the following note group to have a \"stop\" hammer-on type.\nNote Group: "+noteStr);
                    hammerStartStopSet.add(hammerOns.get(j).getType().toLowerCase());
                }
                if (i==1)
                    assertEquals(1, hammerStartStopSet.size(), "One hammer on tag (start) was expected in the JacksonXML model for the note \""+note.origin+"\" in the following note group.\nNote Group: "+noteStr+"\nHammer on tags found: "+hammerStartStopSet.toString()+"\n");
                else if (i==matchCount)
                    assertEquals(1, hammerStartStopSet.size(), "One hammer on tag (stop) was expected in the note "+note.origin+" in the following note group.\nNote Group: "+noteStr+"\nHammer on tags found: "+hammerStartStopSet.toString()+"\n");
                else
                    assertEquals(2, hammerStartStopSet.size(), "Two hammer on tags (start and stop) were expected in the JacksonXML model for the note \""+note.origin+"\" in the following note group.\nNote Group: "+noteStr+"\nHammer on tags found: "+hammerStartStopSet.toString()+"\n");

            }
            assertEquals(i,notes.size(), "More notes were detected in the following note group than was expected.\nNote Group: " + noteStr);
        }
    }

    @Test
    void pullOffTest() {
        for (String noteStr : correctPullOffSamples.keySet()) {
            List<Note> notes = new NoteFactory(noteStr, 0, Instrument.GUITAR, "C", 0).getNotes();
            Matcher matcher = Pattern.compile("[0-9]+").matcher(noteStr);
            int matchCount = 0;
            while (matcher.find()) matchCount++;

            matcher = Pattern.compile("[0-9]+").matcher(noteStr);
            int i=0;
            while (matcher.find()) {
                String expected = matcher.group();
                assertTrue(i<notes.size(), "Some notes in the following note group were not detected.\nNote Group: "+noteStr+"\n");
                Note note = notes.get(i++);
                String actual = note.origin;
                assertEquals(expected, actual, "wrong note detected in the following note group.\nNote Group: "+noteStr+"\n");

                Notations notations = note.getModel().getNotations();
                assertNotNull(notations, "Expected a pull-off in the following note group but none were found.\nNote Group: "+noteStr+"\n");
                assertNotNull(notations.getTechnical(), "Expected a pull-off in the following note group but none were found.\nNote Group: "+noteStr+"\n");
                assertNotNull(notations.getTechnical().getPullOffs(), "Expected a pull-off in the following note group but none were found.\nNote Group: "+noteStr+"\n");

                List<PullOff> pullOffs = notations.getTechnical().getPullOffs();
                assertTrue(pullOffs.size() <= 2 || (i == 1 && pullOffs.size()>=2), "More pull-off tags were detected than was expected in the JacksonXML model for the note \""+note.origin+"\" in the following note group.\nNote Group: "+noteStr+"\n");

                Set<String> pullStartStopSet = new HashSet<>();
                for (PullOff pullOff : pullOffs) {
                    if (i == 1)
                        assertTrue(pullOff.getType().equalsIgnoreCase("start"), "Expected the first note in the following note group to have a \"start\" pull-off type.\nNote Group: " + noteStr);
                    if (i == matchCount)
                        assertTrue(pullOff.getType().equalsIgnoreCase("stop"), "Expected the last note in the following note group to have a \"stop\" pull-off type.\nNote Group: " + noteStr);
                    pullStartStopSet.add(pullOff.getType().toLowerCase());
                }
                if (i==1)
                    assertEquals(1, pullStartStopSet.size(), "One pull-off tag (start) was expected in the JacksonXML model for the note \""+note.origin+"\" in the following note group.\nNote Group: "+noteStr+"\nPull-off tags found: "+pullStartStopSet.toString()+"\n");
                else if (i==matchCount)
                    assertEquals(1, pullStartStopSet.size(), "One pull-off tag (stop) was expected in the note "+note.origin+" in the following note group.\nNote Group: "+noteStr+"\nPull-off tags found: "+pullStartStopSet.toString()+"\n");
                else
                    assertEquals(2, pullStartStopSet.size(), "Two pull-off tags (start and stop) were expected in the JacksonXML model for the note \""+note.origin+"\" in the following note group.\nNote Group: "+noteStr+"\nPull-off tags found: "+pullStartStopSet.toString()+"\n");

            }
            assertEquals(i,notes.size(), "More notes were detected in the following note group than was expected.\nNote Group: " + noteStr);
        }
    }

    @Test
    void slideTest() {
        HashMap<String, Integer> samples = new HashMap<>();
        samples.putAll(correctSlideSamples);
        samples.putAll(correctSlideUpSamples);
        samples.putAll(correctSlideDownSamples);
        for (String noteStr : correctSlideSamples.keySet()) {
            List<Note> notes = new NoteFactory(noteStr, 0, Instrument.GUITAR, "C", 0).getNotes();
            Matcher matcher = Pattern.compile("[0-9]+").matcher(noteStr);
            int matchCount = 0;
            while (matcher.find()) matchCount++;

            matcher = Pattern.compile("[0-9]+").matcher(noteStr);
            int i=0;
            while (matcher.find()) {
                String expected = matcher.group();
                assertTrue(i<notes.size(), "Some notes in the following note group were not detected.\nNote Group: "+noteStr+"\n");
                Note note = notes.get(i++);
                String actual = note.origin;
                assertEquals(expected, actual, "wrong note detected in the following note group.\nNote Group: "+noteStr+"\n");

                Notations notations = note.getModel().getNotations();
                assertNotNull(notations, "Expected a slide in the following note group but none were found.\nNote Group: "+noteStr+"\n");
                assertNotNull(notations.getSlides(), "Expected a slide in the following note group but none were found.\nNote Group: "+noteStr+"\n");

                List<Slide> slides = notations.getSlides();
                assertTrue(slides.size() <= 2 || (i == 1 && slides.size()>=2), "More slide tags were detected than was expected in the JacksonXML model for the note \""+note.origin+"\" in the following note group.\nNote Group: "+noteStr+"\n");

                Set<String> pullStartStopSet = new HashSet<>();
                for (Slide slide : slides) {
                    if (i == 1)
                        assertTrue(slide.getType().equalsIgnoreCase("start"), "Expected the first note in the following note group to have a \"start\" slide type.\nNote Group: " + noteStr);
                    if (i == matchCount)
                        assertTrue(slide.getType().equalsIgnoreCase("stop"), "Expected the last note in the following note group to have a \"stop\" slide type.\nNote Group: " + noteStr);
                    pullStartStopSet.add(slide.getType().toLowerCase());
                }
                if (i==1)
                    assertEquals(1, pullStartStopSet.size(), "One slide tag (start) was expected in the JacksonXML model for the note \""+note.origin+"\" in the following note group.\nNote Group: "+noteStr+"\nSlide tags found: "+pullStartStopSet.toString()+"\n");
                else if (i==matchCount)
                    assertEquals(1, pullStartStopSet.size(), "One slide tag (stop) was expected in the note "+note.origin+" in the following note group.\nNote Group: "+noteStr+"\nSlide tags found: "+pullStartStopSet.toString()+"\n");
                else
                    assertEquals(2, pullStartStopSet.size(), "Two slide tags (start and stop) were expected in the JacksonXML model for the note \""+note.origin+"\" in the following note group.\nNote Group: "+noteStr+"\nSlide tags found: "+pullStartStopSet.toString()+"\n");

            }
            assertEquals(i,notes.size(), "More notes were detected in the following note group than was expected.\nNote Group: " + noteStr);
        }
    }

    @Test
    void flamTest() {
        HashMap<String, Integer> flamSamples = new HashMap<>() {
            {
                put("f", 1);
                put("of", 1);
                put("xf", 1);
                put("fo", 1);
                put("fx", 1);
                put("fd", 1);
                put("df", 1);
                put("ff", 2);
                put("ffo", 2);
                put("ffx", 2);
                put("off", 2);
                put("xff", 2);
                put("offo", 2);
                put("xffx", 2);
                put("fof", 2);
                put("fxf", 2);
                put("fdf", 2);
                put("fxdf", 2);
                put("dff", 2);
                put("ffd", 2);
            }
        };

        for (String noteGroup : flamSamples.keySet()) {
            List<Note> notes = new NoteFactory(noteGroup, 0, Instrument.DRUM, "C", 0).getNotes();

            int flamCount = 0;
            boolean prevWasGrace = false;
            for (Note note : notes) {
                if (!note.origin.equalsIgnoreCase("f")) continue;
                flamCount++;
                if (!prevWasGrace) { //it is the grace note
                    assertNotNull(note.getModel().getGrace(), "The first note produced by the flam in the following note group is expected to be a grace note.\nNote Group: " + noteGroup + "\n");
                    prevWasGrace = true;
                } else { //it is a grace pair
                    assertNull(note.getModel().getGrace(), "The first note produced by the flam in the following note group is expected to NOT be a grace note.\nNote Group: " + noteGroup + "\n");
                    assertNull(note.getModel().getChord(), "The second note in a flam should not be chorded to the first, but it was in a flam in the following note group.\nNote Group: "+noteGroup);
                    prevWasGrace = false;
                }
            }
            flamCount /= 2; //we have a grace note and a grace pair for each flam

            assertEquals(flamSamples.get(noteGroup), flamCount, flamSamples.get(noteGroup) + " flams were expected in the following note group but " + flamCount + " were detected.\nNote Group: " + noteGroup + "\n");
        }
    }

    @Test
    void dragTest() {
        HashMap<String, Integer> dragSamples = new HashMap<>() {
            {
                put("d", 1);
                put("od", 1);
                put("xd", 1);
                put("do", 1);
                put("dx", 1);
                put("df", 1);
                put("fd", 1);
                put("dd", 2);
                put("ddo", 2);
                put("ddx", 2);
                put("odd", 2);
                put("xdd", 2);
                put("oddo", 2);
                put("xddx", 2);
                put("dod", 2);
                put("dxd", 2);
                put("dfd", 2);
                put("dxfd", 2);
                put("fdd", 2);
                put("ddf", 2);
            }
        };

        for (String noteGroup : dragSamples.keySet()) {
            List<Note> notes = new NoteFactory(noteGroup, 0, Instrument.DRUM, "C", 0).getNotes();

            int dragCount = 0;
            Note firstDragNote = null;
            for (Note note : notes) {
                if (!note.origin.equalsIgnoreCase("d")) continue;
                dragCount++;
                if (firstDragNote == null) {
                    firstDragNote = note;
                } else { //it is a grace pair
                    assertEquals(firstDragNote.distance, note.distance, "A drag note in the following note group was not properly created. notes produced by a drag are meant to be equal, but the distances of the notes produced by the drag differs.\nNote Group: "+noteGroup);
                    assertNull(note.getModel().getChord(), "The second note in a drag should not be chorded to the first, but it was in a drag in the following note group.\nNote Group: "+noteGroup);
                    firstDragNote = null;
                }
            }
            dragCount /= 2; //we have a grace note and a grace pair for each flam

            assertEquals(dragSamples.get(noteGroup), dragCount, dragSamples.get(noteGroup) + " drags were expected in the following note group but " + dragCount + " were detected.\nNote Group: " + noteGroup + "\n");
        }
    }
}
