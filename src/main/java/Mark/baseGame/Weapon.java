package Mark.baseGame;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

public interface Weapon
{
    boolean shoot(Entity player, Point2D targetPos);
    WeaponType getWeaponType();
    double getCooldown();
    double getRange();
    ProjectileFlightPath getFlightPath();
    int getDamage();

}
