package models.measure.note.notations;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class PullOff {
    @JacksonXmlProperty(isAttribute = true)
    int number;

    @JacksonXmlProperty(isAttribute = true)
    String type;

    String symbol;
}
