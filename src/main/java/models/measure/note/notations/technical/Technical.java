package models.measure.note.notations.technical;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Technical {
    int string;
    int fret;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "hammer-on")
    List<HammerOn> hammerOns;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "pull-off")
    List<PullOff> pullOffs;
    Harmonic harmonic;
}
