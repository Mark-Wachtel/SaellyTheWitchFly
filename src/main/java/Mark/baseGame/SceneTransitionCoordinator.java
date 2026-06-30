package Mark.baseGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.Scene;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.function.Consumer;

public final class SceneTransitionCoordinator {

	private static final String CURTAIN_TAG = "saellyCurtain";

	private static final int MAX_RADAR_TICKS = 200;

	private static final Duration SCENE_FADE_DURATION = Duration.seconds(0.4);
	private static final Duration LOADING_FADE_DURATION = Duration.seconds(0.5);

	private boolean isTransitioning = false;
	private Runnable coverHook;

	public void transition(Runnable onSwitch) {
		curtainTransition(onSwitch, FXGL.getSceneService().getGameScene());
	}

	public void transition(Runnable onSwitch, Scene targetScene) {
		curtainTransition(onSwitch, targetScene);
	}

	public void curtainTransition(Runnable onSwitch, Scene targetScene) {
		if (isTransitioning) return;
		isTransitioning = true;

		Parent root = FXGL.getPrimaryStage().getScene().getRoot();
		sweepCurtains(root);

		ImageView curtain = createCurtain(root, 0);
		addToRoot(root, curtain);
		startCovering(curtain);

		Timeline close = buildCurtainTimeline(curtain, true);
		close.setOnFinished(e -> {
			if (onSwitch != null) onSwitch.run();

			startRadar(targetScene, () -> {
				Parent newRoot = FXGL.getPrimaryStage().getScene().getRoot();
				CustomTitleBar newTitleBar = findActiveTitleBar(newRoot);

				double newAnchorY = (newTitleBar != null) ? newTitleBar.getCurtainAnchorY() : Settings.getStandardTitlebarHeight();

				curtain.fitHeightProperty().unbind();

				curtain.setLayoutY(newAnchorY);
				curtain.fitHeightProperty().bind(FXGL.getPrimaryStage().heightProperty().subtract(newAnchorY));

				openCurtainThenFinish(curtain);
			});
		});
		close.play();
	}

	public void fadeTransition(Runnable onSwitch, Scene targetScene) {
		if (isTransitioning) return;
		isTransitioning = true;

		Parent root = FXGL.getPrimaryStage().getScene().getRoot();
		sweepCurtains(root);

		Rectangle cover = createBlackCover();
		cover.setOpacity(0.0);
		addToRoot(root, cover);
		startCovering(cover);

		FadeTransition toBlack = new FadeTransition(SCENE_FADE_DURATION, cover);
		toBlack.setFromValue(0.0);
		toBlack.setToValue(1.0);
		toBlack.setOnFinished(e -> {
			if (onSwitch != null) onSwitch.run();
			startRadar(targetScene, () -> {
				FadeTransition fromBlack = new FadeTransition(SCENE_FADE_DURATION, cover);
				fromBlack.setFromValue(1.0);
				fromBlack.setToValue(0.0);
				fromBlack.setOnFinished(ev -> finishCovering(cover));
				fromBlack.play();
				scheduleBackstop(cover, SCENE_FADE_DURATION.toSeconds() + 0.3);
			});
		});
		toBlack.play();
	}

	public void loadingFade(Consumer<Runnable> work, Runnable onComplete) {
		if (isTransitioning) return;
		isTransitioning = true;

		Parent root = FXGL.getPrimaryStage().getScene().getRoot();
		sweepCurtains(root);

		ImageView spinner = createSpinnerView();
		StackPane overlay = createLoadingOverlay(spinner);
		overlay.setOpacity(0.0);
		addToRoot(root, overlay);
		startCovering(overlay);

		Timeline spinnerAnim = buildSpinnerTimeline(spinner);
		spinnerAnim.play();

		FadeTransition in = new FadeTransition(LOADING_FADE_DURATION, overlay);
		in.setFromValue(0.0);
		in.setToValue(1.0);
		in.setOnFinished(e -> {
			Runnable done = new Runnable() {
				private boolean called = false;
				@Override public void run() {
					if (called) return;
					called = true;

					FadeTransition out = new FadeTransition(LOADING_FADE_DURATION, overlay);
					out.setFromValue(1.0);
					out.setToValue(0.0);
					out.setOnFinished(ev -> {
						spinnerAnim.stop();
						finishCovering(overlay);
						if (onComplete != null) onComplete.run();
					});
					out.play();
				}
			};
			work.accept(done);
		});
		in.play();
	}

