package models.part_list;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ScorePart {
    @JacksonXmlProperty(isAttribute = true)
    public String id;

    @JacksonXmlProperty(localName = "part-name")
    public String partName;

    @JacksonXmlProperty(localName = "score-instrument")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<ScoreInstrument> scoreInstruments;

    public ScorePart(String id, String partName) {
        this.id = id;
        this.partName = partName;
    }
    public ScorePart(String id, String partName, List<ScoreInstrument> scoreInstruments) {
        this(id, partName);
        this.scoreInstruments = scoreInstruments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ScoreInstrument> getScoreInstruments() {
        return scoreInstruments;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public void setScoreInstruments(List<ScoreInstrument> scoreInstruments) {
        this.scoreInstruments = scoreInstruments;
    }
}
