package models.measure.barline;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Ending {
    @JacksonXmlProperty(isAttribute = true)
    String number;

    @JacksonXmlProperty(isAttribute = true)
    String type;
}
