package se233.contra.model.weapon;

import se233.contra.model.Bullet;
import se233.contra.util.Vector2D;
import java.util.ArrayList;
import java.util.List;

public class Rifle implements Weapon {
    @Override
    public List<Bullet> fire(double x, double y, Vector2D direction) {
        List<Bullet> bullets = new ArrayList<>();
        bullets.add(new Bullet(x, y, direction, true));
        return bullets;
    }
}