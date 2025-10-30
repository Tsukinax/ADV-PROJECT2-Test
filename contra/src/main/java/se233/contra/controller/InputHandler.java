package se233.contra.controller;

import javafx.scene.input.KeyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InputHandler {
    private static final Logger logger = LoggerFactory.getLogger(InputHandler.class);
    private static InputHandler instance;

    private final Set<KeyCode> pressedKeys;
    private final Map<KeyCode, Boolean> justPressedKeys;
    private final Map<KeyCode, Boolean> previousFrameKeys;

    private InputHandler() {
        pressedKeys = new HashSet<>();
        justPressedKeys = new HashMap<>();
        previousFrameKeys = new HashMap<>();
    }

    public static InputHandler getInstance() {
        if (instance == null) {
            instance = new InputHandler();
        }
        return instance;
    }

    public void keyPressed(KeyCode key) {
        if (!pressedKeys.contains(key)) {
            pressedKeys.add(key);
            justPressedKeys.put(key, true);
            logger.trace("Key pressed: {}", key);
        }
    }

    public void keyReleased(KeyCode key) {
        pressedKeys.remove(key);
        justPressedKeys.put(key, false);
        logger.trace("Key released: {}", key);
    }

    public boolean isKeyPressed(KeyCode key) {
        return pressedKeys.contains(key);
    }

    public boolean isKeyJustPressed(KeyCode key) {
        boolean currentlyPressed = pressedKeys.contains(key);
        boolean wasPressed = previousFrameKeys.getOrDefault(key, false);
        return currentlyPressed && !wasPressed;
    }

    public void update() {
        // Update previous frame state
        previousFrameKeys.clear();
        for (KeyCode key : pressedKeys) {
            previousFrameKeys.put(key, true);
        }

        // Clear just pressed flags
        justPressedKeys.clear();
    }

    public void reset() {
        pressedKeys.clear();
        justPressedKeys.clear();
        previousFrameKeys.clear();
        logger.debug("InputHandler reset");
    }
}