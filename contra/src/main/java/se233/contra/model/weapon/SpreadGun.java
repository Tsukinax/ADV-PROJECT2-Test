package se233.contra.model.weapon;

import se233.contra.model.Bullet;
import se233.contra.util.Vector2D;
import java.util.ArrayList;
import java.util.List;

public class SpreadGun implements Weapon {
    @Override
    public List<Bullet> fire(double x, double y, Vector2D direction) {
        List<Bullet> bullets = new ArrayList<>();
        // สร้างกระสุน 3 นัดในทิศทางต่างกัน
        bullets.add(new Bullet(x, y, direction.normalize(), true)); // ตรงกลาง
        bullets.add(new Bullet(x, y, direction.rotate(-15).normalize(), true)); // เฉียงขึ้น
        bullets.add(new Bullet(x, y, direction.rotate(15).normalize(), true));  // เฉียงลง
        return bullets;
    }
}