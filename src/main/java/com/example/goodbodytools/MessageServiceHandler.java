package com.example.goodbodytools;
import atlantafx.base.controls.Card;
import atlantafx.base.controls.Message;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;

import java.util.ArrayList;
import java.util.List;

public class MessageServiceHandler {
    private static BorderPane messagePane;
    private static List<Message> messages;
    private static VBox messageContainer;
    public MessageServiceHandler(BorderPane messagePane) {
        MessageServiceHandler.messagePane = messagePane;
        messages = new ArrayList<>();
        messageContainer = new VBox();
    }

    public static void addMessage(String title, String desc,  String type) {
        FontIcon icon;
        Message msgToAdd = null;

        if (type.contentEquals("success")){
            icon = new FontIcon(MaterialDesignC.CHECK_CIRCLE_OUTLINE);
            msgToAdd = new Message(title, desc, icon);
            msgToAdd.getStyleClass().add(Styles.SUCCESS);
        }
        if (type.contentEquals("warning")){
            icon = new FontIcon(Material2OutlinedMZ.OUTLINED_FLAG);
            msgToAdd = new Message(title, desc, icon);
            msgToAdd.getStyleClass().add(Styles.WARNING);
        }
        if (type.contentEquals("danger")){
            icon = new FontIcon(Material2OutlinedAL.ERROR_OUTLINE);
            msgToAdd = new Message(title, desc, icon);
            msgToAdd.getStyleClass().add(Styles.DANGER);
        }


        Message finalMsgToAdd = msgToAdd;
        assert msgToAdd != null;
        Message finalMsgToAdd1 = msgToAdd;
        msgToAdd.setOnClose(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), finalMsgToAdd1);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> {
                messages.remove(finalMsgToAdd);
                updateMessagePane();
            });
            fadeOut.play();
            e.consume();
        });

        messages.add(msgToAdd);
        updateMessagePane();
    }

    private static void updateMessagePane() {
        if (messagePane != null) {
            messageContainer.getChildren().clear(); // Clear existing messages
            Card card1 = new Card();
            card1.getStyleClass().add(Styles.ACCENT);

            Button clearMsg = new Button("Clear All");
            clearMsg.setDefaultButton(true);
            clearMsg.setOnAction(e ->{
                messages.clear();
                updateMessagePane();
                e.consume();
            });
            card1.setHeader(new Tile(
                    "Message Handler",
                    ""
            ));
            card1.setBody(clearMsg);

            HBox.setHgrow(card1, Priority.ALWAYS);
            messageContainer.getChildren().add(0,card1);
            // Add messages back to the container
            for (Message message : messages) {
                VBox.setMargin(message, new Insets(5));
                messageContainer.getChildren().add(message);
            }

            ScrollPane scrollPane = new ScrollPane(messageContainer);
            scrollPane.setFitToWidth(true);

            messagePane.setRight(scrollPane);
        }
    }

}
