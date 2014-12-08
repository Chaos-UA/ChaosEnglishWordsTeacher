package com.chaos.english.word.teacher;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FileOperationsSetting {
    public static final int DEFAULT_CHANCE_TO_SHOW_NOT_YET_SHOWN_WORD = 90;
    public static final int MIN_CHANCE = 50;
    public static final int MAX_CHANCE = 90;
    public static final int DEFAULT_CHANCE_TO_SHOW_NOT_YET_REMEMBERED_WORD = 90;
    public static final int DEFAULT_DELAY_IN_MINUTES_TO_SHOW_WORD = 20;
    public static final int MIN_DELAY_IN_MINUTES_TO_SHOW_WORD = 1;
    public static final int MAX_DELAY_IN_MINUTES_TO_SHOW_WORD = 300;

    public static synchronized void loadPropertiesFromFile() throws IOException {
        PathProgram.FILE_SETTINGS.createNewFile();
        Properties prop = new Properties();
        FileInputStream inputStream = new FileInputStream(PathProgram.FILE_SETTINGS);
        prop.load(inputStream);
        inputStream.close();
        int chanceToShowNotYetShownWord = Integer.parseInt(prop.getProperty("chanceToShowNotYetShownWord", "" + DEFAULT_CHANCE_TO_SHOW_NOT_YET_SHOWN_WORD));
        int chanceToShowNotYetRememberedWord = Integer.parseInt(prop.getProperty("chanceToShowNotYetRememberedWord", "" + DEFAULT_CHANCE_TO_SHOW_NOT_YET_REMEMBERED_WORD));
        int reshowDelayInMinutes = Integer.parseInt(prop.getProperty("delayToShowRandomWord", ""+DEFAULT_DELAY_IN_MINUTES_TO_SHOW_WORD));
        String errors = "";
        if (chanceToShowNotYetShownWord > MAX_CHANCE || chanceToShowNotYetShownWord < MIN_CHANCE) {
            errors += "Chance to show not yet shown word must be in range "+MIN_CHANCE+"-"+MAX_CHANCE + "\n";
        }
        else {
            FileOperationsWord.CHANCE_TO_SHOW_NOT_YET_SHOWN_WORD = chanceToShowNotYetShownWord;
        }
        if (chanceToShowNotYetRememberedWord > MAX_CHANCE || chanceToShowNotYetRememberedWord < MIN_CHANCE) {
            errors += "Chance to show not yet remembered word must be in range "+MIN_CHANCE+"-"+MAX_CHANCE + "\n";
        }
        else {
            FileOperationsWord.CHANCE_TO_SHOW_NOT_YET_REMEMBERED_WORD = chanceToShowNotYetRememberedWord;
        }
        if (reshowDelayInMinutes > MAX_DELAY_IN_MINUTES_TO_SHOW_WORD || reshowDelayInMinutes < MIN_DELAY_IN_MINUTES_TO_SHOW_WORD) {
            errors += errors += "Delay to show word in minutes must be in range "+MIN_DELAY_IN_MINUTES_TO_SHOW_WORD+"-"+MAX_DELAY_IN_MINUTES_TO_SHOW_WORD + "\n";
        }
        else {
            ProgramController.setWorkerDelayBetweenShowNextWordInMinutes(reshowDelayInMinutes);
        }
        if (!errors.isEmpty()) {
            System.err.println(errors);
            ProgramController.SYSTEM_TRAY.displayMessage(null, errors, TrayIcon.MessageType.ERROR);
        }
    }

    public static String checkForErrors(int chanceToShowNotYetShownWord, int chanceToShowNotYetRememberedWord, int reshowDelayInMinutes) {
        String errors = "";
        if (chanceToShowNotYetShownWord > MAX_CHANCE || chanceToShowNotYetShownWord < MIN_CHANCE) {
            errors += "Chance to show not yet shown word must be in range "+MIN_CHANCE+"-"+MAX_CHANCE + "\n";
        }
        if (chanceToShowNotYetRememberedWord > MAX_CHANCE || chanceToShowNotYetRememberedWord < MIN_CHANCE) {
            errors += "Chance to show not known word must be in range "+MIN_CHANCE+"-"+MAX_CHANCE + "\n";
        }
        if (reshowDelayInMinutes > MAX_DELAY_IN_MINUTES_TO_SHOW_WORD || reshowDelayInMinutes < MIN_DELAY_IN_MINUTES_TO_SHOW_WORD) {
            errors += errors += "Delay to show word in minutes must be in range "+MIN_DELAY_IN_MINUTES_TO_SHOW_WORD+"-"+MAX_DELAY_IN_MINUTES_TO_SHOW_WORD + "\n";
        }
        return errors;
    }

    public static synchronized void savePropertiesToFile(int chanceToShowNotYetShownWord, int chanceToShowNotYetRememberedWord, int reshowDelayInMinutes) throws IOException {
        String error = checkForErrors(chanceToShowNotYetShownWord, chanceToShowNotYetRememberedWord, reshowDelayInMinutes);
        if (!error.isEmpty()) throw new IllegalArgumentException(error);
        Properties prop = new Properties();
        prop.put("chanceToShowNotYetShownWord", ""+chanceToShowNotYetShownWord);
        prop.put("chanceToShowNotYetRememberedWord", ""+chanceToShowNotYetRememberedWord);
        prop.put("delayToShowRandomWord", ""+reshowDelayInMinutes);
        FileOutputStream outputStream = new FileOutputStream(PathProgram.FILE_SETTINGS);
        prop.store(outputStream, "Chaos english words teacher settings");
        outputStream.close();
    }
}
