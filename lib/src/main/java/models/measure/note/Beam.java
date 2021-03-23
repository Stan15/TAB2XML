package models.measure.note;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;

@Data
public class Beam {
    @JacksonXmlProperty(isAttribute = true)
    int number;

    @JacksonXmlText
    String type;
}
