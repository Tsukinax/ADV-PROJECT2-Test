package se233.contra.model;

import javafx.scene.canvas.GraphicsContext;
import se233.contra.view.Animation;
import se233.contra.view.SpriteLoader;

public class Boss1Core extends GameObject {
    private final Animation glowAnimation;

    public Boss1Core(double x, double y) {
        super(x, y, 32, 32);
        this.glowAnimation = new Animation(SpriteLoader.getBoss1Core(), 0.2);
        this.currentAnimation = glowAnimation;
    }

    @Override
    public void update(double deltaTime) {
        glowAnimation.update(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!active) return;
        renderSprite(gc, glowAnimation.getCurrentFrame());
    }
}