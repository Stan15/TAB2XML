package models.measure.note;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import models.measure.note.notations.Notations;

import java.util.List;

@JsonPropertyOrder({"grace", "chord", "pitch", "rest", "unpitched", "duration", "instrument", "voice", "type", "dot", "stem", "notehead", "beam", "notations"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Note {
    Grace grace;
    Rest rest;
    Chord chord;
    Pitch pitch;
    Unpitched unpitched;
    Integer duration;
    Instrument instrument;
    Integer voice;
    String type;
    @JacksonXmlProperty(localName = "dot")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Dot> dots;
    String stem;
    String notehead;
    Beam beam;
    Notations notations;

    public Grace getGrace() {
        return grace;
    }

    public void setGrace(Grace grace) {
        this.grace = grace;
    }

    public Chord getChord() {
        return chord;
    }

    public Pitch getPitch() {
        return pitch;
    }

    public Rest getRest() {
        return rest;
    }

    public Unpitched getUnpitched() {
        return unpitched;
    }

    public void setChord(Chord chord) {
        this.chord = chord;
    }

    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setUnpitched(Unpitched unpitched) {
        this.unpitched = unpitched;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public void setRest(Rest rest) {
        this.rest = rest;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVoice(Integer voice) {
        this.voice = voice;
    }

    public String getType() {
        return type;
    }

    public List<Dot> getDots() {
        return dots;
    }

    public Beam getBeam() {
        return beam;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getNotehead() {
        return notehead;
    }

    public Notations getNotations() {
        return notations;
    }

    public void setNotehead(String notehead) {
        this.notehead = notehead;
    }

    public void setBeam(Beam beam) {
        this.beam = beam;
    }

    public void setNotations(Notations notations) {
        this.notations = notations;
    }

    public Integer getVoice() {
        return voice;
    }

    public void setDots(List<Dot> dots) {
        this.dots = dots;
    }
}
