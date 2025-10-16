package se233.contra.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se233.contra.controller.InputHandler;
import se233.contra.model.weapon.Rifle;
import se233.contra.model.weapon.SpreadGun;
import se233.contra.model.weapon.Weapon;
import se233.contra.util.Constants;
import se233.contra.util.Rectangle;
import se233.contra.util.Vector2D;
import se233.contra.view.Animation;
import se233.contra.view.SpriteLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Player with ground-hugging PRONE mechanic + Weapon System
 * รองรับทั้ง Rifle และ SpreadGun
 */
public class Player extends GameObject {
    private static final Logger logger = LoggerFactory.getLogger(Player.class);

    // ---- State ----
    public enum State {
        IDLE, RUNNING, JUMPING, FALLING, SHOOTING, PRONE, DEAD
    }

    private State currentState;

    // ---- Size / Hitbox ----
    private static final int NORMAL_WIDTH = Constants.PLAYER_WIDTH;
    private static final int NORMAL_HEIGHT = Constants.PLAYER_HEIGHT;
    private static final int PRONE_HEIGHT = 18;
    private static final int PRONE_WIDTH = 31;

    // ---- Physics ----
    private boolean onGround;
    private boolean isProne;

    // ---- Combat ----
    private final List<Bullet> bullets;
    private Weapon currentWeapon;
    private double shootCooldown;
    private static final double SHOOT_INTERVAL = 0.15;

    // ---- Stats ----
    private int lives;
    private int score;
    private boolean invincible;
    private double invincibleTimer;

    // ---- Animations ----
    private Animation idleAnim;
    private Animation runAnim;
    private Animation jumpAnim;
    private Animation proneAnim;
    private Animation shootAnim;
    private Animation deadAnim;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------
    public Player(double x, double y) {
        super(x, Constants.GROUND_Y - NORMAL_HEIGHT, NORMAL_WIDTH, NORMAL_HEIGHT);
        this.currentState = State.IDLE;
        this.onGround = true;
        this.isProne = false;
        this.bullets = new ArrayList<>();
        this.currentWeapon = new Rifle(); // เริ่มต้นด้วยปืนธรรมดา
        this.shootCooldown = 0;
        this.lives = Constants.STARTING_LIVES;
        this.score = 0;
        this.invincible = false;
        this.invincibleTimer = 0;

        initializeAnimations();
        currentAnimation = idleAnim;

        logger.info("Player created at ({}, {}) with Rifle",
                position.getX(), position.getY());
    }

    // ------------------------------------------------------------------------
    // Initialization
    // ------------------------------------------------------------------------
    private void initializeAnimations() {
        idleAnim = new Animation(SpriteLoader.getPlayerIdle(), Constants.IDLE_ANIMATION_SPEED);
        runAnim = new Animation(SpriteLoader.getPlayerRun(), Constants.RUN_ANIMATION_SPEED);
        jumpAnim = new Animation(SpriteLoader.getPlayerJump(), 0.1);
        proneAnim = new Animation(SpriteLoader.getPlayerProne(), 0.1);
        shootAnim = new Animation(SpriteLoader.getPlayerShoot(), Constants.SHOOT_ANIMATION_SPEED);
        deadAnim = new Animation(SpriteLoader.getExplosion(), 0.1, false);
    }

    // ------------------------------------------------------------------------
    // Update
    // ------------------------------------------------------------------------
    @Override
    public void update(double deltaTime) {
        if (currentState == State.DEAD) {
            deadAnim.update(deltaTime);
            if (deadAnim.isFinished()) {
                active = false;
            }
            return;
        }

        // Update timers
        if (shootCooldown > 0) {
            shootCooldown -= deltaTime;
        }

        ///if (invincible) {
           /// invincibleTimer -= deltaTime;
           /// if (invincibleTimer <= 0) {
            ///    invincible = false;
     ///       }
    ///    }

        // Handle input
        handleInput();

        // Apply physics
        applyPhysics(deltaTime);

        // Update position
        updatePosition(deltaTime);

        // Check ground collision
        checkGroundCollision();

        // Update animation
        updateAnimation(deltaTime);

        // Update bullets
        updateBullets(deltaTime);
    }

