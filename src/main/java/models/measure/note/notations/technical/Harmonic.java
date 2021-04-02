package models.measure.note.notations.technical;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Harmonic {
    Natural natural;
    Artificial artificial;

    public Harmonic(Natural natural) {
        this.natural = natural;
    }
    public Harmonic(Artificial artificial) {
        this.artificial = artificial;
    }
}
