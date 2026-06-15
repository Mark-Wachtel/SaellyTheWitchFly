package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class ActiveBuffComponent extends Component
{

    private AnimatedTexture texture;
    private final BuffPowerupComponent.Type type;

    public ActiveBuffComponent(BuffPowerupComponent.Type type)
    {
        this.type = type;
    }

    @Override
    public void onAdded()
    {
        String assetName;
        int cols, frameW, frameH, numFrames;
        double durationSec;

        if (type == BuffPowerupComponent.Type.SLOW_MOTION) {
            assetName = Settings.getLinkToSlowMotionAnimation();
            cols = Settings.getSlowMotionAnimationCols();
            frameW = Settings.getSlowMotionAnimationFrameWidth();
            frameH = Settings.getSlowMotionAnimationFrameHeight();
            numFrames = Settings.getSlowMotionAnimationNumFrames();
            durationSec = Settings.getSlowMotionAnimationDurationSec();
        } else {
            assetName = Settings.getLinkToInvulnerableAnimation();
            cols = Settings.getInvulnerableAnimationCols();
            frameW = Settings.getInvulnerableAnimationFrameWidth();
            frameH = Settings.getInvulnerableAnimationFrameHeight();
            numFrames = Settings.getInvulnerableAnimationNumFrames();
            durationSec = Settings.getInvulnerableAnimationDurationSec();
        }

        AnimationChannel animationChannel = new AnimationChannel(
                FXGL.image(assetName), cols, frameW, frameH,
                Duration.seconds(durationSec), 0, numFrames - 1
        );

        this.texture = new AnimatedTexture(animationChannel);

        double scale = Settings.getBuffScaleFactor();
        texture.setScaleX(scale);
        texture.setScaleY(scale);

        double playerWidth = Settings.getInitPlayerScaleWidth();
        double playerHeight = Settings.getInitPlayerScaleHeight();

        double offsetX = (frameW - playerWidth) / 2.0;
        double offsetY = (frameH - playerHeight) / 2.0;

        double customOffsetX = 0.0;
        double customOffsetY = 0.0;

        if (type == BuffPowerupComponent.Type.SLOW_MOTION) {
            customOffsetX = Settings.getSlowMotionOffsetX();
            customOffsetY = Settings.getSlowMotionOffsetY();
        }

        texture.setTranslateX(-offsetX + customOffsetX);
        texture.setTranslateY(-offsetY + customOffsetY);

        getEntity().getViewComponent().addChild(texture);
        texture.loop();
    }

    @Override
    public void onRemoved()
    {
        if (texture != null) {
            getEntity().getViewComponent().removeChild(texture);
        }
    }

}
