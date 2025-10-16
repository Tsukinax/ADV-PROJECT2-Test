package se233.contra.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import se233.contra.util.Constants;
import se233.contra.util.Vector2D;
import se233.contra.view.SpriteLoader;

import java.util.ArrayList;
import java.util.List;

public class Boss1Cannon extends GameObject {
    private final Image cannonSprite;
    private final boolean isLeft;
    private int health;
    private double shootTimer;
    private final List<Bullet> bullets;

    public Boss1Cannon(double x, double y, boolean isLeft) {
        super(x, y, 24, 16);
        this.cannonSprite = SpriteLoader.getBoss1Cannon();
        this.isLeft = isLeft;
        this.health = Constants.BOSS1_CANNON_HP;
        this.shootTimer = 0;
        this.bullets = new ArrayList<>();
        this.facingRight = !isLeft; // Left cannon faces right, right cannon faces left
    }

    public void tryShoot(double deltaTime) {
        if (!active) return;

        shootTimer += deltaTime;
        if (shootTimer >= Constants.BOSS1_ATTACK_INTERVAL) {
            shoot();
            shootTimer = 0;
        }
    }

    private void shoot() {
        Vector2D bulletPos = new Vector2D(
                position.getX() + (facingRight ? bounds.getWidth() : -8),
                position.getY() + bounds.getHeight() / 2
        );

        Vector2D direction = new Vector2D(facingRight ? -1 : 1, 0); // Shoot toward player
        Bullet bullet = new Bullet(bulletPos.getX(), bulletPos.getY(), direction, false);
        bullets.add(bullet);
    }

    public void hit(int damage) {
        health -= damage;
        if (health <= 0) {
            active = false;
        }
    }

    @Override
    public void update(double deltaTime) {
        bullets.removeIf(b -> !b.isActive());
        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!active) return;

        // Render bullets
        for (Bullet bullet : bullets) {
            bullet.render(gc);
        }

        // Render cannon
        renderSprite(gc, cannonSprite);
    }

    public List<Bullet> getBullets() {
        return bullets;
    }
}