	public void curtainClose(Runnable onClosed) {
		if (isTransitioning) return;
		isTransitioning = true;

		Parent root = FXGL.getPrimaryStage().getScene().getRoot();
		sweepCurtains(root);

		ImageView curtain = createCurtain(root, 0);
		addToRoot(root, curtain);
		startCovering(curtain);

		Timeline close = buildCurtainTimeline(curtain, true);
		close.setOnFinished(e -> {
			if (onClosed != null) onClosed.run();
		});
		close.play();
	}

	public void curtainOpen() {
		if (isTransitioning) return;
		isTransitioning = true;

		Parent root = FXGL.getPrimaryStage().getScene().getRoot();
		sweepCurtains(root);

		ImageView curtain = createCurtain(root, Settings.getCurtainCloseMaxFrame());
		addToRoot(root, curtain);
		startCovering(curtain);
		openCurtainThenFinish(curtain);
	}

	public void requestExit(Runnable exitAction) {
		curtainClose(() -> {
			if (FXGL.getPrimaryStage() != null) FXGL.getPrimaryStage().hide();
			if (exitAction != null) exitAction.run();
		});
	}

	public void runWhenSceneActive(Scene scene, Runnable action) {
		final int[] ticks = {0};
		Timeline radar = new Timeline();
		radar.getKeyFrames().add(new KeyFrame(Duration.millis(30), ev -> {
			ticks[0]++;
			boolean ready = (scene != null && FXGL.getSceneService().getCurrentScene() == scene);
			boolean timedOut = ticks[0] >= MAX_RADAR_TICKS;
			if (ready || timedOut) {
				radar.stop();
				if (action != null) action.run();
			}
		}));
		radar.setCycleCount(Timeline.INDEFINITE);
		radar.play();
	}

	public Animation popIn(Node node, Duration duration, Runnable onFinished) {
		node.setScaleX(0);
		node.setScaleY(0);
		node.setVisible(true);

		ScaleTransition st = new ScaleTransition(duration, node);
		st.setToX(1.0);
		st.setToY(1.0);
		st.setInterpolator(Interpolator.EASE_OUT);
		if (onFinished != null) st.setOnFinished(e -> onFinished.run());
		st.play();
		return st;
	}

	public Animation popOut(Node node, Duration duration, Runnable onFinished) {
		ScaleTransition st = new ScaleTransition(duration, node);
		st.setToX(0.0);
		st.setToY(0.0);
		st.setInterpolator(Interpolator.EASE_IN);
		st.setOnFinished(e -> {
			node.setVisible(false);
			if (onFinished != null) onFinished.run();
		});
		st.play();
		return st;
	}

	public Animation slideInFromTop(Node node, Duration duration, Runnable onFinished) {
		node.setTranslateY(-FXGL.getAppHeight());
		node.setVisible(true);

		TranslateTransition tt = new TranslateTransition(duration, node);
		tt.setToY(0);
		tt.setInterpolator(Interpolator.EASE_OUT);
		if (onFinished != null) tt.setOnFinished(e -> onFinished.run());
		tt.play();
		return tt;
	}

	public Animation slideOutToTop(Node node, Duration duration, Runnable onFinished) {
		TranslateTransition tt = new TranslateTransition(duration, node);
		tt.setToY(-FXGL.getAppHeight());
		tt.setInterpolator(Interpolator.EASE_IN);
		if (onFinished != null) tt.setOnFinished(e -> onFinished.run());
		tt.play();
		return tt;
	}

	public Animation fadeIn(Node node, Duration duration, Runnable onFinished) {
		node.setOpacity(0.0);
		node.setVisible(true);

		FadeTransition ft = new FadeTransition(duration, node);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		if (onFinished != null) ft.setOnFinished(e -> onFinished.run());
		ft.play();
		return ft;
	}

	public Animation fadeOut(Node node, Duration duration, Runnable onFinished) {
		FadeTransition ft = new FadeTransition(duration, node);
		ft.setToValue(0.0);
		if (onFinished != null) ft.setOnFinished(e -> onFinished.run());
		ft.play();
		return ft;
	}

