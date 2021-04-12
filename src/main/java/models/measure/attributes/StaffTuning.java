package models.measure.attributes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

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

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public int getTuningOctave() {
        return tuningOctave;
    }

    public String getTuningStep() {
        return tuningStep;
    }

    public void setTuningOctave(int tuningOctave) {
        this.tuningOctave = tuningOctave;
    }

    public void setTuningStep(String tuningStep) {
        this.tuningStep = tuningStep;
    }
}
