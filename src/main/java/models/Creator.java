package models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;

@Data
public class Creator {
    @JacksonXmlProperty(isAttribute = true)
    String type;

    @JacksonXmlText
    String name;

    public Creator(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
