package se233.contra.util;

public class Rectangle {
    private double x, y, width, height;

    public Rectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Getters/Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Collision detection
    public boolean intersects(Rectangle other) {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
    }

    public boolean contains(double px, double py) {
        return px >= x && px <= x + width &&
                py >= y && py <= y + height;
    }

    // Boundaries
    public double getLeft() { return x; }
    public double getRight() { return x + width; }
    public double getTop() { return y; }
    public double getBottom() { return y + height; }
    public double getCenterX() { return x + width / 2; }
    public double getCenterY() { return y + height / 2; }

    @Override
    public String toString() {
        return String.format("Rectangle(%.0f, %.0f, %.0f, %.0f)", x, y, width, height);
    }
}