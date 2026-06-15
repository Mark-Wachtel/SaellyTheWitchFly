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
	
	public CustomTitleBar(String title, String logoResourcePath)
	{
		this.getStyleClass().add("custom-title-bar");
		setPrefHeight(TITLE_BAR_HEIGHT);
		setMinHeight(TITLE_BAR_HEIGHT);
		setMaxHeight(TITLE_BAR_HEIGHT);
		
		setAlignment(Pos.CENTER_LEFT);
		int pad = Settings.getTitlebarPadding();
		setPadding(new Insets(0,pad,0,pad));
		setSpacing(pad);

		getStyleClass().add("custom-title-bar");

		Region left = buildLeftLogo(logoResourcePath);
		Region center = buildCenterTitle(title);
		Group right = buildRightButton();
		
		HBox.setHgrow(center, Priority.ALWAYS);
		
		getChildren().addAll(left,center,right);
		
		setupDragBehavior();
	}

	private Region buildLeftLogo(String logoResourcePath)
	{
		StackPane box = new StackPane();
		box.setPrefSize(Settings.getLogoPrefSizeWidth(), Settings.getLogoPrefSizeHeight());
		box.setMinSize(Settings.getLogoPrefSizeWidth(), Settings.getLogoPrefSizeHeight());
		
		try 
		{
		Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Settings.getBasePathTextures()+logoResourcePath)));
		ImageView logo = new ImageView(img);
		logo.setPreserveRatio(true);
		logo.setFitHeight(Settings.getLogoFitHeight());
		box.getChildren().add(logo);
		}
		catch(Exception e)
		{
			Label fallback = new Label(Settings.getErrorMsgTitlebarLogo());
			fallback.setTextFill(Color.WHITE);
			box.getChildren().add(fallback);
		}
		
		box.setCursor(Cursor.HAND);

		double animDur = Settings.getTitlebarLogoAnimDuration();

		box.setOnMouseEntered(e -> {
			ScaleTransition st = new ScaleTransition(Duration.seconds(animDur), box);
			st.setToX(1.15);
			st.setToY(1.15);
			st.play();
		});

		box.setOnMouseExited(e -> {
			ScaleTransition st = new ScaleTransition(Duration.seconds(animDur), box);
			st.setToX(1.0);
			st.setToY(1.0);
			st.play();
		});

		box.setOnMouseClicked(e -> {
			if(e.getButton() == MouseButton.PRIMARY && onLogoClicked != null)
			{

				double clickDur = Settings.getTitlebarLogoClickDuration();
				ScaleTransition shrink = new ScaleTransition(Duration.seconds(clickDur), box);
				shrink.setToX(0.9);
				shrink.setToY(0.9);

				ScaleTransition grow = new ScaleTransition(Duration.seconds(clickDur), box);
				grow.setToX(1.15);
				grow.setToY(1.15);

				SequentialTransition clickAnim = new SequentialTransition(shrink, grow);
				clickAnim.setOnFinished(event -> onLogoClicked.run());
				clickAnim.play();
			}
			e.consume();
		});
		
		return box;
	}
	
	private Region buildCenterTitle(String title)
	{
		StackPane box = new StackPane();
		box.setAlignment(Pos.CENTER);

		var titleLable = FXGL.getUIFactoryService().newText(title, Color.WHITE, Settings.getTitlebarFontSize());
		box.getChildren().add(titleLable);

		return box;
	}

	private Group buildRightButton()
	{
		Group magicX = new Group();
		int size = Settings.getTitlebarCloseBtnSize();
		Line line1 = new Line(0,0,size,size);
		Line line2 = new Line(size,0,0,size);
		line1.getStyleClass().add("magic-close-button");
		line2.getStyleClass().add("magic-close-button");
		magicX.getChildren().addAll(line1,line2);
		magicX.setCursor(Cursor.HAND);
		magicX.setFocusTraversable(false);
		Glow glow = new Glow(0.5);
		magicX.setEffect(glow);

		double breathDur = Settings.getTitlebarCloseBreathingDuration();

		ScaleTransition breathing = new ScaleTransition(Duration.seconds(breathDur), magicX);
		breathing.setFromX(1.0); breathing.setFromY(1.0);
		breathing.setToX(1.08); breathing.setToY(1.08);
		breathing.setAutoReverse(true);
		breathing.setCycleCount(Animation.INDEFINITE);
		breathing.play();

		double hoverDur = Settings.getTitlebarCloseHoverDuration();
		magicX.setOnMouseEntered(e ->
		{
			breathing.pause();
			ScaleTransition st = new ScaleTransition(Duration.seconds(hoverDur), magicX);
			st.setToX(1.2);
			st.setToY(1.2);
			st.play();
			glow.setLevel(0.9);
		});

		magicX.setOnMouseExited(e ->
		{
			ScaleTransition st = new ScaleTransition(Duration.seconds(hoverDur), magicX);
			st.setToX(1.0);
			st.setToY(1.0);
			st.setOnFinished(ev -> breathing.play());
			st.play();
			glow.setLevel(0.5);
		});

		magicX.setOnMouseClicked(e ->
		{
			if(e.getButton() == MouseButton.PRIMARY && onCloseClicked != null) {

				breathing.stop();

				ScaleTransition shrink = new ScaleTransition(Duration.seconds(0.05), magicX);
				shrink.setToX(0.9);
				shrink.setToY(0.9);

				ScaleTransition grow = new ScaleTransition(Duration.seconds(0.05), magicX);
				grow.setToX(1.2);
				grow.setToY(1.2);

				SequentialTransition clickAnim = new SequentialTransition(shrink, grow);
				clickAnim.setOnFinished(event -> {

					var emitter = ParticleEmitters.newExplosionEmitter(40);
					emitter.setSourceImage(new Circle(3, Color.GOLD).snapshot(null, null));
					emitter.setNumParticles(35);

					Entity particleEntity = FXGL.entityBuilder()
							.at(e.getSceneX(), e.getSceneY())
							.with(new ParticleComponent(emitter))
							.with(new ExpireCleanComponent(Duration.seconds(1.5)))
							.buildAndAttach();

					FXGL.getGameScene().addUINode(particleEntity.getViewComponent().getParent());
					magicX.setVisible(false);

					var wholeScene = getScene().getRoot();
					getScene().setFill(Color.BLACK);

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
		});
		
		setOnMouseDragged(e -> { 
			if(e.getTarget() instanceof Button) return;
			if(getScene() != null && getScene().getWindow() != null)
			{
				var window = getScene().getWindow();
				window.setX(e.getScreenX() - dragOffsetX);
				window.setY(e.getScreenY() - dragOffsetY);
			}
		});
		
		setOnMouseReleased (e -> {
			if(onDragEnd != null) onDragEnd.run();
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
	}

	public void setOnCloseClicked(Runnable onCloseClicked) {
		this.onCloseClicked = onCloseClicked;
	}
	
	
	
	
}
