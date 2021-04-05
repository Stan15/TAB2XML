package models.measure.note.notations.technical;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;
import models.ScorePartwise;

@Data
public class HammerOn {
    @JacksonXmlProperty(isAttribute = true)
    int number;

    @JacksonXmlProperty(isAttribute = true)
    String type;

    @JacksonXmlText
    String symbol = "H";

    @JsonIgnore
    private static int NEXT_NUMBER = 1;
    @JsonIgnore
    private static int PREV_SCORE_COUNT = 0;

    private HammerOn() {
        if (ScorePartwise.getScoreCount()!=PREV_SCORE_COUNT || NEXT_NUMBER>6) {
            PREV_SCORE_COUNT = ScorePartwise.getScoreCount();
            NEXT_NUMBER = 1;
        }
    }

    public HammerOn(String type) {
        this();
        this.type = type;
        this.number = NEXT_NUMBER++;
    }
    public HammerOn(String type, int number) {
        this.type = type;
        this.number = number;
    }
}
