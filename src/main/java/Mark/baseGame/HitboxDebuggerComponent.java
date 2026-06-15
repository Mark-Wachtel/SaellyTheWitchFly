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
        rect.setStrokeWidth(2.0);
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
            String typeName = entity.getType().toString();
            if (typeName.contains("PLAYER")) {
                rect.setStroke(Settings.getHitboxColorPlayer());
            } else if (typeName.contains("BARRIER")) {
                rect.setStroke(Settings.getHitboxColorBarrier());
            } else {
                rect.setStroke(Settings.getHitboxColorBuff());
            }
        }

        // Rechteck zeichnen
        rect.setX(bbox.getMinXLocal());
        rect.setY(bbox.getMinYLocal());
        rect.setWidth(bbox.getWidth());
        rect.setHeight(bbox.getHeight());
    }
}