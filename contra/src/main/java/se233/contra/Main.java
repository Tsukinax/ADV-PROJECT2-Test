package se233.contra;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se233.contra.controller.GameController;
import se233.contra.controller.InputHandler;
import se233.contra.exception.GameException;
import se233.contra.util.Constants;
import se233.contra.view.GameView;
import se233.contra.view.SpriteLoader;

public class
Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private GameController gameController;
    private GameView gameView;
    private GameLoop gameLoop;


    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting Contra Boss Fight...");

            // Initialize sprite loader
            SpriteLoader.initialize();

            // Create game controller
            gameController = new GameController();

            // Create game view
            gameView = new GameView(gameController);

            // Setup scene
            StackPane root = new StackPane(gameView);
            Scene scene = new Scene(root, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

            // Setup input handling
            setupInputHandling(scene);

            // Configure stage
            primaryStage.setTitle(Constants.GAME_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            // Start game loop
            gameLoop = new GameLoop();
            gameLoop.start();

            logger.info("Game started successfully!");

        } catch (Exception e) {
            logger.error("Failed to start game", e);
            throw new GameException("Application startup failed",
                    GameException.ErrorType.INVALID_GAME_STATE, e);
        }
    }

    private void setupInputHandling(Scene scene) {
        InputHandler inputHandler = InputHandler.getInstance();

        scene.setOnKeyPressed(event -> {
            inputHandler.keyPressed(event.getCode());

            // Menu controls
            if (gameController.getCurrentState() == GameController.GameState.MENU) {
                if (event.getCode() == KeyCode.ENTER) {
                    gameController.startGame();
                    logger.info("Game started from menu");
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            inputHandler.keyReleased(event.getCode());
        });

        logger.debug("Input handling configured");
    }

    @Override
    public void stop() {
        logger.info("Shutting down game...");
        if (gameLoop != null) {
            gameLoop.stop();
        }
        logger.info("Game shutdown complete");
    }

    /**
     * Game loop using JavaFX AnimationTimer
     */
    private class GameLoop extends AnimationTimer {
        private static final long TARGET_FPS = 60;
        private static final long FRAME_TIME = 1_000_000_000 / TARGET_FPS;

        private long lastUpdate = 0;
        private long frameCount = 0;
        private long lastFpsTime = 0;
        private int fps = 0;

        @Override




        public void handle(long now) {
            if (lastUpdate == 0) {
                lastUpdate = now;
                lastFpsTime = now;
                return;
            }

            // Calculate delta time
            double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
            lastUpdate = now;

            // Cap delta time to prevent huge jumps
            if (deltaTime > 0.1) {
                deltaTime = 0.1;
            }

            try {
                // Update game
                gameController.update(deltaTime);

                // Render
                gameView.render();

                // FPS counter
                frameCount++;
                if (now - lastFpsTime >= 1_000_000_000) {
                    fps = (int) frameCount;
                    frameCount = 0;
                    lastFpsTime = now;
                    logger.trace("FPS: {}", fps);
                }

            } catch (Exception e) {
                logger.error("Error in game loop", e);
                this.stop();
                throw e;
            }
        }
    }

    public static void main(String[] args) {
        logger.info("Contra Boss Fight - SE233 Term Project II");
        logger.info("Starting application...");
        launch(args);
    }
}