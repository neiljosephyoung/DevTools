package com.example.devtools;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import org.kordamp.ikonli.materialdesign2.MaterialDesignE;
import org.kordamp.ikonli.materialdesign2.MaterialDesignL;

import java.io.IOException;
import java.util.Map;

public class MainController {
    @FXML
    public TitledPane textFormatterItem;
    @FXML
    public Accordion menuAccordion;
    @FXML
    public TitledPane regexMenuItem;
    @FXML
    private Label welcomeText;

    @FXML
    public BorderPane mainBorderPane;
    private boolean isLightMode = false;
    @FXML
    private void initialize() {
        Platform.runLater(this::buildMenuItems);
        buildToolbar();
        MessageServiceHandler messageServiceHandler = new MessageServiceHandler(mainBorderPane);

    }
    private void buildToolbar() {
        VBox box = new VBox();
        ToolBar toolbar = new ToolBar();

        // Determine the icon based on the current mode
        FontIcon icon = isLightMode ? new FontIcon(MaterialDesignL.LIGHTBULB_OFF) : new FontIcon(MaterialDesignL.LIGHTBULB_ON);

        Button modeToggle = new Button("Mode", icon);

        modeToggle.setOnAction(actionEvent -> {
            if (isLightMode) {
                Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            } else {
                CustomAlert alert = new CustomAlert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Are you sure you want to switch to Light Mode?");
                ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
                if (result == ButtonType.OK) {
                    // User clicked OK
                    // Handle OK button action
                    System.out.println("OK button clicked");
                    MessageServiceHandler.addMessage("Welcome Danny", "", "success");
                    Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
                } else {
                    // User clicked Cancel or closed the dialog
                    // Handle Cancel button action
                    System.out.println("Cancel button clicked or dialog closed");
                }
            }

            // Toggle the mode after handling the alert result
            isLightMode = !isLightMode;

            // Determine the icon based on the updated mode and set it to the modeToggle button
            modeToggle.setGraphic(isLightMode ? new FontIcon(MaterialDesignL.LIGHTBULB_OFF) : new FontIcon(MaterialDesignL.LIGHTBULB_ON));
        });
        Map<String,String> fonts = DataUtils.getFontsAvailable();
        Application.setUserAgentStylesheet(fonts.get("Primer Dark"));

        toolbar.getItems().add(modeToggle);
        box.getChildren().add(toolbar);
        mainBorderPane.setTop(box);
    }

    public MainController(){

    }
    private void buildMenuItems() {
        // Create separate instances of buttons for each VBox
        Button jsonButton = new Button(" - JSON", new FontIcon(MaterialDesignA.AB_TESTING));
        jsonButton.getStyleClass().add(Styles.BUTTON_OUTLINED);
        jsonButton.setOnAction((e) -> {
            loadClassIntoCenter("text-formatter-view.fxml",TextFormatterController.class);
            e.consume();
        });

        Button regexButton = new Button(" - REGEX", new FontIcon(MaterialDesignA.AB_TESTING));
        regexButton.getStyleClass().add(Styles.BUTTON_OUTLINED);
        regexButton.setOnAction((e) -> {
            loadClassIntoCenter("regex-view.fxml", RegexController.class);
            e.consume();
        });

        // Create VBox for each TitledPane and add buttons
        //VBox contentPane1 = new VBox(10,jsonButton, xmlBtn, txtBtn);
        VBox contentPane1 = new VBox(10,jsonButton, regexButton);
      //  VBox contentPane2 = new VBox(10, xmlBtn);

        TitledPane titledPane1 = new TitledPane("Text Formatter", contentPane1);
       // TitledPane titledPane2 = new TitledPane("Regex Helper", contentPane2);
        //TitledPane titledPane3 = new TitledPane("Settings", contentPane3);
        menuAccordion.getPanes().addAll(titledPane1);
        menuAccordion.getPanes().get(0).setExpanded(true);
    }

    public void loadClassIntoCenter(String fxmlName, Class<?> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
            Parent root = loader.load();

            Object controller = loader.getController();

            mainBorderPane.setCenter(root);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}