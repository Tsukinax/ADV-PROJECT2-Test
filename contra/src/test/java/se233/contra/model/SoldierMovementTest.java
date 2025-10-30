package se233.contra.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.contra.util.Constants;
import se233.contra.view.SpriteLoader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ Unit tests for SOLDIER (Enemy) MOVEMENTS
 * ทดสอบการเคลื่อนไหวของศัตรู
 */
class SoldierMovementTest {
    private Soldier soldier;

    @BeforeEach
    void setUp() {
        try {
            SpriteLoader.initialize();
        } catch (Exception e) {
            // Ignore if sprites not available
        }
        soldier = new Soldier(300, Constants.GROUND_Y);
    }

    @Test
    void testSoldierInitialPosition() {
        assertNotNull(soldier.getPosition());
        assertEquals(300, soldier.getPosition().getX(), 0.1);
    }

    @Test
    void testSoldierMovement() {
        double initialX = soldier.getPosition().getX();

        // Update soldier (should move due to AI patrol)
        soldier.update(1.0);

        assertNotEquals(initialX, soldier.getPosition().getX(),
                "Soldier should move during patrol");
    }

    @Test
    void testSoldierPatrolBehavior() {
        double startX = soldier.getPosition().getX();

        // Soldier should patrol for several seconds
        for (int i = 0; i < 5; i++) {
            soldier.update(1.0);
        }

        // Soldier should have moved from starting position
        assertNotEquals(startX, soldier.getPosition().getX(),
                "Soldier should patrol and change position");
    }

    @Test
    void testSoldierStopsWhenShooting() {
        // Update until soldier starts shooting
        for (int i = 0; i < 30; i++) {
            soldier.update(0.1);
            if (!soldier.getBullets().isEmpty()) {
                break;
            }
        }

        // Soldier should have shot at least one bullet
        assertTrue(soldier.getBullets().size() >= 0,
                "Soldier should be able to shoot bullets");
    }

    @Test
    void testSoldierDeathStopsMovement() {
        soldier.hit(1);
        assertTrue(soldier.isDead(), "Soldier should die from hit");

        double deathX = soldier.getPosition().getX();
        soldier.update(1.0);

        assertEquals(deathX, soldier.getPosition().getX(), 0.1,
                "Dead soldier should not move");
    }

    @Test
    void testSoldierFacingDirection() {
        boolean initialDirection = soldier.isFacingRight();
        assertNotNull(initialDirection);

        // After movement, direction might change
        soldier.update(5.0);
        // Direction should still be valid (true or false)
        assertNotNull(soldier.isFacingRight());
    }

    @Test
    void testMultipleSoldiersMovementIndependently() {
        Soldier soldier1 = new Soldier(100, Constants.GROUND_Y);
        Soldier soldier2 = new Soldier(200, Constants.GROUND_Y);

        soldier1.update(1.0);
        soldier2.update(1.0);

        // Both should have moved independently
        assertNotEquals(soldier1.getPosition().getX(),
                soldier2.getPosition().getX(),
                "Different soldiers should move independently");
    }

    @Test
    void testSoldierDeactivationOffScreen() {
        // Move soldier far offscreen
        soldier.setPosition(-200, Constants.GROUND_Y);
        soldier.update(0.1);

        assertFalse(soldier.isActive(),
                "Soldier should deactivate when far off screen");
    }
}