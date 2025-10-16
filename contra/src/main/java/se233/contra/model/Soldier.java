package se233.contra.model;

import javafx.scene.canvas.GraphicsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se233.contra.util.Constants;
import se233.contra.util.Vector2D;
import se233.contra.view.Animation;
import se233.contra.view.SpriteLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Soldier (Minion) enemy class
 * Implements Enemy interface to demonstrate POLYMORPHISM
 * FIXED: ตำแหน่งพื้นให้ตรงกับ Constants.GROUND_Y
 */
public class Soldier extends GameObject implements Enemy {
    private static final Logger logger = LoggerFactory.getLogger(Soldier.class);

    public enum State {
        RUNNING,
        SHOOTING,
        DEAD
    }

    private State currentState;
    private int health;
    private boolean onGround;

    // AI properties
    private double shootTimer;
    private double shootCooldown;
    private static final double SHOOT_INTERVAL = 2.0;
    private static final double SHOOT_DURATION = 0.5;

    // Movement
    private double patrolSpeed;
    private double targetX;
    private static final Random random = new Random();

    // Animations
    private Animation runAnim;
    private Animation shootAnim;
    private Animation deathAnim;

    // Bullets
    private final List<Bullet> bullets;

    public Soldier(double x, double y) {
        // ✅ แก้ไข: ให้ spawn ที่ตำแหน่งถูกต้องบนพื้น
        super(x, Constants.GROUND_Y - Constants.SOLDIER_HEIGHT,
                Constants.SOLDIER_WIDTH, Constants.SOLDIER_HEIGHT);

        this.health = 1; // One-hit kill
        this.currentState = State.RUNNING;
        this.onGround = true; // เริ่มต้นบนพื้น
        this.shootTimer = random.nextDouble() * SHOOT_INTERVAL;
        this.shootCooldown = 0;
        this.patrolSpeed = 50 + random.nextDouble() * 30;
        this.targetX = random.nextDouble() * 400 + 100;
        this.bullets = new ArrayList<>();

        initializeAnimations();

        // Random starting direction
        facingRight = random.nextBoolean();
        velocity.setX(facingRight ? patrolSpeed : -patrolSpeed);

        logger.debug("Soldier spawned at ({}, {}) - ground at {}",
                position.getX(), position.getY(), Constants.GROUND_Y);
    }

    private void initializeAnimations() {
        runAnim = new Animation(SpriteLoader.getSoldierRun(), 0.15);
        shootAnim = new Animation(SpriteLoader.getSoldierShoot(), 0.2);
        deathAnim = new Animation(SpriteLoader.getExplosion(), 0.15, false);
        currentAnimation = runAnim;
    }

    @Override
    public void update(double deltaTime) {
        if (currentState == State.DEAD) {
            updateDeath(deltaTime);
            return;
        }

        // Update AI behavior
        updateAI(deltaTime);

        // Apply physics
        applyGravity(deltaTime);
        updatePosition(deltaTime);
        checkGroundCollision();

        // Update animation
        updateAnimation(deltaTime);

        // Update bullets
        updateBullets(deltaTime);

        // Deactivate if offscreen
        if (position.getX() < -100 || position.getX() > Constants.SCREEN_WIDTH + 100) {
            active = false;
        }
    }

    private void updateAI(double deltaTime) {
        shootTimer += deltaTime;

        if (currentState == State.SHOOTING) {
            velocity.setX(0);
            shootCooldown -= deltaTime;

            if (shootCooldown <= 0) {
                currentState = State.RUNNING;
                velocity.setX(facingRight ? patrolSpeed : -patrolSpeed);
            }
        } else {
            // Patrol behavior
            if (facingRight && position.getX() >= targetX) {
                facingRight = false;
                velocity.setX(-patrolSpeed);
                targetX = random.nextDouble() * 200;
            } else if (!facingRight && position.getX() <= targetX) {
                facingRight = true;
                velocity.setX(patrolSpeed);
                targetX = random.nextDouble() * 200 + 300;
            }

            // Shoot periodically
            if (shootTimer >= SHOOT_INTERVAL) {
                currentState = State.SHOOTING;
                shootCooldown = SHOOT_DURATION;
                shootTimer = 0;
                shoot();
            }
        }
    }

    private void shoot() {
        Vector2D bulletPos = new Vector2D(
                position.getX() + (facingRight ? bounds.getWidth() : -8),
                position.getY() + bounds.getHeight() / 2
        );

        Vector2D direction = new Vector2D(facingRight ? 1 : -1, 0);
        Bullet bullet = new Bullet(bulletPos.getX(), bulletPos.getY(), direction, false);
        bullets.add(bullet);

        logger.trace("Soldier shot bullet");
    }

    private void applyGravity(double deltaTime) {
        if (!onGround) {
            velocity.setY(velocity.getY() + Constants.GRAVITY * deltaTime);
            if (velocity.getY() > 600) {
                velocity.setY(600);
            }
        }
    }

    private void checkGroundCollision() {
        // ✅ แก้ไข: ใช้ Constants.GROUND_Y แทน
        double soldierBottom = position.getY() + bounds.getHeight();

        if (soldierBottom >= Constants.GROUND_Y) {
            position.setY(Constants.GROUND_Y - bounds.getHeight());
            velocity.setY(0);
            onGround = true;
        } else {
            onGround = false;
        }

        updateBounds();
    }

    private void updateBullets(double deltaTime) {
        bullets.removeIf(bullet -> !bullet.isActive());
        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);
        }
    }

    private void updateAnimation(double deltaTime) {
        Animation targetAnimation = switch (currentState) {
            case RUNNING -> runAnim;
            case SHOOTING -> shootAnim;
            case DEAD -> deathAnim;
        };

        if (currentAnimation != targetAnimation) {
            currentAnimation = targetAnimation;
            currentAnimation.reset();
        }

        currentAnimation.update(deltaTime);
    }

    private void updateDeath(double deltaTime) {
        deathAnim.update(deltaTime);
        if (deathAnim.isFinished()) {
            active = false;
        }
    }

    // ===== Enemy Interface Implementation =====

    @Override
    public void hit(int damage) {
        if (currentState == State.DEAD) return;

        health -= damage;
        if (health <= 0) {
            die();
        }
    }

    @Override
    public boolean isDead() {
        return currentState == State.DEAD;
    }

    @Override
    public List<Bullet> getBullets() {
        return bullets;
    }

    @Override
    public int getScoreValue() {
        return Constants.SCORE_MINION_KILL;
    }

    // isActive() inherited from GameObject

    private void die() {
        currentState = State.DEAD;
        currentAnimation = deathAnim;
        deathAnim.reset();
        velocity.set(0, 0);
        logger.debug("Soldier killed at ({}, {})", position.getX(), position.getY());
    }

    @Override
    public void render(GraphicsContext gc) {
        // Render bullets first
        for (Bullet bullet : bullets) {
            bullet.render(gc);
        }

        // Render soldier sprite
        renderSprite(gc, currentAnimation.getCurrentFrame());

        // Debug: Draw hitbox
        if (false) {
            gc.setStroke(javafx.scene.paint.Color.YELLOW);
            gc.strokeRect(bounds.getX(), bounds.getY(),
                    bounds.getWidth(), bounds.getHeight());
        }
    }
}