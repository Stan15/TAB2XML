package GUI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import converter.Instrument;
import converter.Score;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.*;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import utility.Parser;

public class FXMLController extends Application {
    Preferences p = Preferences.userNodeForPackage(MainApp.class);
    private static File saveFile;
    private static boolean isEditingSavedFile;
    private String InstrumentSetting = "auto";

    private static String generatedOutput;


    private static Window convertWindow = new Stage();
    @FXML public CodeArea TEXT_AREA;

    @FXML private ComboBox errorSensitivity;
    @FXML private ComboBox cmbNumerator;
    @FXML private ComboBox cmbDenominator;
    @FXML private ComboBox cmbScoreType;
    
    @FXML private TextField outputFolderField;
    @FXML private TextField gotoMeasureField;
    @FXML private CheckBox wrapCheckbox;
    @FXML private BorderPane borderPane;
    @FXML private Button convertButton = new Button();
    @FXML private Button goToline = new Button();
    @FXML TextField titleField;
    @FXML TextField artistField;
    @FXML TextField fileNameField;

    private static CodeArea savedTextArea;      //this is a variable used to fix the bug where a new window can't be opened when the "convert" button is clicked. It is kind of a hack, not fixing the actual problem


    /* Settings Window */
    @FXML private void handleErrorSensitivity() {
            p.put("errorSensitivity", errorSensitivity.getValue().toString() );
            changeErrorSensitivity(errorSensitivity.getValue().toString());
    }

    @FXML
    private void handleSettings() {
        convertWindow = this.openNewWindow("GUI/settingsWindow.fxml", "Program Settings");
    }

    @FXML
    private void handleUserManual() throws URISyntaxException  {
        URL resource = getClass().getClassLoader().getResource("org.openjfx/UserManual.pdf");
        File file = new File(resource.toURI());

        HostServices hostServices = getHostServices();
        hostServices.showDocument(file.getAbsolutePath());

    }

