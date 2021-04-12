package models.part_list;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ScoreInstrument {
    @JacksonXmlProperty(isAttribute = true)
    public String id;

    @JacksonXmlProperty(localName = "instrument-name")
    public String instrumentName;

    public ScoreInstrument(String id, String instrumentName) {
        this.id = id;
        this.instrumentName = instrumentName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }
}
