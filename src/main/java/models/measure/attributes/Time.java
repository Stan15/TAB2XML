package models.measure.attributes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Time {
    int beats;

    @JacksonXmlProperty(localName = "beat-type")
    int beatType;

    public Time(int beats, int beatType) {
        this.beats = beats;
        this.beatType = beatType;
    }
}
