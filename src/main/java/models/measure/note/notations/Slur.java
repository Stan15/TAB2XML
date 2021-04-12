package models.measure.note.notations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import models.ScorePartwise;

public class Slur {
    @JacksonXmlProperty(isAttribute = true)
    int number;

    @JacksonXmlProperty(isAttribute = true)
    String placement;

    @JacksonXmlProperty(isAttribute = true)
    String type;

    @JsonIgnore
    private static int NEXT_NUMBER = 1;
    @JsonIgnore
    private static int PREV_SCORE_COUNT = 0;

    private Slur() {
        if (ScorePartwise.getScoreCount()!=PREV_SCORE_COUNT || NEXT_NUMBER>6) {
            PREV_SCORE_COUNT = ScorePartwise.getScoreCount();
            NEXT_NUMBER = 1;
        }
    }

    public Slur(String type) {
        this();
        this.type = type;
        this.number = NEXT_NUMBER++;
    }
    public Slur(String type, int number) {
        this.type = type;
        this.number = number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public String getPlacement() {
        return placement;
    }
}
