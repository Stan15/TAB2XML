package models.measure.attributes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class StaffTuning {
    @JacksonXmlProperty(isAttribute = true)
    public int line;


    @JacksonXmlProperty(localName = "tuning-step")
    public String tuningStep;
    @JacksonXmlProperty(localName = "tuning-octave")
    public int tuningOctave;

    public StaffTuning(int line, String tuningStep, int tuningOctave) {
        this.line = line;
        this.tuningStep = tuningStep;
        this.tuningOctave = tuningOctave;
    }
}
