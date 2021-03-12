package models;

import models.part_list.PartList;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import models.part_list.ScorePart;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(localName = "score-partwise")
public class ScorePartwise {
    @JacksonXmlProperty(isAttribute = true)
    public String version;

    @JacksonXmlProperty(localName = "part-list")
    public PartList partList;

    @JacksonXmlProperty(localName = "part")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Part> parts;

    public ScorePartwise(String version, PartList partList, List<Part> parts) {
        this.version = version;
        this.partList = partList;
        this.parts = parts;
    }
}
