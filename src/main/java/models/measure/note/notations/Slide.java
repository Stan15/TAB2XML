package models.measure.note.notations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import models.ScorePartwise;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Slide {
    @JacksonXmlProperty(isAttribute = true)
    String type;
    @JacksonXmlProperty(isAttribute = true)
    int number;

    @JsonIgnore
    private static int NEXT_NUMBER = 1;
    @JsonIgnore
    private static int PREV_SCORE_COUNT = 0;

    private Slide() {
        if (ScorePartwise.getScoreCount()!=PREV_SCORE_COUNT || NEXT_NUMBER>6) {
            PREV_SCORE_COUNT = ScorePartwise.getScoreCount();
            NEXT_NUMBER = 1;
        }
    }

    public Slide(String type) {
        this();
        this.type = type;
        this.number = NEXT_NUMBER++;
    }
    public Slide(String type, int number) {
        this.type = type;
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }
}
