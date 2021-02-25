package converter.notes;

import converter.GuitarConverter.GuitarNote;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuitarNotesTest {

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

    void makeKey1(){

    }

    void makeKey2(){

    }

    void makePitch1(){

    }

    void makePitch2(){

    }
}