package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

public class EffectComponent extends Component
{
    public enum EffectType { CRASH, PICKUP }

    private final AnimatedTexture texture;
    private final double scaleMultiplier;
    private final double originalFrameW;
    private final double originalFrameH;

    public EffectComponent(EffectType type)
    {
        String assetName = "";
        int cols = 1, frameWidth = 1, frameHeight = 1, numFrames = 1;
        double duration = 1.0;

        this.scaleMultiplier = (type == EffectType.CRASH) ? Settings.getCrashEffectScale() : Settings.getPickupEffectScale();

        switch (type)
        {
            case CRASH:
                assetName = Settings.getLinkToCrashEffect();
                cols = Settings.getCrashEffectCols();
                frameWidth = Settings.getCrashEffectFrameWidth();
                frameHeight = Settings.getCrashEffectFrameHeight();
                numFrames = Settings.getCrashEffectNumFrames();
                duration = Settings.getCrashEffectDurationSec();
                break;

            case PICKUP:
                assetName = Settings.getLinkToPickupEffect();
                cols = Settings.getPickupEffectCols();
                frameWidth = Settings.getPickupEffectFrameWidth();
                frameHeight = Settings.getPickupEffectFrameHeight();
                numFrames = Settings.getPickupEffectNumFrames();
                duration = Settings.getPickupEffectDurationSec();
                break;
        }

        this.originalFrameW = frameWidth;
        this.originalFrameH = frameHeight;

        AnimationChannel animationChannel = new AnimationChannel(
                FXGL.image(assetName), cols, frameWidth, frameHeight,
                Duration.seconds(duration), 0, numFrames - 1
        );
        this.texture = new AnimatedTexture(animationChannel);
        this.texture.setPreserveRatio(false);
    }

    @Override
    public void onAdded()
    {
        double finalW = originalFrameW * scaleMultiplier;
        double finalH = originalFrameH * scaleMultiplier;

        Rectangle bounds = new Rectangle(finalW, finalH, Color.TRANSPARENT);

        texture.getTransforms().add(new Scale(scaleMultiplier, scaleMultiplier, 0, 0));

        getEntity().getViewComponent().addChild(bounds);
        getEntity().getViewComponent().addChild(texture);

        texture.play();
        texture.setOnCycleFinished(() -> {
            if (getEntity().isActive()) getEntity().removeFromWorld();
        });
    }
}