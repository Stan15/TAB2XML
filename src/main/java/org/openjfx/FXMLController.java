package org.openjfx;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import utility.Parser;

public class FXMLController implements Initializable {

    public CodeArea INPUT_FIELD;

    @FXML private AnchorPane anchorPane;

    @FXML
    private void convertButtonHandle() {
        Parser.createScore(INPUT_FIELD.getText());
        String output = Parser.parse();

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
        INPUT_FIELD.setWrapText(true);

        Popup popup = new Popup();
        Label popupMsg = new Label();
        popupMsg.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 5;");
        popup.getContent().add(popupMsg);

        INPUT_FIELD.setMouseOverTextDelay(Duration.ofMillis(TabInput.HOVER_DELAY));
        INPUT_FIELD.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            if (TabInput.ACTIVE_ERROR_MESSAGES.isEmpty()) return;
            int chIdx = e.getCharacterIndex();
            String message = TabInput.getMessageOfCharAt(chIdx);
            if (message.isEmpty()) return;
            Point2D pos = e.getScreenPosition();
            popupMsg.setText(message);
            popup.show(INPUT_FIELD, pos.getX(), pos.getY() + 10);
        });
        INPUT_FIELD.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
            popup.hide();
        });

    }
}