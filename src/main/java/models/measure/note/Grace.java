package models.measure.note;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Grace {
    @JacksonXmlProperty(isAttribute = true)
    String slash;
    public Grace(){}
    public Grace(String slash) {
        this.slash = slash;
    }

    public String getSlash() {
        return slash;
    }

    public void setSlash(String slash) {
        this.slash = slash;
    }
}
