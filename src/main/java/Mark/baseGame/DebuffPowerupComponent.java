package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

public class DebuffPowerupComponent extends Component
{
    private final DebuffType type;

    public DebuffPowerupComponent(DebuffType type)
    {
        this.type = type;
    }

    @Override
    public void onUpdate(double tpf)
    {
        double speed = FXGL.getd(Settings.getKeyBarrierSpeed());
        entity.translateX(-speed * tpf);
        if (entity.getRightX() < Settings.getDespawnXBoundary()) entity.removeFromWorld();
    }

    public void activateEffect()
    {
        FXGL.set(Settings.getKeyIsDebuffActive(), true);

        switch (type)
        {
            case GAP_NARROWER:
                double originalGap = Settings.getBarrierGap();
                double narrowGap = originalGap * Settings.getDebuffGapNarrowerRatio();
                double gapDiff = originalGap - narrowGap;

                FXGL.set(Settings.getKeyCurrentBarrierGap(), narrowGap);
                FXGL.play(Settings.getLinkToGapNarrowerPickupSound());

                FXGL.getGameWorld().getEntitiesByType(EntityType.BARRIER).forEach(barrier -> {
                    if (barrier.hasComponent(ObstacleComponent.class)) {
                        if (barrier.getY() < 0) {
                            barrier.translateY(gapDiff / 2.0);
                        } else {
                            barrier.translateY(-gapDiff / 2.0);
                        }
                    }
                });

                FXGL.getGameTimer().runOnceAfter(() ->
                {
                    FXGL.set(Settings.getKeyCurrentBarrierGap(), originalGap);

                    FXGL.getGameWorld().getEntitiesByType(EntityType.BARRIER).forEach(barrier -> {
                        if (barrier.hasComponent(ObstacleComponent.class)) {
                            if (barrier.getY() < 0) {
                                barrier.translateY(-gapDiff / 2.0);
                            } else {
                                barrier.translateY(gapDiff / 2.0);
                            }
                        }
                    });
                }, Duration.seconds(Settings.getGapNarrowerDebuffDurationSec()));
                break;

            case SPEED_UP:
                double oldSpeed = FXGL.getd(Settings.getKeyBarrierSpeed());
                double multiplier = Settings.getSpeedUpDebuffRatio();

                FXGL.set(Settings.getKeyBarrierSpeed(), oldSpeed * multiplier);
                FXGL.play(Settings.getLinkToSpeedUpPickupSound());

                FXGL.getGameTimer().runOnceAfter(() ->
                {
                    double currentSpeed = FXGL.getd(Settings.getKeyBarrierSpeed());
                    FXGL.set(Settings.getKeyBarrierSpeed(), currentSpeed / multiplier);
                }, Duration.seconds(Settings.getSpeedUpDebuffDurationSec()));
                break;
        }
    }

    public DebuffType getType()
    {
        return type;
    }
}