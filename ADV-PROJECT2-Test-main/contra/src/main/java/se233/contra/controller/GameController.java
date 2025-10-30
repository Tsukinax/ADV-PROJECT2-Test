package se233.contra.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se233.contra.model.Boss1;
import se233.contra.model.Explosion;
import se233.contra.model.Player;
import se233.contra.model.Soldier;
import se233.contra.exception.GameException;
import se233.contra.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    public enum GameState {
        MENU,
        MINION_WAVE,
        BOSS_FIGHT,
        GAME_OVER,
        VICTORY
    }

    private GameState currentState;
    private Player player;
    private List<Soldier> soldiers;
    private Boss1 boss;
    private List<Explosion> explosions;

    // Wave management
    private int currentWave;
    private int minionsKilled;
    private double waveTimer;
    private boolean waveComplete;

    // Pause
    private boolean paused;

    private final Random random;

    public GameController() {
        this.currentState = GameState.MENU;
        this.soldiers = new ArrayList<>();
        this.explosions = new ArrayList<>();
        this.random = new Random();
        this.paused = false;

        logger.info("GameController initialized");
    }

    public void startGame() {
        try {
            logger.info("Starting new game...");

            // Initialize player
            player = new Player(100, Constants.GROUND_Y);

            // Start with minion waves
            currentWave = 0;
            minionsKilled = 0;
            waveTimer = 0;
            waveComplete = false;

            currentState = GameState.MINION_WAVE;
            spawnMinionWave();

            logger.info("Game started successfully");
        } catch (Exception e) {
            throw new GameException("Failed to start game",
                    GameException.ErrorType.INVALID_GAME_STATE, e);
        }
    }

    private void spawnMinionWave() {
        currentWave++;
        logger.info("Spawning minion wave {}/{}", currentWave,
                Constants.MINION_WAVES_BEFORE_BOSS);

        soldiers.clear();

        for (int i = 0; i < Constants.MINIONS_PER_WAVE; i++) {
            double spawnX = Constants.SCREEN_WIDTH + 50 + (i * 100);
            double spawnY = Constants.GROUND_Y;
            soldiers.add(new Soldier(spawnX, spawnY));
        }
    }

    private void spawnBoss() {
        logger.info("Spawning Boss 1!");
        boss = new Boss1(Constants.BOSS1_X, Constants.BOSS1_Y);
        currentState = GameState.BOSS_FIGHT;
    }

    public void update(double deltaTime) {
        if (paused || currentState == GameState.MENU) {
            return;
        }

        try {
            handleInput();

            switch (currentState) {
                case MINION_WAVE -> updateMinionWave(deltaTime);
                case BOSS_FIGHT -> updateBossFight(deltaTime);
                case GAME_OVER -> updateGameOver(deltaTime);
                case VICTORY -> updateVictory(deltaTime);
            }

            // Update explosions
            explosions.removeIf(e -> !e.isActive());
            for (Explosion explosion : explosions) {
                explosion.update(deltaTime);
            }

            InputHandler.getInstance().update();

        } catch (Exception e) {
            logger.error("Error in game update", e);
            throw new GameException("Game update failed",
                    GameException.ErrorType.INVALID_GAME_STATE, e);
        }
    }

    private void handleInput() {
        InputHandler input = InputHandler.getInstance();

        // Pause
        if (input.isKeyJustPressed(Constants.KEY_PAUSE)) {
            paused = !paused;
            logger.info("Game {}", paused ? "paused" : "resumed");
        }

        // Restart
        if (input.isKeyJustPressed(Constants.KEY_RESTART)) {
            if (currentState == GameState.GAME_OVER ||
                    currentState == GameState.VICTORY) {
                startGame();
            }
        }
    }

    private void updateMinionWave(double deltaTime) {
        // Update player
        player.update(deltaTime);

        // Update soldiers
        soldiers.removeIf(s -> !s.isActive());
        for (Soldier soldier : soldiers) {
            soldier.update(deltaTime);

            if (soldier.isDead() && !soldier.isActive()) {
                minionsKilled++;
                addExplosion(soldier.getPosition().getX(), soldier.getPosition().getY());
            }
        }

        // Check collisions
        CollisionDetector.checkPlayerBulletsVsSoldiers(player.getBullets(), soldiers, player);
        CollisionDetector.checkSoldierBulletsVsPlayer(soldiers, player);

        // Check wave completion
        if (soldiers.isEmpty()) {
            waveTimer += deltaTime;

            if (waveTimer > 2.0) { // 2 second delay between waves
                if (currentWave < Constants.MINION_WAVES_BEFORE_BOSS) {
                    spawnMinionWave();
                    waveTimer = 0;
                } else {
                    spawnBoss();
                }
            }
        }

        // Check game over
        if (!player.isAlive()) {
            currentState = GameState.GAME_OVER;
            logger.info("Game Over! Final Score: {}", player.getScore());
        }
    }

    private void updateBossFight(double deltaTime) {
        // Update player
        player.update(deltaTime);

        // Update boss
        if (boss != null && boss.isActive()) {
            boss.update(deltaTime);

            // Check collisions
            CollisionDetector.checkPlayerBulletsVsBoss1(player.getBullets(), boss, player);
            CollisionDetector.checkBossBulletsVsPlayer(boss, player);

            // Check boss defeat
            if (boss.isBossDefeated()) {
                currentState = GameState.VICTORY;
                addExplosion(boss.getPosition().getX() + 100, boss.getPosition().getY() + 100);
                logger.info("Victory! Final Score: {}", player.getScore());
            }
        }

        // Check game over
        if (!player.isAlive()) {
            currentState = GameState.GAME_OVER;
            logger.info("Game Over! Final Score: {}", player.getScore());
        }
    }

    private void updateGameOver(double deltaTime) {
        // Show game over screen
    }

    private void updateVictory(double deltaTime) {
        // Show victory screen
    }

    private void addExplosion(double x, double y) {
        explosions.add(new Explosion(x, y));
    }

    public void togglePause() {
        paused = !paused;
        logger.info("Game {}", paused ? "paused" : "resumed");
    }

    // Getters
    public GameState getCurrentState() { return currentState; }
    public Player getPlayer() { return player; }
    public List<Soldier> getSoldiers() { return soldiers; }
    public Boss1 getBoss() { return boss; }
    public List<Explosion> getExplosions() { return explosions; }
    public boolean isPaused() { return paused; }
    public int getCurrentWave() { return currentWave; }
}