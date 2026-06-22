package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

public class ActiveBuffComponent extends Component
{
    private final Entity player;

    private LocalTimer lifeTimer;
    private double buffLifeDurationSec;
    private BuffType buffType;

    private double effectW;
    private double effectH;
    private double offsetX;
    private double offsetY;

    public ActiveBuffComponent(Entity player, BuffType type)
    {
        this.player = player;
        this.buffType = type;
    }

    @Override
    public void onAdded()
    {
        String assetName;
        int cols, frameWidth, frameHeight, numFrames;
        double animSpeedSec, scaleMultiplier;

        switch (buffType)
        {
            case INVULNERABILITY:
                assetName = Settings.getLinkToInvulnerableAnimation();
                cols = Settings.getInvulnerableAnimationCols();
                frameWidth = Settings.getInvulnerableAnimationFrameWidth();
                frameHeight = Settings.getInvulnerableAnimationFrameHeight();
                numFrames = Settings.getInvulnerableAnimationNumFrames();
                animSpeedSec = Settings.getInvulnerableAnimationDurationSec();
                scaleMultiplier = Settings.getInvulnerableAnimationScale();
                offsetX = Settings.getInvulnerableOffsetX();
                offsetY = Settings.getInvulnerableOffsetY();
                buffLifeDurationSec = Settings.getInvulnerableDurationInSeconds();
                break;

            case SLOW_MOTION:
                assetName = Settings.getLinkToSlowMotionAnimation();
                cols = Settings.getSlowMotionAnimationCols();
                frameWidth = Settings.getSlowMotionAnimationFrameWidth();
                frameHeight = Settings.getSlowMotionAnimationFrameHeight();
                numFrames = Settings.getSlowMotionAnimationNumFrames();
                animSpeedSec = Settings.getSlowMotionAnimationDurationSec();
                scaleMultiplier = Settings.getSlowMotionAnimationScale();
                offsetX = Settings.getSlowMotionOffsetX();
                offsetY = Settings.getSlowMotionOffsetY();
                buffLifeDurationSec = Settings.getSlowMotionBuffPowerupDurationSeconds();
                break;

            default:
                entity.removeFromWorld();
                return;
        }

        effectW = frameWidth * scaleMultiplier;
        effectH = frameHeight * scaleMultiplier;

        AnimationChannel channel = new AnimationChannel(
                FXGL.image(assetName), cols, frameWidth, frameHeight,
                Duration.seconds(animSpeedSec), 0, numFrames - 1
        );

        AnimatedTexture texture = new AnimatedTexture(channel);
        texture.loop();
        texture.getTransforms().add(new Scale(scaleMultiplier, scaleMultiplier, 0, 0));

        entity.getViewComponent().addChild(texture);

        lifeTimer = FXGL.newLocalTimer();
        lifeTimer.capture();
    }

    @Override
    public void onUpdate(double tpf)
    {
        if (!player.isActive())
        {
            entity.removeFromWorld();
            return;
        }

        javafx.geometry.Point2D center = player.getCenter();
        double targetX = center.getX() - (effectW / 2.0) + offsetX;
        double targetY = center.getY() - (effectH / 2.0) + offsetY;

        entity.setPosition(targetX, targetY);

        if (lifeTimer.elapsed(Duration.seconds(buffLifeDurationSec)))
        {
            entity.removeFromWorld();
        }
    }
}