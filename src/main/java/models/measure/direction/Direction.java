package models.measure.direction;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Direction {
    @JacksonXmlProperty(isAttribute = true)
    String placement;

    @JacksonXmlProperty(localName = "direction-type")
    DirectionType directionType;
}