	private void startRadar(Scene target, Runnable onReached) {
		final int[] ticks = {0};
		Timeline radar = new Timeline();
		radar.getKeyFrames().add(new KeyFrame(Duration.millis(30), ev -> {
			ticks[0]++;
			Scene current = FXGL.getSceneService().getCurrentScene();
			boolean reached = (target != null && current == target);
			boolean timedOut = ticks[0] >= MAX_RADAR_TICKS;
			if (reached || timedOut) {
				radar.stop();
				onReached.run();
			}
		}));
		radar.setCycleCount(Timeline.INDEFINITE);
		radar.play();
	}

	private void openCurtainThenFinish(ImageView curtain) {
		buildCurtainTimeline(curtain, false).play();
		scheduleBackstop(curtain, Settings.getCurtainCloseDurationSec() + 0.3);
	}

	private void scheduleBackstop(Node cover, double seconds) {
		PauseTransition backstop = new PauseTransition(Duration.seconds(seconds));
		backstop.setOnFinished(ev -> finishCovering(cover));
		backstop.play();
	}

	private void finishCovering(Node cover) {
		stopCovering();
		Parent root = FXGL.getPrimaryStage().getScene().getRoot();
		removeFromRoot(root, cover);
		sweepCurtains(root);
		isTransitioning = false;
	}

	private void startCovering(Node cover) {
		coverHook = () -> {
			Parent root = FXGL.getPrimaryStage().getScene().getRoot();
			if (root instanceof Pane p) {
				if (!p.getChildren().contains(cover)) p.getChildren().add(cover);
			} else if (root instanceof Group g) {
				if (!g.getChildren().contains(cover)) g.getChildren().add(cover);
			}
		};
		FXGL.getPrimaryStage().getScene().addPostLayoutPulseListener(coverHook);
	}

	private void stopCovering() {
		if (coverHook != null) {
			FXGL.getPrimaryStage().getScene().removePostLayoutPulseListener(coverHook);
			coverHook = null;
		}
	}

	private ImageView createCurtain(Parent root, int startFrame) {
		double anchorY = Settings.getStandardTitlebarHeight();

		CustomTitleBar titleBar = findActiveTitleBar(root);
		if (titleBar != null) {
			anchorY = titleBar.getCurtainAnchorY();
		}

		ImageView view = new ImageView(FXGL.image(Settings.getCurtainCloseTexture()));
		view.setViewport(getViewportForFrame(startFrame));
		view.setPreserveRatio(false);
		view.setSmooth(false);
		view.setManaged(false);
		view.setLayoutY(anchorY);
		view.fitWidthProperty().bind(FXGL.getPrimaryStage().widthProperty());
		view.fitHeightProperty().bind(FXGL.getPrimaryStage().heightProperty().subtract(anchorY));
		view.setViewOrder(-9999);
		view.getProperties().put(CURTAIN_TAG, Boolean.TRUE);
		return view;
	}

	private Rectangle createBlackCover() {
		Rectangle rect = new Rectangle();
		rect.setFill(Color.BLACK);
		rect.widthProperty().bind(FXGL.getPrimaryStage().widthProperty());
		rect.heightProperty().bind(FXGL.getPrimaryStage().heightProperty());
		rect.setViewOrder(-9999);
		rect.getProperties().put(CURTAIN_TAG, Boolean.TRUE);
		return rect;
	}

	private ImageView createSpinnerView() {
		ImageView spinner = new ImageView(FXGL.image(Settings.getLinkToLoadingAnimation()));
		double viewSize = Settings.getLoadingAnimViewSize();
		spinner.setFitWidth(viewSize);
		spinner.setFitHeight(viewSize);
		spinner.setPreserveRatio(true);
		spinner.setViewport(new Rectangle2D(0, 0, Settings.getLoadingAnimFrameWidth(), Settings.getLoadingAnimFrameHeight()));
		return spinner;
	}

