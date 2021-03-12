package models.measure.note.notations;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
public class Technical {
    int string;
    int fret;
    @JacksonXmlProperty(localName = "hammer-on")
    List<HammerOn> hammerOns;
    @JacksonXmlProperty(localName = "pull-off")
    List<PullOff> pullOffs;
}
