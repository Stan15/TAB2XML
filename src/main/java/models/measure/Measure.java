package models.measure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import models.measure.attributes.Attributes;
import models.measure.barline.BarLine;
import models.measure.direction.Direction;
import models.measure.note.Note;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"attributes", "noteBefore", "backup", "noteAfter", "barlines", "direction"})
public class Measure {
    @JacksonXmlProperty(isAttribute = true)
    int number;

    Attributes attributes;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "noteBefore")
    List<Note> notesBeforeBackup;

    Backup backup;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "noteAfter")
    List<Note> notesAfterBackup;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "barline")
    List<BarLine> barlines;

    Direction direction;

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Backup getBackup() {
        return backup;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public List<BarLine> getBarlines() {
        return barlines;
    }

    public Direction getDirection() {
        return direction;
    }

    public List<Note> getNotesBeforeBackup() {
        return notesBeforeBackup;
    }

    public void setBackup(Backup backup) {
        this.backup = backup;
    }

    public void setBarlines(List<BarLine> barlines) {
        this.barlines = barlines;
    }

    public void setNotesAfterBackup(List<Note> notesAfterBackup) {
        this.notesAfterBackup = notesAfterBackup;
    }

    public void setNotesBeforeBackup(List<Note> notesBeforeBackup) {
        this.notesBeforeBackup = notesBeforeBackup;
    }

    public List<Note> getNotesAfterBackup() {
        return notesAfterBackup;
    }
}
