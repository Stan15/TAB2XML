package models.measure.note;

import models.measure.note.notations.Notations;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Note {
    Rest rest;
    Chord chord;
    Pitch pitch;
    Unpitched unpitched;
    int duration;
    Instrument instrument;
    int voice;
    String type;
    String stem;
    String notehead;
    Beam beam;
    Notations notations;
}
