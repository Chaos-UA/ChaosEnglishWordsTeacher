package com.chaos.english.word.teacher;


import javafx.application.Platform;
import javafx.concurrent.Task;

import java.awt.*;

public class ProgramController {
    private static StageWordList stageWordList = null;
    private static StageSetting stageSettings = null;

    private static volatile int workerDelayBetweenShowNextWordInMinutes = FileOperationsSetting.DEFAULT_DELAY_IN_MINUTES_TO_SHOW_WORD;

    private static StageWord stageWord = null;

    public static final SystemTray SYSTEM_TRAY = new SystemTray();

    private static final Thread threadWordWorker = new Thread(new WordWorker());

    static {
        threadWordWorker.setDaemon(true);
        threadWordWorker.start();
        try {
            FileOperationsSetting.loadPropertiesFromFile();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (FileOperationsWord.class) {
                    System.out.println("ChaosEnglishWordsTeacher exited");
                }
            }
        }));
        try {
            FileOperationsWord.makeBackup();
            SYSTEM_TRAY.displayMessage(null, String.format("There is %d words",FileOperationsWord.getWordCount()), TrayIcon.MessageType.INFO);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void setWorkerDelayBetweenShowNextWordInMinutes(int minutes) {
        if (workerDelayBetweenShowNextWordInMinutes != minutes) {
            workerDelayBetweenShowNextWordInMinutes = minutes;
            threadWordWorker.interrupt();
        }
    }

    public static int getWorkerDelayBetweenShowNextWordInMinutes() {return workerDelayBetweenShowNextWordInMinutes;}

    public static synchronized void showWord(final String words) {
        if (stageWord == null) {
            try {
                stageWord = new StageWord(words);
                stageWord.showAndWait();
            }
            finally {
                stageWord = null;
                System.gc();
            }
        }
        else {
            stageWord.show();
            stageWord.toFront();
            stageWord.playWord();
        }
    }

    public static void showRandomWord() {
        try {
            String word = FileOperationsWord.getRandomWord();
            if (word == null) {
                SYSTEM_TRAY.displayMessage(null, "List of words is empty! Nothing to show", TrayIcon.MessageType.ERROR);
                return;
            }
            showWord(word);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSettings() {
        if (stageSettings == null) {
            try {
                stageSettings = new StageSetting();
                stageSettings.showAndWait();
            }
            finally {
                stageSettings = null;
                System.gc();
            }
        }
        else {
            stageSettings.show();
            stageSettings.toFront();
        }
    }

    public static void showWordList() {
        if (stageWordList == null) {
            try {
                stageWordList = new StageWordList();
                stageWordList.showAndWait();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                stageWordList = null;
                System.gc();
            }
        }
        else {
            stageWordList.show();
            stageWordList.toFront();
        }
    }

    public static void deleteWord(String word) {
        try {
            FileOperationsWord.deleteWord(word);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static class WordWorker extends Task {

        @Override protected Object call() throws Exception {
            while (true) {
                try {
                    Thread.sleep(workerDelayBetweenShowNextWordInMinutes * 60 * 1000); // to milliseconds
                    System.gc();
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            showRandomWord();
                        }
                    });
                }
                catch (Exception ex) {

                }
            }
        }
    }

    public static void moveWordToRemembered(String word) {
        try {
            FileOperationsWord.moveWordToRemembered(word);
        } catch (Exception e) {
            SYSTEM_TRAY.displayMessage(null, e.getLocalizedMessage(), TrayIcon.MessageType.ERROR);
            e.printStackTrace();
        }
    }

    public static void moveWordToNotRemembered(String word) {
        try {
            FileOperationsWord.moveWordToNotRemembered(word);
        }
        catch (Exception e) {
            SYSTEM_TRAY.displayMessage(null, e.getLocalizedMessage(), TrayIcon.MessageType.ERROR);
            e.printStackTrace();
        }
    }
}