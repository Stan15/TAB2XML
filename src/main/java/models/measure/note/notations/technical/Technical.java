package models.measure.note.notations.technical;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

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

    public int getFret() {
        return fret;
    }

    public int getString() {
        return string;
    }

    public void setFret(int fret) {
        this.fret = fret;
    }

    public void setString(int string) {
        this.string = string;
    }

    public List<HammerOn> getHammerOns() {
        return hammerOns;
    }

    public void setHammerOns(List<HammerOn> hammerOns) {
        this.hammerOns = hammerOns;
    }

    public Harmonic getHarmonic() {
        return harmonic;
    }

    public List<PullOff> getPullOffs() {
        return pullOffs;
    }

    public void setHarmonic(Harmonic harmonic) {
        this.harmonic = harmonic;
    }

    public void setPullOffs(List<PullOff> pullOffs) {
        this.pullOffs = pullOffs;
    }
}
