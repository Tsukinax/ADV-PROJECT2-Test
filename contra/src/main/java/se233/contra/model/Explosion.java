package se233.contra.model;

import javafx.scene.canvas.GraphicsContext;
import se233.contra.util.Constants;
import se233.contra.view.Animation;
import se233.contra.view.SpriteLoader;

public class Explosion extends GameObject {
    private final Animation explosionAnimation;

    public Explosion(double x, double y) {
        super(x, y, Constants.EXPLOSION_SIZE, Constants.EXPLOSION_SIZE);
        explosionAnimation = new Animation(
                SpriteLoader.getExplosion(),
                Constants.EXPLOSION_ANIMATION_SPEED,
                false  // Don't loop
        );
        currentAnimation = explosionAnimation;
    }

    @Override
    public void update(double deltaTime) {
        if (explosionAnimation.isFinished()) {
            active = false;
            return;
        }
        explosionAnimation.update(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!active) return;
        renderSprite(gc, explosionAnimation.getCurrentFrame());
    }
}