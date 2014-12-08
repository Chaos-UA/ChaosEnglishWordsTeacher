package com.chaos.english.word.teacher;

import java.io.File;
import java.net.URISyntaxException;

public class PathProgram {
    public static final File JAR_FILE_DIRECTORY;

    static {
        try {
            JAR_FILE_DIRECTORY = new File(PathProgram.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static final File FILE_WORDS = new File(JAR_FILE_DIRECTORY + "/words.xml");
    public static final File FILE_SETTINGS = new File(JAR_FILE_DIRECTORY.getAbsolutePath() + "/settings.ini");


}
