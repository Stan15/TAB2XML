package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;


public class MainApp extends Application {

    public static ExecutorService executor;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("org.openjfx/scene.fxml"));


        Scene scene = new Scene(root);

        Flow.Subscription cleanupWhenDone = TabInput.TEXT_AREA.multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(TabInput::computeHighlightingAsync)
                .awaitLatest(TAB_INPUT_AREA.multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(TabInput::applyHighlighting);


        scene.getStylesheets().add(getClass().getClassLoader().getResource("org.openjfx/styles.css").toExternalForm());

        stage.setTitle("JavaFX and Gradle");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        executor.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }

}