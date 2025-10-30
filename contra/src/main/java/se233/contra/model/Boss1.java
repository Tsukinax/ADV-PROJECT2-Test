package se233.contra.model;

import javafx.scene.canvas.GraphicsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se233.contra.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Boss 1: Defense Wall
 * Demonstrates INHERITANCE from Boss abstract class
 * FIXED: ตำแหน่งให้ตรงกับระดับพื้น Constants.GROUND_Y
 */
public class Boss1 extends Boss {
    private static final Logger logger = LoggerFactory.getLogger(Boss1.class);

    public enum State {
        IDLE,
        ATTACKING,
        DOOR_OPENING,
        VULNERABLE,
        DOOR_CLOSING,
        EXPLODING
    }

    private State currentState;

    // Boss components - demonstrates COMPOSITION
    private Boss1Door door;
    private Boss1Cannon leftCannon;
    private Boss1Cannon rightCannon;
    private List<Boss1Core> cores;

    public Boss1(double x, double y) {
        super(x, y, 200, 236, Constants.BOSS1_DOOR_HP);
        this.currentState = State.IDLE;
        initializeComponents();
        logger.info("Boss 1 (Defense Wall) initialized at ({}, {})", x, y);
    }

    private void initializeComponents() {
        // Create door
        door = new Boss1Door(position.getX() + 60, position.getY() + 28);

        // ✅ แก้ไข: ปรับตำแหน่ง cannon ให้อยู่บนพื้นที่เหมาะสม
        // Cannon อยู่ส่วนล่างของ boss
        leftCannon = new Boss1Cannon(
                position.getX() + 20,
                position.getY() + 180,  // ใกล้พื้นมากขึ้น
                true
        );
        rightCannon = new Boss1Cannon(
                position.getX() + 140,
                position.getY() + 180,  // ใกล้พื้นมากขึ้น
                false
        );

        // Create cores (visual only)
        cores = new ArrayList<>();
        cores.add(new Boss1Core(position.getX() + 40, position.getY() + 80));
        cores.add(new Boss1Core(position.getX() + 80, position.getY() + 80));
        cores.add(new Boss1Core(position.getX() + 120, position.getY() + 80));
    }

    @Override
    protected void updateBehavior(double deltaTime) {
        switch (currentState) {
            case IDLE -> {
                if (stateTimer > 2.0) {
                    changeState(State.ATTACKING);
                }
            }
            case ATTACKING -> {
                attack(deltaTime);
                if (stateTimer > 5.0) {
                    changeState(State.DOOR_OPENING);
                }
            }
            case DOOR_OPENING -> {
                door.open();
                if (stateTimer > Constants.BOSS1_DOOR_ANIMATION_TIME) {
                    changeState(State.VULNERABLE);
                }
            }
            case VULNERABLE -> {
                if (stateTimer > Constants.BOSS1_VULNERABLE_TIME) {
                    changeState(State.DOOR_CLOSING);
                }
            }
            case DOOR_CLOSING -> {
                door.close();
                if (stateTimer > Constants.BOSS1_DOOR_ANIMATION_TIME) {
                    changeState(State.ATTACKING);
                }
            }
            case EXPLODING -> {
                if (stateTimer > 4.0) {
                    onDefeated();
                }
            }
        }
    }

    @Override
    protected void updateComponents(double deltaTime) {
        door.update(deltaTime);

        if (leftCannon.isActive()) {
            leftCannon.update(deltaTime);
        }

        if (rightCannon.isActive()) {
            rightCannon.update(deltaTime);
        }

        for (Boss1Core core : cores) {
            if (core.isActive()) {
                core.update(deltaTime);
            }
        }
    }

    @Override
    public void attack(double deltaTime) {
        // Cannons shoot at player
        leftCannon.tryShoot(deltaTime);
        rightCannon.tryShoot(deltaTime);
    }

    @Override
    public List<Bullet> getAllBullets() {
        List<Bullet> bullets = new ArrayList<>();
        bullets.addAll(leftCannon.getBullets());
        bullets.addAll(rightCannon.getBullets());
        return bullets;
    }

    @Override
    public void takeDamage(int damage) {
        if (currentState != State.VULNERABLE) {
            logger.debug("Boss door is closed, damage blocked");
            return;
        }

        hit(damage);

        if (health <= 0 && !defeated) {
            currentState = State.EXPLODING;
            stateTimer = 0;
        }
    }

    private void changeState(State newState) {
        logger.debug("Boss 1 state: {} -> {}", currentState, newState);
        currentState = newState;
        stateTimer = 0;
    }

    /**
     * Hit the door (only when vulnerable)
     */
    public void hitDoor(int damage) {
        takeDamage(damage);
    }

    /**
     * Hit a specific cannon
     */
    public void hitCannon(boolean isLeft, int damage) {
        Boss1Cannon cannon = isLeft ? leftCannon : rightCannon;
        cannon.hit(damage);

        if (!cannon.isActive()) {
            logger.info("{} cannon destroyed!", isLeft ? "Left" : "Right");
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Render cores (background layer)
        for (Boss1Core core : cores) {
            if (core.isActive()) {
                core.render(gc);
            }
        }

        // Render cannons
        if (leftCannon.isActive()) {
            leftCannon.render(gc);
        }

        if (rightCannon.isActive()) {
            rightCannon.render(gc);
        }

        // Render door (foreground layer)
        door.render(gc);

        // Debug: Draw hitbox
        if (false) {
            gc.setStroke(javafx.scene.paint.Color.RED);
            gc.strokeRect(bounds.getX(), bounds.getY(),
                    bounds.getWidth(), bounds.getHeight());
        }
    }

    // Getters
    public State getCurrentState() {
        return currentState;
    }

    public Boss1Door getDoor() {
        return door;
    }

    public Boss1Cannon getLeftCannon() {
        return leftCannon;
    }

    public Boss1Cannon getRightCannon() {
        return rightCannon;
    }

    public boolean isBossDefeated() {
        return defeated;
    }
}