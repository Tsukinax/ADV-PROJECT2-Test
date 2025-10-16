package se233.contra.model;

import javafx.scene.canvas.GraphicsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Abstract base class for all bosses
 * Demonstrates INHERITANCE and POLYMORPHISM for OOP requirement
 */
public abstract class Boss extends GameObject {
    private static final Logger logger = LoggerFactory.getLogger(Boss.class);

    protected int health;
    protected int maxHealth;
    protected boolean defeated;
    protected double stateTimer;

    /**
     * Constructor for Boss
     * @param x X position
     * @param y Y position
     * @param width Width of boss
     * @param height Height of boss
     * @param maxHealth Maximum health points
     */
    public Boss(double x, double y, double width, double height, int maxHealth) {
        super(x, y, width, height);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.defeated = false;
        this.stateTimer = 0;

        logger.info("{} created with {} HP at ({}, {})",
                getClass().getSimpleName(), maxHealth, x, y);
    }

    /**
     * Abstract method: Each boss has different attack patterns
     */
    public abstract void attack(double deltaTime);

    /**
     * Abstract method: Get all bullets fired by this boss
     */
    public abstract List<Bullet> getAllBullets();

    /**
     * Abstract method: Each boss takes damage differently
     */
    public abstract void takeDamage(int damage);

    /**
     * Main update method - Template Method Pattern
     */
    @Override
    public void update(double deltaTime) {
        if (defeated) {
            return;
        }

        stateTimer += deltaTime;
        updateBehavior(deltaTime);
        updateComponents(deltaTime);
    }

    /**
     * Update boss-specific behavior (state machine, AI, etc.)
     */
    protected abstract void updateBehavior(double deltaTime);

    /**
     * Update boss components (cannons, parts, etc.)
     */
    protected abstract void updateComponents(double deltaTime);

    /**
     * Common method to handle damage
     */
    public void hit(int damage) {
        if (defeated) return;

        health -= damage;
        logger.info("{} took {} damage. HP: {}/{}",
                getClass().getSimpleName(), damage, health, maxHealth);

        if (health <= 0) {
            onDefeated();
        }
    }

    /**
     * Called when boss is defeated
     */
    protected void onDefeated() {
        defeated = true;
        active = false;
        logger.info("{} defeated!", getClass().getSimpleName());
    }

    /**
     * Abstract render method - each boss renders differently
     */
    @Override
    public abstract void render(GraphicsContext gc);

    // Getters
    public boolean isDefeated() {
        return defeated;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public double getHealthPercentage() {
        return (double) health / maxHealth;
    }

    public double getStateTimer() {
        return stateTimer;
    }
}