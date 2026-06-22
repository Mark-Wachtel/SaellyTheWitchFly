package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.shape.Rectangle;

public class HitboxDebuggerComponent extends Component {
    private Rectangle rect;
    private boolean isColliding = false;

    @Override
    public void onAdded() {
        rect = new Rectangle();
        rect.setFill(null);
        rect.setStrokeWidth(Settings.getHitboxStrokeWidth());
        entity.getViewComponent().addChild(rect);
    }

    public void setColliding(boolean isColliding) {
        this.isColliding = isColliding;
    }

    @Override
    public void onUpdate(double tpf) {
        var bbox = entity.getBoundingBoxComponent();

        boolean isDebugging = FXGL.getb(Settings.getKeyIsDebugging());

        if (!isDebugging || bbox == null) {
            rect.setVisible(false);
            return;
        }

        rect.setVisible(true);

        if (isColliding) {
            rect.setStroke(Settings.getHitboxColorColliding());
        } else {
            Object type = entity.getType();
            if (type == EntityType.PLAYER) {
                rect.setStroke(Settings.getHitboxColorPlayer());
            } else if (type == EntityType.BARRIER) {
                rect.setStroke(Settings.getHitboxColorBarrier());
            } else if (type == EntityType.PROJECTILE) {
                rect.setStroke(Settings.getHitboxColorProjectile());
            } else if (type == EntityType.BUFF_POWERUP){
                rect.setStroke(Settings.getHitboxColorBuff());
            } else if (type == EntityType.DEBUFF_POWERUP){
                rect.setStroke(Settings.getHitboxColorDebuff());
            }
        }

        // Rechteck zeichnen
        rect.setX(bbox.getMinXLocal());
        rect.setY(bbox.getMinYLocal());
        rect.setWidth(bbox.getWidth());
        rect.setHeight(bbox.getHeight());
    }
}