package models.measure.note;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Instrument {
    @JacksonXmlProperty(isAttribute = true)
    String id;

    public Instrument (String drumID){
        this.id = drumID;
    }
}
