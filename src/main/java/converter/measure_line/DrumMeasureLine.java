package converter.measure_line;

import GUI.TabInput;
import converter.Instrument;
import converter.Score;
import converter.note.Note;
import converter.note.NoteFactory;
import utility.DrumUtils;

import java.util.*;

public class DrumMeasureLine extends MeasureLine {
    public static Set<String> USED_DRUM_PARTS = new HashSet<>();
    public static String COMPONENT = "[xXoOdDfF]";
    public static String INSIDES_PATTERN_SPECIAL_CASE = "$a"; //doesnt match anything
    private String partID;

    protected DrumMeasureLine(String line, String[] nameAndPosition, int position) {
        super(line, nameAndPosition, position);
        this.instrument = Instrument.DRUM;
        this.partID = DrumUtils.getPartID(this.name);
        if (this.partID!=null)
            USED_DRUM_PARTS.add(this.partID);
        this.noteList = this.createNoteList(this.line, position);
    }

    /**
     * TODO validate that the symbols in the measure line correspond to the measure line name.
     *      look at this wikipedia page. If the measure line is a Cymbal measure line,
     *      only certain types of symbols, or "notes" can be in that measure line
     *      https://en.wikipedia.org/wiki/Drum_tablature#:~:text=Drum%20tablature,%20commonly%20known%20as%20a%20drum%20tab,,to%20stroke.%20Drum%20tabs%20frequently%20depict%20drum%20patterns.
     * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
     * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
     * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
     * found in the root string from which it was derived (i.e Score.ROOT_STRING).
     * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public List<HashMap<String, String>> validate() {
        List<HashMap<String, String>> result = new ArrayList<>(super.validate());

        if (!isDrumName(this.name)) {
            HashMap<String, String> response = new HashMap<>();
            if (isGuitarName(this.name))
                response.put("message", "A Drum measure lineBand:Black Sabbath\n" +
                        "Song:War Pigs\n" +
                        "Tabber:Jared Myers\n" +
                        "\n" +
                        "This is my fav. Black Sabbath song and i think this is Bill Ward's best performance.  The Fills in this song\n" +
                        "are complicated.  The timing is 6/8.  When u can play this song u can play drums well.  This song took me forever to learn.\n" +
                        "Enjoy.Please Rate.\n" +
                        "\n" +
                        " Intro\n" +
                        "C |x--------x--------|---------x--x-----|x--------x--------|---------x--x-----|\n" +
                        "R |------x-----x--x--|x--x-xx-----------|---x-xx-----x-xx--|x--x-xx-----------|\n" +
                        "SD|---------o--------|---------o--o-----|---------o--------|---------o--o-----|\n" +
                        "FT|------------------|--------------o-o-|------------------|--------------oo--|\n" +
                        "B |o----------------o|o----------o------|o-----------------|o----------oo-----|\n" +
                        "  (1tl2tl3tl4tl5tl6tl|1tl2tl3tl4tl5tl6tl|1tl2tl3tl4tl5tl6tl|1tl2tl3tl4tl5tl6tl)\n" +
                        "\n" +
                        "C |x--------x--------|---------x--x-----|x--------x--------|---------x--x-----|\n" +
                        "R |---x--x-----x-xx--|x--x-xx-----------|---x--x-----x-xx--|x--x-xx-----------|\n" +
                        "SD|---------o--------|---------o--------|---------o--------|---------o--o-----|\n" +
                        "FT|------------------|------------oooo--|------------------|--------------o-o-|\n" +
                        "B |o-----------------|o----------o------|o-----------------|o----------o------|\n" +
                        "  (1tl2tl3tl4tl5tl6tl|1tl2tl3tl4tl5tl6tl|1tl2tl3tl4tl5tl6tl|1tl2tl3tl4tl5tl6tl)\n" +
                        "\n" +
                        "C |x--------x--------|---------x--------|x--------x--------|---------x--------|\n" +
                        "R |---x--x-----x--x--|x--x-xx-----x-xx--|---x--x-----x-xx--|x--x--x-----x-----|\n" +
                        "SD|---------o--------|---------o--------|---------o--------|---------o----do--|\n" +
                        "FT|------------------|------------oooo-o|------------------|------------------|\n" +
                        "B |o-----------------|o----------o------|o-----------------|o----------o------|\n" +
                        "  (1tl2tl3tl4tl5tl6tl|1tl2tl3tl4tl5tl6tl|1tl2tl3tl4tl5tl6tl|1tl2tl3tl4tl5tl6tl)\n" +
                        "\n" +
                        "C |---------x--x-----|x--------x--------|x--------x--------|------------------|\n" +
                        "R |x--x-xx-----------|---x-xx-----------|---x-xx-----x-xx--|x--x-xx--x--x-xx--|\n" +
                        "T |-----------oo-----|------------oo----|------------------|------------------|\n" +
                        "SD|---------o--------|---------o-o------|---------o--------|---------o--------|\n" +
                        "FT|--------------oo--|---------------oo-|------------------|------------------|\n" +
                        "B |o-----------------|o-----------------|o-----------------|o-----------------|\n" +
                        "  (1tl2tl3tl4tl5tl6tl|1tl2tl3tl4tl5tl6tl|1tl2tl3tl4tl5tl6tl|1tl2tl3tl4tl5tl6tl)\n" +
                        "\n" +
                        "  4/4 Time\n" +
                        "\n" +
                        "  1st Verse\n" +
                        "  |------------REPEAT-7X------------|\n" +
                        "C |xx--------------|----------------|xx--------------|----------------|\n" +
                        "HH|----x-x-x-x-x-x-|x-x-x-x-xox-x-x-|----x-x-x-x-x-x-|----------x-x-x-|\n" +
                        "T |----------------|----------------|----------------|--o-------------|\n" +
                        "SD|----------------|----------------|----------------|o----o--f-------|\n" +
                        "B |oo--------------|----------------|oo--------------|-------o-o-oo-o-|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "C |xx--------------|----------------|\n" +
                        "HH|----x-x-x-x-x-x-|x-x-x-x-xox-x-x-|\n" +
                        "T |----------------|----------------|\n" +
                        "SD|----------------|----------------|\n" +
                        "B |oo--------------|----------------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "  Interlude\n" +
                        "C |xx--------------|x---x---------------|x---------------|\n" +
                        "T |----------------|----------------o---|--------ff------|\n" +
                        "SD|----oo-o-ooo----|----o---oooooooo----|----ff----------|\n" +
                        "FT|------------ooo-|-----------------o--|------------fff-|\n" +
                        "B |oo--------------|o--o---o------------|o--o---o---o----|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3tl+tl4tl+tl|1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "C |x---x-----------|x---------------------|x---x-x-------------|\n" +
                        "T |----------------|----------------dddo--|--------x-----oooo--|\n" +
                        "SD|----o---oooo----|----dddo--dddo--------|----o---oooooo------|\n" +
                        "FT|------------ooo-|----------------------|--------------------|\n" +
                        "B |o--o------------|o--o-----o-----o-----o|o--o----------------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2tl+tl3tl+tl4tl+tl|1 + 2 + 3tl+tl4tl+tl)\n" +
                        "\n" +
                        "C |x---------------------|x---x-----------|\n" +
                        "t |----------ooooo-------|----------------|\n" +
                        "T |----------------do----|----------oo----|\n" +
                        "SD|----oooo-----------do-|----o---o-------|\n" +
                        "FT|----------------------|-------------oo-|\n" +
                        "B |o--o-----o------------|o--o---o--------|\n" +
                        "  (1 + 2tl+tl3tl+tl4tl+tl|1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "  2nd Verse\n" +
                        "  |------------REPEAT-1X------------|\n" +
                        "  |----REPEAT-4X---|\n" +
                        "C |x---------------|x---------------|\n" +
                        "HH|--x-x-x-x-x-x-x-|--x-x-x-x-x-x---|\n" +
                        "SD|----o----o--o---|----o----o--ooo-|\n" +
                        "B |o--o----o--o--o-|o--o----o--o----|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "  Interlude\n" +
                        "C |x---x-----------|x---x-----------|x---x-----------|x---x-----------|\n" +
                        "HH|--x---x-x-x-x---|--x---x-x-x---x-|--x---x-x-x-x-x-|--x---x-x-x-x---|\n" +
                        "t |----------------|----------------|----------------|--------------o-|\n" +
                        "SD|----o----o--ooo-|----o----o--f---|----o----o--o---|----o----o--oo--|\n" +
                        "B |o--o----o--o----|o--o----o-------|o--o----o--o--o-|o--o----o---o---|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "  3rd Verse\n" +
                        "  |----REPEAT-7X---|\n" +
                        "C |x---------------|\n" +
                        "HH|--x-x-x-x-x-x-x-|\n" +
                        "SD|----o----o--o---|\n" +
                        "B |o--o----o--o--o-|\n" +
                        "  (1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "  Interlude\n" +
                        "C |xx--------------|x---x--------------|x---------------|x---x-----------|\n" +
                        "T |------------ooo-|--------------ooo--|--------fff-----|----------------|\n" +
                        "SD|----oo-o-ooo----|----o---oooooo-----|----fff---------|----o---oooo----|\n" +
                        "FT|----------------|-----------------o-|------------fff-|------------ooo-|\n" +
                        "B |oo--------------|o--o---o-----------|o--o---o---o----|o--o------------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3tl+tl4tl+ |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "C |x-----------------|x---x--------------|x---------------|x---x-----------|\n" +
                        "T |------------------|--------------ooo--|--------ff------|----------------|\n" +
                        "SD|----ddddo--ooo----|----o---oooooo-----|----ff----------|----o---oooo----|\n" +
                        "FT|--------------ooo-|-----------------o-|------------ff--|-------------oo-|\n" +
                        "B |o--o-----o--------|o--o---o-----------|o--o---o---o----|o--o----o-------|\n" +
                        "  (1 + 2tl+tl3 + 4 + |1 + 2 + 3tl+tl4tl+ |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "  Guitar Solo\n" +
                        "  |---REPEAT-14X---|----REPEAT-2X---|----REPEAT-1X---|\n" +
                        "C |x---------------|x---x-----------|x---x-x---------|x---x---x-x-x---|\n" +
                        "R |--x-x-x-xxxxxxx-|--x---x-xxxxx---|----------------|----------------|\n" +
                        "T |----------------|-------------oo-|----------------|-----------o-oo-|\n" +
                        "SD|----o---o--o--o-|----o---o--o----|----o-----------|----o---o-------|\n" +
                        "FT|----------------|----------------|--------ffffffff|----------------|\n" +
                        "B |o-o----o-o---oo-|o-o------o------|o-oo---o--------|o-oo------------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "C |x---------------|\n" +
                        "R |--x-x-x-xxxxxxx-|\n" +
                        "SD|----o---o--o--o-|\n" +
                        "B |o-o----o-o---oo-|\n" +
                        "  (1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "  4th Verse\n" +
                        "  |------------REPEAT-7X------------|\n" +
                        "C |xx--------------|----------------|xx--------------|----------------|\n" +
                        "HH|----x-x-x-x-x-x-|x-x-x-x-xox-x-x-|----x-x-x-x-x-x-|----------------|\n" +
                        "T |----------------|----------------|----------------|--o---------f---|\n" +
                        "SD|----------------|----------------|----------------|o----o--f-------|\n" +
                        "B |oo--------------|----------------|oo--------------|-------o-o-o-oo-|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "C |xx--------------|----------------|\n" +
                        "HH|----x-x-x-x-x-x-|x-x-x-x-xox-x-x-|\n" +
                        "T |----------------|----------------|\n" +
                        "SD|----------------|----------------|\n" +
                        "B |oo--------------|----------------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "  Interlude\n" +
                        "C |xx----------------|x---x-----------|x---------------------|\n" +
                        "T |------------------|----------------|----------dddddd------|\n" +
                        "SD|----oo-o-oddddo-f-|----o---oooo----|----dddddd------------|\n" +
                        "FT|------------------|------------ooo-|----------------dddddd|\n" +
                        "B |oo-------------o--|o--o------------|o--o------------------|\n" +
                        "  (1 + 2 + 3 +tl4tl+ |1 + 2 + 3 + 4 + |1 + 2tl+tl3tl+tl4tl+tl)\n" +
                        "\n" +
                        "C |x---x-----------|x---------------|x---x-----------|x---------------|\n" +
                        "T |----------------|------f-f-------|----------------|--------ff------|\n" +
                        "SD|----o---oooo----|----f-----------|----o---oooo----|----ff----------|\n" +
                        "FT|------------ooo-|----------f-f-f-|------------ooo-|------------fff-|\n" +
                        "B |o--o------------|o--o-o-o-o-o-o--|o--o------------|o--o---o---o----|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "                                    |------------REPEAT-3X------------|\n" +
                        "C |x---x-----------|#---------------|----------------|x--x--x---------|\n" +
                        "SD|----o---oooo----|----------------|----------------|o--o--o--oooooo-|\n" +
                        "FT|-------------oo-|----------------|----------------|---------oooooo-|\n" +
                        "B |o--o------------|o-x-x-x-x-x-x-x-|x-x-x-x-x-x-x-x-|o--o--o---------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "                   |----REPEAT-6X---|\n" +
                        "C |x---x---x--x----|x---------------|\n" +
                        "R |--x---x--xx-xxx-|--x-x-x-xxxxxxx-|\n" +
                        "SD|----o---o--o--o-|----o---o--o--o-|\n" +
                        "B |o--o---o-o---oo-|o--o---o-o---oo-|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "C |x----------------------|x----------------------|\n" +
                        "R |-----------------------|-----------------------|\n" +
                        "T |-----------------------|--------------dddd-----|\n" +
                        "SD|--dddd--dddd--dddd--f--|--ddoodddddddd---------|\n" +
                        "FT|-----------------------|------------------ddddd|\n" +
                        "B |o-----o-----o-----o----|o----------------------|\n" +
                        "  (1 +tl2tl+tl3tl+tl4tl+tl|1 +tl2tl+tl3tl+tl4tl+tl)\n" +
                        "\n" +
                        "  2nd Guitar Solo\n" +
                        "  |----REPEAT-2X---|\n" +
                        "C |x---x-----------|x---x---------------|x-x-x-x-x-x-x---|x---------------|\n" +
                        "R |--x---x-xxxxxxx-|--x---x-------------|----------------|--X-X-X-X-X-X---|\n" +
                        "T |----------------|--------dddddd------|-------------oo-|---------oooo-oo|\n" +
                        "SD|----o---o--o--o-|----o---------------|----o---o---o---|----o-----------|\n" +
                        "FT|----------------|--------------dddddd|----------------|----------------|\n" +
                        "B |o--o---o-o---oo-|o--o----------------|o--o---o---o----|o--o------------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3tl+tl4tl+tl|1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "C |x---------------|x---x-----------|x---x-----------|x---------------|\n" +
                        "R |--X-X-X-X-XXXXX-|--X---X-X-XX----|--X---X-X-X-X-X-|--X-X-X-X-X-X-X-|\n" +
                        "T |----------------|----------------|----------------|-----------o----|\n" +
                        "SD|----o---o--o-oo-|----o---o-------|----o---o---o-o-|----o---o-------|\n" +
                        "FT|----------------|----------ooooo-|----------------|-------------oo-|\n" +
                        "B |o-o----o-o------|o-o----o--------|o--o---o---o-o--|o-o----o--------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "C |x---------------|----------------|x---x-----------|x---------------|\n" +
                        "R |--X-X-X-X-X-X-X-|X-X-X-XXXXXXXXX-|--X---X-X-X-X-X-|--X-X-X-XXXXX---|\n" +
                        "T |----------------|----------------|-------------oo-|-------------oo-|\n" +
                        "SD|----o---o--o--o-|-o--o------oooo-|----o---o--o----|----o---o--o----|\n" +
                        "B |o-o----o-----o--|o--o---o-o-o-o--|o--o---o--------|o-o----o--------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "C |x---x-----------|x---------------|x---------------|x---x-----------|\n" +
                        "R |--X---X-X-X-X-X-|--X-X-X-XXXXXXX-|--X-X-X-X-X-X-X-|--X---X---------|\n" +
                        "T |----------------|-----------o----|----------------|--------ooooooo-|\n" +
                        "SD|----o---o--o--o-|----o---o-------|----o---o--o--o-|----o-----------|\n" +
                        "FT|----------------|-------------oo-|----------------|--------ooooooo-|\n" +
                        "B |o-o----o-----o--|o-o----o--------|o--o---o-----o--|o-o----o--------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "C |x---x-----------|x---x---------------|x---x-----------|x---x-----------|\n" +
                        "R |--X---X-X-X-X-X-|--X---X-------------|--X---X-X-X-X-X-|--X---X-X-X-X-X-|\n" +
                        "T |----------------|--------dddddd------|-------------oo-|----------------|\n" +
                        "SD|----o---o--o--o-|----o---------------|----o---o--o----|----o---o--o-oo-|\n" +
                        "FT|----------------|--------------ddddo-|----------------|----------------|\n" +
                        "B |o--o---o-----o--|o--o---o------------|o--o---o--------|o-o----o--------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3tl+tl4tl+tl|1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "C |x---x-----------|x---------------|\n" +
                        "R |--X---X-X-X-X-X-|--X-X-X-XXXXXXX-|\n" +
                        "SD|----o---o--o----|----o-----------|\n" +
                        "FT|-------------oo-|----------------|\n" +
                        "B |o-o----o--------|o--o---oo--o-oo-|\n" +
                        "Hf|----------------|----------------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "  Outro\n" +
                        "C |x-------------x-|x-------------x-|x-----------x-x-|\n" +
                        "SD|--------------o-|o-------------o-|o-----------o-o-|\n" +
                        "B |o-------------o-|o-------------o-|o---------------|\n" +
                        "Hf|--x-x-x-x-x-x-x-|x-x-x-x-x-x-x-x-|x-x-x-x-x-x-x-x-|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + )\n" +
                        "\n" +
                        "  Start slow and speed up\n" +
                        "C |----------------|----------------|X---------------|\n" +
                        "SD|oooooooooooooooo|oooooooooooooof-|----------------|\n" +
                        "B |----------------|----------------|o---------------|\n" +
                        "  (1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + |1 + 2 + 3 + 4 + ) is expected here.");
            else
                response.put("message", "Invalid measure line name.");
            response.put("positions", "["+this.namePosition+","+(this.namePosition+this.line.length())+"]");
            int priority = 1;
            response.put("priority", ""+priority);
            if (TabInput.ERROR_SENSITIVITY>=priority)
                result.add(response);
        }else if (this.partID==null || !DrumUtils.isSupportedName(this.name)) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "This drum part is unsupported.");
            response.put("positions", "["+this.namePosition+","+(this.namePosition+this.line.length())+"]");
            int priority = 1;
            response.put("priority", ""+priority);
            if (TabInput.ERROR_SENSITIVITY>=priority)
                result.add(response);
        }

        for (HashMap<String, String> error : result) {
            if (Integer.parseInt(error.get("priority")) <= Score.CRITICAL_ERROR_CUTOFF) {
                return result;
            }
        }

        for (Note note : this.noteList)
            result.addAll(note.validate());

        return result;
    }
}
