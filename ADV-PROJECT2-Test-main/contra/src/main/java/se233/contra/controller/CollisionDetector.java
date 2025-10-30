package se233.contra.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se233.contra.model.Boss1;
import se233.contra.model.Bullet;
import se233.contra.model.Player;
import se233.contra.model.Soldier;
import se233.contra.exception.GameException;
import se233.contra.util.Constants;

import java.util.List;

public class CollisionDetector {
    private static final Logger logger = LoggerFactory.getLogger(CollisionDetector.class);

    // Player vs Enemy bullets
    public static void checkPlayerBulletCollisions(Player player, List<Bullet> enemyBullets) {
        if (!player.isActive() || player.isInvincible()) {
            return;
        }

        try {
            for (Bullet bullet : enemyBullets) {
                // ✅ FIX: เช็คว่ากระสุนยังไม่เคย hit และเป็นกระสุนของศัตรู
                if (!bullet.isActive() || bullet.isPlayerBullet() || bullet.hasHit()) {
                    continue;
                }

                if (player.collidesWith(bullet)) {
                    player.hit();
                    bullet.onHit();
                    logger.info("Player hit by enemy bullet");
                    break; // Only one hit per frame
                }
            }
        } catch (Exception e) {
            throw new GameException("Error in player bullet collision detection",
                    GameException.ErrorType.COLLISION_ERROR, e);
        }
    }

    // Player bullets vs Soldiers
    public static void checkPlayerBulletsVsSoldiers(List<Bullet> playerBullets,
                                                    List<Soldier> soldiers,
                                                    Player player) {
        try {
            for (Bullet bullet : playerBullets) {
                // ✅ FIX: เช็คว่ากระสุนยังไม่เคย hit
                if (!bullet.isActive() || !bullet.isPlayerBullet() || bullet.hasHit()) {
                    continue;
                }

                for (Soldier soldier : soldiers) {
                    if (!soldier.isActive() || soldier.isDead()) continue;

                    if (bullet.collidesWith(soldier)) {
                        soldier.hit(bullet.getDamage());
                        bullet.onHit();

                        if (soldier.isDead()) {
                            player.addScore(Constants.SCORE_MINION_KILL);
                            logger.info("Soldier killed! Score: +{}", Constants.SCORE_MINION_KILL);
                        }
                        break; // Bullet hit, stop checking other soldiers
                    }
                }
            }
        } catch (Exception e) {
            throw new GameException("Error in player bullets vs soldiers collision",
                    GameException.ErrorType.COLLISION_ERROR, e);
        }
    }

    // Player bullets vs Boss 1
    public static void checkPlayerBulletsVsBoss1(List<Bullet> playerBullets,
                                                 Boss1 boss,
                                                 Player player) {
        if (!boss.isActive()) return;

        try {
            for (Bullet bullet : playerBullets) {
                // ✅ FIX: เช็คว่ากระสุนยังไม่เคย hit
                if (!bullet.isActive() || !bullet.isPlayerBullet() || bullet.hasHit()) {
                    continue;
                }

                // Check door collision (only when vulnerable)
                if (boss.getCurrentState() == Boss1.State.VULNERABLE) {
                    if (bullet.collidesWith(boss.getDoor())) {
                        boss.hitDoor(bullet.getDamage());
                        bullet.onHit();

                        if (boss.isBossDefeated()) {
                            player.addScore(Constants.SCORE_BOSS_DEFEAT);
                            logger.info("Boss 1 defeated! Score: +{}", Constants.SCORE_BOSS_DEFEAT);
                        }
                        continue;
                    }
                }

                // Check left cannon collision
                if (boss.getLeftCannon().isActive() &&
                        bullet.collidesWith(boss.getLeftCannon())) {
                    boss.hitCannon(true, bullet.getDamage());
                    bullet.onHit();

                    if (!boss.getLeftCannon().isActive()) {
                        player.addScore(Constants.SCORE_CANNON_DESTROY);
                        logger.info("Left cannon destroyed! Score: +{}",
                                Constants.SCORE_CANNON_DESTROY);
                    }
                    continue;
                }

                // Check right cannon collision
                if (boss.getRightCannon().isActive() &&
                        bullet.collidesWith(boss.getRightCannon())) {
                    boss.hitCannon(false, bullet.getDamage());
                    bullet.onHit();

                    if (!boss.getRightCannon().isActive()) {
                        player.addScore(Constants.SCORE_CANNON_DESTROY);
                        logger.info("Right cannon destroyed! Score: +{}",
                                Constants.SCORE_CANNON_DESTROY);
                    }
                }
            }
        } catch (Exception e) {
            throw new GameException("Error in player bullets vs boss collision",
                    GameException.ErrorType.COLLISION_ERROR, e);
        }
    }

    // Boss bullets vs Player
    public static void checkBossBulletsVsPlayer(Boss1 boss, Player player) {
        if (!boss.isActive() || !player.isActive() || player.isInvincible()) {
            return;
        }

        try {
            List<Bullet> bossBullets = boss.getAllBullets();
            checkPlayerBulletCollisions(player, bossBullets);
        } catch (Exception e) {
            throw new GameException("Error in boss bullets vs player collision",
                    GameException.ErrorType.COLLISION_ERROR, e);
        }
    }

    // Soldier bullets vs Player
    public static void checkSoldierBulletsVsPlayer(List<Soldier> soldiers, Player player) {
        if (!player.isActive() || player.isInvincible()) {
            return;
        }

        try {
            for (Soldier soldier : soldiers) {
                if (!soldier.isActive()) continue;
                checkPlayerBulletCollisions(player, soldier.getBullets());
            }
        } catch (Exception e) {
            throw new GameException("Error in soldier bullets vs player collision",
                    GameException.ErrorType.COLLISION_ERROR, e);
        }
    }
}