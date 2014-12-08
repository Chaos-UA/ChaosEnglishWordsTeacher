package com.chaos.english.word.teacher;

import com.chaos.english.word.teacher.ChaosComponents.ChaosTrayIcon;
import com.chaos.english.word.teacher.images.Images;
import javafx.application.Platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SystemTray extends ChaosTrayIcon implements ActionListener {
    public static final String TRAY_TITLE = "Chaos english worlds teacher";
    private final JMenuItem btnShowWordImmediately = new JMenuItem("Show word immediately");
    private final JMenuItem btnShowWordList = new JMenuItem("List of words");
    private final JMenuItem setting = new JMenuItem("Settings");
    private final JMenuItem exitProgram = new JMenuItem("Exit");

    public SystemTray() {
        super(Images.getScaledImageIcon(java.awt.SystemTray.getSystemTray().getTrayIconSize().width, java.awt.SystemTray.getSystemTray().getTrayIconSize().height, Images.MAIN_64x64.getImage()).getImage());
        this.setJPopupMenu(new TrayJPopUpMenu());
        this.displayMessage(null, "Chaos english worlds teacher started", MessageType.INFO);
        initActions();
        try {
            java.awt.SystemTray.getSystemTray().add(this);
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(null,e.getLocalizedMessage());
            System.exit(0);
        }
    }



    /**
     * @param caption if null then standart AMS caption
     */
    @Override public void displayMessage(String caption, String text, MessageType messageType) {
        super.displayMessage(caption == null ? TRAY_TITLE : caption, text, messageType);
    }

    /***************************************************/
    class TrayJPopUpMenu extends JPopupMenu {
        public TrayJPopUpMenu() {
            this.add(btnShowWordImmediately);
            this.add(btnShowWordList);
            this.add(setting);
            this.add(exitProgram);

        }
    }
    /***************************************************/
    private void initActions() {
        btnShowWordImmediately.addActionListener(this);
        btnShowWordList.addActionListener(this);
        setting.addActionListener(this);
        exitProgram.addActionListener(this);
        this.addActionListener(this);
    }

    @Override public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == setting) {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    ProgramController.showSettings();
                }
            });
        }
        else if (e.getSource() == btnShowWordList) {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    ProgramController.showWordList();
                }
            });
        }
        else if (obj == exitProgram) {
            System.exit(0);
        }
        else if (obj == btnShowWordImmediately || obj == this) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ProgramController.showRandomWord();
                }
            });
        }
    }

}
