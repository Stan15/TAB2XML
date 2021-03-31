package models.measure.note.notations.technical;

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
