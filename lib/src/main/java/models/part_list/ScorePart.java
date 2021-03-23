package models.part_list;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
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
}
