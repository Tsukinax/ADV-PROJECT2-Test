package se233.contra.view;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se233.contra.exception.GameException;
import se233.contra.util.Constants;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteLoader {
    private static final Logger logger = LoggerFactory.getLogger(SpriteLoader.class);
    private static final Map<String, Image> spritesheets = new HashMap<>();
    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) {
            logger.warn("SpriteLoader already initialized");
            return;
        }

        try {
            logger.info("Loading spritesheets...");
            loadSpritesheet("player", Constants.PLAYER_SPRITE);
            loadSpritesheet("enemies", Constants.ENEMIES_SPRITE);
            loadSpritesheet("boss1", Constants.BOSS1_SPRITE);
            loadSpritesheet("ui", Constants.UI_SPRITE);
            initialized = true;
            logger.info("All spritesheets loaded successfully");
        } catch (Exception e) {
            throw new GameException("Failed to load spritesheets",
                    GameException.ErrorType.SPRITE_LOAD_ERROR, e);
        }
    }

    private static void loadSpritesheet(String key, String path) {
        try {
            InputStream is = SpriteLoader.class.getResourceAsStream(path);
            if (is == null) {
                throw new GameException("Spritesheet not found: " + path,
                        GameException.ErrorType.RESOURCE_NOT_FOUND);
            }
            Image image = new Image(is);
            spritesheets.put(key, image);
            logger.debug("Loaded spritesheet: {} ({}x{})", key,
                    image.getWidth(), image.getHeight());
        } catch (Exception e) {
            logger.error("Failed to load spritesheet: {}", path, e);
            throw new GameException("Failed to load: " + path,
                    GameException.ErrorType.SPRITE_LOAD_ERROR, e);
        }
    }

    public static Image getSprite(String sheetKey, int x, int y, int width, int height) {
        if (!initialized) {
            throw new GameException("SpriteLoader not initialized",
                    GameException.ErrorType.INVALID_GAME_STATE);
        }

        Image sheet = spritesheets.get(sheetKey);
        if (sheet == null) {
            throw new GameException("Spritesheet not found: " + sheetKey,
                    GameException.ErrorType.RESOURCE_NOT_FOUND);
        }

        try {
            PixelReader reader = sheet.getPixelReader();
            return new WritableImage(reader, x, y, width, height);
        } catch (Exception e) {
            logger.error("Failed to extract sprite from {} at ({},{},{}x{})",
                    sheetKey, x, y, width, height, e);
            throw new GameException("Failed to extract sprite",
                    GameException.ErrorType.SPRITE_LOAD_ERROR, e);
        }
    }

    // Player animations
    public static List<Image> getPlayerIdle() {
        List<Image> frames = new ArrayList<>();
        frames.add(getSprite("player", 0, 8, 24, 34));
        frames.add(getSprite("player", 24, 8, 24, 34));
        return frames;
    }

    public static List<Image> getPlayerRun() {
        List<Image> frames = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            frames.add(getSprite("player", i * 20, 43, 20, 35));
        }
        return frames;
    }

    public static List<Image> getPlayerJump() {
        List<Image> frames = new ArrayList<>();
        frames.add(getSprite("player", 122, 52, 20, 20));
        frames.add(getSprite("player", 142, 52, 20, 20));
        frames.add(getSprite("player", 162, 52, 20, 20));
        return frames;
    }

    public static List<Image> getPlayerShoot() {
        List<Image> frames = new ArrayList<>();
        frames.add(getSprite("player", 0, 79, 25, 34));
        frames.add(getSprite("player", 25, 79, 25, 34));
        return frames;
    }

    // ✅ แก้ไข: Prone animation - ใช้เฉพาะ 1 เฟรม เพื่อป้องกันการซ้อนกัน
    public static List<Image> getPlayerProne() {
        List<Image> frames = new ArrayList<>();
        // ใช้เฉพาะเฟรมแรก (หรือเฟรมที่ชัดเจนที่สุด)
        frames.add(getSprite("player", 80, 25, 31, 18));  // Prone - ขนาดเตี้ยลง
        return frames;
    }

    public static List<Image> getPlayerDeath() {
        List<Image> frames = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            frames.add(getSprite("player", 61 + i * 32, 161, 32, 23));
        }
        return frames;
    }

    // Soldier (Minion) animations
    public static List<Image> getSoldierRun() {
        List<Image> frames = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            frames.add(getSprite("enemies", 40 + i * 18, 417, 18, 25));
        }
        return frames;
    }

    public static List<Image> getSoldierShoot() {
        List<Image> frames = new ArrayList<>();
        frames.add(getSprite("enemies", 95, 418, 15, 24));
        return frames;
    }

    // Boss 1 components
    public static Image getBoss1Door() {
        return getSprite("boss1", 80, 0, 80, 180);
    }

    public static Image getBoss1Cannon() {
        return getSprite("boss1", 10, 100, 24, 16);
    }

    public static List<Image> getBoss1Core() {
        List<Image> frames = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            frames.add(getSprite("boss1", 10 + i * 32, 80, 32, 32));
        }
        return frames;
    }

    // Effects
    public static List<Image> getExplosion() {
        List<Image> frames = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            frames.add(getSprite("enemies", 92 + i * 30, 611, 30, 30));
        }
        return frames;
    }

    public static Image getBullet() {
        return getSprite("enemies", 199, 72, 3, 3);
    }

    // UI
    public static Image getLifeIcon() {
        return getSprite("ui", 0, 0, 16, 10);
    }
}