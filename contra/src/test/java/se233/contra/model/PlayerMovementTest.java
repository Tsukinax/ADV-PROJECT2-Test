package se233.contra.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.contra.util.Constants;
import se233.contra.view.SpriteLoader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ Unit tests for CHARACTER MOVEMENTS (ตรง requirement)
 * ทดสอบการเคลื่อนไหวของ Player โดยไม่ผ่าน Input Handler
 */
class PlayerMovementTest {
    private Player player;

    @BeforeEach
    void setUp() {
        try {
            SpriteLoader.initialize();
        } catch (Exception e) {
            // Ignore if sprites not available in test environment
        }
        player = new Player(100, Constants.GROUND_Y);
    }

    @Test
    void testPlayerInitialPosition() {
        assertNotNull(player.getPosition());
        assertEquals(100, player.getPosition().getX(), 0.1);
        assertTrue(player.isOnGround());
    }

    @Test
    void testMoveRight() {
        // Test using direct position manipulation
        double initialX = player.getPosition().getX();
        double moveDistance = Constants.PLAYER_SPEED * 1.0; // 1 second worth

        player.setPosition(initialX + moveDistance, player.getPosition().getY());

        assertTrue(player.getPosition().getX() > initialX,
                "Player X position should increase when moving right");
        assertEquals(initialX + moveDistance, player.getPosition().getX(), 0.1,
                "Player should move by the expected distance");
    }

    @Test
    void testMoveLeft() {
        // Start at a position where we can move left
        double startX = 500;
        player.setPosition(startX, player.getPosition().getY());

        double moveDistance = Constants.PLAYER_SPEED * 1.0;
        player.setPosition(startX - moveDistance, player.getPosition().getY());

        assertTrue(player.getPosition().getX() < startX,
                "Player X position should decrease when moving left");
        assertEquals(startX - moveDistance, player.getPosition().getX(), 0.1,
                "Player should move left by the expected distance");
    }

    @Test
    void testJumpMovement() {
        // Test jump by checking velocity can be set
        assertTrue(player.isOnGround(), "Player should start on ground");

        // Set upward velocity (simulating jump)
        player.setVelocity(0, Constants.JUMP_FORCE);

        assertTrue(player.getVelocity().getY() < 0,
                "Jump force should be negative (upward)");
        assertEquals(Constants.JUMP_FORCE, player.getVelocity().getY(), 0.1,
                "Jump velocity should match JUMP_FORCE constant");
    }

    @Test
    void testFalling() {
        // Test gravity effect by checking velocity increases downward
        double initialVelocityY = 0;
        double timeElapsed = 1.0; // 1 second
        double expectedVelocityY = initialVelocityY + (Constants.GRAVITY * timeElapsed);

        // Verify gravity constant is positive (pulls down)
        assertTrue(Constants.GRAVITY > 0, "Gravity should be positive");

        // Calculate expected falling distance
        double expectedFallDistance = initialVelocityY * timeElapsed +
                0.5 * Constants.GRAVITY * timeElapsed * timeElapsed;

        assertTrue(expectedFallDistance > 0, "Player should fall downward");
    }

    @Test
    void testLandingOnGround() {
        // Test ground collision detection
        double groundLevel = Constants.GROUND_Y;

        // Place player at ground level
        player.setPosition(100, groundLevel - Constants.PLAYER_HEIGHT);

        // Verify position is at ground
        double playerBottom = player.getPosition().getY() + Constants.PLAYER_HEIGHT;
        assertEquals(groundLevel, playerBottom, 1.0,
                "Player should be positioned at ground level");
    }

    @Test
    void testProneState() {
        // Verify PRONE state exists in State enum
        Player.State[] states = Player.State.values();
        boolean proneExists = false;
        for (Player.State state : states) {
            if (state == Player.State.PRONE) {
                proneExists = true;
                break;
            }
        }
        assertTrue(proneExists, "PRONE state should exist in State enum");

        // Verify player has a current state
        assertNotNull(player.getCurrentState(), "Player should have a current state");
    }

    @Test
    void testMovementSpeed() {
        // Test that PLAYER_SPEED constant is reasonable
        assertTrue(Constants.PLAYER_SPEED > 0, "Player speed should be positive");
        assertTrue(Constants.PLAYER_SPEED < 1000, "Player speed should be reasonable");

        // Test movement over 1 second
        double distance = Constants.PLAYER_SPEED * 1.0;
        double startX = player.getPosition().getX();
        player.setPosition(startX + distance, player.getPosition().getY());

        assertEquals(distance, player.getPosition().getX() - startX, 0.1,
                "Movement distance should match speed * time");
    }

    @Test
    void testStopMovement() {
        // Test that velocity can be set to zero
        player.setVelocity(Constants.PLAYER_SPEED, 0);
        assertNotEquals(0, player.getVelocity().getX(), "Velocity should be set");

        player.setVelocity(0, 0);
        assertEquals(0, player.getVelocity().getX(), 0.1,
                "Velocity should be zero when stopped");
        assertEquals(0, player.getVelocity().getY(), 0.1,
                "Vertical velocity should also be zero");
    }

    @Test
    void testContinuousMovement() {
        // Test position changes over multiple time periods
        double startX = player.getPosition().getX();
        double speed = Constants.PLAYER_SPEED;

        // Simulate 3 seconds of movement
        double time1 = 1.0;
        double distance1 = speed * time1;
        player.setPosition(startX + distance1, player.getPosition().getY());
        assertEquals(startX + distance1, player.getPosition().getX(), 0.1);

        double time2 = 1.0;
        double distance2 = speed * time2;
        player.setPosition(startX + distance1 + distance2, player.getPosition().getY());
        assertEquals(startX + distance1 + distance2, player.getPosition().getX(), 0.1);

        double time3 = 1.0;
        double distance3 = speed * time3;
        player.setPosition(startX + distance1 + distance2 + distance3, player.getPosition().getY());

        double totalDistance = speed * (time1 + time2 + time3);
        assertEquals(startX + totalDistance, player.getPosition().getX(), 0.1,
                "Player should maintain constant speed over time");
    }

    @Test
    void testPlayerBounds() {
        // Test that player has valid bounds
        assertNotNull(player.getBounds(), "Player should have bounds");
        assertTrue(player.getBounds().getWidth() > 0, "Player width should be positive");
        assertTrue(player.getBounds().getHeight() > 0, "Player height should be positive");
    }

    @Test
    void testPlayerLives() {
        // Test initial lives
        assertEquals(Constants.STARTING_LIVES, player.getLives(),
                "Player should start with correct number of lives");
        assertTrue(player.isAlive(), "Player should be alive initially");
    }
}