package com.example.flashcards;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DeckManager.class})
public class DeckManagerTest {

    @Test
    public void isThisADeckFileTest() {
        PowerMockito.mockStatic(DeckManager.class);
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
}