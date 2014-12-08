package com.chaos.english.word.teacher;


import com.chaos.english.word.teacher.images.Images;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.jdom2.JDOMException;

import java.io.IOException;

public class StageWordList extends Stage implements EventHandler<ActionEvent> {

    private final Button btnDeleteWord = new Button("Delete word");


    private final PanelAddWord pnlAddWord = new PanelAddWord();
    private final PanelMain pnlMain = new PanelMain();

    public StageWordList() throws JDOMException, IOException {
        this.setTitle("List of words (" + FileOperationsWord.getWordCount() + " words)");
        this.getIcons().add(Images.FX_MAIN_64x64);

        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(pnlMain);

        scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observableValue, Bounds oldBounds, Bounds newBounds) {
                scrollPane.setFitToWidth(newBounds.getWidth() > pnlMain.prefWidth(Region.USE_PREF_SIZE));
                scrollPane.setFitToHeight(newBounds.getHeight() > pnlMain.prefHeight(Region.USE_PREF_SIZE));
            }
        });

        scrollPane.getStyleClass().add("background");
        Scene myScene = new Scene(scrollPane);
        myScene.getStylesheets().add("com/chaos/english/word/teacher/global.css");
        this.setScene(myScene);
        this.sizeToScene();
        this.initActions();
        this.setMinWidth(640); this.setMinWidth(480);
        this.centerOnScreen();
    }



    public class PanelMain extends GridPane {
        PanelMain() {

            this.setPadding(new Insets(5));
            this.setVgap(5); this.setHgap(5);
            int vIndex = 0;
            GridPane.setConstraints(pnlAddWord, 0,vIndex++,Integer.MAX_VALUE,1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
            this.getChildren().addAll(pnlAddWord);
            Label lbl = new Label("Дописати потім видалення / перегляд");
            lbl.setTextAlignment(TextAlignment.CENTER);
            GridPane.setConstraints(lbl, 0,vIndex++,Integer.MAX_VALUE, Integer.MAX_VALUE, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
            this.getChildren().add(lbl);
        }
    }

    public class PanelAddWord extends HBox {
        public final Button btnAddNewWord = new Button("Add word");
        public final TextField tfNewWord = new TextField();

        PanelAddWord() {
            super(5);
            this.setAlignment(Pos.CENTER);
            HBox.setHgrow(tfNewWord, Priority.ALWAYS);
            tfNewWord.setPromptText("New word");
            tfNewWord.setMaxWidth(Double.MAX_VALUE);
            this.getChildren().addAll(new Label("New word"), tfNewWord, btnAddNewWord);
        }
    }

    private void initActions() {
        pnlAddWord.btnAddNewWord.setOnAction(this);
        btnDeleteWord.setOnAction(this);
    }

    @Override public void handle(ActionEvent actionEvent) {
        try {

            if (actionEvent.getSource() == pnlAddWord.btnAddNewWord) {
                FileOperationsWord.addNewWord(pnlAddWord.tfNewWord.getText());
            }
            else if (actionEvent.getSource() == btnDeleteWord) {

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
