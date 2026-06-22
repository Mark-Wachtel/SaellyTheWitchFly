package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class CloudComponent extends Component
{
    @Override
    public void onUpdate(double tpf)
    {
        double speed = FXGL.getd(Settings.getKeyBarrierSpeed()) * Settings.getParallaxFactorForeground();

        entity.translateX(-speed * tpf);

        if (entity.getRightX() < Settings.getDespawnXBoundary())
        {
            entity.removeFromWorld();
        }
    }
}