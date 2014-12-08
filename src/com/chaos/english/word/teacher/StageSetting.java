package com.chaos.english.word.teacher;


import com.chaos.english.word.teacher.images.Images;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.awt.*;

public class StageSetting extends Stage implements EventHandler<ActionEvent> {
    private final TextField tfChanceToShowNotYetShownWord = new TextField(""+FileOperationsWord.CHANCE_TO_SHOW_NOT_YET_SHOWN_WORD);
    private final TextField tfChanceToShowNotYetRememberedWord = new TextField(""+FileOperationsWord.CHANCE_TO_SHOW_NOT_YET_REMEMBERED_WORD);
    private final TextField tfDelayToShow = new TextField(""+ProgramController.getWorkerDelayBetweenShowNextWordInMinutes());
    private final Button btnApply = new Button("Apply");
    private final Button btnCancel = new Button("Cancel");

    private final PanelMain pnlMain = new PanelMain();

    public StageSetting() {
        this.setTitle("Settings");
        this.getIcons().add(Images.FX_MAIN_64x64);
        final javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
        final BorderPane panelRoot = new BorderPane();
        panelRoot.setPadding(new Insets(5));
        scrollPane.setContent(pnlMain);
        scrollPane.setStyle("-fx-background-color: rgba(0,0,0,0);");
        scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override public void changed(ObservableValue<? extends Bounds> observableValue, Bounds oldBounds, Bounds newBounds) {
                scrollPane.setFitToWidth(newBounds.getWidth() > panelRoot.prefWidth(Region.USE_PREF_SIZE));
                scrollPane.setFitToHeight(newBounds.getHeight() > panelRoot.prefHeight(Region.USE_PREF_SIZE));
            }
        });
        panelRoot.setCenter(scrollPane);
        HBox hBox = new HBox(5);
        hBox.setPadding(new Insets(5,0,0,0));
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(btnApply, btnCancel);
        panelRoot.setBottom(hBox);
        panelRoot.getStyleClass().add("background");
        Scene myScene = new Scene(panelRoot);
        myScene.getStylesheets().add("com/chaos/english/word/teacher/global.css");
        this.setScene(myScene);
        this.sizeToScene();
        this.initActions();
        this.setMinWidth(640); this.setMinWidth(480);
        this.centerOnScreen();
    }

    public class PanelMain extends HBox {

        PanelMain() {

        }
    }

    private void initActions() {
        btnApply.setOnAction(this);
        btnCancel.setOnAction(this);
    }

    @Override public void handle(ActionEvent actionEvent) {
        try {
            if (actionEvent.getSource() == btnApply) {
                FileOperationsSetting.savePropertiesToFile(
                        Integer.parseInt(tfChanceToShowNotYetShownWord.getText().trim()),
                        Integer.parseInt(tfChanceToShowNotYetRememberedWord.getText().trim()),
                        Integer.parseInt(tfDelayToShow.getText().trim()));
                FileOperationsSetting.loadPropertiesFromFile();
                hide();
            }
            else if (actionEvent.getSource() == btnCancel) {
                hide();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            ProgramController.SYSTEM_TRAY.displayMessage(null, e.getLocalizedMessage(), TrayIcon.MessageType.ERROR);
        }
    }
}
