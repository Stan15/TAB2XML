package models.measure.attributes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Time {
    int beats;

    @JacksonXmlProperty(localName = "beat-type")
    int beatType;

    public Time(int beats, int beatType) {
        this.beats = beats;
        this.beatType = beatType;
    }

    public int getBeats() {
        return beats;
    }

    public int getBeatType() {
        return beatType;
    }

    public void setBeats(int beats) {
        this.beats = beats;
    }

    public void setBeatType(int beatType) {
        this.beatType = beatType;
    }
}
