package org.openjfx;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.reactfx.Subscription;
import utility.Parser;

public class FXMLController implements Initializable {

    public static ExecutorService executor;

    @FXML public CodeArea TEXT_AREA;

    @FXML private AnchorPane anchorPane;

    @FXML
    private void convertButtonHandle() throws IOException {


        Parser.createScore(TEXT_AREA.getText());
        String output = Parser.parse();

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("org.openjfx/convertWindow.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Convert Settings");
        stage.setScene(new Scene(root, 450, 450));
        stage.show();

        /*
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fc.getExtensionFilters().add(extFilter);

        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        fc.setInitialDirectory(userDirectory);


        File file = fc.showSaveDialog( anchorPane.getScene().getWindow() );

        if (file != null) {
            saveToFile(output, file);
        }
        */
    }

    private void saveToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TEXT_AREA.setParagraphGraphicFactory(LineNumberFactory.get(TEXT_AREA));
        new TabInput(TEXT_AREA).enableHighlighting();

        Popup popup = new Popup();
        Label popupMsg = new Label();
        popupMsg.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 5;");
        popup.getContent().add(popupMsg);

        TEXT_AREA.setMouseOverTextDelay(Duration.ofMillis(TabInput.HOVER_DELAY));
        TEXT_AREA.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            if (TabInput.ACTIVE_ERRORS.isEmpty()) return;
            int chIdx = e.getCharacterIndex();
            String message = TabInput.getMessageOfCharAt(chIdx);
            if (message.isEmpty()) return;
            Point2D pos = e.getScreenPosition();
            popupMsg.setText(message);
            popup.show(TEXT_AREA, pos.getX(), pos.getY() + 10);
        });
        TEXT_AREA.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
            popup.hide();
        });

    }
}