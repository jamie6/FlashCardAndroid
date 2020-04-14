package com.example.flashcards;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DeckManager.class})
public class DeckManagerTest {
    @Before
    public void before() {
        PowerMockito.mockStatic(DeckManager.class);
    }

    @Test
    public void isThisADeckFileTest() {
        when(DeckManager.isThisADeckFile(anyString())).thenCallRealMethod();

        assertFalse(DeckManager.isThisADeckFile("test"));
        assertFalse(DeckManager.isThisADeckFile("testdeck"));
        assertFalse(DeckManager.isThisADeckFile("test deck"));
        assertFalse(DeckManager.isThisADeckFile("New File"));
        assertFalse(DeckManager.isThisADeckFile("New File 1"));

        assertTrue(DeckManager.isThisADeckFile("test.deck"));
        assertTrue(DeckManager.isThisADeckFile("test deck.deck"));
        assertTrue(DeckManager.isThisADeckFile("New File.deck"));
        assertTrue(DeckManager.isThisADeckFile("New File 1.deck"));
    }

    @Test
    public void getCurrentFolderNameTest() throws IllegalAccessException {
         when(DeckManager.getCurrentFolderName()).thenCallRealMethod();

         Field field = PowerMockito.field(DeckManager.class, "currentDirectoryPath");
         field.setAccessible(true);
         field.set(DeckManager.class, "hello/bye");
         assertTrue(DeckManager.getCurrentFolderName().equals("bye"));

        field.set(DeckManager.class, "hello/bye bye");
        assertTrue(DeckManager.getCurrentFolderName().equals("bye bye"));

        field.set(DeckManager.class, "hello/bye/what/who/when where/okay man");
        assertTrue(DeckManager.getCurrentFolderName().equals("okay man"));

        field.set(DeckManager.class, "/hello");
        assertTrue(DeckManager.getCurrentFolderName().equals("hello"));

        field.set(DeckManager.class, "/");
        assertTrue(DeckManager.getCurrentFolderName().equals(""));
    }

    @Test
    public void moveUpDirectoryTest() throws IllegalAccessException {
        when(DeckManager.moveUpDirectory()).thenCallRealMethod();

        Field field = PowerMockito.field(DeckManager.class, "currentDirectoryPath");
        field.setAccessible(true);
        field.set(DeckManager.class, "hello/bye");
        assertTrue(DeckManager.moveUpDirectory().equals("hello"));

        field.set(DeckManager.class, "hello/bye bye");
        assertTrue(DeckManager.moveUpDirectory().equals("hello"));

        field.set(DeckManager.class, "/what's that");
        assertTrue(DeckManager.moveUpDirectory().equals(""));

        field.set(DeckManager.class, "/");
        assertTrue(DeckManager.moveUpDirectory().equals(""));
    }
}