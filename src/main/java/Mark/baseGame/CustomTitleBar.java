package Mark.baseGame;

import java.util.Objects;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class CustomTitleBar extends HBox
{
	public static final double TITLE_BAR_HEIGHT = Settings.getStandardTitlebarHeight();

	private double dragOffsetX;
	private double dragOffsetY;
	private Runnable onDragStart;
	private Runnable onDragEnd;
	private Runnable onLogoClicked;
	private Runnable onCloseClicked;

	private StackPane logoBox;

	public CustomTitleBar(String logoResourcePath)
	{
		this.getStyleClass().add(Settings.getCssClassCustomTitleBar());
		setPrefHeight(TITLE_BAR_HEIGHT);
		setMinHeight(TITLE_BAR_HEIGHT);
		setMaxHeight(TITLE_BAR_HEIGHT);

		setAlignment(Pos.CENTER_LEFT);
		int pad = Settings.getTitlebarPadding();
		setPadding(new Insets(0,pad,0,pad));
		setSpacing(pad);

		Region left = buildLeftLogo(logoResourcePath);
		Region center = buildCenterTitle();
		Group right = buildRightButton();

		HBox.setHgrow(center, Priority.ALWAYS);

		getChildren().addAll(left,center,right);

		setupDragBehavior();
	}

	private Region buildLeftLogo(String logoResourcePath)
	{
		logoBox = new StackPane();
		logoBox.setPrefSize(Settings.getLogoPrefSizeWidth(), Settings.getLogoPrefSizeHeight());
		logoBox.setMinSize(Settings.getLogoPrefSizeWidth(), Settings.getLogoPrefSizeHeight());
		try
		{
			Image img = FXGL.image(logoResourcePath);

			ImageView logo = new ImageView(img);
			logo.setPreserveRatio(true);
			logo.setFitHeight(Settings.getLogoFitHeight());
			logoBox.getChildren().add(logo);
		}
		catch(Exception e)
		{
			System.err.println(Settings.getErrorMsgTitlebarLogo());
		}

		return logoBox;
	}

	private Region buildCenterTitle()
	{
		StackPane box = new StackPane();
		box.setAlignment(Pos.CENTER);

		Text titleText = new Text();
		titleText.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyGameTitle()));
		titleText.getStyleClass().add(Settings.getCssClassMagicalText());
		box.getChildren().add(titleText);

		return box;
	}

	private Group buildRightButton()
	{
		Group magicX = new Group();
		int size = Settings.getTitlebarCloseBtnSize();
		Line line1 = new Line(0,0,size,size);
		Line line2 = new Line(size,0,0,size);

		line1.getStyleClass().add(Settings.getCssClassMagicCloseBtn());
		line2.getStyleClass().add(Settings.getCssClassMagicCloseBtn());

		magicX.getChildren().addAll(line1,line2);
		magicX.setCursor(Cursor.HAND);
		magicX.setFocusTraversable(false);

		Glow glow = new Glow(Settings.getTitlebarGlowNormal());
		magicX.setEffect(glow);

		double breathDur = Settings.getTitlebarCloseBreathingDuration();
		ScaleTransition breathing = new ScaleTransition(Duration.seconds(breathDur), magicX);

		breathing.setFromX(Settings.getTitlebarBreathingScaleFrom());
		breathing.setFromY(Settings.getTitlebarBreathingScaleFrom());
		breathing.setToX(Settings.getTitlebarBreathingScaleTo());
		breathing.setToY(Settings.getTitlebarBreathingScaleTo());

		breathing.setAutoReverse(true);
		breathing.setCycleCount(Animation.INDEFINITE);
		breathing.play();

		double hoverDur = Settings.getTitlebarCloseHoverDuration();
		magicX.setOnMouseEntered(e ->
		{
			breathing.pause();
			ScaleTransition st = new ScaleTransition(Duration.seconds(hoverDur), magicX);
			st.setToX(Settings.getTitlebarHoverScale());
			st.setToY(Settings.getTitlebarHoverScale());
			st.play();
			glow.setLevel(Settings.getTitlebarGlowHover());
		});

		magicX.setOnMouseExited(e ->
		{
			ScaleTransition st = new ScaleTransition(Duration.seconds(hoverDur), magicX);
			st.setToX(Settings.getTitlebarBreathingScaleFrom());
			st.setToY(Settings.getTitlebarBreathingScaleFrom());
			st.setOnFinished(ev -> breathing.play());
			st.play();
			glow.setLevel(Settings.getTitlebarGlowNormal());
		});

		magicX.setOnMouseClicked(e ->
		{
			if(e.getButton() == MouseButton.PRIMARY && onCloseClicked != null) {

				breathing.stop();

				ScaleTransition shrink = new ScaleTransition(Duration.seconds(Settings.getTitlebarLogoClickDuration()), magicX);
				shrink.setToX(Settings.getTitlebarClickShrinkScale());
				shrink.setToY(Settings.getTitlebarClickShrinkScale());

				ScaleTransition grow = new ScaleTransition(Duration.seconds(Settings.getTitlebarLogoClickDuration()), magicX);
				grow.setToX(Settings.getTitlebarHoverScale());
				grow.setToY(Settings.getTitlebarHoverScale());

				SequentialTransition clickAnim = new SequentialTransition(shrink, grow);
				clickAnim.setOnFinished(event -> {

					var emitter = ParticleEmitters.newExplosionEmitter(Settings.getTitlebarParticleEmitterSize());

					Circle particleShape = new Circle(Settings.getTitlebarParticleRadius());
					particleShape.getStyleClass().add(Settings.getCssClassTitlebarParticle());

					emitter.setSourceImage(new Circle(Settings.getTitlebarParticleRadius(), Settings.getTitlebarParticleColor()).snapshot(null, null));
					emitter.setNumParticles(Settings.getTitlebarParticleNum());

					Entity particleEntity = FXGL.entityBuilder()
							.at(e.getSceneX(), e.getSceneY())
							.with(new ParticleComponent(emitter))
							.with(new ExpireCleanComponent(Duration.seconds(Settings.getTitlebarParticleLifespanSec())))
							.buildAndAttach();

					FXGL.getGameScene().addUINode(particleEntity.getViewComponent().getParent());
					magicX.setVisible(false);

					var wholeScene = getScene().getRoot();
					wholeScene.getStyleClass().add(Settings.getCssClassTitlebarFadeBg());

					FadeTransition fadeOut = new FadeTransition(Duration.seconds(Settings.getTitlebarCloseFadeOutDuration()), wholeScene);
					fadeOut.setToValue(0.0);
					fadeOut.setOnFinished(ev -> onCloseClicked.run());
					fadeOut.play();

				});
				clickAnim.play();
			}});
		return magicX;
	}

	private void setupDragBehavior()
	{
		setCursor(Cursor.MOVE);

		setOnMousePressed(e -> {
			if(e.getTarget() instanceof Button) return;
			if(getScene() != null && getScene().getWindow() != null)
			{
				dragOffsetX = e.getSceneX();
				dragOffsetY = e.getSceneY();
			}
			if(onDragStart != null) onDragStart.run();
			e.consume();
		});
		setOnMouseDragged(e -> {
			if(e.getTarget() instanceof Button) return;
			if(getScene() != null && getScene().getWindow() != null)
			{
				var window = getScene().getWindow();
				window.setX(e.getScreenX() - dragOffsetX);
				window.setY(e.getScreenY() - dragOffsetY);
			}
			e.consume();
		});
		setOnMouseReleased (e -> {
			if(onDragEnd != null) onDragEnd.run();
			e.consume();
		});
	}

	public void setOnDragStart(Runnable onDragStart) {
		this.onDragStart = onDragStart;
	}

	public void setOnDragEnd(Runnable onDragEnd) {
		this.onDragEnd = onDragEnd;
	}

	public void setOnLogoClicked(Runnable onLogoClicked) {
		this.onLogoClicked = onLogoClicked;
		if (this.onLogoClicked != null && logoBox != null)
		{
			logoBox.setCursor(Cursor.HAND);

			double animDur = Settings.getTitlebarLogoAnimDuration();

			logoBox.setOnMouseEntered(e ->
			{
				ScaleTransition st = new ScaleTransition(Duration.seconds(animDur), logoBox);
				st.setToX(Settings.getTitlebarLogoHoverScale());
				st.setToY(Settings.getTitlebarLogoHoverScale());
				st.play();
			});
			logoBox.setOnMouseExited(e ->
			{
				ScaleTransition st = new ScaleTransition(Duration.seconds(animDur), logoBox);
				st.setToX(Settings.getTitlebarBreathingScaleFrom());
				st.setToY(Settings.getTitlebarBreathingScaleFrom());
				st.play();
			});

			logoBox.setOnMouseClicked(e ->
			{
				if(e.getButton() == MouseButton.PRIMARY) {
					double clickDur = Settings.getTitlebarLogoClickDuration();

					ScaleTransition shrink = new ScaleTransition(Duration.seconds(clickDur), logoBox);
					shrink.setToX(Settings.getTitlebarClickShrinkScale());
					shrink.setToY(Settings.getTitlebarClickShrinkScale());

					ScaleTransition grow = new ScaleTransition(Duration.seconds(clickDur), logoBox);
					grow.setToX(Settings.getTitlebarLogoHoverScale());
					grow.setToY(Settings.getTitlebarLogoHoverScale());

					SequentialTransition clickAnim = new SequentialTransition(shrink, grow);
					clickAnim.setOnFinished(event -> this.onLogoClicked.run());
					clickAnim.play();
				}
				e.consume();
			});
		}
	}

	public void setOnCloseClicked(Runnable onCloseClicked) {
		this.onCloseClicked = onCloseClicked;
	}
}