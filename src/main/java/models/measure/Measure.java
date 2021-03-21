package models.measure;

import lombok.Data;
import models.measure.attributes.Attributes;
import models.measure.barline.BarLine;
import models.measure.direction.Direction;
import models.measure.note.Note;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Measure {
    @JacksonXmlProperty(isAttribute = true)
    int number;

    Attributes attributes;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "note")
    List<Note> notes;

    @JacksonXmlElementWrapper(useWrapping = false)
    List<Backup> backup;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "barline")
    List<BarLine> barlines;

    Direction direction;
}
