package com.example.goodbodytools;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexController {


    public Label infoLabel;
    @FXML
    private final PauseTransition regexPause;

    //private static final String JSON_KEYWORDS = "\\b(true|false|null)\\b";
    private static final String JSON_KEYWORDS = "(?<![\"])(\\b(?:true|false|null)\\b)(?![\"])";

    private static final String JSON_STRING = ":\\s*(\".*?\")";
    private static final String JSON_PROPERTY = "\"([^\"]+)\"(?=\\s*:)";

    private static final String JSON_NUMBER = "\\b-?\\d+(\\.\\d+)?\\b";
    private static final String JSON_BRACES = "[\\[\\]{}]";


    private static final Pattern JSON_PATTERN = Pattern.compile(
            "(?<KEYWORD>" + JSON_KEYWORDS + ")"
                    + "|(?<STRING>" + JSON_STRING + ")"
                    + "|(?<NUMBER>" + JSON_NUMBER + ")"
                    + "|(?<BRACE>" + JSON_BRACES + ")"
                    + "|(?<PROPERTY>" +JSON_PROPERTY + ")"
    );


    private static final Pattern XML_TAG = Pattern.compile("(?<ELEMENT>(</?\\h*)(\\w+)([^<>]*)(\\h*/?>))"
            +"|(?<COMMENT><!--(.|\\v)+?-->)");

    private static final Pattern ATTRIBUTES = Pattern.compile("(\\w+\\h*)(=)(\\h*\"[^\"]+\")");

    private static final int GROUP_OPEN_BRACKET = 2;
    private static final int GROUP_ELEMENT_NAME = 3;
    private static final int GROUP_ATTRIBUTES_SECTION = 4;
    private static final int GROUP_CLOSE_BRACKET = 5;
    private static final int GROUP_ATTRIBUTE_NAME = 1;
    private static final int GROUP_EQUAL_SYMBOL = 2;
    private static final int GROUP_ATTRIBUTE_VALUE = 3;

    @FXML
    public CodeArea parsedCodeArea;

    @FXML
    public Tab textFormatterTab;
    @FXML
    public CodeArea regexCodeArea;
    public VBox messagesContainer;

    public RegexController() {
        regexPause = new PauseTransition(Duration.seconds(1));
        // Set an action to be performed when the pause is finished
        regexPause.setOnFinished(event -> {
            handleRegex();
        });
    }

    private void handleRegex(){
        System.out.println("in handleRegex");

        String wordToHighlight = "1";
        // Create a pattern to match the word (case-insensitive)
        Pattern pattern = Pattern.compile("\\b" + wordToHighlight + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(regexCodeArea.getText());

        // Clear previous highlights
        regexCodeArea.clearStyle(0, regexCodeArea.getLength());

        List<Map<String, Integer>> positions = new ArrayList<>();

        // Apply highlights to all instances of the word
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            regexCodeArea.setStyleClass(start,end, "highlighted-text");
            Map<String, Integer> coordinates = new HashMap<>();
            coordinates.put("start", start);
            coordinates.put("end", end);
            positions.add(coordinates);
        }
        System.out.println(positions);


        // Create a ListView to display positions
        ListView<Map<String, Integer>> listView = new ListView<>();
        for (Map<String, Integer> instance : positions) {
            listView.getItems().add(instance);
        }

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Handle the selected item change
            if (newValue != null) {
                regexCodeArea.selectRange(newValue.get("start"), newValue.get("end"));
                regexCodeArea.moveTo(newValue.get("start"));
                regexCodeArea.requestFollowCaret();
            }
        });

        // Create an Alert dialog with the ListView
        Alert alert = new CustomAlert(Alert.AlertType.INFORMATION);
        alert.setTitle("Found Items");
        alert.setHeaderText("Select an Item");

        alert.getDialogPane().setContent(listView);
        Platform.runLater(alert::showAndWait);

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Double-click detected, close the alert
                alert.close();
            }
        });

        // Add a custom event handler to close the alert when the close button (X) is clicked
        // Add an event filter to close the alert when ESC key is pressed
        alert.getDialogPane().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                alert.close();
            }
        });

    }


    @FXML
    private void initialize() {
        initCodeAreasDefault();
        regexCodeArea.setLineHighlighterFill(Paint.valueOf("#D3D3D3"));
        regexCodeArea.setLineHighlighterOn(true);

        // Add a keyTyped event handler to the TextArea
        regexCodeArea.setOnKeyPressed( event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.F) {
                System.out.println("Ctrl + F pressed");
                regexPause.playFromStart();
                event.consume(); // Consume the event to prevent it from being processed by other handlers

            }
        });



        regexCodeArea.setOnKeyPressed( event -> {
            if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.U) {
                regexCodeArea.replaceText(DataUtils.toggleCase(regexCodeArea.getText().trim()));
            }
        });

    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = JSON_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = null;
            if (matcher.group("KEYWORD") != null) {
                styleClass = "keyword";
            } else if (matcher.group("STRING") != null) {
                styleClass = "string";
            } else if (matcher.group("NUMBER") != null) {
                styleClass = "number";
            }else if (matcher.group("BRACE") != null) {
                styleClass = "brace";
            }else if (matcher.group("PROPERTY") != null) {
                styleClass = "property";
            }

            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }


    private void initCodeAreasDefault(){
        regexCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(regexCodeArea));
    }

}