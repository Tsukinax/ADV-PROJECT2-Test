package se233.contra.util;

public class Vector2D {
    private double x;
    private double y;

    public Vector2D() {
        this(0, 0);
    }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D other) {
        this(other.x, other.y);
    }

    // Getters/Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Vector operations
    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(x - other.x, y - other.y);
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        double mag = magnitude();
        if (mag == 0) return new Vector2D(0, 0);
        return new Vector2D(x / mag, y / mag);
    }

    public double distance(Vector2D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // เพิ่ม: เมธอดสำหรับหมุนเวกเตอร์ (เป็นองศา)
    public Vector2D rotate(double degrees) {
        double radians = Math.toRadians(degrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double newX = this.x * cos - this.y * sin;
        double newY = this.x * sin + this.y * cos;
        return new Vector2D(newX, newY);
    }

    @Override
    public String toString() {
        return String.format("Vector2D(%.2f, %.2f)", x, y);
    }
}