package GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.prefs.Preferences;
import javafx.fxml.FXML;

@ExtendWith(ApplicationExtension.class)
public class GuiTest  extends ApplicationTest {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("GUI/scene.fxml"));
        Scene scene = new Scene(root);


        scene.getStylesheets().add(getClass().getClassLoader().getResource("GUI/styles.css").toExternalForm());

        stage.setTitle("TAB 2 XML");
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testEmptyInput(FxRobot robot) {
        robot.clickOn("#convertButton");
        FxAssert.verifyThat("#convertButton", NodeMatchers.isDisabled());
    }

    @Test
    public void invalidInput(FxRobot robot) {
        robot.clickOn("#TEXT_AREA");
        robot.write("this text is not  a valid measure", 0);
        FxAssert.verifyThat("#convertButton", NodeMatchers.isDisabled());
    }

    @Test
    public void testGoTo(FxRobot robot) {
        robot.clickOn("#TEXT_AREA");
        robot.write("e|-------5-----2-------0--|----------------------------|-------------------7-----|\n" +
                "B|--1-------5-------2-----|--3----2--0--------------0--|--0-------------------7--|\n" +
                "G|------------------------|-------------4-3-4-3-4-3----|-------------------------|\n" +
                "D|-------------0----------|-------2-----4--------------|--------------1----------|\n" +
                "A|--0----4----------1-----|--2-------------------------|--2--1--2--4-------------|\n" +
                "E|------------------------|---------------------2------|-------------------7-----|", 0);
        robot.clickOn("#gotoMeasureField");
        robot.write("1");
        robot.clickOn("#gotoMeasureButton");
        assertEquals(2, FXMLController.TEXT_AREA_ALIAS.getCaretPosition());
    }

    @Test
    public void testWrapText(FxRobot robot) {
        robot.clickOn("#wrapCheckbox");
        assertEquals(true, FXMLController.TEXT_AREA_ALIAS.isWrapText());
    }
}

