package se233.contra.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se233.contra.model.Boss1;
import se233.contra.model.Explosion;
import se233.contra.model.Player;
import se233.contra.model.Soldier;
import se233.contra.controller.GameController;
import se233.contra.exception.GameException;
import se233.contra.util.Constants;

public class GameView extends Canvas {
    private static final Logger logger = LoggerFactory.getLogger(GameView.class);
    private final GraphicsContext gc;
    private final GameController gameController;

    private final Font titleFont;
    private final Font normalFont;
    private final Font smallFont;

    private final Image background;
    private final Image menuBackground;

    public GameView(GameController gameController) {
        super(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        this.gc = getGraphicsContext2D();
        this.gameController = gameController;

        // Initialize fonts
        titleFont = Font.font("Courier New", FontWeight.BOLD, 48);
        normalFont = Font.font("Courier New", FontWeight.BOLD, 24);
        smallFont = Font.font("Courier New", FontWeight.NORMAL, 16);

        // Load background images
        try {
            this.background = new Image(getClass().getResourceAsStream(Constants.BACKGROUND));
            this.menuBackground = new Image(getClass().getResourceAsStream("/sprites/background1.png"));
        } catch (Exception e) {
            throw new GameException("Failed to load background images", GameException.ErrorType.RESOURCE_NOT_FOUND, e);
        }

        logger.info("GameView initialized ({}x{})", Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
    }

    public void render() {
        try {
            // Clear screen
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

            switch (gameController.getCurrentState()) {
                case MENU -> renderMenu();
                case MINION_WAVE, BOSS_FIGHT -> renderGame();
                case GAME_OVER -> renderGameOver();
                case VICTORY -> renderVictory();
            }

            // Render pause overlay
            if (gameController.isPaused()) {
                renderPauseOverlay();
            }

        } catch (Exception e) {
            logger.error("Error rendering game", e);
            throw new GameException("Render failed",
                    GameException.ErrorType.INVALID_GAME_STATE, e);
        }
    }

    private void renderMenu() {
        // วาดภาพพื้นหลังเมนู
        gc.drawImage(menuBackground, 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
    }

    private void renderGame() {
        // Draw background (full screen)
        drawBackground();

        // ✅ ลบ drawGround() ออก - ไม่วาดพื้นสีเขียวแล้ว

        // Draw game objects
        Player player = gameController.getPlayer();
        if (player != null && player.isActive()) {
            player.render(gc);
        }

        // Draw soldiers
        for (Soldier soldier : gameController.getSoldiers()) {
            if (soldier.isActive()) {
                soldier.render(gc);
            }
        }

        // Draw boss
        Boss1 boss = gameController.getBoss();
        if (boss != null && boss.isActive()) {
            boss.render(gc);
        }

        // Draw explosions
        for (Explosion explosion : gameController.getExplosions()) {
            if (explosion.isActive()) {
                explosion.render(gc);
            }
        }

        // Draw UI
        drawUI();
    }

    private void drawBackground() {
        // ✅ วาดภาพพื้นหลังเต็มจอ
        if (background != null) {
            gc.drawImage(background, 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        } else {
            // Fallback
            gc.setFill(Color.rgb(20, 30, 40));
            gc.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        }
    }

    // ✅ ลบ method drawGround() ออก - ไม่ต้องการพื้นสีเขียวแล้ว

    private void drawUI() {
        Player player = gameController.getPlayer();
        if (player == null) return;

        gc.setFont(normalFont);
        gc.setFill(Color.WHITE);

        // Score
        gc.fillText("SCORE: " + player.getScore(), 20, 40);

        // Lives
        gc.fillText("LIVES:", 20, 80);
        for (int i = 0; i < player.getLives(); i++) {
            gc.setFill(Color.RED);
            gc.fillRect(120 + i * 30, 65, 20, 15);
        }

        // Wave info
        if (gameController.getCurrentState() == GameController.GameState.MINION_WAVE) {
            gc.setFill(Color.YELLOW);
            gc.fillText("WAVE " + gameController.getCurrentWave() + "/" +
                            Constants.MINION_WAVES_BEFORE_BOSS,
                    Constants.SCREEN_WIDTH - 200, 40);
        } else if (gameController.getCurrentState() == GameController.GameState.BOSS_FIGHT) {
            gc.setFill(Color.RED);
            gc.fillText("BOSS FIGHT!", Constants.SCREEN_WIDTH - 200, 40);
        }
    }

    private void renderGameOver() {
        renderGame();
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        gc.setFill(Color.RED);
        gc.setFont(titleFont);
        gc.fillText("GAME OVER", Constants.SCREEN_WIDTH / 2 - 150,
                Constants.SCREEN_HEIGHT / 2 - 50);
        Player player = gameController.getPlayer();
        if (player != null) {
            gc.setFill(Color.WHITE);
            gc.setFont(normalFont);
            gc.fillText("Final Score: " + player.getScore(),
                    Constants.SCREEN_WIDTH / 2 - 120, Constants.SCREEN_HEIGHT / 2 + 20);
        }
        gc.setFont(smallFont);
        gc.fillText("Press R to Restart", Constants.SCREEN_WIDTH / 2 - 100,
                Constants.SCREEN_HEIGHT / 2 + 80);
    }

    private void renderVictory() {
        renderGame();
        gc.setFill(Color.rgb(255, 255, 0, 0.3));
        gc.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        gc.setFill(Color.YELLOW);
        gc.setFont(titleFont);
        gc.fillText("VICTORY!", Constants.SCREEN_WIDTH / 2 - 120,
                Constants.SCREEN_HEIGHT / 2 - 50);
        Player player = gameController.getPlayer();
        if (player != null) {
            gc.setFill(Color.WHITE);
            gc.setFont(normalFont);
            gc.fillText("Final Score: " + player.getScore(),
                    Constants.SCREEN_WIDTH / 2 - 120, Constants.SCREEN_HEIGHT / 2 + 20);
        }
        gc.setFont(smallFont);
        gc.fillText("Press R to Play Again", Constants.SCREEN_WIDTH / 2 - 120,
                Constants.SCREEN_HEIGHT / 2 + 80);
    }

    private void renderPauseOverlay() {
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        gc.setFill(Color.WHITE);
        gc.setFont(titleFont);
        gc.fillText("PAUSED", Constants.SCREEN_WIDTH / 2 - 100,
                Constants.SCREEN_HEIGHT / 2);
        gc.setFont(smallFont);
        gc.fillText("Press P to Resume", Constants.SCREEN_WIDTH / 2 - 100,
                Constants.SCREEN_HEIGHT / 2 + 50);
    }
}