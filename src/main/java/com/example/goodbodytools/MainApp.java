package com.example.goodbodytools;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApp.class.getResourceAsStream("brackets.png"))));
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("mainScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 900);
        stage.setTitle("DevTools");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}