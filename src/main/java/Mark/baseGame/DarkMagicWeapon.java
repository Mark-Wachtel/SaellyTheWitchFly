package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

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

        double frameWidth = Settings.getDarkMagicWidth();
        double frameHeight = Settings.getDarkMagicHeight();

        double hitboxW = Settings.getDarkMagicHitboxWidth();
        double hitboxH = Settings.getDarkMagicHitboxHeight();

        Point2D actualSpawnPoint = new Point2D(spawnX, baseSpawnPoint.getY() - (frameHeight / 2.0));

        double offsetX = ((frameWidth - hitboxW) / 2.0) + Settings.getDarkMagicHitboxOffsetX();
        double offsetY = ((frameHeight - hitboxH) / 2.0) + Settings.getDarkMagicHitboxOffsetY();

        int cols = Settings.getDarkMagicAnimCols();
        int frames = Settings.getDarkMagicAnimNumFrames();
        int rows = (int) Math.ceil((double) frames / cols);

        double totalSheetWidth = frameWidth * cols;
        double totalSheetHeight = frameHeight * rows;

        Image resizedSheet = FXGL.texture(Settings.getLinkToDarkMagicImage(), totalSheetWidth, totalSheetHeight).getImage();

        AnimationChannel channel = new AnimationChannel(
                resizedSheet,
                cols,
                (int) frameWidth,
                (int) frameHeight,
                Duration.seconds(Settings.getDarkMagicAnimDurationSec()),
                0,
                frames - 1
        );

        AnimatedTexture animatedTexture = new AnimatedTexture(channel);
        animatedTexture.loop();

        FXGL.entityBuilder()
                .type(EntityType.PROJECTILE)
                .at(actualSpawnPoint)
                .view(animatedTexture)
                .bbox(new HitBox(Settings.getHitboxNameProjectile(), new Point2D(offsetX, offsetY), BoundingShape.box(hitboxW, hitboxH)))
                .with(new OffscreenCleanComponent())
                .with(new CollidableComponent(true))
                .with(new CustomProjectileComponent(this, shootDirection, Settings.getDarkMagicSpeed()))
                .zIndex(Settings.getZIndexGame() + 1)
                .buildAndAttach();

        FXGL.play(Settings.getLinkToShootSound());

        return true;
    }

    public static void spawnExplosion(Point2D impactPoint)
    {
        double frameWidth = Settings.getDarkMagicExplosionWidth();
        double frameHeight = Settings.getDarkMagicExplosionHeight();
        int cols = Settings.getDarkMagicExplosionCols();
        int frames = Settings.getDarkMagicExplosionNumFrames();
        int rows = (int) Math.ceil((double) frames / cols);

        double totalSheetWidth = frameWidth * cols;
        double totalSheetHeight = frameHeight * rows;

        var resizedSheet = FXGL.texture(Settings.getLinkToDarkMagicExplosionImage(), totalSheetWidth, totalSheetHeight).getImage();

        AnimationChannel channel = new AnimationChannel(
                resizedSheet,
                cols,
                (int) frameWidth,
                (int) frameHeight,
                Duration.seconds(Settings.getDarkMagicExplosionDurationSec()),
                0,
                frames - 1
        );

        AnimatedTexture animatedTexture = new AnimatedTexture(channel);

        Point2D centeredPosition = impactPoint.subtract(frameWidth / 2.0, frameHeight / 2.0);

        Entity explosionEntity = FXGL.entityBuilder()
                .at(centeredPosition)
                .view(animatedTexture)
                .zIndex(Settings.getZIndexForeground())
                .buildAndAttach();

        animatedTexture.play();

        animatedTexture.setOnCycleFinished(() -> explosionEntity.removeFromWorld());
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