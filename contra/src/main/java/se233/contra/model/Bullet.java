package se233.contra.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se233.contra.util.Constants;
import se233.contra.util.Vector2D;
import se233.contra.view.Animation;
import se233.contra.view.SpriteLoader;

/**
 * Bullet class with hit animation effect
 * FIXED: Prevent bullet from hitting multiple times
 */
public class Bullet extends GameObject {
    private static final Logger logger = LoggerFactory.getLogger(Bullet.class);

    private final boolean isPlayerBullet;
    private final Image sprite;
    private final int damage;

    // Hit animation
    private boolean isHit;
    private Animation hitAnimation;

    public Bullet(double x, double y, Vector2D direction, boolean isPlayerBullet) {
        super(x, y, Constants.BULLET_SIZE, Constants.BULLET_SIZE);
        this.isPlayerBullet = isPlayerBullet;
        this.damage = 1;
        this.sprite = SpriteLoader.getBullet();
        this.isHit = false;

        // Create hit animation (small explosion)
        this.hitAnimation = new Animation(
                SpriteLoader.getExplosion(),
                0.05,  // Fast animation (0.05s per frame)
                false  // Don't loop
        );

        // Set velocity based on direction
        Vector2D normalized = direction.normalize();
        double speed = Constants.BULLET_SPEED;
        velocity.set(normalized.getX() * speed, normalized.getY() * speed);

        logger.debug("Bullet created at ({}, {}) direction: {} player: {}",
                x, y, direction, isPlayerBullet);
    }

    @Override
    public void update(double deltaTime) {
        // If bullet hit something, play explosion animation
        if (isHit) {
            hitAnimation.update(deltaTime);
            if (hitAnimation.isFinished()) {
                active = false;
                logger.trace("Bullet explosion animation finished");
            }
            return;
        }

        // Normal bullet movement
        updatePosition(deltaTime);

        // Deactivate if out of screen bounds
        if (position.getX() < -50 || position.getX() > Constants.SCREEN_WIDTH + 50 ||
                position.getY() < -50 || position.getY() > Constants.SCREEN_HEIGHT + 50) {
            active = false;
            logger.trace("Bullet out of bounds, deactivated");
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!active) return;

        // If bullet hit, show explosion animation
        if (isHit) {
            Image frame = hitAnimation.getCurrentFrame();
            if (frame != null) {
                // Draw explosion centered on bullet position
                double explosionX = position.getX() - 12;
                double explosionY = position.getY() - 12;
                gc.drawImage(frame, explosionX, explosionY);
            }
        } else if (sprite != null) {
            // Draw normal bullet sprite
            gc.drawImage(sprite, position.getX(), position.getY());
        }
    }

    public boolean isPlayerBullet() {
        return isPlayerBullet;
    }

    public int getDamage() {
        return damage;
    }

    /**
     * NEW: Check if bullet has already hit something
     * Prevents multiple hits from same bullet
     */
    public boolean hasHit() {
        return isHit;
    }

    /**
     * Called when bullet hits a target
     * Shows explosion animation before deactivating
     */
    public void onHit() {
        if (isHit) return; // Prevent double-hit

        isHit = true;
        velocity.set(0, 0);  // Stop moving
        hitAnimation.reset();
        logger.debug("Bullet hit target at ({}, {}), playing explosion",
                position.getX(), position.getY());
    }
}