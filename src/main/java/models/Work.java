package models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Work {
    @JacksonXmlProperty(localName = "work-title")
    String workTitle;

    public Work(String workTitle) {
        this.workTitle = workTitle;
    }

    public String getWorkTitle() {
        return workTitle;
    }

    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }
}
