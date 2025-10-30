package se233.contra.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import se233.contra.util.Rectangle;
import se233.contra.view.SpriteLoader;

public class Boss1Door extends GameObject {
    private final Image doorSprite;
    private boolean isOpen;

    public Boss1Door(double x, double y) {
        super(x, y, 80, 180);
        this.doorSprite = SpriteLoader.getBoss1Door();
        this.isOpen = false;
    }

    public void open() {
        isOpen = true;
    }

    public void close() {
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void update(double deltaTime) {
        // Door animation would go here
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!active) return;
        gc.drawImage(doorSprite, position.getX(), position.getY());
    }

    public Rectangle getHitbox() {
        return bounds;
    }
}