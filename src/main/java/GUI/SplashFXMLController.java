package GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SplashFXMLController implements Initializable {
    @FXML
    ProgressBar bar = new ProgressBar(0);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new SplashScreen().start();
    }

    class SplashScreen extends Thread {
        long duration = 500;
        long progressUnits = 1;
        @Override
        public void run() {
            try {
                progress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Parent root = null;
                    try {
                        root = FXMLLoader.load(getClass().getClassLoader().getResource("GUI/scene.fxml"));
                    }catch (IOException e) {
                        Logger.getLogger(SplashFXMLController.class.getName()).log(Level.SEVERE, null, e);
                    }
                    Stage stage = new Stage();
                    MainApp.STAGE = stage;
                    MainApp.STAGE.setOnCloseRequest(e -> MainApp.exit());

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getClassLoader().getResource("GUI/styles.css").toExternalForm());

                    stage.setTitle("TAB 2 XML");
                    stage.setMinWidth(700);
                    stage.setMinHeight(500);
                    stage.setScene(scene);
                    stage.show();

                    MainApp.SPLASH_STAGE.close();
                }
            });
        }

        private void progress() throws InterruptedException {
            if (bar==null) return;
            double time = 0;
            while(time<duration) {
                Thread.sleep(progressUnits);
                bar.setProgress(time/duration);
                time += progressUnits;
            }
        }
    }

}
