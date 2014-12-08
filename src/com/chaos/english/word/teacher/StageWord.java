package com.chaos.english.word.teacher;

import com.chaos.english.word.teacher.images.Images;
import com.gtranslate.Audio;
import com.gtranslate.Language;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.InputStream;

public class StageWord extends Stage implements EventHandler<ActionEvent> {
    public final Button btnWord = new Button();
    public final PanelAnswer pnlAnswer = new PanelAnswer();
    public final WebView webView = new WebView();
    private PanelMain pnlMain = new PanelMain();
    private boolean isWordAlreadyClicked = false;

    public StageWord(String word) {
        webView.setPrefSize(0,0);
        this.btnWord.setTooltip(new Tooltip("Click to see translation"));
        this.btnWord.setText(word);
        this.setTitle("Chaos english word teacher");
        this.getIcons().add(Images.FX_MAIN_64x64);
        Scene myScene = new Scene(pnlMain);
        myScene.getStylesheets().add("com/chaos/english/word/teacher/global.css");
        this.setScene(myScene);
        this.initActions();
        this.sizeToScene();
        Task task = new Task() {
            @Override protected Object call() throws Exception {
                try {
                    Thread.sleep(1000);
                    playWord();
                }
                catch (Exception ex) {
                    ProgramController.SYSTEM_TRAY.displayMessage(null,ex.getLocalizedMessage(), TrayIcon.MessageType.ERROR);
                }
                return null;
            }
        };
        new Thread(task).start();
        webView.getEngine().load("http://translate.google.com.ua/#en/uk/" + btnWord.getText());
    }

    @Override public void showAndWait() {
        try {
            super.showAndWait();
        }
        finally {
            webView.getEngine().load(null);
        }
    }

    private void initActions() {
        btnWord.setOnAction(this);
        pnlAnswer.btnRemindMeLater.setOnAction(this);
        pnlAnswer.btnIRememberIt.setOnAction(this);
        pnlAnswer.btnDeleteCompletely.setOnAction(this);
    }

    @Override public void handle(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnWord) {
            if (!isWordAlreadyClicked) {
                isWordAlreadyClicked = true;
                double newWidth = 800;
                double newHeight = 600;
                double x = this.getX();
                double y = this.getY();
                if (this.getWidth() < newWidth) {
                    double width = (newWidth - this.getWidth());
                    this.setX(x - width / 2);
                    this.setWidth(newWidth);
                }
                if (this.getHeight() < newHeight) {
                    double height = (newHeight - this.getHeight()) / 2;
                    this.setY(y - height / 2);
                    this.setHeight(newHeight);
                }
            }
            playWord();
        }
        else if (actionEvent.getSource() == pnlAnswer.btnIRememberIt) {
            ProgramController.moveWordToRemembered(btnWord.getText());
            this.hide();
        }
        else if (actionEvent.getSource() == pnlAnswer.btnRemindMeLater) {
            ProgramController.moveWordToNotRemembered(btnWord.getText());
            this.hide();
        }
        else if (actionEvent.getSource() == pnlAnswer.btnDeleteCompletely) {
            ProgramController.deleteWord(btnWord.getText());
            this.hide();
        }

    }

    public class PanelAnswer extends HBox {
        public final Button btnRemindMeLater = new Button("Remind me later");
        public final Button btnIRememberIt = new Button("I get it");
        public final Button btnDeleteCompletely = new Button("Delete word");

        PanelAnswer() {
            super(5);
            this.setAlignment(Pos.CENTER);
            btnDeleteCompletely.setMaxWidth(Double.MAX_VALUE);
            btnRemindMeLater.setTooltip(new Tooltip("The word will be shown again randomly."));
            btnIRememberIt.setTooltip(new Tooltip("The word will be shown again randomly. But chance is very low"));
            btnDeleteCompletely.setTooltip(new Tooltip("Word will be deleted completely and never be shown again"));
            this.getChildren().addAll(btnRemindMeLater, btnIRememberIt, btnDeleteCompletely);

        }
    }

    class PanelMain extends GridPane {
        PanelMain() {
            this.getStyleClass().add("background");
            this.setPadding(new Insets(5));
            this.setHgap(5); this.setVgap(5);
            int vIndex = 0;
           // GridPane.set
            Label lbl = new Label("Word");
            lbl.setMinWidth(lbl.prefWidth(Region.USE_PREF_SIZE));
            lbl.minWidthProperty().bind(lbl.widthProperty());
            GridPane.setConstraints(lbl, 0,vIndex,1,1,HPos.LEFT, VPos.CENTER);
            this.getChildren().add(lbl);
            GridPane.setConstraints(btnWord,1,vIndex++,Integer.MAX_VALUE,1,HPos.CENTER, VPos.CENTER,Priority.SOMETIMES, Priority.NEVER);
            btnWord.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            this.getChildren().add(btnWord);
          //  lbl = new Label("Translation of the above Word");
          //  GridPane.setConstraints(lbl,0,vIndex++,Integer.MAX_VALUE,1,HPos.CENTER,VPos.CENTER);
          //  this.getChildren().add(lbl);
            //
            GridPane.setConstraints(webView,0,vIndex++,Integer.MAX_VALUE, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
            this.getChildren().add(webView);
            //
            GridPane.setConstraints(pnlAnswer,0,vIndex++,Integer.MAX_VALUE,1);
            this.getChildren().add(pnlAnswer);

        }
    }

    public void playWord() {
        Task task = new Task() {
            @Override protected Object call() throws Exception {
                try {
                    Audio audio = Audio.getInstance();
                    InputStream isTextToSpeech = audio.getAudio(btnWord.getText(), Language.ENGLISH);
                    audio.play(isTextToSpeech);
                    isTextToSpeech.close();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    ProgramController.SYSTEM_TRAY.displayMessage(null,ex.getLocalizedMessage(), TrayIcon.MessageType.ERROR);
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
