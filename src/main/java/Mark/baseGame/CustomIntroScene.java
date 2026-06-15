package Mark.baseGame;

import com.almasb.fxgl.app.scene.IntroScene;
import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.FadeTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class CustomIntroScene extends IntroScene
{

    private FadeTransition fadeTransition;

    public CustomIntroScene()
    {
        ImageView background = new ImageView(FXGL.image(Settings.getLinkToUiIntroBackgroundImage()));
        background.setFitWidth(FXGL.getAppWidth());
        background.setFitHeight(FXGL.getAppHeight());

        ImageView logo = new ImageView(FXGL.image(Settings.getLinkToLogo()));

        logo.setTranslateX(FXGL.getAppWidth() / 2.0 -logo.getImage().getWidth() / 2.0);
        logo.setTranslateY(FXGL.getAppHeight() / 2.0 -logo.getImage().getHeight() / 2.0);

        logo.setOpacity(0.0);

        fadeTransition = new FadeTransition(Duration.seconds(Settings.getIntroFadeDurationSeconds()), logo);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        fadeTransition.setOnFinished(event -> {
            finishIntro();
        });
        getContentRoot().getChildren().addAll(background,logo);
    }

    @Override
    public void startIntro()
    {
        fadeTransition.play();
    }


}