package converter.note;

public class DrumNote extends Note{
    int line;
    public DrumNote (String line, String lineName, int distanceFromStart, int position){
        super(line, lineName, distanceFromStart, position);
    }
    public int convertNametoNumber(String lineName){
        lineName = lineName.strip();
        if (lineName.equalsIgnoreCase("C")) //crash
            return 1;
        else if (lineName.equalsIgnoreCase("R")) // ride
            return 2;
        else if (lineName.equals("T")) //High Tom
            return 3;
        else if (lineName.equals("t")) // medium Tom
            return 4;
        else if (lineName.equals("FT")) // floor Tom
            return 5;
        else if (lineName.equals("HH")) // High Hat
            return 6;
        else if (lineName.equals("SD")) // Snare Drum
            return 7;
        else if (lineName.equals("B")) // Base Drum
            return 8;
        return 0;
    }
    public String toXML() {
        return null;
    }

    public models.measure.note.Note getModel(){ //toXML

    }
}
