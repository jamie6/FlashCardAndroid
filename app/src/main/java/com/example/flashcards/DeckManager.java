package com.example.flashcards;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DeckManager {
    public String currentDirectoryPath;

    public DeckManager(String currentDirectoryPath) {
        this.currentDirectoryPath = currentDirectoryPath;
    }

    public void setCurrentDirectoryPath(String path) {
        currentDirectoryPath = path;
    }

    public boolean createNewDeck(String newDeckName, String contents) throws IOException {
        File file = new File(currentDirectoryPath + "/" + newDeckName + ".deck");
        if (!file.exists()) {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(contents.getBytes());
            out.close();
        }
    }

    public boolean createNewFolder(String newFolderName) {
        File file = new File(currentDirectoryPath + "/" + newFolderName);
        if (!file.exists()) {
            file.mkdir();
            return true;
        }
        return false;
    }

    public void renameDeck() {

    }

    public void renameFolder() {

    }

    public void moveItem() {

    }

    public String[] getCurrentDirectoryList() {
        File file = new File(currentDirectoryPath);
        return file.list();
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
