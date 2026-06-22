package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class ObstacleComponent extends Component
{
    private boolean passed = false;
    private final boolean givesScore;

    public ObstacleComponent(boolean givesScore)
    {
        this.givesScore = givesScore;
    }

    @Override
    public void onUpdate(double tpf)
    {
        if (FXGL.getb(Settings.getKeyIsGameOver())) return;

        var playerOpt = FXGL.getGameWorld().getSingletonOptional(EntityType.PLAYER);
        double speed = FXGL.getd(Settings.getKeyBarrierSpeed());

        if (playerOpt.isPresent())
        {
            Entity player = playerOpt.get();
            entity.translateX(-speed * tpf);

            if (entity.getRightX() < Settings.getDespawnXBoundary()) entity.removeFromWorld();

            if (givesScore && !passed && entity.getRightX() < player.getRightX())
            {
                passed = true;
                FXGL.inc(Settings.getKeyScore(), Settings.getScoreMulti());
                FXGL.play(Settings.getLinkToSoundBarrierPassed());
            }
        }
    }
}