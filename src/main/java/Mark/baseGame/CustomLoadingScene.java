package Mark.baseGame;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ProgressBar;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.prefs.Preferences;

public class CustomLoadingScene extends LoadingScene
{

    private Text loadingText;
    private Timeline dotsTimeline;
    private Timeline animationTimeline;
    private int dotCount = 0;
    private int currentFrame = 0;

    public CustomLoadingScene() {

        try {
            String css = getClass().getResource(Settings.getLinkToCss()).toExternalForm();
            getContentRoot().getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println(Settings.getMsgErrCssLoading());
        }

        getContentRoot().getChildren().clear();

        double width = FXGL.getAppWidth();
        double height = FXGL.getAppHeight();

        ImageView background = new ImageView(FXGL.image(Settings.getLinkToInitLoadingScreenBackground()));
        background.setFitWidth(width);
        background.setFitHeight(height);

        ImageView animImageView = new ImageView(FXGL.image(Settings.getLinkToLoadingAnimation()));

        double viewSize = Settings.getLoadingAnimViewSize();
        animImageView.setFitWidth(viewSize);
        animImageView.setFitHeight(viewSize);
        animImageView.setPreserveRatio(true);

        int frameWidth = Settings.getLoadingAnimFrameWidth();
        int frameHeight = Settings.getLoadingAnimFrameHeight();
        animImageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));

        animationTimeline = new Timeline(new KeyFrame(Duration.millis(60), event -> {
            int totalFrames = Settings.getLoadingAnimTotalFrames();
            int columns = Settings.getLoadingAnimColumns();

            currentFrame = (currentFrame + 1) % totalFrames;

            int col = currentFrame % columns;
            int row = currentFrame / columns;

            double x = col * frameWidth;
            double y = row * frameHeight;

            animImageView.setViewport(new Rectangle2D(x, y, frameWidth, frameHeight));
        }));
        animationTimeline.setCycleCount(Animation.INDEFINITE);

        Preferences prefs = Preferences.userNodeForPackage(SaellyApp.class);
        String savedLang = prefs.get(Settings.getPrefsKeyLanguage(), Settings.getLangGerman());
        String baseText = "";

        if (savedLang.equals(Settings.getLangEnglish()))
        {
            baseText = "loading";
        }
        else
        {
            baseText = "Wird geladen";
        }
        final String bText = baseText;
        loadingText = FXGL.getUIFactoryService().newText(baseText, Color.WHITE, Settings.getLoadingTextSize());

        dotsTimeline = new Timeline(new KeyFrame(Duration.seconds(Settings.getLoadingDotsDurationSeconds()), event -> {
            dotCount = (dotCount + 1) % 4;
            String dots = ".".repeat(dotCount);
            loadingText.setText(bText + dots);
        }));
        dotsTimeline.setCycleCount(Animation.INDEFINITE);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(width * Settings.getLoadingBarWidthRatio());
        progressBar.setPrefHeight(Settings.getLoadingBarHeight());
        progressBar.setProgress(-1.0);
        progressBar.getStyleClass().add("loading-progress-bar");

        VBox container = new VBox(Settings.getLoadingVBoxSpacing(), animImageView, loadingText, progressBar);
        container.setAlignment(Pos.CENTER);
        container.setPrefSize(width, height);

        getContentRoot().getChildren().addAll(background, container);
    }

    @Override
    public void onCreate() {
        if (dotsTimeline != null) {
            dotsTimeline.play();
        }
        if (animationTimeline != null) {
            animationTimeline.play();
        }
    }

    @Override
    public void onDestroy() {
        if (dotsTimeline != null) {
            dotsTimeline.stop();
        }
        if (animationTimeline != null) {
            animationTimeline.stop();
        }
    }

}