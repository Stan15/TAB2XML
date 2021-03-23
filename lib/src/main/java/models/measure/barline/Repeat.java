package models.measure.barline;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Repeat {
    @JacksonXmlProperty(isAttribute = true)
    String direction;

    @JacksonXmlProperty(isAttribute = true)
    String winged;
}
