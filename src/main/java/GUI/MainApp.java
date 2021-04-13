package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class MainApp extends Application {

    public static Stage STAGE;
    public static Stage SPLASH_STAGE;

    @Override
    public void start(Stage stage) throws Exception {
        SPLASH_STAGE = stage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("GUI/splashScreen.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("GUI/styles.css").toExternalForm());

        SPLASH_STAGE.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);

        stage.setTitle("TAB 2 XML");
        stage.setScene(scene);
        stage.show();
    }

    public static void exit() {
        TabInput.executor.shutdown();
        NotePlayer.kill();
    }

    public static void main(String[] args) {
        launch(args);
    }

}