package se233.contra.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import se233.contra.util.Rectangle;
import se233.contra.util.Vector2D;
import se233.contra.view.Animation;

public abstract class GameObject {
    protected Vector2D position;
    protected Vector2D velocity;
    protected Rectangle bounds;
    protected boolean active;
    protected boolean facingRight;

    // Animation
    protected Animation currentAnimation;

    public GameObject(double x, double y, double width, double height) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0, 0);
        this.bounds = new Rectangle(x, y, width, height);
        this.active = true;
        this.facingRight = true;
    }

    // Abstract methods - must be implemented by subclasses
    public abstract void update(double deltaTime);
    public abstract void render(GraphicsContext gc);

    // Common update logic
    protected void updatePosition(double deltaTime) {
        position.setX(position.getX() + velocity.getX() * deltaTime);
        position.setY(position.getY() + velocity.getY() * deltaTime);
        updateBounds();
    }

    protected void updateBounds() {
        bounds.setPosition(position.getX(), position.getY());
    }

    // Collision detection
    public boolean collidesWith(GameObject other) {
        return this.active && other.active &&
                this.bounds.intersects(other.bounds);
    }

    // Getters/Setters
    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public Rectangle getBounds() { return bounds; }
    public boolean isActive() { return active; }
    public boolean isFacingRight() { return facingRight; }

    public void setPosition(double x, double y) {
        position.set(x, y);
        updateBounds();
    }

    public void setVelocity(double vx, double vy) {
        velocity.set(vx, vy);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    // Helper method for rendering with flip
    protected void renderSprite(GraphicsContext gc, Image sprite) {
        if (sprite == null) return;

        if (facingRight) {
            gc.drawImage(sprite, position.getX(), position.getY());
        } else {
            // Flip horizontally
            gc.save();
            gc.scale(-1, 1);
            gc.drawImage(sprite,
                    -position.getX() - sprite.getWidth(),
                    position.getY());
            gc.restore();
        }
    }

    // Cleanup
    public void destroy() {
        active = false;
    }
}