package models.measure.note.notations.technical;

import com.fasterxml.jackson.annotation.JsonInclude;

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

    public Artificial getArtificial() {
        return artificial;
    }

    public Natural getNatural() {
        return natural;
    }

    public void setArtificial(Artificial artificial) {
        this.artificial = artificial;
    }

    public void setNatural(Natural natural) {
        this.natural = natural;
    }
}
