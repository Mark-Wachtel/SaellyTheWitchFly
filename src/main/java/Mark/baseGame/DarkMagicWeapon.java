package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import javafx.geometry.Point2D;

public class DarkMagicWeapon implements Weapon
{

    @Override
    public boolean shoot(Entity player, Point2D targetPos)
    {
        double spawnX = player.getRightX();
        double spawnY = player.getCenter().getY();
        Point2D baseSpawnPoint = new Point2D(spawnX, spawnY);

        if (targetPos.getX() <= baseSpawnPoint.getX())
        {
            return false;
        }

        Point2D shootDirection = targetPos.subtract(baseSpawnPoint).normalize();

        double width = Settings.getDarkMagicWidth();
        double height = Settings.getDarkMagicHeight();
        double hitboxW = Settings.getDarkMagicHitboxWidth();
        double hitboxH = Settings.getDarkMagicHitboxHeight();

        Point2D actualSpawnPoint = new Point2D(spawnX, baseSpawnPoint.getY() - (height / 2.0));
        double offsetX = ((width - hitboxW) / 2.0) + Settings.getDarkMagicHitboxOffsetX();
        double offsetY = ((height - hitboxH) / 2.0) + Settings.getDarkMagicHitboxOffsetY();

        var weaponTexture = FXGL.texture(Settings.getLinkToDarkMagicImage(), width, height);

        FXGL.entityBuilder()
                .type(EntityType.PROJECTILE)
                .at(actualSpawnPoint)
                .view(weaponTexture)
                .bbox(new com.almasb.fxgl.physics.HitBox(Settings.getHitboxNameProjectile(), new Point2D(offsetX, offsetY), BoundingShape.box(hitboxW, hitboxH)))
                .with(new OffscreenCleanComponent())
                .with(new CollidableComponent(true))
                .with(new CustomProjectileComponent(this, shootDirection, Settings.getDarkMagicSpeed()))
                .zIndex(Settings.getZIndexGame() + 1)
                .buildAndAttach();

        FXGL.play(Settings.getLinkToShootSound());

        return true;
    }

    @Override
    public double getCooldown()
    {
        return Settings.getDarkMagicCooldownSec();
    }

    @Override
    public int getDamage()
    {
        return Settings.getDarkMagicDamage();
    }

    @Override
    public WeaponType getWeaponType()
    {
        return WeaponType.DARK_MAGIC;
    }

    @Override
    public double getRange() { return Settings.getDarkMagicRange(); }

    @Override
    public ProjectileFlightPath getFlightPath() { return ProjectileFlightPath.WOBBLE; }

}
