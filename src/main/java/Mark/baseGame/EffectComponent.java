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
    public enum EffectType
    {
        CRASH, PICKUP
    }

    private final AnimatedTexture texture;
    private final double targetWidth;
    private final double targetHeight;

    public EffectComponent(EffectType type)
    {

        String assetName = "";
        int cols = 1;
        int frameWidth = 1;
        int frameHeight = 1;
        int numFrames = 1;
        double duration = 1.0, targetW = 256.0, targetH = 256.0;
        this.targetWidth = (type == EffectType.CRASH) ? Settings.getCrashEffectTargetWidth() : Settings.getPickupEffectTargetWidth();
        this.targetHeight = (type == EffectType.CRASH) ? Settings.getCrashEffectTargetHeight() : Settings.getPickupEffectTargetHeight();

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

        AnimationChannel animationChannel = new AnimationChannel(FXGL.image(assetName),cols,frameWidth,frameHeight,Duration.seconds(5.0),0,numFrames -1);
        this.texture = new AnimatedTexture(animationChannel);
        this.texture.setPreserveRatio(false);
        this.texture.setFitWidth(targetW);
        this.texture.setFitHeight(targetH);
    }

    @Override
    public void onAdded()
    {
        Rectangle bounds = new Rectangle(targetWidth, targetHeight, Color.TRANSPARENT);

        getEntity().getViewComponent().addChild(bounds);
        getEntity().getViewComponent().addChild(texture);

        texture.play();

        texture.setOnCycleFinished(() -> {
            if (getEntity().isActive()) {
                getEntity().removeFromWorld();
            }
        });


    }
    }
