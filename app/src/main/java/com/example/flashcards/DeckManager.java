package com.example.flashcards;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class DeckManager {
    public static String MAIN_DECKS_FOLDER = "DECKS";
    public static String DECK_EXT = ".deck";
    public static String currentDirectoryPath;

    public static void setCurrentDirectoryPath(String path) {
        currentDirectoryPath = path;
        File file = new File(currentDirectoryPath);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static boolean createNewDeck(String newDeckName, String contents) throws IOException {
        File file = new File(currentDirectoryPath + "/" + newDeckName + DECK_EXT);
        if (!file.exists()) {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(contents.getBytes());
            out.close();

            return true;
        }
        return false;
    }

    public static boolean createNewFolder(String newFolderName) {
        File file = new File(currentDirectoryPath + "/" + newFolderName);
        if (!file.exists()) {
            file.mkdir();
            return true;
        }
        return false;
    }

    public static void renameDeck() {

    }

    public static void renameFolder() {

    }

    public static void moveItem() {

    }

    public static String[] getCurrentDirectoryList() {
        File file = new File(currentDirectoryPath);
        return file.list();
    }

    public static String[] getDeckNamesInCurrentDirectory() {
        File file = new File(currentDirectoryPath);
        String[] list = file.list();

        List<String> deckNames = new LinkedList<>();
        for (String item : list) {
            if (item.contains(DECK_EXT)) {
                deckNames.add(item.substring(0, item.length()-DECK_EXT.length()));
            }
        }
        String[] deckNamesArr = new String[deckNames.size()];
        deckNames.toArray(deckNamesArr);
        return deckNamesArr;
    }

    public static String incrementFileName(String[] existingFileNames, String newFileName) {
        for (int i = 0; i < existingFileNames.length; i++) {
            String oldFileName;
            do {
                oldFileName = newFileName;
                newFileName = nameIncrementer(existingFileNames, newFileName);
            } while ( !oldFileName.equals(newFileName) );
        }
        return newFileName;
    }

    private static String nameIncrementer(String[] existingFileNames, String newFileName) {
        for (int i = 0; i < existingFileNames.length; i++) {
            if (newFileName.equals(existingFileNames[i])) {
                String[] splittedFileName = newFileName.split(" ");
                String lastSplit = splittedFileName[splittedFileName.length-1];
                if (splittedFileName.length == 1 || !isThisStringAnInteger(lastSplit)) {
                    newFileName = newFileName + " 1";
                } else {
                    String fileNameWithoutNumber = concatStringArray(splittedFileName, " ", 0, splittedFileName.length - 1);
                    String incrementedCount = String.valueOf(Integer.valueOf(lastSplit) + 1);
                    newFileName =  fileNameWithoutNumber + " " + incrementedCount;
                }
            }
        }
        return newFileName;
    }

    private static String concatStringArray(String[] args, String str, int start, int end) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = start; i < end; i++) {
            stringBuilder.append(args[i]);
            if (i + 1 != end) {
                stringBuilder.append(str);
            }
        }
        return stringBuilder.toString();
    }

    private static boolean isThisStringAnInteger(String str) {
        return str.matches("(\\d+)");
    }

    private static boolean isThisADeckFile(String str) {
        return str.matches("(.+)" + DECK_EXT);
    }

    public static class Item {
        private String name;
        private boolean isFolder;

        public Item(String name, boolean isFolder) {
            this.name = name;
            this.isFolder = isFolder;
        }

        public String getName() { return name; }
        public boolean rename(String name, String defaultName) {
            if (name.length() > 0) {
                this.name = name;
                return true;
            }

            this.name = defaultName;
            return false;
        }

        public boolean getIsFolder() { return isFolder; }
        public void setIsFolder(boolean isFolder) { this.isFolder = isFolder; }
    }
}