	private StackPane createLoadingOverlay(ImageView spinner) {
		StackPane overlay = new StackPane();

		ImageView bg = new ImageView(FXGL.image(Settings.getLinkToInitLoadingScreenBackground()));
		bg.fitWidthProperty().bind(FXGL.getPrimaryStage().widthProperty());
		bg.fitHeightProperty().bind(FXGL.getPrimaryStage().heightProperty());

		Text text = FXGL.getUIFactoryService().newText(
				FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeyLoadingText()),
				Color.WHITE, Settings.getLoadingTextSize());
		text.getStyleClass().add(Settings.getCssClassLoadingText());

		VBox box = new VBox(Settings.getLoadingVBoxSpacing(), spinner, text);
		box.setAlignment(Pos.CENTER);

		overlay.getChildren().addAll(bg, box);
		overlay.prefWidthProperty().bind(FXGL.getPrimaryStage().widthProperty());
		overlay.prefHeightProperty().bind(FXGL.getPrimaryStage().heightProperty());
		overlay.setViewOrder(-9999);
		overlay.getProperties().put(CURTAIN_TAG, Boolean.TRUE);
		return overlay;
	}

	private Timeline buildSpinnerTimeline(ImageView spinner) {
		int frameWidth = Settings.getLoadingAnimFrameWidth();
		int frameHeight = Settings.getLoadingAnimFrameHeight();
		int totalFrames = Settings.getLoadingAnimTotalFrames();
		int columns = Settings.getLoadingAnimColumns();
		final int[] frame = {0};

		Timeline t = new Timeline(new KeyFrame(Duration.millis(Settings.getLoadingAnimDurationMillis()), e -> {
			frame[0] = (frame[0] + 1) % totalFrames;
			int col = frame[0] % columns;
			int row = frame[0] / columns;
			spinner.setViewport(new Rectangle2D(col * frameWidth, row * frameHeight, frameWidth, frameHeight));
		}));
		t.setCycleCount(Animation.INDEFINITE);
		return t;
	}

	private Timeline buildCurtainTimeline(ImageView curtain, boolean isClosing) {
		int maxFrame = Settings.getCurtainCloseMaxFrame();
		int totalFrames = maxFrame + 1;
		double secPerFrame = Settings.getCurtainCloseDurationSec() / totalFrames;
		Timeline timeline = new Timeline();

		for (int i = 0; i < totalFrames; i++) {
			final int frameIndex = isClosing ? i : (maxFrame - i);
			timeline.getKeyFrames().add(
					new KeyFrame(Duration.seconds(i * secPerFrame),
							e -> curtain.setViewport(getViewportForFrame(frameIndex)))
			);
		}
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(Settings.getCurtainCloseDurationSec())));
		return timeline;
	}

	private Rectangle2D getViewportForFrame(int frameIndex) {
		int col = frameIndex % Settings.getCurtainColumns();
		int row = frameIndex / Settings.getCurtainColumns();
		return new Rectangle2D(
				col * Settings.getCurtainFrameWidth(),
				row * Settings.getCurtainFrameHeight(),
				Settings.getCurtainFrameWidth(),
				Settings.getCurtainFrameHeight()
		);
	}

	private void addToRoot(Parent root, Node n) {
		if (root == null) return;
		if (root instanceof Pane p && !p.getChildren().contains(n)) p.getChildren().add(n);
		else if (root instanceof Group g && !g.getChildren().contains(n)) g.getChildren().add(n);
	}

	private void removeFromRoot(Parent root, Node n) {
		if (root == null) return;
		if (root instanceof Pane p) p.getChildren().remove(n);
		else if (root instanceof Group g) g.getChildren().remove(n);
	}

	private void sweepCurtains(Parent root) {
		if (root == null) return;
		if (root instanceof Pane p)
			p.getChildren().removeIf(n -> n.getProperties().containsKey(CURTAIN_TAG));
		else if (root instanceof Group g)
			g.getChildren().removeIf(n -> n.getProperties().containsKey(CURTAIN_TAG));
	}

	private CustomTitleBar findActiveTitleBar(Parent parent) {
		if (parent == null) return null;
		for (Node child : parent.getChildrenUnmodifiable()) {
			if (child instanceof CustomTitleBar) {
				if (child.isVisible()) return (CustomTitleBar) child;
			} else if (child instanceof Parent) {
				CustomTitleBar found = findActiveTitleBar((Parent) child);
				if (found != null) return found;
			}
		}
		return null;
	}
}