    // ------------------------------------------------------------------------
    // Input Handling
    // ------------------------------------------------------------------------
    private void handleInput() {
        InputHandler input = InputHandler.getInstance();

        if (currentState == State.DEAD) {
            return;
        }

        // Movement (LEFT/RIGHT)
        double moveSpeed = 0;
        if (input.isKeyPressed(Constants.KEY_LEFT)) {
            moveSpeed = -Constants.PLAYER_SPEED;
            facingRight = false;
        }
        if (input.isKeyPressed(Constants.KEY_RIGHT)) {
            moveSpeed = Constants.PLAYER_SPEED;
            facingRight = true;
        }

        // PRONE MECHANIC
        boolean downPressed = input.isKeyPressed(Constants.KEY_DOWN);

        if (downPressed && onGround && !isProne) {
            enterProne();
        } else if (!downPressed && isProne) {
            exitProne();
        }

        // ถ้า prone อยู่ ห้ามเดิน
        if (isProne) {
            moveSpeed = 0;
        }

        velocity.setX(moveSpeed);

        // Jump (ห้ามกระโดดตอน prone)
        if (input.isKeyJustPressed(Constants.KEY_JUMP) && onGround && !isProne) {
            velocity.setY(Constants.JUMP_FORCE);
            onGround = false;
            currentState = State.JUMPING;
            logger.debug("Player jumped");
        }

        // ✅ Shoot - Z key (ยิงด้วยอาวุธปัจจุบัน)
        if (input.isKeyJustPressed(Constants.KEY_SHOOT) && shootCooldown <= 0) {
            shoot();
            shootCooldown = SHOOT_INTERVAL;
        }

        // ✅ Switch to SpreadGun - S key (Special Weapon)
        if (input.isKeyJustPressed(KeyCode.S)) {
            switchWeapon(new SpreadGun());
        }

        // ✅ Switch to Rifle - A key (Normal Weapon)
        if (input.isKeyJustPressed(KeyCode.A)) {
            switchWeapon(new Rifle());
        }
    }

    // ------------------------------------------------------------------------
    // PRONE MECHANICS
    // ------------------------------------------------------------------------
    private void enterProne() {
        isProne = true;
        currentState = State.PRONE;

        double heightDiff = NORMAL_HEIGHT - PRONE_HEIGHT;
        position.setY(position.getY() + heightDiff);
        bounds = new Rectangle(position.getX(), position.getY(), PRONE_WIDTH, PRONE_HEIGHT);

        logger.debug("Enter PRONE - Y: {} (shifted down by {})", position.getY(), heightDiff);
    }

    private void exitProne() {
        isProne = false;

        double heightDiff = NORMAL_HEIGHT - PRONE_HEIGHT;
        position.setY(position.getY() - heightDiff);
        bounds = new Rectangle(position.getX(), position.getY(), NORMAL_WIDTH, NORMAL_HEIGHT);

        if (onGround) {
            currentState = Math.abs(velocity.getX()) > 1 ? State.RUNNING : State.IDLE;
        }

        logger.debug("Exit PRONE - Y: {} (shifted up by {})", position.getY(), heightDiff);
    }

    // ------------------------------------------------------------------------
    // Physics
    // ------------------------------------------------------------------------
    private void applyPhysics(double deltaTime) {
        if (!onGround) {
            velocity.setY(velocity.getY() + Constants.GRAVITY * deltaTime);

            if (velocity.getY() > 600) {
                velocity.setY(600);
            }
        }
    }

    private void checkGroundCollision() {
        double currentHeight = isProne ? PRONE_HEIGHT : NORMAL_HEIGHT;
        double playerBottom = position.getY() + currentHeight;

        if (playerBottom >= Constants.GROUND_Y) {
            position.setY(Constants.GROUND_Y - currentHeight);
            velocity.setY(0);
            onGround = true;

            if (!isProne) {
                if (Math.abs(velocity.getX()) > 1) {
                    currentState = State.RUNNING;
                } else {
                    currentState = State.IDLE;
                }
            }
        } else {
            onGround = false;
            if (!isProne) {
                currentState = velocity.getY() < 0 ? State.JUMPING : State.FALLING;
            }
        }

        updateBounds();
    }

