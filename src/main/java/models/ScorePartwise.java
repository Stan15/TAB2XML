package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonPropertyOrder({"movement-title", "identification"})
public class ScorePartwise {
    @JsonIgnore
    private static int SCORE_COUNT = 1;

    @JacksonXmlProperty(isAttribute = true)
    String version;

    Work work;
    @JacksonXmlProperty(localName = "movement-title")
    String movementTitle;
    Identification identification;


    @JacksonXmlProperty(localName = "part-list")
    PartList partList;

    @JacksonXmlProperty(localName = "part")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<Part> parts;

    ScorePartwise() {
        SCORE_COUNT++;
    }

    public ScorePartwise(String version, PartList partList, List<Part> parts) {
        this();
        this.version = version;
        this.partList = partList;
        this.parts = parts;
    }

    public static int getScoreCount() {
        return SCORE_COUNT;
    }
}
