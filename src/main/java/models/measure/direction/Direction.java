package models.measure.direction;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Direction {
    @JacksonXmlProperty(isAttribute = true)
    String placement;

    @JacksonXmlProperty(localName = "direction-type")
    DirectionType directionType;

    public DirectionType getDirectionType() {
        return directionType;
    }

    public String getPlacement() {
        return placement;
    }

    public void setDirectionType(DirectionType directionType) {
        this.directionType = directionType;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }
}