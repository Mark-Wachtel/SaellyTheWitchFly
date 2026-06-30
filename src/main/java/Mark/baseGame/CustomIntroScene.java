package Mark.baseGame;

import com.almasb.fxgl.app.scene.IntroScene;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class CustomIntroScene extends IntroScene
{
    private final ImageView logo;

    public CustomIntroScene()
    {
        ImageView background = new ImageView(FXGL.image(Settings.getLinkToUiIntroBackgroundImage()));
        background.setFitWidth(FXGL.getAppWidth());
        background.setFitHeight(FXGL.getAppHeight());

        logo = new ImageView(FXGL.image(Settings.getLinkToLogo()));
        logo.setTranslateX(FXGL.getAppWidth() / 2.0 - logo.getImage().getWidth() / 2.0);
        logo.setTranslateY(FXGL.getAppHeight() / 2.0 - logo.getImage().getHeight() / 2.0);
        logo.setOpacity(0.0);

        getContentRoot().getChildren().addAll(background, logo);
    }

    @Override
    public void startIntro()
    {
        ((SaellyApp) FXGL.getAppCast()).getSceneTransitionCoordinator()
                .fadeIn(logo, Duration.seconds(Settings.getIntroFadeDurationSeconds()), this::finishIntro);
    }
}