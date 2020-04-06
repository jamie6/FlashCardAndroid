package com.example.flashcards;

import android.os.Environment;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DeckManager {
    public static final String FOLDER_TAG = "FOLDER";
    public static final String DECK_TAG = "DECK";
    public static final String MAIN_DECKS_FOLDER = "DECKS";
    public static final String DECK_EXT = ".deck";
    private static String BASE_DIRECTORY;
    private static String currentDirectoryPath;

    public static void setBaseDirectoryPath(String path) {
        BASE_DIRECTORY = path;
    }

    public static void resetCurrentDirectoryToBaseDirectory() {
        currentDirectoryPath = BASE_DIRECTORY;
    }

    public static boolean isCurrentDirectoryAtBaseDirectory() {
        return currentDirectoryPath.equals(BASE_DIRECTORY);
    }

    public static void setCurrentDirectoryPath(String path) {
        currentDirectoryPath = path;
        File file = new File(currentDirectoryPath);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static String getCurrentDirectoryPath() {
        return currentDirectoryPath;
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

    public static boolean deleteItem(String itemName, boolean isFolder) {
        if (isFolder) {
            return deleteFolder(itemName);
        } else {
            return deleteDeck(itemName);
        }
    }

    private static boolean deleteDeck(String deckName) {
        return deleteDeck(deckName, currentDirectoryPath);
    }

    private static boolean deleteDeck(String deckName, String path) {
        if (!isThisADeckFile(deckName)) {
            deckName += DECK_EXT;
        }
        File file = new File(path + "/" + deckName);
        return file.delete();
    }

    private static boolean deleteFolder(String folderName) {
        return deleteFolder(folderName, currentDirectoryPath);
    }

    private static boolean deleteFolder(String folderName, String path) {
        File folder = new File(path + "/" + folderName);
        String[] list = folder.list();
        for (String item : list) {
            if (isThisADeckFile(item)) {
                deleteDeck(item, folder.getPath());
            } else {
                deleteFolder(item, folder.getPath());
            }
        }
        return folder.delete();
    }

    public static String[] getCurrentDirectoryList() {
        File file = new File(currentDirectoryPath);
        return file.list();
    }

    public static String[] getFolderNamesInCurrentDirectory() {
        File file = new File(currentDirectoryPath);
        String[] list = file.list();

        List<String> folderNames = new LinkedList<>();
        for (String item : list) {
            if (!isThisADeckFile(item)) {
                folderNames.add(item);
            }
        }
        String[] folderNamesArr = new String[folderNames.size()];
        folderNames.toArray(folderNamesArr);
        return folderNamesArr;
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

    public static boolean isThisADeckFile(String str) {
        return str.matches("(.+)\\" + DECK_EXT); // "\\" used to ignore period "."
    }

    public static Card[] getDeckFromFile(String deckName) {
        Card[] deck = null;
        try {
            String deckContent = getContentFromDeckFile(deckName);
            deck = parseDeckContent(deckContent);
        } catch (IOException e) {
            return null;
        }
        return deck;
    }

    private static Card[] parseDeckContent(String deckContent) throws IOException {
        List<Card> deck = new LinkedList<>();
        JsonReader jsonReader = new JsonReader(new StringReader(deckContent));
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            deck.add(readCardFromJsonReader(jsonReader));
        }
        jsonReader.endArray();
        Card[] deckArr = new Card[deck.size()];
        deck.toArray(deckArr);
        return deckArr;
    }

    private static Card readCardFromJsonReader(JsonReader jsonReader) throws IOException {
        String term = "";
        String definition = "";

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (name.equals(Card.TERM)) {
                term = jsonReader.nextString();
            } else if (name.equals(Card.DEFINITION)) {
                definition = jsonReader.nextString();
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return new Card(term, definition);
    }

    public static JSONArray getDeckJSONArray(List<Card> deck) {
        JSONArray deckJSON = new JSONArray();
        for (Card card : deck) {
            deckJSON.put(card.getJSONObject());
        }
        return deckJSON;
    }

    private static String getContentFromDeckFile(String deckName) throws IOException {
        if (!isThisADeckFile(deckName)) {
            deckName += DECK_EXT;
        }
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(currentDirectoryPath + "/" + deckName);
        if (file.exists()) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
        }

        return stringBuilder.toString();
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
