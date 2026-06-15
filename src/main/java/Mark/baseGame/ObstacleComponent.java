package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class ObstacleComponent extends Component {

    private boolean passed = false;

    @Override
    public void onUpdate(double tpf) {

        if (FXGL.getb(Settings.getKeyIsGameOver())) return;

        var playerOpt = FXGL.getGameWorld().getSingletonOptional(EntityType.PLAYER);

        double speed = FXGL.getd(Settings.getKeyBarrierSpeed());
        if (playerOpt.isPresent()) {
            Entity player = playerOpt.get();
            entity.translateX(-speed * tpf);

            if (entity.getRightX() < 0) entity.removeFromWorld();

            if (!passed && entity.getRightX() < player.getRightX()) {
                passed = true;
                FXGL.inc(Settings.getKeyScore(), Settings.getScoreMulti());
                int currentScore = FXGL.geti(Settings.getKeyScore());
                if (currentScore > 0 && currentScore % Settings.getBarriersToSpeedupTheGame() == 0) {
                    FXGL.inc(Settings.getKeyGeneralSpeed(), Settings.getGameSpeed());
                    FXGL.inc(Settings.getKeyBarrierSpeed(), Settings.getGameSpeed());
                }
            }
        }
    }
}