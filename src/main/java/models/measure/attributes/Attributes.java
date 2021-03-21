package models.measure.attributes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Attributes {
    public Integer divisions;
    public Key key;
    public Time time;
    public Clef clef;
    @JacksonXmlProperty(localName = "staff-details")
    public StaffDetails staffDetails;
}
