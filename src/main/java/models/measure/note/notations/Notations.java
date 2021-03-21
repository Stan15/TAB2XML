package models.measure.note.notations;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Notations {
    Technical technical;
    Slur slur;
}
