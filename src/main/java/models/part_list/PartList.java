package models.part_list;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
public class PartList {

    @JacksonXmlProperty(localName = "score-part")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<ScorePart> scoreParts;

    public PartList(List<ScorePart> scoreParts) {
        this.scoreParts = scoreParts;
    }
}
