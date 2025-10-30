package se233.contra.model;

import java.util.List;

/**
 * Interface for all enemies in the game
 * Demonstrates POLYMORPHISM - different enemy types can be treated uniformly
 * This allows for flexible enemy management in collections
 */
public interface Enemy {
    /**
     * Make the enemy take damage
     * @param damage Amount of damage to deal
     */
    void hit(int damage);

    /**
     * Check if enemy is dead/defeated
     * @return true if enemy is dead
     */
    boolean isDead();

    /**
     * Get all bullets currently fired by this enemy
     * @return List of active bullets
     */
    List<Bullet> getBullets();

    /**
     * Get score value awarded when this enemy is defeated
     * @return Score points
     */
    int getScoreValue();

    /**
     * Check if enemy is still active in the game
     * @return true if active
     */
    boolean isActive();

    /**
     * Update enemy state
     * @param deltaTime Time since last frame
     */
    void update(double deltaTime);
}