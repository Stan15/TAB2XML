package models.measure.attributes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StaffDetails {
    @JacksonXmlProperty(localName = "staff-lines")
    public int staffLines;
    @JacksonXmlProperty(localName = "staff-tuning")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<StaffTuning> staffTuning;

    public StaffDetails(int staffLines, List<StaffTuning> staffTuning) {
        this.staffLines = staffLines;
        this.staffTuning = staffTuning;
    }
}
