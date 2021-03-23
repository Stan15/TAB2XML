package models.measure.direction;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;

@Data
public class Words {
    @JacksonXmlProperty(isAttribute = true, localName = "relative-x")
    double relativeX;

    @JacksonXmlProperty(isAttribute = true, localName = "relative-y")
    double relativeY;

    @JacksonXmlText
    String repeatText;
}
