package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {

    public static Stage STAGE;

    @Override
    public void start(Stage stage) throws Exception {
        STAGE = stage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("GUI/scene.fxml"));
        Scene scene = new Scene(root);
        STAGE.setOnCloseRequest(e -> stop());

        scene.getStylesheets().add(getClass().getClassLoader().getResource("GUI/styles.css").toExternalForm());

        stage.setTitle("TAB 2 XML");
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.setScene(scene);
        LoadScreen.run();
        stage.show();
    }

    @Override
    public void stop() {
        TabInput.executor.shutdown();
        NotePlayer.kill();
    }

    public static void main(String[] args) {
        launch(args);
    }

}