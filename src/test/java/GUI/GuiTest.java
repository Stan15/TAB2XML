package GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
public class GuiTest {

    @Start
    private void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("org.openjfx/scene.fxml"));
        Scene scene = new Scene(root);


        scene.getStylesheets().add(getClass().getClassLoader().getResource("org.openjfx/styles.css").toExternalForm());

        stage.setTitle("TAB 2 XML");
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.setScene(scene);
        LoadScreen.run();
        stage.show();
    }

    @Test
    public void testEmptyInput(FxRobot robot) {
        robot.clickOn("#convertButton");
        FxAssert.verifyThat("#convertButton", LabeledMatchers.hasText("Convert"));
    }
}

