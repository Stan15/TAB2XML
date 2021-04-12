package GUI;

import converter.Score;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
//import nu.xom.ParsingException;
import org.fxmisc.richtext.CodeArea;
//import org.jfugue.integration.MusicXmlParser;
//import org.jfugue.pattern.Pattern;
//import org.jfugue.player.ManagedPlayer;
//import org.jfugue.player.Player;
//import org.staccato.StaccatoParserListener;
//import utility.Parser;
//
//import javax.sound.midi.InvalidMidiDataException;
//import javax.sound.midi.MidiUnavailableException;
//import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotePlayer {
    //private static ManagedPlayer PLAYER = new ManagedPlayer();
    private static Stage STAGE;
    private static String DISPLAY_TEXT;
    @FXML
    private TextArea tabPlayerDisplay = new TextArea();
    @FXML
    private Slider seekSlider = new Slider();
    @FXML
    private Button playButton = new Button();
    @FXML
    private Button pauseButton = new Button();
    private static String TAB_TEXT;
    private static Score SCORE;

    public NotePlayer(){
        initialize();
    }

    public NotePlayer(CodeArea textArea) {
        this();
        String text = textArea.getSelectedText();
        SCORE = null;
        Score scoreTmp = new Score(text);
        if (text.isBlank())
            DISPLAY_TEXT = "Select a measure to play it.";
        else if (scoreTmp.measureCollectionList.isEmpty())
            DISPLAY_TEXT = "No measure detected in selection.";
        else if (!scoreTmp.isGuitar(true) && !scoreTmp.isBass(true))
            DISPLAY_TEXT = "Only guitar and bass measures can be played.";
        else {
            DISPLAY_TEXT = scoreTmp.toString();
            SCORE = scoreTmp;
        }

        try {
            if (STAGE!=null)
                STAGE.close();
            STAGE = new Stage();
            STAGE.setTitle("Note Player");
            STAGE.initModality(Modality.APPLICATION_MODAL);
            STAGE.initOwner(MainApp.STAGE);
            //STAGE.setOnCloseRequest(e -> NotePlayer.kill());

            Parent root = FXMLLoader.load(NotePlayer.class.getClassLoader().getResource("GUI/tabPlayer.fxml"));
            Scene scene = new Scene(root);
            STAGE.setScene(scene);
            STAGE.setMinHeight(270);
            STAGE.setMaxHeight(270);
            STAGE.setMinWidth(430);
            STAGE.setMaxWidth(430);
            STAGE.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    @FXML
    public void exit() {
        STAGE.close();
    }

    public static boolean play(Score score) {// throws ParserConfigurationException, MidiUnavailableException, URISyntaxException, ParsingException, IOException, InvalidMidiDataException {
//        if (!score.isGuitar(false) && !score.isBass(false)) return false;
//        MusicXmlParser parser = new MusicXmlParser();
//        StaccatoParserListener listener = new StaccatoParserListener();
//        parser.addParserListener(listener);
//        parser.parse(Parser.parse(score));
//        final Pattern musicXMLPattern = listener.getPattern().setInstrument("Guitar");
//        PLAYER = new ManagedPlayer();
//        PLAYER.start(new Player().getSequence(musicXMLPattern));
        return true;
    }

    public static void kill() {
//        PLAYER.finish();
    }

    @FXML
    public void play() {
//        try {
//            if (PLAYER.isFinished() || !PLAYER.isStarted()) {
//                play(SCORE);
//                seekSlider.setMax(PLAYER.getTickLength());
//                seekSlider.setValue(0);
//            } else if (PLAYER.isPaused())
//                PLAYER.resume();
//            seekSlider.setValue(PLAYER.getTickLength());
//        } catch (Exception e) {
//            e.printStackTrace();
//            tabPlayerDisplay.setText("Measure could not be played.");
//            disableControls();
//            kill();
//        }

    }

    @FXML
    public void pause() {
//        if (!PLAYER.isPaused())
//            PLAYER.pause();
    }

    public void initialize() {
        tabPlayerDisplay.setEditable(false);
        tabPlayerDisplay.setText(DISPLAY_TEXT);
        if (SCORE==null) {
            disableControls();
        }
        seekSlider.setValue(0);
    }

    private void disableControls() {
        playButton.setDisable(true);
        pauseButton.setDisable(true);
        seekSlider.setValue(0);
        seekSlider.setDisable(true);
    }
}
