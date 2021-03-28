package converter.notes;

import converter.GuitarConverter.GuitarNote;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuitarNotesTest {

    //Guitar's octave and keys
    //reference: https://www.reddit.com/r/guitarlessons/comments/cp7dg5/guitar_fretboard_octavesinrelationtopianooctaves/

    @Test
    void makeOctave(){
        int stringNumber = 6;
        int fretNumber = 0;
        while(fretNumber < 20){
            int octave = GuitarNote.octave(stringNumber,fretNumber);
            if(fretNumber <= 7){
                assertEquals(2, octave);
            }
            else{
                assertEquals(3, octave);
            }
            fretNumber++;
        }

        stringNumber = 5;
        fretNumber = 0;
        while(fretNumber < 20){
            int octave = GuitarNote.octave(stringNumber,fretNumber);
            if(fretNumber <= 2){
                assertEquals(2, octave);
            }
            else if(fretNumber <= 14){
                assertEquals(3, octave);
            }
            else{
                assertEquals(4, octave);
            }
            fretNumber++;
        }

        stringNumber = 4;
        fretNumber = 0;
        while(fretNumber < 20){
            int octave = GuitarNote.octave(stringNumber,fretNumber);
            if(fretNumber <= 9){
                assertEquals(3, octave);
            }
            else{
                assertEquals(4, octave);
            }
            fretNumber++;
        }

        stringNumber = 3;
        fretNumber = 0;
        while(fretNumber < 20){
            int octave = GuitarNote.octave(stringNumber,fretNumber);
            if(fretNumber <= 4){
                assertEquals(3, octave);
            }
            else if(fretNumber <= 16){
                assertEquals(4, octave);
            }
            else{
                assertEquals(5, octave);
            }
            fretNumber++;
        }

        stringNumber = 2;
        fretNumber = 0;
        while(fretNumber <= 0){
            int octave = GuitarNote.octave(stringNumber,fretNumber);
            if(fretNumber <= 4){
                assertEquals(3, octave);
            }
            else if(fretNumber <= 12){
                assertEquals(4, octave);
            }
            else{
                assertEquals(5, octave);
            }
            fretNumber++;
        }

        stringNumber = 1;
        fretNumber = 0;
        while(fretNumber <= 0){
            int octave = GuitarNote.octave(stringNumber,fretNumber);
            if(fretNumber <= 7){
                assertEquals(4, octave);
            }
            else{
                assertEquals(5, octave);
            }
            fretNumber++;
        }
    }

    @Test
    void makeKey(){
        String[] keys = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

        int stringNumber = 6;
        int fretNumber = 0;
        int keyIndex = 4;
        while(fretNumber < 20){
            String key = GuitarNote.key(stringNumber, fretNumber);
            assertTrue(keys[keyIndex].equals(key));
            fretNumber++;
            keyIndex++;
            if(keyIndex == 12){
                keyIndex = 0;
            }
        }

        stringNumber = 5;
        fretNumber = 0;
        keyIndex = 9;
        while(fretNumber < 20){
            String key = GuitarNote.key(stringNumber, fretNumber);
            assertTrue(keys[keyIndex].equals(key));
            fretNumber++;
            keyIndex++;
            if(keyIndex == 12){
                keyIndex = 0;
            }
        }

        stringNumber = 4;
        fretNumber = 0;
        keyIndex = 2;
        while(fretNumber < 20){
            String key = GuitarNote.key(stringNumber, fretNumber);
            assertTrue(keys[keyIndex].equals(key));
            fretNumber++;
            keyIndex++;
            if(keyIndex == 12){
                keyIndex = 0;
            }
        }

        stringNumber = 3;
        fretNumber = 0;
        keyIndex = 7;
        while(fretNumber < 20){
            String key = GuitarNote.key(stringNumber, fretNumber);
            assertTrue(keys[keyIndex].equals(key));
            fretNumber++;
            keyIndex++;
            if(keyIndex == 12){
                keyIndex = 0;
            }
        }

        stringNumber = 2;
        fretNumber = 0;
        keyIndex = 11;
        while(fretNumber < 20){
            String key = GuitarNote.key(stringNumber, fretNumber);
            assertTrue(keys[keyIndex].equals(key));
            fretNumber++;
            keyIndex++;
            if(keyIndex == 12){
                keyIndex = 0;
            }
        }

        stringNumber = 1;
        fretNumber = 0;
        keyIndex = 4;
        while(fretNumber < 20){
            String key = GuitarNote.key(stringNumber, fretNumber);
            assertTrue(keys[keyIndex].equals(key));
            fretNumber++;
            keyIndex++;
            if(keyIndex == 12){
                keyIndex = 0;
            }
        }
    }

    @Test
    void testPitch1(){
        String pitchStr1 = GuitarNote.pitchScript(3,"C");
        String pitchStr2 = GuitarNote.pitchScript(5,"F");
        String pitchStr3 = GuitarNote.pitchScript(2,"B");

        String expected1 = "<pitch>\n" +
                "<step>C</step>\n" +
                "<octave>3</octave>\n" +
                "</pitch>\n";

        String expected2 = "<pitch>\n" +
                "<step>F</step>\n" +
                "<octave>5</octave>\n" +
                "</pitch>\n";

        String expected3 = "<pitch>\n" +
                "<step>B</step>\n" +
                "<octave>2</octave>\n" +
                "</pitch>\n";

        assertTrue(pitchStr1.equals(expected1));
        assertTrue(pitchStr2.equals(expected2));
        assertTrue(pitchStr3.equals(expected3));
    }

    @Test
    void testPitch2(){
        String pitchStr1 = GuitarNote.pitchScript(2,"G#");
        String pitchStr2 = GuitarNote.pitchScript(3,"A#");
        String pitchStr3 = GuitarNote.pitchScript(5,"D#");

        String expected1 = "<pitch>\n" +
                "<step>G</step>\n" +
                "<alter>" + 1 + "</alter>\n"+
                "<octave>2</octave>\n" +
                "</pitch>\n";

        String expected2 = "<pitch>\n" +
                "<step>A</step>\n" +
                "<alter>" + 1 + "</alter>\n"+
                "<octave>3</octave>\n" +
                "</pitch>\n";

        String expected3 = "<pitch>\n" +
                "<step>D</step>\n" +
                "<alter>" + 1 + "</alter>\n"+
                "<octave>5</octave>\n" +
                "</pitch>\n";

        assertTrue(pitchStr1.equals(expected1));
        assertTrue(pitchStr2.equals(expected2));
        assertTrue(pitchStr3.equals(expected3));
    }
}