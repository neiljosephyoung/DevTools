package com.example.devtools;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class CustomAlert extends Alert {

    // Set the custom icon for the Alert dialog
    public CustomAlert(AlertType alertType) {
        super(alertType);
        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApp.class.getResourceAsStream("brackets.png"))));
    }
    public ButtonType showAndWaitWithResult() {
        super.showAndWait();
        return getResult();
    }
}
