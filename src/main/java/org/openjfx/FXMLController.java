package org.openjfx;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import utility.Parser;

public class FXMLController implements Initializable {

    private File saveFile;
    private boolean isEditingSavedFile;

    private String generatedOutput;

    @FXML public Stage convertWindow = new Stage();

    @FXML public CodeArea TEXT_AREA;

    @FXML private CheckBox wrapCheckbox;
    @FXML private BorderPane borderPane;

    @FXML
    private void handleNew() {
        if (!TEXT_AREA.getText().isBlank()) {
            boolean userOkToGoAhead = promptSave("Do you want to save this document first?");
            if (!userOkToGoAhead) return;
        }
        this.TEXT_AREA.clear();
        this.isEditingSavedFile = false;
    }

    @FXML
    private void handleOpen() {
        handleNew();

        String userDirectoryString = System.getProperty("user.home");

        File openDirectory;
        if (this.saveFile!=null && saveFile.canRead()) {
            openDirectory = new File(this.saveFile.getParent());
        }else
            openDirectory = new File(userDirectoryString);

        if(!openDirectory.canRead()) {
            openDirectory = new File("c:/");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialDirectory(openDirectory);
        File openedFile = fileChooser.showOpenDialog(MainApp.STAGE);
        if (openedFile.exists()) {
            try {
                String newText = Files.readString(Path.of(openedFile.getAbsolutePath())).replace("\r\n", "\n");
                TEXT_AREA.replaceText(new IndexRange(0, TEXT_AREA.getText().length()), newText);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.saveFile = openedFile;
        this.isEditingSavedFile = true;
    }

    @FXML
    private boolean handleSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        if (this.saveFile!=null) {
            fileChooser.setInitialFileName(saveFile.getName());
            fileChooser.setInitialDirectory(new File(saveFile.getParent()));
        }

        File saveFile = fileChooser.showSaveDialog(MainApp.STAGE);
        if (saveFile==null) return false;
        try {
            FileWriter myWriter = new FileWriter(saveFile.getPath());
            myWriter.write(TEXT_AREA.getText());
            myWriter.close();

            this.saveFile = saveFile;
            this.isEditingSavedFile = true;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @FXML
    private boolean handleSave() {
        if (!this.isEditingSavedFile || !this.saveFile.exists())
            return this.handleSaveAs();
        try {
            FileWriter myWriter = new FileWriter(this.saveFile.getPath());
            myWriter.write(TEXT_AREA.getText());
            myWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private boolean promptSave(String message) {

        // open popup dialog that asks if user wants to cancel their action, save before continuing with action, or not save.

        boolean userAskedToSave = true;
        boolean userCancelledDialog = false;
        if (userCancelledDialog) return false;

        if (this.TEXT_AREA.getText().isBlank()) return true;    // in this case, there is nothing to save.

        if (userCancelledDialog) return false;
        if (userAskedToSave) {
            boolean saved = false;
            if (isEditingSavedFile) {
                saved = handleSave();
            }else {
                saved = handleSaveAs();
            }
            if (!saved) return false;
        }
        return true;
    }

    @FXML
    private void convertButtonHandle() throws IOException {
        Parser.createScore(TEXT_AREA.getText());
        generatedOutput = Parser.parse();

        /*This code for some reason will not create a new window*/
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("org.openjfx/convertWindow.fxml"));
        convertWindow.setTitle("Convert Settings");
        convertWindow.setScene(new Scene(root, 575, 960));
        convertWindow.show();
    }

    @FXML
    private void saveButtonHandle() throws IOException {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fc.getExtensionFilters().add(extFilter);

        String userDirectoryString = System.getProperty("user.home");
        String saveFileDir = this.saveFile.getParent();
        File saveDirectory = new File(saveFileDir ==null ? userDirectoryString : saveFileDir);
        if(!saveDirectory.canRead()) {
            saveDirectory = new File("c:/");
        }
        fc.setInitialDirectory(saveDirectory);


        File file = fc.showSaveDialog( borderPane.getScene().getWindow() );

        if (file != null) {
            saveToFile(generatedOutput, file);
        }
    }

    @FXML
    private void cancelButtonHandle()  {
        convertWindow.hide();
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

    @FXML
    private void setWrapProperty() {
        TEXT_AREA.setWrapText(this.wrapCheckbox.isSelected());
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