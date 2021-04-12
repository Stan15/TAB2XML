package models.measure.barline;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"bar-style", "repeat"})
public class BarLine {
    @JacksonXmlProperty(isAttribute = true)
    public String location;

    @JacksonXmlProperty(localName = "bar-style")
    public String barStyle;

    public Ending ending;

    public Repeat repeat;

    public Ending getEnding() {
        return ending;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public String getBarStyle() {
        return barStyle;
    }

    public String getLocation() {
        return location;
    }

    public void setBarStyle(String barStyle) {
        this.barStyle = barStyle;
    }

    public void setEnding(Ending ending) {
        this.ending = ending;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }
}
