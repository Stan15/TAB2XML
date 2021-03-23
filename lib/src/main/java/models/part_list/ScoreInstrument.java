package models.part_list;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class ScoreInstrument {
    @JacksonXmlProperty(isAttribute = true)
    public String id;

    @JacksonXmlProperty(localName = "instrument-name")
    public String instrumentName;

    public ScoreInstrument(String id, String instrumentName) {
        this.id = id;
        this.instrumentName = instrumentName;

    }
}
