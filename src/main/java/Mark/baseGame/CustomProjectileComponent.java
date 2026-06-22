package Mark.baseGame;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class CustomProjectileComponent extends Component
{
    private final Weapon weapon;
    private final Point2D direction;
    private final double speed;

    private double distanceTraveled = 0;
    private double timeAlive = 0;

    public CustomProjectileComponent(Weapon weapon, Point2D direction, double speed)
    {
        this.weapon = weapon;
        this.direction = direction;
        this.speed = speed;
    }

    public Weapon getWeapon()
    {
        return weapon;
    }

    @Override
    public void onUpdate(double tpf)
    {
        timeAlive += tpf;
        double moveStep = speed * tpf;
        distanceTraveled += moveStep;

        if (distanceTraveled >= weapon.getRange())
        {
            entity.removeFromWorld();
            return;
        }

        switch (weapon.getFlightPath())
        {
            case STRAIGHT:
                entity.translate(direction.multiply(moveStep));
                break;

            case WOBBLE:
                entity.translate(direction.multiply(moveStep));

                double wobbleAmplitude = Settings.getProjectileWobbleAmplitude();
                double wobbleFrequency = Settings.getProjectileWobbleFrequency();
                double offsetY = Math.sin(timeAlive * wobbleFrequency) * wobbleAmplitude * tpf;
                entity.translateY(offsetY);
                break;

            case ARC:
                entity.translate(direction.multiply(moveStep));
                entity.translateY(timeAlive * Settings.getProjectileArcGravity() * tpf);
                break;
        }
        entity.rotateBy(Settings.getProjectileRotationSpeed() * tpf);
    }
}