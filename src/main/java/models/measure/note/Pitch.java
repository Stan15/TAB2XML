package models.measure.note;

import lombok.Data;

@Data
public class Pitch {
    String step;
    int alter;
    int octave;

    public Pitch(String step, int alter, int octave) {
        this.step = step;
        this.alter = alter;
        this.octave = octave;
    }
}
