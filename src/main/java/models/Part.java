package models;

import models.measure.Measure;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
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
}
