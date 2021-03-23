package models.measure.attributes;

import lombok.Data;

@Data
public class Key {
    public int fifths;

    public Key(int fifths) {
        this.fifths = fifths;
    }
}
