package utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import converter.Instrument;
import converter.Score;
import custom_exceptions.TXMLException;

import java.nio.file.Files;
import java.nio.file.Path;

public class Parser {
    public static Score SCORE;

    public static void createScore(String rootString) {
        SCORE = new Score(rootString);
    }

    public Parser(Path filePath) {
        try {
            String rootString = Files.readString(filePath).replace("\r\n","\n");
            SCORE = new Score(rootString);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String parse(Score score) {
        if(score.ROOT_STRING.isBlank()){
            return "";
        }
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String xmlString = "";
        try {
            xmlString = mapper.writeValueAsString(score.getModel());
            xmlString = xmlString.replace("noteBefore", "note");
            xmlString = xmlString.replace("noteAfter", "note");
        }catch (JsonProcessingException | TXMLException e) {
            e.printStackTrace();
            return "";
        }

        xmlString = """
                <?xml version="1.0" encoding="UTF-8" standalone="no"?>
                <!DOCTYPE score-partwise PUBLIC "-//Recordare//DTD MusicXML 3.1 Partwise//EN" "http://www.musicxml.org/dtds/partwise.dtd">
                """
                + xmlString;
        return xmlString;
    }
    public static String parse() {
        return parse(SCORE);
    }

    public static Instrument getInstrumentEnum(String instrument) {
        if (instrument.equalsIgnoreCase("guitar"))
            return Instrument.GUITAR;
        else if (instrument.equalsIgnoreCase("drum"))
            return Instrument.DRUM;
        else if (instrument.equalsIgnoreCase("bass"))
            return Instrument.BASS;
        else
            return Instrument.AUTO;
    }

    public int getMeasureAt(int index) {
        return 1;
    }

    public static void setTitle(String title) {
        if (SCORE==null || title.isBlank()) return;
        SCORE.title = title;
    }
    public static void setArtist(String artist) {
        if (SCORE==null || artist.isBlank()) return;
        SCORE.artist = artist;
    }

    public boolean setMeasureTimeSignature(int measureNum) {
        return true;
    }
}
