package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

public class BuffPowerupComponent extends Component
{
    private final BuffType type;

    public BuffPowerupComponent(BuffType type)
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
        if (type != BuffType.EXTRA_LIFE) FXGL.set(Settings.getKeyIsBuffActive(), true);

        switch (type)
        {
            case SLOW_MOTION:
                double oldSpeed = FXGL.getd(Settings.getKeyBarrierSpeed());
                double ratio = Settings.getSlowMotionBuffPowerupRatio();
                FXGL.set(Settings.getKeyBarrierSpeed(), oldSpeed * ratio);
                FXGL.play(Settings.getLinkToSlowMotionPickupSound());

                FXGL.set(Settings.getKeyActiveBuffImagePath(), Settings.getLinkToSlowMotionImage());

                FXGL.getGameTimer().runOnceAfter(() ->
                {
                    double currentSpeed = FXGL.getd(Settings.getKeyBarrierSpeed());
                    FXGL.set(Settings.getKeyBarrierSpeed(), currentSpeed / ratio);
                    FXGL.set(Settings.getKeyIsBuffActive(), false);

                    FXGL.set(Settings.getKeyActiveBuffImagePath(), "");
                }, Duration.seconds(Settings.getSlowMotionBuffPowerupDurationSeconds()));
                break;

            case SCORE_X10:
                Settings.setScoreMulti(Settings.getScoreMulti() * Settings.getScorePowerupMultiplierBonus());
                FXGL.play(Settings.getLinkToScorex10PickupSound());

                FXGL.set(Settings.getKeyActiveBuffImagePath(), Settings.getLinkToScorex10Image());

                FXGL.getGameTimer().runOnceAfter(() ->
                {
                    Settings.setScoreMulti(Settings.getScoreMulti() / Settings.getScorePowerupMultiplierBonus());
                    FXGL.set(Settings.getKeyIsBuffActive(), false);

                    FXGL.set(Settings.getKeyActiveBuffImagePath(), "");
                }, Duration.seconds(Settings.getScoreX10DurationSeconds()));
                break;

            case INVULNERABILITY:
                FXGL.set(Settings.getKeyIsInvulnerable(), true);
                FXGL.play(Settings.getLinkToInvulnerablePickupSound());

                FXGL.set(Settings.getKeyActiveBuffImagePath(), Settings.getLinkToInvulnerableImage());

                FXGL.getGameTimer().runOnceAfter(() ->
                {
                    FXGL.set(Settings.getKeyIsInvulnerable(), false);
                    FXGL.set(Settings.getKeyIsBuffActive(), false);

                    FXGL.set(Settings.getKeyActiveBuffImagePath(), "");
                }, Duration.seconds(Settings.getInvulnerableDurationInSeconds()));
                break;

            case EXTRA_LIFE:
                FXGL.inc(Settings.getKeyLives(), +Settings.getExtraLifePowerupBonus());
                FXGL.play(Settings.getLinkToExtraLivesPickupSound());
                break;

            case SHRINK:
                var playerOpt = FXGL.getGameWorld().getSingletonOptional(EntityType.PLAYER);
                if (playerOpt.isEmpty()) return;
                Entity player = playerOpt.get();

                double originalScale = Settings.getInitPlayerScale();
                double shrinkScale = originalScale * Settings.getBuffShrinkScaleRatio();

                player.setScaleUniform(shrinkScale);
                FXGL.play(Settings.getLinkToShrinkPickupSound());
                FXGL.set(Settings.getKeyActiveBuffImagePath(), Settings.getLinkToShrinkImage());

                FXGL.getGameTimer().runOnceAfter(() ->
                {
                    player.setScaleUniform(originalScale);
                    FXGL.set(Settings.getKeyIsBuffActive(), false);
                    FXGL.set(Settings.getKeyActiveBuffImagePath(), "");
                }, Duration.seconds(Settings.getBuffShrinkDurationSec()));
                break;

            case GAP_WIDER:
                double originalGap = Settings.getBarrierGap();
                double wideGap = originalGap * Settings.getBuffGapWiderRatio();
                double gapDiff = wideGap - originalGap;

                FXGL.set(Settings.getKeyCurrentBarrierGap(), wideGap);
                FXGL.play(Settings.getLinkToGapWiderPickupSound());
                FXGL.set(Settings.getKeyActiveBuffImagePath(), Settings.getLinkToGapWiderImage());

                FXGL.getGameWorld().getEntitiesByType(EntityType.BARRIER).forEach(barrier -> {
                    if (barrier.hasComponent(ObstacleComponent.class)) {
                        if (barrier.getY() < 0) {
                            barrier.translateY(-gapDiff / 2.0);
                        } else {
                            barrier.translateY(gapDiff / 2.0);
                        }
                    }
                });

                FXGL.getGameTimer().runOnceAfter(() ->
                {
                    FXGL.set(Settings.getKeyCurrentBarrierGap(), originalGap);
                    FXGL.set(Settings.getKeyIsBuffActive(), false);
                    FXGL.set(Settings.getKeyActiveBuffImagePath(), "");

                    FXGL.getGameWorld().getEntitiesByType(EntityType.BARRIER).forEach(barrier -> {
                        if (barrier.hasComponent(ObstacleComponent.class)) {

                            if (barrier.getY() < 0) {
                                barrier.translateY(gapDiff / 2.0);
                            } else {
                                barrier.translateY(-gapDiff / 2.0);
                            }
                        }
                    });

                }, Duration.seconds(Settings.getBuffGapWiderDurationSec()));
                break;
        }
    }

    public BuffType getType()
    {
        return type;
    }
}