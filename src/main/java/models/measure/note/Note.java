package models.measure.note;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import models.measure.attributes.StaffTuning;
import models.measure.note.notations.Notations;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({"grace", "chord", "pitch", "rest", "unpitched", "duration", "instrument", "voice", "type", "dot", "stem", "notehead", "beam", "notations"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Note {
    Grace grace;
    Rest rest;
    Chord chord;
    Pitch pitch;
    Unpitched unpitched;
    int duration;
    Instrument instrument;
    int voice;
    String type;
    @JacksonXmlProperty(localName = "dot")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Dot> dots;
    String stem;
    String notehead;
    Beam beam;
    Notations notations;
}
