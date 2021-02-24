package org.openjfx;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.StyleClassedTextArea;

public class FXMLController {

    @FXML public StyleClassedTextArea INPUT_FIELD;

    @FXML private AnchorPane anchorPane;

    @FXML
    private void convertButtonHandle() {
        parser.Parser p = new parser.Parser(INPUT_FIELD.getText());
        String output = p.parse();

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
}