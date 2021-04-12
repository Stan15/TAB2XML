package models.measure.barline;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Repeat {
    @JacksonXmlProperty(isAttribute = true)
    String direction;

    @JacksonXmlProperty(isAttribute = true)
    String winged;

    public String getDirection() {
        return direction;
    }

    public String getWinged() {
        return winged;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setWinged(String winged) {
        this.winged = winged;
    }
}
