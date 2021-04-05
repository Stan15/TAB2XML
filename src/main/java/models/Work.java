package models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Work {
    @JacksonXmlProperty(localName = "work-title")
    String workTitle;

    public Work(String workTitle) {
        this.workTitle = workTitle;
    }
}
