package models.measure.attributes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Attributes {
    public Integer divisions;
    public Key key;
    public Time time;
    public Clef clef;
    @JacksonXmlProperty(localName = "staff-details")
    public StaffDetails staffDetails;

    public Integer getDivisions() {
        return divisions;
    }

    public Clef getClef() {
        return clef;
    }

    public Key getKey() {
        return key;
    }

    public Time getTime() {
        return time;
    }

    public StaffDetails getStaffDetails() {
        return staffDetails;
    }

    public void setClef(Clef clef) {
        this.clef = clef;
    }

    public void setDivisions(Integer divisions) {
        this.divisions = divisions;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public void setStaffDetails(StaffDetails staffDetails) {
        this.staffDetails = staffDetails;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
