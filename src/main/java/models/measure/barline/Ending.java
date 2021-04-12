package models.measure.barline;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Ending {
    @JacksonXmlProperty(isAttribute = true)
    String number;

    @JacksonXmlProperty(isAttribute = true)
    String type;

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }
}
