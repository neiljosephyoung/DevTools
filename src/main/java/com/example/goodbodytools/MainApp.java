package com.example.goodbodytools;

import atlantafx.base.theme.PrimerDark;
import com.example.goodbodytools.datastore.TempData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MainApp extends Application {
    public static final Logger LOGGER = LogManager.getLogger(MainApp.class);
    public static TempData TEMP_DATA = new TempData();
    public static final String CONTEXT_FONT = "-fx-font-family: 'Arial'; -fx-font-size: 12;";
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApp.class.getResourceAsStream("brackets.png"))));
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("mainScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 860,500);
        stage.setTitle("DevTools");
        stage.setScene(scene);
        stage.setMaximized(true);

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            LOGGER.info("Uncaught exception in thread {}.", t, e);
        });

        LOGGER.info("App Started");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}