    @FXML
    private void handleChangeFolder() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("src"));
        File selected = dc.showDialog(MainApp.STAGE);
        outputFolderField.setText(selected.getAbsolutePath());

        p.put("outputFolder", selected.getAbsolutePath());
    }

    @FXML
    private void handleTSNumerator() {
        String value = cmbNumerator.getValue().toString();
        p.put("tsNumerator", value);
        Score.DEFAULT_BEAT_COUNT = Integer.parseInt(value);
    }
    @FXML
    private void handleTSDenominator() {
        String value = cmbDenominator.getValue().toString();
        p.put("tsDenominator", value);
        Score.DEFAULT_BEAT_TYPE = Integer.parseInt(value);
    }
    /* --------------------------------------------------------------- */

    /* Main Window */
    @FXML
    private void handleNew() {
        boolean userOkToGoAhead = promptSave();
        if (!userOkToGoAhead) return;
        this.TEXT_AREA.clear();
        isEditingSavedFile = false;
    }

    @FXML
    private void handleOpen() {
        boolean userOkToGoAhead = promptSave();
        if (!userOkToGoAhead) return;

        String userDirectoryString = System.getProperty("user.home");
        File openDirectory;
        if (saveFile!=null && saveFile.canRead()) {
            openDirectory = new File(saveFile.getParent());
        }else
            openDirectory = new File(userDirectoryString);

        if(!openDirectory.canRead()) {
            openDirectory = new File("c:/");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialDirectory(openDirectory);
        File openedFile = fileChooser.showOpenDialog(MainApp.STAGE);
        if (openedFile==null) return;
        if (openedFile.exists()) {
            try {
                String newText = Files.readString(Path.of(openedFile.getAbsolutePath())).replace("\r\n", "\n");
                TEXT_AREA.replaceText(new IndexRange(0, TEXT_AREA.getText().length()), newText);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        saveFile = openedFile;
        isEditingSavedFile = true;
        new TabInput(TEXT_AREA, convertButton).computeHighlightingAsync();

    }

    @FXML
    private boolean handleSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        if (saveFile!=null) {
            fileChooser.setInitialFileName(saveFile.getName());
            fileChooser.setInitialDirectory(new File(saveFile.getParent()));
        }

        File newSaveFile = fileChooser.showSaveDialog(MainApp.STAGE);
        if (newSaveFile==null) return false;
        try {
            FileWriter myWriter = new FileWriter(newSaveFile.getPath());
            myWriter.write(TEXT_AREA.getText());
            myWriter.close();

            saveFile = newSaveFile;
            isEditingSavedFile = true;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @FXML
    private boolean handleSave() {
        if (!isEditingSavedFile || saveFile==null || !saveFile.exists())
            return this.handleSaveAs();
        try {
            FileWriter myWriter = new FileWriter(saveFile.getPath());
            myWriter.write(TEXT_AREA.getText());
            myWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private boolean promptSave() {

        //we don't care about overwriting a blank file. If file is blank, we are ok to go. it doesn't matter if it is saved or not
        if (TEXT_AREA.getText().isBlank())  return true;

        try {
            if (saveFile!=null && Files.readString(Path.of(saveFile.getAbsolutePath())).replace("\r\n", "\n").equals(TEXT_AREA.getText()))
                return true;    //if file didn't change, we are ok to go. no need to save anything, no chance of overwriting.
        }catch (Exception e){
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved file");
        alert.setHeaderText("This document is unsaved and will be overwritten. Do you want to save it first?");
        alert.setContentText("Choose your option.");

        ButtonType buttonTypeSave = new ButtonType("Save");
        ButtonType buttonTypeOverwrite = new ButtonType("Overwrite");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeOverwrite, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        boolean userOkToGoAhead = false;
        if (result.get() == buttonTypeSave){
            boolean saved;
            if (isEditingSavedFile) {
                saved = handleSave();
            }else {
                saved = handleSaveAs();
            }
            if (saved)
                userOkToGoAhead = true;
        } else if (result.get() == buttonTypeOverwrite) {
            // ... user chose "Override". we are good to go ahead
            userOkToGoAhead = true;
        }
        //if user chose "cancel", userOkToGoAhead is still false. we are ok.
        return userOkToGoAhead;
    }

    private Window openNewWindow(String fxmlPath, String windowName) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(fxmlPath));

            Stage stage = new Stage();
            stage.setTitle(windowName);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(MainApp.STAGE);
            stage.setResizable(false);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            return scene.getWindow();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
        return null;
    }

    @FXML
    private void convertButtonHandle() throws IOException {
        convertWindow = this.openNewWindow("GUI/convertWindow.fxml", "ConversionOptions");
    }


    @FXML
    private void saveConvertedButtonHandle() {
        Parser.createScore(TEXT_AREA.getText());
        generatedOutput = Parser.parse();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MusicXML files", "*.xml", "*.mxl", "*.musicxml");
        fileChooser.getExtensionFilters().add(extFilter);

        File initialDir = null;
        String initialName = null;
        if (!fileNameField.getText().isBlank() && fileNameField.getText().length()<50)
            initialName = fileNameField.getText().strip();

        if (saveFile!=null) {
            if (initialName==null) {
                String name = saveFile.getName();
                if(name.contains("."))
                    name = name.substring(0, name.lastIndexOf('.'));
                initialName = name;
            }
            File parentDir = new File(saveFile.getParent());
            if (parentDir.exists())
                initialDir = parentDir;
        }
        if (initialName!=null)
            fileChooser.setInitialFileName(initialName);

        if (initialDir==null || !(initialDir.exists() && initialDir.canRead()))
            initialDir = new File(System.getProperty("user.home"));
        if (!(initialDir.exists() && initialDir.canRead()))
            initialDir = new File("c:/");

        fileChooser.setInitialDirectory(initialDir);


        File file = fileChooser.showSaveDialog(convertWindow);

        if (file != null) {
            saveToXMLFile(generatedOutput, file);
            saveFile = file;
            cancelConvertButtonHandle();
        }
    }

    @FXML
    private void cancelConvertButtonHandle()  {
        convertWindow.hide();
        new TabInput(TEXT_AREA,convertButton).enableHighlighting();
    }


    private void saveToXMLFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void setWrapProperty() {
        TEXT_AREA.setWrapText(this.wrapCheckbox.isSelected());
    }


    @FXML 
    public void initialize() {
        initializeTextArea();
        initializeSettings();
    }

    @FXML
    private void handleScoreType() {
        InstrumentSetting = cmbScoreType.getValue().toString().strip();
        Score.INSTRUMENT_MODE = Parser.getInstrumentEnum(InstrumentSetting);
        new TabInput(TEXT_AREA, convertButton).refresh();
    }

    @FXML
    private void handleGotoMeasure() {
        int measureNumber = Integer.parseInt( gotoMeasureField.getText() );
        new TabInput(TEXT_AREA, convertButton).goToMeasure(measureNumber);
    }

    private void initializeTextArea() {
        if (TEXT_AREA==null && savedTextArea!=null) {
            this.TEXT_AREA = savedTextArea;
        }
        initializeTextAreaErrorPopups();
        ContextMenu context = new ContextMenu();
        MenuItem menuItem = new MenuItem("Play Notes");
        menuItem.setOnAction(e -> {
            new NotePlayer(TEXT_AREA);
        });
        context.getItems().add(menuItem);
        TEXT_AREA.setContextMenu(context);

    }

    private void initializeTextAreaErrorPopups() {
        TEXT_AREA.setParagraphGraphicFactory(LineNumberFactory.get(TEXT_AREA));
        new TabInput(TEXT_AREA, convertButton).enableHighlighting();

        savedTextArea = TEXT_AREA;

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


    private void changeErrorSensitivity(String prefValue) {
        switch (prefValue) {
            case "Level 1 - Minimal Error Checking" -> TabInput.ERROR_SENSITIVITY = 1;
            case "Level 3 - Advanced Error Checking" -> TabInput.ERROR_SENSITIVITY = 3;
            case "Level 4 - Detailed Error Checking" -> TabInput.ERROR_SENSITIVITY = 4;
            default -> TabInput.ERROR_SENSITIVITY = 2;
        }

        new TabInput(TEXT_AREA, convertButton).refresh();
    }
    private void initializeSettings() {
        if (errorSensitivity != null && outputFolderField != null) {
            Preferences p;
            p = Preferences.userNodeForPackage(MainApp.class);
            String level = p.get("errorSensitivity", "Level 2 - Standard Error Checking");
            errorSensitivity.setValue(level);
            changeErrorSensitivity(errorSensitivity.getValue().toString());
            String outputFolder = p.get("outputFolder", new File("src").getAbsolutePath());
            outputFolderField.setText(outputFolder);

            String tsNumerator = p.get("tsNumerator", "4");
            String tsDenominator = p.get("tsDenominator", "4");

            cmbNumerator.setValue(tsNumerator);
            cmbDenominator.setValue(tsDenominator);
            Score.DEFAULT_BEAT_COUNT = Integer.parseInt(tsNumerator);
            Score.DEFAULT_BEAT_TYPE = Integer.parseInt(tsDenominator);
        }
    }
    TabInput goTo = new TabInput(TEXT_AREA, convertButton);
    void goToMeasure() {
        if(true){
            goToMeasure();
        }
        if(false){

        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}