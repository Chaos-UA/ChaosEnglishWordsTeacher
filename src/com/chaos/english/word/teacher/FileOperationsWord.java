package com.chaos.english.word.teacher;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class FileOperationsWord {
    public static volatile int CHANCE_TO_SHOW_NOT_YET_SHOWN_WORD = FileOperationsSetting.DEFAULT_CHANCE_TO_SHOW_NOT_YET_SHOWN_WORD;  // 90/10
    public static volatile int CHANCE_TO_SHOW_NOT_YET_REMEMBERED_WORD = FileOperationsSetting.DEFAULT_CHANCE_TO_SHOW_NOT_YET_REMEMBERED_WORD; // 90/10

   // private static final File FILE_WORD_LIST = new File("/home/chaos/Desktop/words.xml");//new File(FileOperationsWord.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "/words.xml");
    private static final String STR_ROOT = "words";
    private static final String STR_REMEMBERED_WORDS = "remembered";
    private static final String STR_NOT_REMEMBERED_WORDS = "notRemembered";
    private static final String STR_NOT_YET_SHOWN = "notYetShown";
    private static final String STR_RECENTLY_SHOWN = "recentlyShown";
    private static final String STR_WORD = "word";
    private static final String STR_WORD_NAME = "name";

    static {
        try {
            if (PathProgram.FILE_WORDS.createNewFile()) {
              //  SAXBuilder saxBuilder = new SAXBuilder();
              //  Document doc = saxBuilder.build(FILE_WORD_LIST);
                Element elemRoot = new Element(STR_ROOT);
                Element elemRememberedWords = new Element(STR_REMEMBERED_WORDS);
                Element elemNotRememberedWords = new Element(STR_NOT_REMEMBERED_WORDS);
                elemRoot.addContent(elemNotRememberedWords);
                elemRoot.addContent(elemRememberedWords);
                elemNotRememberedWords.addContent(new Element(STR_RECENTLY_SHOWN));
                elemNotRememberedWords.addContent(new Element(STR_NOT_YET_SHOWN));
                elemRememberedWords.addContent(new Element(STR_RECENTLY_SHOWN));
                elemRememberedWords.addContent(new Element(STR_NOT_YET_SHOWN));
                saveDocumentWords(new Document(elemRoot));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), "Error message", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public static synchronized void moveWordToNotRemembered(String word) throws IOException {
        Element elemWord = getWordByName(word);
        if (elemWord == null) {
            IllegalArgumentException error = new IllegalArgumentException("Can't find \""+word+"\"");
            error.printStackTrace();
            ProgramController.SYSTEM_TRAY.displayMessage(null,error.getLocalizedMessage(), TrayIcon.MessageType.ERROR);
        }
        Document doc = elemWord.getDocument();
        Element elemRoot = doc.getRootElement();
        elemWord.getParentElement().removeContent(elemWord);
        Element elemNotRememberedWords = elemRoot.getChild(STR_NOT_REMEMBERED_WORDS);
        Element elemRecentlyShown = elemNotRememberedWords.getChild(STR_RECENTLY_SHOWN);
        elemRecentlyShown.addContent(elemWord);
        refreshWordsIfNeeded(doc);
        saveDocumentWords(doc);
    }

    private static synchronized void refreshWordsIfNeeded(Document doc) {
        Element elemROOT = doc.getRootElement();
        Element elemRememberedWords = elemROOT.getChild(STR_REMEMBERED_WORDS);
        Element elemNotRememberedWords = elemROOT.getChild(STR_NOT_REMEMBERED_WORDS);
        // NOT YET SHOWN
        Element elemNotYetShown = elemRememberedWords.getChild(STR_NOT_YET_SHOWN);
        Element elemRecentlyShown = elemRememberedWords.getChild(STR_RECENTLY_SHOWN);
        List<Element> lstNotYetShown = elemNotYetShown.getChildren(STR_WORD);
        if (lstNotYetShown.size() == 0) {
            elemNotYetShown.setContent(elemRecentlyShown.removeContent());
        }
        // ALREADY SHOWN
        elemNotYetShown = elemNotRememberedWords.getChild(STR_NOT_YET_SHOWN);
        elemRecentlyShown = elemNotRememberedWords.getChild(STR_RECENTLY_SHOWN);
        lstNotYetShown = elemNotYetShown.getChildren(STR_WORD);
        if (lstNotYetShown.size() == 0) {
            elemNotYetShown.setContent(elemRecentlyShown.removeContent());
        }
    }

    public static synchronized void moveWordToRemembered(String word) throws IOException {
        Element elemWord = getWordByName(word);
        if (elemWord == null) {
            IllegalArgumentException error = new IllegalArgumentException("Can't find \""+word+"\"");
            error.printStackTrace();
            ProgramController.SYSTEM_TRAY.displayMessage(null,error.getLocalizedMessage(), TrayIcon.MessageType.ERROR);
        }
        Document doc = elemWord.getDocument();
        Element elemRoot = doc.getRootElement();
        elemWord.getParentElement().removeContent(elemWord);
        Element elemRememberedWords = elemRoot.getChild(STR_REMEMBERED_WORDS);
        Element elemRecentlyShown = elemRememberedWords.getChild(STR_RECENTLY_SHOWN);
        elemRecentlyShown.addContent(elemWord);
        refreshWordsIfNeeded(doc);
        saveDocumentWords(doc);
    }

    /**
     * @return null if list is empty
     */
    public static synchronized String getRandomWord() throws JDOMException, IOException {
        class ClosedClass {
            String getRandomWord(List<Element> lstNotYetShownWords, List<Element> lstRecentlyShownWords) {
                if (lstNotYetShownWords.size() == 0 && lstRecentlyShownWords.size() == 0) throw new IllegalArgumentException("Fatal error. Both == 0");
                if (lstNotYetShownWords.size() == 0) {
                    return getRandomWordFromList(lstRecentlyShownWords);
                }
                else if (lstRecentlyShownWords.size() == 0) {
                    return getRandomWordFromList(lstNotYetShownWords);
                }
                else {
                    int random = (int)Math.round(Math.random() * 100);

                    if (random <= CHANCE_TO_SHOW_NOT_YET_SHOWN_WORD) return getRandomWordFromList(lstNotYetShownWords);
                    else return getRandomWordFromList(lstRecentlyShownWords);
                }
            }

            String getRandomWordFromList(List<Element> lstWords) {
                int random = (int)Math.round(Math.random() * (lstWords.size()-1));
                return lstWords.get(random).getAttributeValue(STR_WORD_NAME);
            }
        }
        ClosedClass closedClass = new ClosedClass();
        Document doc = getDocumentWords();
        Element elemRoot = doc.getRootElement();

        Element elemNotRememberedWords = elemRoot.getChild(STR_NOT_REMEMBERED_WORDS);
        List<Element> lstNotRememberedNotYetShown = elemNotRememberedWords.getChild(STR_NOT_YET_SHOWN).getChildren(STR_WORD);
        List<Element> lstNotRememberedRecentlyShown = elemNotRememberedWords.getChild(STR_RECENTLY_SHOWN).getChildren(STR_WORD);
        Element elemRememberedWords = elemRoot.getChild(STR_REMEMBERED_WORDS);
        List<Element> lstRememberedNotYetShown = elemRememberedWords.getChild(STR_NOT_YET_SHOWN).getChildren(STR_WORD);
        List<Element> lstRememberedRecentlyShown = elemRememberedWords.getChild(STR_RECENTLY_SHOWN).getChildren(STR_WORD);
        if (lstNotRememberedNotYetShown.size() == 0 && lstNotRememberedRecentlyShown.size() == 0 &&
                lstRememberedNotYetShown.size() == 0 && lstRememberedRecentlyShown.size() == 0) {
            return null;
        }
        else if (lstNotRememberedNotYetShown.size() == 0 && lstNotRememberedRecentlyShown.size() == 0) {
            return closedClass.getRandomWord(lstRememberedNotYetShown, lstRememberedRecentlyShown);
        }
        else if (lstRememberedNotYetShown.size() == 0 && lstRememberedRecentlyShown.size() == 0) {
            return closedClass.getRandomWord(lstNotRememberedNotYetShown, lstNotRememberedRecentlyShown);
        }
        else {
            int random = (int)Math.round(Math.random() * 100);
            if (random <= CHANCE_TO_SHOW_NOT_YET_REMEMBERED_WORD) return closedClass.getRandomWord(lstNotRememberedNotYetShown, lstNotRememberedRecentlyShown);
            else return closedClass.getRandomWord(lstRememberedNotYetShown, lstRememberedRecentlyShown);
        }
    }

    public static synchronized void addNewWord(String newWord) throws JDOMException, IOException {
        newWord = newWord.trim();
        if (newWord.isEmpty()) {
            ProgramController.SYSTEM_TRAY.displayMessage(null, "Word can't be empty", TrayIcon.MessageType.ERROR);
            return;
        }
        Element alreadyExists = getWordByName(newWord);
        if (alreadyExists != null) {
            ProgramController.SYSTEM_TRAY.displayMessage(null, "Word \""+alreadyExists.getAttributeValue(STR_WORD_NAME)+"\" already added", TrayIcon.MessageType.ERROR);
            return;
        }
        Element elemWord = new Element(STR_WORD);
        elemWord.setAttribute(STR_WORD_NAME, newWord);
        Document doc = getDocumentWords();
        doc.getRootElement().getChild(STR_NOT_REMEMBERED_WORDS).getChild(STR_NOT_YET_SHOWN).addContent(elemWord);
        saveDocumentWords(doc);
    }

    public static synchronized int getWordCount() throws JDOMException, IOException {
        Document doc = getDocumentWords();
        Element root = doc.getRootElement();
        Element elem = root.getChild(STR_NOT_REMEMBERED_WORDS);
        int wordCount = elem.getChild(STR_NOT_YET_SHOWN).getChildren(STR_WORD).size();
        wordCount += elem.getChild(STR_RECENTLY_SHOWN).getChildren(STR_WORD).size();
        elem = root.getChild(STR_REMEMBERED_WORDS);
        wordCount += elem.getChild(STR_NOT_YET_SHOWN).getChildren(STR_WORD).size();
        wordCount += elem.getChild(STR_RECENTLY_SHOWN).getChildren(STR_WORD).size();
        return wordCount;
    }

    private static synchronized void saveDocumentWords(Document doc) throws IOException {
        if (doc == null) throw new NullPointerException();
        OutputStream outputStream = new FileOutputStream(PathProgram.FILE_WORDS);
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(doc, outputStream);
        outputStream.close();
    }

    private static synchronized Document getDocumentWords() throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(PathProgram.FILE_WORDS);
    }

    /**
     * @param word to find in file. Case sensitivity ignored
     * @return null if no such word in list
     */
    public static synchronized Element getWordByName(String word) {
        try {
            final String wordToFind = word.trim().toLowerCase();

            Document doc = getDocumentWords();
            Element elemRoot = doc.getRootElement();
            class ClosedClass {
                Element getWord(List<Element> lstWords) {
                    for (Element elemWord: lstWords) {
                        if (elemWord.getAttributeValue(STR_WORD_NAME).toLowerCase().equals(wordToFind)) {
                            return elemWord;
                        }
                    }
                    return null;
                }
            }

            ClosedClass closedClass = new ClosedClass();
            Element elemNotRememberedWords = elemRoot.getChild(STR_NOT_REMEMBERED_WORDS);
            Element result = closedClass.getWord(elemNotRememberedWords.getChild(STR_NOT_YET_SHOWN).getChildren(STR_WORD));
            if (result != null) return result;
            result = closedClass.getWord(elemNotRememberedWords.getChild(STR_RECENTLY_SHOWN).getChildren(STR_WORD));
            if (result != null) return result;
            Element elemRememberedWords = elemRoot.getChild(STR_REMEMBERED_WORDS);
            result = closedClass.getWord(elemRememberedWords.getChild(STR_NOT_YET_SHOWN).getChildren(STR_WORD));
            if (result != null) return result;
            result = closedClass.getWord(elemRememberedWords.getChild(STR_RECENTLY_SHOWN).getChildren(STR_WORD));
            if (result != null) return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            ProgramController.SYSTEM_TRAY.displayMessage(null, ex.getLocalizedMessage(), TrayIcon.MessageType.ERROR);
        }
        return null;
    }

    public static synchronized void deleteWord(String word) throws IOException {
        Element elemWord = getWordByName(word);
        if (elemWord == null) {
            ProgramController.SYSTEM_TRAY.displayMessage(null, "Can't find word \""+word+"\" to delete it", TrayIcon.MessageType.ERROR);
            return;
        }
        Document doc = elemWord.getDocument();
        elemWord.getParent().removeContent(elemWord);
        saveDocumentWords(doc);
    }

    public static synchronized void makeBackup() throws JDOMException, IOException {
        File fBackup = new File(PathProgram.FILE_WORDS.getPath().substring(0, PathProgram.FILE_WORDS.getPath().length() - PathProgram.FILE_WORDS.getName().length()) + "backup-" + PathProgram.FILE_WORDS.getName());
        if (getRandomWord() != null && PathProgram.FILE_WORDS.isFile()) {
            Files.copy(PathProgram.FILE_WORDS.toPath(), fBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        else if (fBackup.isFile()) {
            Files.copy(fBackup.toPath(), PathProgram.FILE_WORDS.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
