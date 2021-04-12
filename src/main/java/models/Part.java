package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import models.measure.Measure;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Part {
    @JacksonXmlProperty(isAttribute = true)
    public String id;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "measure")
    public List<Measure> measures;

    public Part(String id, List<Measure> measures) {
        this.id = id;
        this.measures = measures;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
    }
}
