package se233.contra.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.contra.util.Constants;
import se233.contra.view.SpriteLoader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ Unit tests for SCORING SYSTEM (ตรง requirement)
 * ทดสอบระบบคะแนน: การเพิ่มคะแนน, คะแนนเริ่มต้น, คะแนนสะสม
 */
class ScoringTest {
    private Player player;

    @BeforeEach
    void setUp() {
        try {
            SpriteLoader.initialize();
        } catch (Exception e) {
            // Ignore if sprites not available
        }
        player = new Player(100, Constants.GROUND_Y);
    }

    @Test
    void testInitialScore() {
        assertEquals(0, player.getScore(),
                "Player should start with score 0");
    }

    @Test
    void testAddScore() {
        player.addScore(100);
        assertEquals(100, player.getScore(),
                "Score should increase by the added amount");
    }

    @Test
    void testMultipleScoreAdditions() {
        player.addScore(100);
        player.addScore(200);
        player.addScore(50);

        assertEquals(350, player.getScore(),
                "Score should accumulate correctly");
    }

    @Test
    void testMinionKillScore() {
        int initialScore = player.getScore();
        player.addScore(Constants.SCORE_MINION_KILL);

        assertEquals(initialScore + Constants.SCORE_MINION_KILL,
                player.getScore(),
                "Should get correct score for killing minion");
    }

    @Test
    void testCannonDestroyScore() {
        int initialScore = player.getScore();
        player.addScore(Constants.SCORE_CANNON_DESTROY);

        assertEquals(initialScore + Constants.SCORE_CANNON_DESTROY,
                player.getScore(),
                "Should get correct score for destroying cannon");
    }

    @Test
    void testBossDefeatScore() {
        int initialScore = player.getScore();
        player.addScore(Constants.SCORE_BOSS_DEFEAT);

        assertEquals(initialScore + Constants.SCORE_BOSS_DEFEAT,
                player.getScore(),
                "Should get correct score for defeating boss");
    }

    @Test
    void testScoreAfterMultipleKills() {
        // Kill 3 minions
        for (int i = 0; i < 3; i++) {
            player.addScore(Constants.SCORE_MINION_KILL);
        }

        // Destroy 2 cannons
        for (int i = 0; i < 2; i++) {
            player.addScore(Constants.SCORE_CANNON_DESTROY);
        }

        // Defeat boss
        player.addScore(Constants.SCORE_BOSS_DEFEAT);

        int expectedScore = (3 * Constants.SCORE_MINION_KILL) +
                (2 * Constants.SCORE_CANNON_DESTROY) +
                Constants.SCORE_BOSS_DEFEAT;

        assertEquals(expectedScore, player.getScore(),
                "Score should calculate correctly for multiple actions");
    }

    @Test
    void testScoreDoesNotDecrease() {
        player.addScore(1000);
        int scoreAfterAdd = player.getScore();

        // Try to add 0
        player.addScore(0);

        assertEquals(scoreAfterAdd, player.getScore(),
                "Score should not change when adding 0");
        assertTrue(player.getScore() >= 0,
                "Score should never be negative");
    }

    @Test
    void testScorePersistsThroughDamage() {
        player.addScore(500);
        int scoreBefore = player.getScore();

        // Player takes damage
        player.hit();

        assertEquals(scoreBefore, player.getScore(),
                "Score should persist after taking damage");
    }

    @Test
    void testScorePersistsUntilDeath() {
        player.addScore(1000);

        // Kill player
        for (int i = 0; i < Constants.STARTING_LIVES; i++) {
            player.hit();
        }

        assertEquals(1000, player.getScore(),
                "Final score should be preserved after death");
    }

    @Test
    void testLargeScoreAccumulation() {
        int targetScore = 50000;
        int increment = 100;

        for (int i = 0; i < targetScore / increment; i++) {
            player.addScore(increment);
        }

        assertEquals(targetScore, player.getScore(),
                "Should handle large score values correctly");
    }
}