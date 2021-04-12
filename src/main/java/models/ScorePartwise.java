package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import models.part_list.PartList;

import java.util.List;

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

    public Identification getIdentification() {
        return identification;
    }

    public static void setScoreCount(int scoreCount) {
        SCORE_COUNT = scoreCount;
    }

    public List<Part> getParts() {
        return parts;
    }

    public PartList getPartList() {
        return partList;
    }

    public String getMovementTitle() {
        return movementTitle;
    }

    public String getVersion() {
        return version;
    }

    public void setIdentification(Identification identification) {
        this.identification = identification;
    }

    public void setMovementTitle(String movementTitle) {
        this.movementTitle = movementTitle;
    }

    public void setPartList(PartList partList) {
        this.partList = partList;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public Work getWork() {
        return work;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }
}
