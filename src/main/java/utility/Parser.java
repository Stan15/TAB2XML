package utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
    public static String parse() {
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String xmlString = "";
        try {
            xmlString = mapper.writeValueAsString(SCORE.getModel());
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
}
