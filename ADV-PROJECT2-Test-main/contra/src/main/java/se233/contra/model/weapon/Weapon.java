package se233.contra.model.weapon;

import se233.contra.model.Bullet;
import se233.contra.util.Vector2D;
import java.util.List;

public interface Weapon {
    List<Bullet> fire(double x, double y, Vector2D direction);
}