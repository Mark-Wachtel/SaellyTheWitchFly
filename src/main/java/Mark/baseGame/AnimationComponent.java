package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class AnimationComponent extends Component
{

    private AnimatedTexture texture;
    private AnimationChannel animIdle, animJump;
    private PhysicsComponent physics;

    public AnimationComponent()
    {
        animIdle = new AnimationChannel(FXGL.image(Settings.getLinkToImagePlayerIdle()),Settings.getPlayerAnimFramesPerRow(),Settings.getInitPlayerScaleWidth(),Settings.getInitPlayerScaleHeight(), Duration.seconds(Settings.getPlayerAnimDurationSeconds()),Settings.getPlayerAnimIdleStart(),Settings.getPlayerAnimIdleEnd());
        animJump = new AnimationChannel(FXGL.image(Settings.getLinkToImagePlayerJump()),Settings.getPlayerAnimFramesPerRow(),Settings.getInitPlayerScaleWidth(),Settings.getInitPlayerScaleHeight(),Duration.seconds(Settings.getPlayerAnimDurationSeconds()),Settings.getPlayerAnimJumpStart(),Settings.getPlayerAnimJumpEnd());
        texture = new AnimatedTexture(animIdle);
    }

    @Override
    public void onAdded()
    {
        entity.getTransformComponent().setScaleOrigin(new Point2D(Settings.getInitPlayerPivotWidth(),Settings.getInitPlayerPivotHeight()));
        entity.getViewComponent().addChild(texture);
        physics = entity.getComponent(PhysicsComponent.class);
    }

    @Override
    public void onUpdate(double tpf)
    {

        if (FXGL.getb(Settings.getKeyGameStarted()))
        {
            if (physics.getVelocityY() < Settings.getPlayerJumpAnimThreshold())
            {
                if (texture.getAnimationChannel() != animJump)
                {
                    texture.loopAnimationChannel(animJump);
                }
            }
                else
                {
                    if (texture.getAnimationChannel() != animIdle)
                    {
                        texture.loopAnimationChannel(animIdle);
                    }
                }
        }
    }
}