    @Override
    protected void updateBounds() {
        if (isProne) {
            bounds = new Rectangle(position.getX(), position.getY(), PRONE_WIDTH, PRONE_HEIGHT);
        } else {
            bounds = new Rectangle(position.getX(), position.getY(), NORMAL_WIDTH, NORMAL_HEIGHT);
        }
    }

    // ------------------------------------------------------------------------
    // Combat System
    // ------------------------------------------------------------------------
    private void shoot() {
        double muzzleX, muzzleY;

        if (isProne) {
            muzzleX = facingRight ?
                    (position.getX() + PRONE_WIDTH) :
                    (position.getX() - 8);
            muzzleY = position.getY() + PRONE_HEIGHT / 2;
        } else {
            muzzleX = facingRight ?
                    (position.getX() + NORMAL_WIDTH - 2) :
                    (position.getX() + 2);
            muzzleY = position.getY() + NORMAL_HEIGHT * 0.35;
        }

        // ✅ ใช้ Weapon System
        Vector2D direction = new Vector2D(facingRight ? 1 : -1, 0);
        List<Bullet> newBullets = currentWeapon.fire(muzzleX, muzzleY, direction);
        bullets.addAll(newBullets);

        logger.debug("Player shot {} bullet(s) with {}",
                newBullets.size(), currentWeapon.getClass().getSimpleName());
    }

    private void switchWeapon(Weapon newWeapon) {
        currentWeapon = newWeapon;
        logger.info("Weapon switched to: {}", newWeapon.getClass().getSimpleName());
    }

    private void updateBullets(double deltaTime) {
        bullets.removeIf(bullet -> !bullet.isActive());
        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);
        }
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------
    private void updateAnimation(double deltaTime) {
        Animation targetAnimation = switch (currentState) {
            case IDLE -> idleAnim;
            case RUNNING -> runAnim;
            case JUMPING, FALLING -> jumpAnim;
            case PRONE -> proneAnim;
            case SHOOTING -> shootAnim;
            case DEAD -> deadAnim;
        };

        if (currentAnimation != targetAnimation) {
            currentAnimation = targetAnimation;
            currentAnimation.reset();
        }

        currentAnimation.update(deltaTime);
    }

    // ------------------------------------------------------------------------
    // Render
    // ------------------------------------------------------------------------
    @Override
    public void render(GraphicsContext gc) {
        if (!active) return;

        // Render bullets
        for (Bullet bullet : bullets) {
            bullet.render(gc);
        }

        // Render player sprite
        renderSprite(gc, currentAnimation.getCurrentFrame());

        // Debug mode
        if (false) {
            gc.setStroke(javafx.scene.paint.Color.GREEN);
            gc.strokeRect(bounds.getX(), bounds.getY(),
                    bounds.getWidth(), bounds.getHeight());

            gc.setStroke(javafx.scene.paint.Color.RED);
            gc.strokeLine(0, Constants.GROUND_Y, Constants.SCREEN_WIDTH, Constants.GROUND_Y);
        }
    }

    // ------------------------------------------------------------------------
    // Damage & Death
    // ------------------------------------------------------------------------
    public void hit() {
        if (invincible || currentState == State.DEAD) {
            return;
        }

        lives--;
        logger.info("Player hit! Lives remaining: {}", lives);

        if (lives <= 0) {
            die();
        }
    }

    private void die() {
        currentState = State.DEAD;
        velocity.set(0, 0);
        currentAnimation = deadAnim;
        deadAnim.reset();
        logger.info("Player died! Final score: {}", score);
    }

    // ------------------------------------------------------------------------
    // Getters & Setters
    // ------------------------------------------------------------------------
    public boolean isAlive() {
        return currentState != State.DEAD;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        score += points;
        logger.debug("Score +{} = {}", points, score);
    }

    public State getCurrentState() {
        return currentState;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public void setWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
        logger.info("Weapon set to: {}", weapon.getClass().getSimpleName());
    }
}

