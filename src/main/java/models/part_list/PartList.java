package models.part_list;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class PartList {

    @JacksonXmlProperty(localName = "score-part")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<ScorePart> scoreParts;

    public PartList(List<ScorePart> scoreParts) {
        this.scoreParts = scoreParts;
    }

    public List<ScorePart> getScoreParts() {
        return scoreParts;
    }

    public void setScoreParts(List<ScorePart> scoreParts) {
        this.scoreParts = scoreParts;
    }
}
