package models.measure.barline;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BarLine {
    @JacksonXmlProperty(isAttribute = true)
    public String location;

    @JacksonXmlProperty(localName = "bar-style")
    public String barStyle;

    public Ending ending;

    public Repeat repeat;
}
