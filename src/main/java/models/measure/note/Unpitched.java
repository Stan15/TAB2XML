package models.measure.note;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Unpitched {
    @JacksonXmlProperty(localName = "display-step")
    String displayStep;
    @JacksonXmlProperty(localName = "display-octave")
    int displayOctave;

    public Unpitched(String displayStep, int displayOctave) {
        this.displayOctave = displayOctave;
        this.displayStep = displayStep;
    }

    public int getDisplayOctave() {
        return displayOctave;
    }

    public void setDisplayOctave(int displayOctave) {
        this.displayOctave = displayOctave;
    }

    public String getDisplayStep() {
        return displayStep;
    }

    public void setDisplayStep(String displayStep) {
        this.displayStep = displayStep;
    }
}

