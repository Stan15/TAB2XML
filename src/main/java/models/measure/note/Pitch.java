package models.measure.note;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
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
}
