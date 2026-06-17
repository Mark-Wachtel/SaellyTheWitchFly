package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

public class BuffPowerupComponent extends Component
{

    public enum Type
    {
        SLOW_MOTION, SCORE_X10, INVULNERABILITY, EXTRA_LIFE
    }

    private final Type type;

    public BuffPowerupComponent(Type type)
    {
        this.type = type;
    }

    @Override
    public void onUpdate(double tpf)
    {

        double speed = FXGL.getd(Settings.getKeyBarrierSpeed());
        entity.translateX(-speed * tpf);
        if (entity.getRightX() < 0) entity.removeFromWorld();

    }

    public void activateEffect()
    {
        if (type != Type.EXTRA_LIFE) FXGL.set(Settings.getKeyIsBuffActive(), true);
        switch (type)
        {
            case SLOW_MOTION:
                double oldSpeed = FXGL.getd(Settings.getKeyBarrierSpeed());
                double ratio = Settings.getSlowMotionBuffPowerupRatio();
                FXGL.set(Settings.getKeyBarrierSpeed(), oldSpeed * Settings.getSlowMotionBuffPowerupRatio());
                FXGL.play(Settings.getLinkToSlowMotionPickupSound());

                FXGL.getGameTimer().runOnceAfter(() ->
                {
                    double currentSpeed = FXGL.getd(Settings.getKeyBarrierSpeed());
                    FXGL.set(Settings.getKeyBarrierSpeed(), currentSpeed / ratio);
                    FXGL.set(Settings.getKeyIsBuffActive(), false);
                }, Duration.seconds(Settings.getSlowMotionBuffPowerupDurationSeconds()));
                break;

            case SCORE_X10:
                Settings.setScoreMulti(Settings.getScoreMulti() * Settings.getScorePowerupMultiplierBonus());
                FXGL.play(Settings.getLinkToScorex10PickupSound());
                FXGL.getGameTimer().runOnceAfter(() ->
                {
                    Settings.setScoreMulti(Settings.getScoreMulti() / Settings.getScorePowerupMultiplierBonus());
                    FXGL.set(Settings.getKeyIsBuffActive(), false);
                }, Duration.seconds(Settings.getScoreX10DurationSeconds()));
                break;

            case INVULNERABILITY:
                FXGL.set(Settings.getKeyIsInvulnerable(), true);
                FXGL.play(Settings.getLinkToInvulnerablePickupSound());

                FXGL.getGameTimer().runOnceAfter(() ->
                {
                    FXGL.set(Settings.getKeyIsInvulnerable(), false);
                    FXGL.set(Settings.getKeyIsBuffActive(), false);
                }, Duration.seconds(Settings.getInvulnerableDurationInSeconds()));
                break;

            case EXTRA_LIFE:
                FXGL.inc(Settings.getKeyLives(), +Settings.getExtraLifePowerupBonus());
                FXGL.play(Settings.getLinkToExtraLivesPickupSound());
                break;
        }
    }

    public Type getType()
    {
        return type;
    }

}
