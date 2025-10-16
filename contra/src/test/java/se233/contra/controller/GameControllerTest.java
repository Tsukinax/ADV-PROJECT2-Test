package se233.contra.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.contra.view.SpriteLoader;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    private GameController gameController;

    @BeforeEach
    void setUp() {
        try {
            SpriteLoader.initialize();
        } catch (Exception e) {
            // Ignore
        }

        gameController = new GameController();
    }

    @Test
    void testGameControllerInitialization() {
        assertNotNull(gameController);
        assertEquals(GameController.GameState.MENU, gameController.getCurrentState());
    }

    @Test
    void testStartGame() {
        gameController.startGame();

        assertEquals(GameController.GameState.MINION_WAVE, gameController.getCurrentState());
        assertNotNull(gameController.getPlayer());
        assertTrue(gameController.getPlayer().isActive());
    }

    @Test
    void testWaveProgression() {
        gameController.startGame();

        assertEquals(1, gameController.getCurrentWave());
        assertFalse(gameController.getSoldiers().isEmpty());
    }

    @Test
    void testPause() {
        gameController.startGame();
        assertFalse(gameController.isPaused());

        gameController.togglePause();
        assertTrue(gameController.isPaused());

        gameController.togglePause();
        assertFalse(gameController.isPaused());
    }

    @Test
    void testUpdate() {
        gameController.startGame();

        // Should not throw exception
        assertDoesNotThrow(() -> {
            gameController.update(0.016); // ~60 FPS
        });
    }
}