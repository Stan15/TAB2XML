package models.measure.note;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pitch {
    String step;
    Integer alter;
    int octave;

    public Pitch(String step, int alter, int octave) {
        this.step = step;
        this.alter = alter==0 ? null : alter;
        this.octave = octave;
    }

    public Integer getAlter() {
        return alter;
    }

    public String getStep() {
        return step;
    }

    public void setAlter(Integer alter) {
        this.alter = alter;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public int getOctave() {
        return octave;
    }
}
