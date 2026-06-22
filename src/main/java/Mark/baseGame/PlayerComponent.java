package Mark.baseGame;

import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class PlayerComponent extends Component
{

    private PhysicsComponent physics;
    private Sound jumpSound;
    private double timer = 0;
    private double startX;
    private double startY;
    private static final double MAX_FALL_SPEED = Settings.getPlayerMaxFallSpeed(); //Maximale Fallgeschwindigkeit
    private static final double MAX_RISE_SPEED = Settings.getPlayerMaxRiseSpeed(); //Maximale Aufstiegsgeschwindigkeit
    private boolean canShoot = true;
    private Weapon currentWeapon = new DarkMagicWeapon();

    @Override
    public void onAdded()
    {
        startY = entity.getY();
        startX = entity.getX();
        physics = entity.getComponent(PhysicsComponent.class);
        physics.setBodyType(BodyType.DYNAMIC);

        jumpSound = FXGL.getAssetLoader().loadSound(Settings.getLinkToJumpSound());

    }

    public void setWeapon(Weapon newWeapon) {
        this.currentWeapon = newWeapon;
    }


    @Override
    public void onUpdate(double tpf)
    {

        if (physics.getBody() != null && !physics.getBody().isFixedRotation()) physics.getBody().setFixedRotation(true);
        boolean gameStarted = FXGL.getb(Settings.getKeyGameStarted());

        if (FXGL.getb(Settings.getKeyIsGameOver())) return;

        double currentRatio = FXGL.getd(Settings.getKeyBarrierSpeed()) / Settings.getBarrierSpeed();
        if (physics.getBody() != null)physics.getBody().setGravityScale((float) currentRatio);
        if (tpf > Settings.getPhysicsLagThreshold())
        {
            double targetNormalTpf = Settings.getPhysicsTargetNormalTpf();
            double timeScaleFactor = targetNormalTpf / tpf;
            physics.setLinearVelocity(physics.getLinearVelocity().getX() * timeScaleFactor, physics.getLinearVelocity().getY() * timeScaleFactor);
        }
        else
        {
            double currentYVelocity = physics.getLinearVelocity().getY();
            double adjustedMaxFall = MAX_FALL_SPEED * currentRatio;
            double adjustedMaxRise = MAX_RISE_SPEED * currentRatio;
            if (currentYVelocity > adjustedMaxFall)
            {
                physics.setLinearVelocity(physics.getLinearVelocity().getX(), adjustedMaxFall);
            } else if (currentYVelocity < adjustedMaxRise)
            {
                physics.setLinearVelocity(physics.getLinearVelocity().getX(), adjustedMaxRise);
            }
        }


        if (!gameStarted)
        {
            double animationTpf = Math.min(tpf, Settings.getPlayerAnimTpfLimit());
            timer += animationTpf * Settings.getPlayerIdleAnimSpeed();
            double offset = Math.sin(timer) * Settings.getIdleJumpHeight();
            physics.overwritePosition(new Point2D(startX,startY +offset));
            physics.setVelocityY(0);
        }
        else
        {
            if (entity.getY() < 0 || entity.getBottomY() > FXGL.getAppHeight())
            {
                handleBoundsCollision();
            }
        }

    }

    private void handleBoundsCollision()
    {
        if (FXGL.getb(Settings.getKeyIsInvulnerable()))
        {
            bounce();
            return;
        }

        int currentLives = FXGL.geti(Settings.getKeyLives());
        if (currentLives > 1)
        {
            FXGL.inc(Settings.getKeyLives(), -1);
            FXGL.set(Settings.getKeyIsInvulnerable(), true);
            FXGL.getGameTimer().runOnceAfter(() -> FXGL.set(Settings.getKeyIsInvulnerable(), false), Duration.seconds(Settings.getInvulnerableDurationInSeconds()));
            bounce();
        }
        else
        {
            double effectW = Settings.getCrashEffectFrameWidth() * Settings.getCrashEffectScale();
            double effectH = Settings.getCrashEffectFrameHeight() * Settings.getCrashEffectScale();

            Point2D center = entity.getCenter();

            double spawnX = center.getX() - (effectW / 2.0) + Settings.getCrashEffectOffsetX();
            double spawnY = center.getY() - (effectH / 2.0) + Settings.getCrashEffectOffsetY();

            entity.removeFromWorld();

            FXGL.entityBuilder()
                    .at(spawnX, spawnY)
                    .zIndex(Settings.getZIndexGame())
                    .with(new EffectComponent(EffectComponent.EffectType.CRASH))
                    .buildAndAttach();

            FXGL.<SaellyApp>getAppCast().gameOver();
        }
    }

    private void bounce()
    {
        if (entity.getY() < 0)
        {
            physics.overwritePosition(new Point2D(entity.getX(), Settings.getPlayerBounceMargin()));
            physics.setVelocityY(0);
        }
        else
        {
            physics.overwritePosition(new Point2D(entity.getX(), FXGL.getAppHeight() - entity.getHeight() - 20));
            jump();
        }
    }

    public void jump()
    {

        if (FXGL.getb(Settings.getKeyIsGameOver())) return;
        boolean gameStarted = FXGL.getb(Settings.getKeyGameStarted());

        if (!gameStarted)
        {
            FXGL.set(Settings.getKeyGameStarted(), true);
            FXGL.set(Settings.getKeyGeneralSpeed(), Settings.getGameSpeed());
            FXGL.set(Settings.getKeyBarrierSpeed(), Settings.getBarrierSpeed());
        }

        double currentRatio = FXGL.getd(Settings.getKeyBarrierSpeed()) / Settings.getBarrierSpeed();
        physics.setVelocityY(Settings.getJumpMulti() * currentRatio);

        FXGL.getAudioPlayer().stopSound(jumpSound);
        FXGL.getAudioPlayer().playSound(jumpSound);

    }

    public void shoot()
    {
        if (!canShoot || FXGL.getb(Settings.getKeyIsGameOver())) return;

        Point2D mousePos = FXGL.getInput().getMousePositionWorld();

        boolean shotFired = currentWeapon.shoot(entity, mousePos);

        if (shotFired)
        {
            canShoot = false;
            FXGL.getGameTimer().runOnceAfter(() -> {
                canShoot = true;
            }, javafx.util.Duration.seconds(currentWeapon.getCooldown()));
        }
    }

}
