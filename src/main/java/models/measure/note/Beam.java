package models.measure.note;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import models.ScorePartwise;

public class Beam {
    @JacksonXmlProperty(isAttribute = true)
    int number;

    @JacksonXmlText
    String type;
    @JsonIgnore
    private static int NEXT_NUMBER = 1;
    @JsonIgnore
    private static int PREV_SCORE_COUNT = 0;

    private Beam() {
        if (ScorePartwise.getScoreCount()!=PREV_SCORE_COUNT) {
            PREV_SCORE_COUNT = ScorePartwise.getScoreCount();
            NEXT_NUMBER = 1;
        }
    }

    public Beam(String type) {
        this();
        this.type = type;
        this.number = NEXT_NUMBER++;
    }
    public Beam(String type, int number) {
        this.type = type;
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
