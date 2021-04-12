package converter.note;

import converter.Instrument;

public class BassNote extends GuitarNote {

    public BassNote(String origin, int position, String lineName, int distanceFromMeasureStart) {
        super(origin, position, lineName, distanceFromMeasureStart);
        this.instrument = Instrument.BASS;
        this.fret = Integer.parseInt(origin);
        String noteDetails = this.noteDetails(this.lineName, this.fret);
        this.step = this.step(noteDetails);
        this.alter = this.alter(noteDetails);
        this.octave = this.octave(noteDetails);
        this.sign = this.fret+"";
    }

    @Override
    protected int getDefaultOctave(String name, int offset) {
        if (name.equals("e"))
            return 3+offset;
        else if (name.equalsIgnoreCase("B"))
            return 2+offset;
        else if (name.equalsIgnoreCase("G"))
            return 2+offset;
        else if (name.equalsIgnoreCase("D"))
            return 2+offset;
        else if (name.equalsIgnoreCase("A"))
            return 1+offset;
        else if (name.equalsIgnoreCase("E"))
            return 0+offset;
        return -1;
    }
}
