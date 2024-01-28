package com.example.goodbodytools;
import atlantafx.base.controls.Card;
import atlantafx.base.controls.ModalPane;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignX;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.goodbodytools.MainApp.*;


public class XmlFormatterController {

    @FXML
    public Button apiRequestButton;
    @FXML
    public Button dbRequestButton;
    @FXML
    public Button parseButton;
    @FXML
    public TextArea parsedTextArea;
    @FXML
    public Label infoLabel;
    @FXML
    public TextFlow textFlow;
    @FXML
    public TextFlow mainTextFlow;
    private final PauseTransition textformatPause;

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
    public CodeArea mainCodeArea;

    public VBox messagesContainer;
    public HBox wrapper;
    public VBox optionsContainer;
    public ModalPane mainModalPane;

    public XmlFormatterController() {
        // Initialize PauseTransition with a delay of 1 second (adjust as needed)
        textformatPause = new PauseTransition(Duration.seconds(0));
    }


    @FXML
    private void initialize() {
        Card card1 = new Card();
        card1.getStyleClass().add(Styles.ELEVATED_2);

        card1.setHeader(new Tile(
                "XML Formatter",
                "This service will auto parse for XML structures just paste in the data"
        ));
        //card1.setBody(new Label("This is content"));

        HBox.setHgrow(card1, Priority.ALWAYS);
        wrapper.getChildren().add(0,card1);

        mainCodeArea.clear();
        parsedCodeArea.clear();

        // Add a keyTyped event handler to the TextArea
        mainCodeArea.setOnKeyTyped(event -> {
            // When a key is typed, restart the pause timer
            handleTextAreaChanged();
        });

        // Set the text color using inline CSS
        infoLabel.setStyle("-fx-text-fill: red;");

        // Change the text color when the label text is set to a specific value
        infoLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("Unsupported data format")) {
                infoLabel.setStyle("-fx-text-fill: green;");
            } else {
                infoLabel.setStyle("-fx-text-fill: red;");
            }
        });
        initCodeAreasDefault();

        buildDataOptionsTile();
        mainCodeArea.replaceText(TEMP_DATA.getXmlText());
        //mainCodeArea.replaceText(DataUtils.readFile("mainCodeArea.txt"));
        //parsedCodeArea.replaceText(DataUtils.readFile("parsedCodeArea.txt"));
        mainCodeArea.requestFocus();
    }

    private void buildDataOptionsTile(){
//        Tile minify = new Tile(
//                "Minify",
//                "Reduce text to single line"
//        );

        Button formatBtn = new Button(
                "Format", new FontIcon(MaterialDesignX.XML)
        );
        formatBtn.getStyleClass().addAll(
                Styles.BUTTON_OUTLINED, Styles.SUCCESS
        );
        formatBtn.setMnemonicParsing(true);
        formatBtn.setOnAction(e -> formatText(mainCodeArea.getText()));

        Button minifyBtn = new Button(
                "Minify", new FontIcon(MaterialDesignC.CHECK)
        );
        minifyBtn.getStyleClass().addAll(
                Styles.BUTTON_OUTLINED, Styles.SUCCESS
        );
        minifyBtn.setMnemonicParsing(true);
        minifyBtn.setOnAction(e -> minifyButtonClick());

//        minify.setAction(minifyBtn);
//        minify.setActionHandler(minifyBtn::fire);


//        Tile showSettingsTile = new Tile(
//                "Settings",
//                "Show Settings"
//        );

        Button showSettingsBtn = new Button(
                "Settings", new FontIcon(MaterialDesignC.COG)
        );
        showSettingsBtn.getStyleClass().addAll(
                Styles.BUTTON_OUTLINED, Styles.SUCCESS
        );
        showSettingsBtn.setMnemonicParsing(true);
        showSettingsBtn.setOnAction(e ->{
            showSettings();
        });

//        showSettingsTile.setAction(showSettingsBtn);
//        showSettingsTile.setActionHandler(showSettingsBtn::fire);

        Button removeNilLines = new Button(
                "Remove Nil", new FontIcon(MaterialDesignC.CROSSHAIRS_OFF)
        );
        removeNilLines.getStyleClass().addAll(
                Styles.BUTTON_OUTLINED, Styles.SUCCESS
        );
        removeNilLines.setOnAction(e -> {
            try {
                if (!parsedCodeArea.getText().isEmpty()){
                    parsedCodeArea.replaceText(DataUtils.removeEntriesWithNilValue(parsedCodeArea.getText()));
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        HBox operations = new HBox( formatBtn, new Separator(), minifyBtn, new Separator(), removeNilLines, new Separator(),  showSettingsBtn);
        //VBox box = new VBox(new Separator(), minify,  new Separator(), showSettingsTile ,new Separator(), removeNilLines);
        optionsContainer.getChildren().add(operations);

    }

    private void showSettings() {

        Alert alert = new CustomAlert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Edit CSS Values");

        VBox box = new VBox();

        Card card1 = new Card();
        card1.getStyleClass().add(Styles.ELEVATED_2);

        card1.setHeader(new Tile(
                "CSS Colour Values",
                "Change these to affect the Text Formatter"
        ));
        card1.setBody(
                new ColorPicker(Color.RED)
        );

        box.getChildren().addAll(card1);


        alert.getDialogPane().setContent(box);
        alert.getDialogPane().requestFocus();
        alert.getDialogPane().setPrefWidth(800); // Set your preferred width
        alert.getDialogPane().setPrefHeight(600); // Set your preferred height
        alert.setResizable(true);
        alert.showAndWait();


    }

    private void initCodeAreasDefault(){

        mainCodeArea.setLineHighlighterFill(Paint.valueOf("#272727"));
        mainCodeArea.setLineHighlighterOn(true);
        parsedCodeArea.setLineHighlighterFill(Paint.valueOf("#272727"));
        parsedCodeArea.setLineHighlighterOn(true);

        mainCodeArea.getStylesheets().add("style.css");
        parsedCodeArea.getStylesheets().add("style.css");


        // Attach the context menu to the CodeArea
        parsedCodeArea.setContextMenu(buildContextMenu(parsedCodeArea));
        parsedCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(parsedCodeArea));
        parsedCodeArea.textProperty().addListener((obs, oldText, newText) -> parsedCodeArea.setStyleSpans(0, computeXmlHighlighting(newText)));

        // Attach the context menu to the CodeArea
        mainCodeArea.setContextMenu(buildContextMenu(mainCodeArea));
        mainCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(mainCodeArea));
        mainCodeArea.textProperty().addListener((obs, oldText, newText) -> mainCodeArea.setStyleSpans(0, computeXmlHighlighting(newText)));

        mainCodeArea.setOnKeyPressed( event -> {
            if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.U) {
                mainCodeArea.replaceText(DataUtils.toggleCase(mainCodeArea.getText().trim()));
            }
        });

    }

    private ContextMenu buildContextMenu(CodeArea codeArea) {
        // Create menu items for the context menu and add them to the ArrayList
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Cut"));
        menuItems.add(new MenuItem("Copy"));
        menuItems.add(new MenuItem("Paste"));
        menuItems.add(new MenuItem("Select All"));
        menuItems.add(new MenuItem("Undo"));
        menuItems.add(new MenuItem("Redo"));

        for (MenuItem item : menuItems) {
            item.setStyle(CONTEXT_FONT);
        }

        // Set actions for menu items
        menuItems.get(0).setOnAction(e -> codeArea.cut());
        menuItems.get(1).setOnAction(e -> codeArea.copy());
        menuItems.get(2).setOnAction(e -> {
            codeArea.paste();
            textformatPause.playFromStart();
        });
        menuItems.get(3).setOnAction(e -> codeArea.selectAll());
        menuItems.get(4).setOnAction(e -> codeArea.undo());
        menuItems.get(5).setOnAction(e -> codeArea.redo());

        // Add menu items from the ArrayList to the context menu
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(menuItems);
        return contextMenu;
    }

//    private void handleTextAreaChanged() {
//        // This method will be called after the specified pause duration
//        String rawText = mainCodeArea.getText();
//        if (rawText.isEmpty()){
//            System.out.println("No data detected.");
//            return;
//        }
//        parsedCodeArea.clear();
//        formatText(rawText);
//    }

    private void handleTextAreaChanged() {
        // This method will be called after the specified pause duration
        String rawText = mainCodeArea.getText();
        if (rawText.isEmpty()){
            System.out.println("No data detected.");
            return;
        }
        parsedCodeArea.clear();

        // Check if the data looks like JSON
        if (DataUtils.isXML(rawText)) {
            String json = DataUtils.parseXmlToPretty(rawText);
            System.out.println("Detected JSON data.");

            Platform.runLater(()->{
                infoLabel.setText("XML Data Detected");
                parsedCodeArea.clear();
                parsedCodeArea.replaceText(json);
                TEMP_DATA.setXmlText(json);
            });
            return;
        }

        Platform.runLater(textformatPause::playFromStart);
    }

    private void formatText(String rawText) {
        if (!rawText.isEmpty()) {
            if (DataUtils.isXML(rawText)){
                String xml = DataUtils.parseXmlToPretty(rawText);
                Platform.runLater(() ->{
                    infoLabel.setText("XML Data Detected");
                    parsedCodeArea.replaceText(xml);
                    TEMP_DATA.setXmlText(xml);
                    handleTextAreaChanged();
                });
            }
            else {
                infoLabel.setText("Unsupported data format");
                System.out.println("Unsupported data format.");
            }
        }
    }

    private static StyleSpans<Collection<String>> computeXmlHighlighting(String text) {
        Matcher matcher = XML_TAG.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            if(matcher.group("COMMENT") != null) {
                spansBuilder.add(Collections.singleton("comment"), matcher.end() - matcher.start());
            }
            else {
                if(matcher.group("ELEMENT") != null) {
                    String attributesText = matcher.group(GROUP_ATTRIBUTES_SECTION);

                    spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_OPEN_BRACKET) - matcher.start(GROUP_OPEN_BRACKET));
                    spansBuilder.add(Collections.singleton("anytag"), matcher.end(GROUP_ELEMENT_NAME) - matcher.end(GROUP_OPEN_BRACKET));

                    if(!attributesText.isEmpty()) {

                        lastKwEnd = 0;

                        Matcher amatcher = ATTRIBUTES.matcher(attributesText);
                        while(amatcher.find()) {
                            spansBuilder.add(Collections.emptyList(), amatcher.start() - lastKwEnd);
                            spansBuilder.add(Collections.singleton("attribute"), amatcher.end(GROUP_ATTRIBUTE_NAME) - amatcher.start(GROUP_ATTRIBUTE_NAME));
                            spansBuilder.add(Collections.singleton("tagmark"), amatcher.end(GROUP_EQUAL_SYMBOL) - amatcher.end(GROUP_ATTRIBUTE_NAME));
                            spansBuilder.add(Collections.singleton("avalue"), amatcher.end(GROUP_ATTRIBUTE_VALUE) - amatcher.end(GROUP_EQUAL_SYMBOL));
                            lastKwEnd = amatcher.end();
                        }
                        if(attributesText.length() > lastKwEnd)
                            spansBuilder.add(Collections.emptyList(), attributesText.length() - lastKwEnd);
                    }

                    lastKwEnd = matcher.end(GROUP_ATTRIBUTES_SECTION);

                    spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_CLOSE_BRACKET) - lastKwEnd);
                }
            }
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }


    @FXML
    protected void minifyButtonClick(){
        String rawJson = mainCodeArea.getText();
        if (!rawJson.isEmpty()) {
            System.out.println("Minify clicked: "+rawJson);
            String parsed = DataUtils.minifyText(rawJson);
            parsedCodeArea.replaceText(parsed);
        }
    }

    private void showErrorAlert(String errorData){
        java.awt.Toolkit.getDefaultToolkit().beep();
        Platform.runLater(() ->{
            Alert alert = new CustomAlert(Alert.AlertType.ERROR);
            //alert.getDialogPane().getStylesheets().add("style.css");
            alert.setTitle("Error");
            alert.setHeaderText("Error: "+errorData);
            alert.setContentText("An Unexpected Error Has Occurred");
            alert.showAndWait();
        });
    }

    protected void copyToClipboard(String data){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(data);
        clipboard.setContent(content);
        System.out.println("String copied to clipboard: " + content);
    }

    public void closeApp(ActionEvent actionEvent) {

        Platform.runLater(() ->{
            Alert alert = new CustomAlert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Close Application");
            alert.setContentText("Are you sure you want to close the application?");

            // Customize the buttons in the alert
            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No");

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);


            // Show and wait for the user's choice
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == buttonTypeYes) {
                    // User clicked "Yes," close the application
                    Platform.exit();
                } else if (buttonType == buttonTypeNo) {
                    // User clicked "No," do nothing and close the dialog
                    alert.close();
                }
            });
        });


    }
}