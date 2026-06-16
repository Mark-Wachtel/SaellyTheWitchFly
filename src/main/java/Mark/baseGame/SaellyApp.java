package Mark.baseGame;

import java.util.*;
import java.util.prefs.Preferences;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityWorldListener;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;

import com.almasb.fxgl.localization.Language;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import static com.almasb.fxgl.dsl.FXGL.*;


public class SaellyApp extends GameApplication {
	

	private boolean debugMode = false;
	private boolean isCtrlDown = false;
	private boolean isAltDown = false;

	private WindowManager windowManager;
    private ExitCoordinator exitCoordinator;
    private CustomTitleBar titleBar;
	private ScrollingBackgroundView bgView;

	private double bgScrollX = 0;
	private double barrierTimer = 0;
	private double powerupTimer = 0;

	private Texture soundOnIcon;
	private Texture soundOffIcon;
	private Texture heartIcon;
	private Texture buffIcon;
	private Texture crashEffect;
	private Texture pickupEffect;
	private Texture invulnerableEffect;
	private Texture slowMotionEffect;
	private Texture loadingAnimation;

	private Music backgroundMusic;
	private Music menuMusic;
	private Music gameOverMusic;

	private Image backgroundImage;
	private Image topBarrier;
	private Image bottomBarrier;
	private Image slowMotionBuffImage;
	private Image scoreX10BuffImage;
	private Image invulnerableBuffImage;
	private Image extraLivesBuffImage;

	private Sound gameOverSound;
	private Sound buttonTickSound;

	private Button muteButton;

	private Text ingameScoreText;

	private Rectangle gameOverBg;
	private VBox gameOverBox;
	private TextField nameInputField;
	private ScoreEntry currentRun;
	private HighscoreLoader highscoreL;
	private Circle statusIndicator;
	private Boolean isServerReachable = null;
	private boolean isFetchingData = false;
	private StackPane modalOverlay;
	private Text gameVersion;
	private HBox updatedBox;
	private HBox topBar;
	private SimpleBooleanProperty serverOnlineProp = new SimpleBooleanProperty(true);

	//Testliste
	private List<ScoreEntry> highscoreList = new ArrayList<>();

	
	//Java Entry.
    public static void main(String[] args) 
    {
    	
        launch(args);
    }

	@Override
	protected void onUpdate(double tpf)
	{

		if (tpf > Settings.getMaxTpfThreshold()) tpf = Settings.getNormalTpfFallback();

		double currentSpeed = getd(Settings.getKeyGeneralSpeed());
		bgScrollX += currentSpeed * tpf;
		bgView.setScrollX(bgScrollX);

		if (muteButton != null && !muteButton.isVisible()) muteButton.setVisible(true);
		if (gameVersion != null && !gameVersion.isVisible()) gameVersion.setVisible(true);
		if (statusIndicator != null && !statusIndicator.isVisible()) statusIndicator.setVisible(true);

		if (!getb(Settings.getKeyIsGameOver())) {
			if (topBar != null && !topBar.isVisible()) topBar.setVisible(true);

			if (getb(Settings.getKeyGameStarted())) {

				double slowMoFactor = getd(Settings.getKeyBarrierSpeed()) / Settings.getBarrierSpeed();
				barrierTimer += tpf * slowMoFactor;

				if (barrierTimer >= Settings.getSpawnDurationBarrier()) {
					barrierTimer = 0;

					double barrierY = FXGLMath.random(Settings.getMinSpawnHeightBarrier(), Settings.getMaxSpawnHeightBarrier());
					var scaledTexture = texture(Settings.getLinkToTopBarrierImage(), Settings.getBarrierScaleWidth(), Settings.getBarrierScaleHeight());

					double hitboxW = Settings.getBarrierScaleWidth() * 0.4;
					double hitboxH = Settings.getBarrierScaleHeight() * 0.8;
					double offsetX = (Settings.getBarrierScaleWidth() - hitboxW) / 2.0;
					double offsetY = (Settings.getBarrierScaleHeight() - hitboxH) / 2.0;

					Entity newBarrier = entityBuilder()
							.type(EntityType.BARRIER)
							.at(getAppWidth(), barrierY)
							.view(scaledTexture)
							.bbox(new HitBox("MainBeam", new Point2D(offsetX, offsetY), BoundingShape.box(hitboxW, hitboxH)))
							.with(new CollidableComponent(true))
							.with(new ObstacleComponent())
							.buildAndAttach();

					boolean shouldSpawnPowerup = FXGLMath.randomBoolean(Settings.getPowerupSpawnChance()); //Chance in settings!!

					if (!getb(Settings.getKeyIsBuffActive()) && shouldSpawnPowerup) {

						BuffPowerupComponent.Type[] types = BuffPowerupComponent.Type.values();
						BuffPowerupComponent.Type randomType = types[FXGLMath.random(0, types.length - 1)];

						String textureName = switch (randomType) {
							case SLOW_MOTION -> Settings.getLinkToSlowMotionImage();
							case SCORE_X10 -> Settings.getLinkToScorex10Image();
							case INVULNERABILITY -> Settings.getLinkToInvulnerableImage();
							case EXTRA_LIFE -> Settings.getLinkToExtraLivesImage();
						};

						var scaledBuffTexture = switch (textureName) {
							case "powerups/slowMotion.png" -> texture(textureName, Settings.getSlowMotionImageWidth(), Settings.getSlowMotionImageHeight());
							case "powerups/x10Score.png" -> texture(textureName, Settings.getScoreX10ImageWidth(), Settings.getScoreX10ImageHeight());
							case "powerups/invulnerable.png" -> texture(textureName, Settings.getInvulnerableImageWidth(), Settings.getInvulnerableImageHeight());
							case "powerups/extraLife.png" -> texture(textureName, Settings.getExtraLifeImageWidth(), Settings.getExtraLifeImageHeight());
							default -> throw new IllegalStateException("Unexpected value: " + textureName);
						};

						double padding = scaledBuffTexture.getHeight() * 0.5;
						double spawnY = newBarrier.getBottomY() + padding;

						if (spawnY + scaledBuffTexture.getHeight() > Settings.getStandardWindowHeightWithoutTitlebar()) {
							spawnY = newBarrier.getY() - scaledBuffTexture.getHeight() - padding;
						}

						entityBuilder()
								.type(EntityType.BUFF_POWERUP)
								.at(getAppWidth(), spawnY)
								.viewWithBBox(scaledBuffTexture)
								.with(new CollidableComponent(true))
								.with(new BuffPowerupComponent(randomType))
								.buildAndAttach();
					}
				}
			}
		}
	}

    //All stuff that is needed to setup the game.
	@Override
	protected void initSettings(GameSettings settings) 
	{
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		String savedLang = prefs.get(Settings.getPrefsKeyLanguage(), Settings.getLangGerman());
		Language langToSet;
		if (savedLang.equals(Settings.getLangEnglish()))
		{
			langToSet = Language.ENGLISH;
		}
		else
		{
			langToSet = Language.GERMAN;
		}
		settings.setDefaultLanguage(langToSet);



		settings.setWidth(Settings.getStandardWindowWidth());
        settings.setHeight(Settings.getStandardWindowHeight());
        settings.setTitle(Settings.getWindowTitle());
		settings.setSceneFactory(new SceneFactory());
		settings.setVersion(Settings.getGameVersion());
		settings.setAppIcon(Settings.getLinkToUiAppicon());
		settings.setFontUI(Settings.getLinkToFontEagle());
		settings.setFontGame(Settings.getLinkToFontEagle());
		settings.setFontText(Settings.getLinkToFontEagle());
		settings.setFontMono(Settings.getLinkToFontEagle());
		settings.setManualResizeEnabled(false);
		settings.setPreserveResizeRatio(false);
		settings.setStageStyle(StageStyle.UNDECORATED);
		settings.setIntroEnabled(true);
		settings.setMainMenuEnabled(true);
		settings.setMenuKey(KeyCode.F24);



	}

	//All stuff is needed to be loaded pre init the game.
	@Override
	protected void onPreInit()
	{
		getLocalizationService().addLanguageData(Language.ENGLISH, ResourceBundle.getBundle(Settings.getBundlePathTexts(), Locale.US));
		getLocalizationService().addLanguageData(Language.GERMAN, ResourceBundle.getBundle(Settings.getBundlePathTexts(), Locale.GERMAN));

		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		String savedLang = prefs.get(Settings.getPrefsKeyLanguage(), Settings.getLangGerman());
		Language currentLang = savedLang.equals(Settings.getLangEnglish()) ? Language.ENGLISH : Language.GERMAN;

		getSettings().getLanguage().setValue(Language.NONE);
		getSettings().getLanguage().setValue(currentLang);

		FXGL.getSettings().setGlobalMusicVolume(prefs.getDouble("musicVolume", 0.5));
		FXGL.getSettings().setGlobalSoundVolume(prefs.getDouble("soundVolume", 0.5));

		int muteSize = Settings.getIconSizeMute();
		soundOnIcon = getAssetLoader().loadTexture(Settings.getLinkToUiSoundUnmutedImage(),muteSize,muteSize);
		soundOffIcon = getAssetLoader().loadTexture(Settings.getLinkToUiSoundMutedImage(),muteSize,muteSize);
		heartIcon = getAssetLoader().loadTexture(Settings.getLinkToHeartUiImage(),Settings.getHeartUiImageWidth(),Settings.getHeartUiImageHeight());
		int buffSize = Settings.getIconSizeBuff();
		buffIcon = getAssetLoader().loadTexture(Settings.getLinkToInvulnerableImage(),buffSize,buffSize);

		backgroundMusic = getAssetLoader().loadMusic(Settings.getLinkToBackgroundMusic());
		menuMusic = getAssetLoader().loadMusic(Settings.getLinkToMenuMusic());
		gameOverMusic = getAssetLoader().loadMusic(Settings.getLinkToGameOverMusic());
		backgroundImage = getAssetLoader().loadImage(Settings.getLinkToBackgroundImage());

		topBarrier = getAssetLoader().loadImage(Settings.getLinkToTopBarrierImage());
		slowMotionBuffImage = getAssetLoader().loadImage(Settings.getLinkToSlowMotionImage());
		scoreX10BuffImage = getAssetLoader().loadImage(Settings.getLinkToScorex10Image());
		invulnerableBuffImage = getAssetLoader().loadImage(Settings.getLinkToInvulnerableImage());
		extraLivesBuffImage = getAssetLoader().loadImage(Settings.getLinkToExtraLivesImage());

		gameOverSound = getAssetLoader().loadSound(Settings.getLinkToGameOverSoundeffect());
		buttonTickSound = getAssetLoader().loadSound(Settings.getLinkToButtonTickSound());

		crashEffect = getAssetLoader().loadTexture(Settings.getLinkToCrashEffectAnimation());
		pickupEffect = getAssetLoader().loadTexture(Settings.getLinkToPickupAnimation());
		invulnerableEffect = getAssetLoader().loadTexture(Settings.getLinkToInvulnerableAnimation());
		slowMotionEffect = getAssetLoader().loadTexture(Settings.getLinkToSlowMotionAnimation());
		loadingAnimation = getAssetLoader().loadTexture(Settings.getLinkToLoadingAnimation());

		highscoreL = new HighscoreLoader(highscoreList);
		highscoreL.load();

		highscoreL.pingServer(this::updateServerStatusUI);

	}
	//All stuff thats needed to load before the game starts. 
	@Override
	protected void initGame() 
	{

		set(Settings.getKeyIsGameOver(), false);
		set(Settings.getKeyIsCrashing(), false);

		if (gameOverMusic != null) getAudioPlayer().stopMusic(gameOverMusic);
		if (menuMusic != null) getAudioPlayer().stopMusic(menuMusic);
		if (backgroundMusic != null) getAudioPlayer().stopMusic(backgroundMusic);

		getAudioPlayer().loopMusic(backgroundMusic);

		double offsetX = (Settings.getInitPlayerScaleWidth()-Settings.getPlayerCollisionBoxWidth()) / 2.0;
		double offsetY = (Settings.getInitPlayerScaleHeight()-Settings.getPlayerCollisionBoxHeight()) / 2.0;

		entityBuilder().type(EntityType.PLAYER).at(Settings.getPlayerStartXPosition(),Settings.getPlayerStartYPosition()).scale(Settings.getInitPlayerScale(), Settings.getInitPlayerScale()).bbox(new HitBox(Settings.getHitboxNameBody(),new Point2D(offsetX,offsetY),BoundingShape.box(Settings.getPlayerCollisionBoxWidth(),Settings.getPlayerCollisionBoxHeight()))).with(new PhysicsComponent()).with(new CollidableComponent(true)).with(new AnimationComponent()).with(new PlayerComponent()).buildAndAttach();

		bgView = new ScrollingBackgroundView(texture(Settings.getLinkToBackgroundImage(), getAppWidth(), getAppHeight()), Orientation.HORIZONTAL);
		entityBuilder().at(0,Settings.getStandardTitlebarHeight()).view(bgView).zIndex(-1).buildAndAttach();
		startHeartbeat();

		getGameWorld().addWorldListener(new EntityWorldListener() {
			@Override
			public void onEntityAdded(Entity entity) {
				if (entity.getBoundingBoxComponent() != null) {
					if (!entity.hasComponent(HitboxDebuggerComponent.class)) {
						entity.addComponent(new HitboxDebuggerComponent());
					}
				}
			}

			@Override
			public void onEntityRemoved(Entity entity) {
				//remove läuft bereits über die Component Klasse selber
			}
		});

		getGameWorld().getEntities().forEach(f -> {
			if (f.getType() == EntityType.PLAYER || f.getType() == EntityType.BARRIER || f.getType() == EntityType.BUFF_POWERUP) {
				if (!f.hasComponent(HitboxDebuggerComponent.class)) {
					f.addComponent(new HitboxDebuggerComponent());
				}
			}
		});

	}

	@Override
	protected void initPhysics()
	{
		getPhysicsWorld().setGravity(0,Settings.getGravity());
		getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.BARRIER) {
			@Override
			protected void onCollisionBegin(Entity player, Entity barrier) {
				if (getb(Settings.getKeyIsInvulnerable()))return;

				if (player.hasComponent(HitboxDebuggerComponent.class)) player.getComponent(HitboxDebuggerComponent.class).setColliding(true);

				int currentLives = geti(Settings.getKeyLives());
				if (currentLives > 1)
				{
					inc(Settings.getKeyLives(), -1);
					set(Settings.getKeyIsInvulnerable(), true);
					getGameTimer().runOnceAfter(() -> set(Settings.getKeyIsInvulnerable(), false), Duration.seconds(Settings.getInvulnerableDurationInSeconds()));
					barrier.removeFromWorld();
				}
				else
				{
					double effectW = Settings.getCrashEffectTargetWidth();
					double effectH = Settings.getCrashEffectTargetHeight();
					javafx.geometry.Point2D center = player.getCenter();
					double spawnX = center.getX() - (effectW / 2.0) + Settings.getCrashEffectOffsetX();
					double spawnY = center.getY() - (effectH / 2.0) + Settings.getCrashEffectOffsetY();

					player.removeFromWorld();
					entityBuilder()
							.at(spawnX, spawnY)
							.zIndex(100)
							.with(new EffectComponent(EffectComponent.EffectType.CRASH))
							.buildAndAttach();
					gameOver();
				}
			}
		});

				getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.BUFF_POWERUP) {
				@Override
					protected void onCollisionBegin(Entity player, Entity buffPowerup)
				{
					BuffPowerupComponent comp = buffPowerup.getComponent(BuffPowerupComponent.class);
					comp.activateEffect();

					double effectW = Settings.getPickupEffectTargetWidth();
					double effectH = Settings.getPickupEffectTargetHeight();
					javafx.geometry.Point2D center = player.getCenter();
					double spawnX = center.getX() - (effectW / 2.0) + Settings.getPickupEffectOffsetX();
					double spawnY = center.getY() - (effectH / 2.0) + Settings.getPickupEffectOffsetY();

					entityBuilder()
							.at(spawnX, spawnY)
							.zIndex(100)
							.with(new EffectComponent(EffectComponent.EffectType.PICKUP))
							.buildAndAttach();

					var type = comp.getType();
					double buffDuration = switch (type) {
						case INVULNERABILITY -> Settings.getInvulnerableDurationInSeconds();
						case SLOW_MOTION -> Settings.getSlowMotionBuffPowerupDurationSeconds();
						case SCORE_X10 -> Settings.getScoreX10DurationSeconds();
						default -> 1.0;
					};

					if (type != BuffPowerupComponent.Type.EXTRA_LIFE)
					{
						player.addComponent(new ActiveBuffComponent(type));

						getGameTimer().runOnceAfter(()->{
							if (player.hasComponent(ActiveBuffComponent.class)) {
								player.removeComponent(ActiveBuffComponent.class);
							}
						}, Duration.seconds(buffDuration));
					}

					buffPowerup.removeFromWorld();
				}
				});

	}
	
	@Override
	protected void initUI()
	{

		//css loading
		try {
			String css = getClass().getResource(Settings.getLinkToCss()).toExternalForm();
			getGameScene().getRoot().getStylesheets().add(css);
		} catch (NullPointerException e) {
			System.err.println(Settings.getMsgErrCssMain() + Settings.getLinkToCss());
		}

		//click and thick sound for buttons
		final Button[] lastHoveredInGame = {null};

		getGameScene().getRoot().addEventFilter(MouseEvent.MOUSE_PRESSED, e ->
		{
			Node target = (Node) e.getTarget();
			while (target != null) {
				if (target instanceof Button)
				{
					try { FXGL.getAudioPlayer().playSound(FXGL.getAssetLoader().loadSound(Settings.getLinkToClickSound())); } catch (Exception ex) {}
					break;
				}
				target = target.getParent();
			}
		});

		getGameScene().getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, e ->
		{
			Node target = (Node) e.getTarget();
			Button currentHover = null;

			while (target != null)
			{
				if (target instanceof Button)
				{
					currentHover = (Button) target;
					break;
				}
				target = target.getParent();
			}

			if (currentHover != null && currentHover != lastHoveredInGame[0])
			{
				try { FXGL.getAudioPlayer().playSound(FXGL.getAssetLoader().loadSound(Settings.getLinkToButtonTickSound())); } catch (Exception ex) {}
				lastHoveredInGame[0] = currentHover;
			}
			else if (currentHover == null)
			{
				lastHoveredInGame[0] = null;
			}
		});

		//Window - Manager
		if (windowManager == null)
		{
			Platform.runLater(() ->
			{
				Stage stage = getPrimaryStageSafely();
				if (stage == null)
				{
					System.err.println(Settings.getMsgErrStage());
					return;
				}

				try
				{
					stage.getIcons().clear();
					stage.getIcons().add(texture(Settings.getLinkToUiAppicon()).getImage());
				}
				catch (Exception e)
				{
					System.err.println(Settings.getMsgErrStage() + e.getMessage());
				}

				Parent fxglRoot = getFxglGameRootSafely();
				if (fxglRoot == null){
					System.err.println(Settings.getMsgErrRoot());
					return;
				}


				Scene scn = stage.getScene();
				if (scn == null)
				{
					System.err.println(Settings.getMsgErrScene());
					return;
				}

				scn.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event ->
				{
					if (event.getCode() == KeyCode.ESCAPE)
					{
						event.consume();
						if (isFetchingData) return;

						var currentScene = getSceneService().getCurrentScene();
						boolean isGameScene = currentScene == getSceneService().getGameScene();
						boolean isMenuScene = currentScene instanceof FXGLMenu;

						if (isGameScene)
						{
							if (muteButton != null) muteButton.setVisible(false);
							if (gameVersion != null) gameVersion.setVisible(false);
							if (statusIndicator != null) statusIndicator.setVisible(false);
							if (topBar != null) topBar.setVisible(false);
							getGameController().gotoGameMenu();
						}
						else if (isMenuScene)
						{
							if (currentScene instanceof CustomPauseMenuScene)
							{
								CustomPauseMenuScene pauseMenu = (CustomPauseMenuScene) currentScene;
								if (pauseMenu.handleEscapeBack())
								{
									return;
								}
							}
							getGameController().gotoPlay();
						}
					}
				});

				titleBar = new CustomTitleBar(Settings.getLinkToLogo());

				getGameScene().getViewport().setY(Settings.getViewportOffsetY());
				titleBar.visibleProperty().bind(getPrimaryStage().fullScreenProperty().not());
				titleBar.managedProperty().bind(titleBar.visibleProperty());
				titleBar.setPrefHeight(CustomTitleBar.TITLE_BAR_HEIGHT);
				titleBar.setMinHeight(CustomTitleBar.TITLE_BAR_HEIGHT);
				titleBar.setMaxHeight(CustomTitleBar.TITLE_BAR_HEIGHT);
				titleBar.prefWidthProperty().bind(stage.widthProperty());
				titleBar.setViewOrder(Settings.getViewOrderTitlebar());
				getGameScene().getRoot().getChildren().add(titleBar);

				exitCoordinator = new ExitCoordinator(this::safeBeforeExit, () -> {getGameController().exit();});

				titleBar.setOnLogoClicked(() ->
				{
					if (isFetchingData) return;

					var currentScene = getSceneService().getCurrentScene();
					boolean isGameScene = currentScene == getSceneService().getGameScene();
					boolean isMenuScene = currentScene instanceof FXGLMenu;

					if (isGameScene)
					{
						if (muteButton != null) muteButton.setVisible(false);
						if (gameVersion != null) gameVersion.setVisible(false);
						if (statusIndicator != null) statusIndicator.setVisible(false);
						if (topBar != null) topBar.setVisible(false);
						getGameController().gotoGameMenu();
					}
					else if (isMenuScene)
					{
						getGameController().gotoPlay();
					}
				});

				titleBar.setOnCloseClicked(exitCoordinator::requestExit);

				windowManager = new WindowManager(stage);


				windowManager.initAndApplyStartupMode(WindowMode.WINDOWED);

				getSceneService().getGameMenuScene().ifPresent(menuScene ->
				{
					menuScene.getRoot().sceneProperty().addListener((obs, oldScene,newScene)->{
						if (newScene != null)
						{
							if (muteButton != null) muteButton.setVisible(false);
							if (gameVersion != null) gameVersion.setVisible(false);
							if (statusIndicator != null) statusIndicator.setVisible(false);
							if (topBar != null) topBar.setVisible(false);

							if (getb(Settings.getKeyIsGameOver()))
							{
								if (gameOverMusic != null) getAudioPlayer().pauseMusic(gameOverMusic);
							}
							else
							{
								if (backgroundMusic != null) getAudioPlayer().pauseMusic(backgroundMusic);
							}

							if (menuMusic != null) getAudioPlayer().loopMusic(menuMusic);
						}
						else
						{
							if (menuMusic != null) getAudioPlayer().stopMusic(menuMusic);

							if (getb(Settings.getKeyIsGameOver()))
							{
								if (gameOverMusic != null) getAudioPlayer().resumeMusic(gameOverMusic);
							}
							else
							{
								if(backgroundMusic != null) getAudioPlayer().resumeMusic(backgroundMusic);
							}
						}
					});
				});
			});
		}

		final double[] lastVolume = { getSettings().getGlobalMusicVolume() };

		Text scoreUI = getUIFactoryService().newText("",Color.WHITE, Settings.getFontSizeScore());
		this.ingameScoreText = scoreUI;
		scoreUI.textProperty().bind(localizedStringProperty(Settings.getLangKeyIngamePoints()).concat(" ").concat(getWorldProperties().intProperty(Settings.getKeyScore()).asString()));
		Text livesText = getUIFactoryService().newText("", Color.BLACK,Settings.getFontSizeLives());
		livesText.textProperty().bind(getWorldProperties().intProperty(Settings.getKeyLives()).asString(Settings.getFormatLives()));
		HBox livesUi = new HBox(Settings.getSpacingLarge(), heartIcon, livesText);
		HBox powerupUI = new HBox(Settings.getSpacingLarge(),buffIcon);
		powerupUI.visibleProperty().bind(getbp(Settings.getKeyIsBuffActive()));

		this.topBar = new HBox();
		topBar.setPrefWidth(getAppWidth());
		topBar.setPadding(new Insets(Settings.getTopBarPaddingTop(),Settings.getTopBarPaddingSide(),0,Settings.getTopBarPaddingSide()));
		HBox leftContainer = new HBox(Settings.getSpacingLarge(),livesUi);
		leftContainer.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(leftContainer, Priority.ALWAYS);
		HBox centerContainer = new HBox(scoreUI);
		centerContainer.setAlignment(Pos.CENTER);
		HBox.setHgrow(centerContainer,Priority.ALWAYS);
		HBox rightContainer = new HBox(powerupUI);
		rightContainer.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(rightContainer,Priority.ALWAYS);
		topBar.getChildren().addAll(leftContainer,centerContainer,rightContainer);
		addUINode(topBar);
		this.muteButton = new javafx.scene.control.Button("", soundOnIcon);

		muteButton.setOnAction(e -> {
			double currentVol = getSettings().getGlobalMusicVolume();

			if (currentVol > 0) {
				lastVolume[0] = currentVol;
				getSettings().setGlobalMusicVolume(0);
				getSettings().setGlobalSoundVolume(0);
				muteButton.setGraphic(soundOffIcon);
			} else {
				double restoreVol = (lastVolume[0] > 0) ? lastVolume[0] : Settings.getDefaultRestoreVolume();
				getSettings().setGlobalMusicVolume(restoreVol);
				getSettings().setGlobalSoundVolume(restoreVol);
				muteButton.setGraphic(soundOnIcon);
			}
		});
		muteButton.getStyleClass().add("mute-button");
		double animDur = Settings.getMuteBtnAnimDurationMs();
		double scaleHover = Settings.getMuteBtnScaleHover();
		double scalePress = Settings.getMuteBtnScalePress();
		double scaleNormal = Settings.getMuteBtnScaleNormal();

		muteButton.setOnMouseEntered(e ->{
			var st = new ScaleTransition(Duration.millis(animDur),muteButton);
			st.setToX(scaleHover);
			st.setToY(scaleHover);
			st.play();
		});
		muteButton.setOnMouseExited(e->{
			var st = new ScaleTransition(Duration.millis(animDur),muteButton);
			st.setToX(scaleNormal);
			st.setToY(scaleNormal);
			st.play();
		});
		muteButton.setOnMousePressed(e->{
			var st = new ScaleTransition(Duration.millis(animDur),muteButton);
			st.setToX(scalePress);
			st.setToY(scalePress);
			st.play();
		});
		muteButton.setOnMouseReleased(e->{
			var st = new ScaleTransition(Duration.millis(animDur),muteButton);
			st.setToX(scaleHover);
			st.setToY(scaleHover);
			st.play();
		});
		muteButton.setTranslateX(getAppWidth() - Settings.getMuteBtnOffset());
		muteButton.setTranslateY(getAppHeight() - Settings.getMuteBtnOffset());
		muteButton.setFocusTraversable(false);
		muteButton.setViewOrder(Settings.getViewOrderUiTop());
		getGameScene().addUINode(muteButton);

		statusIndicator = new Circle(Settings.getStatusIndicatorRadius(),Color.RED);
		gameVersion = getUIFactoryService().newText(getLocalizationService().getLocalizedString(Settings.getLangKeyServerVersion()) + getSettings().getVersion(), Color.RED, Settings.getFontSizeVersion());
		this.updatedBox = new HBox(Settings.getSpacingMedium(), statusIndicator, gameVersion);
		gameVersion.managedProperty().bind(gameVersion.visibleProperty());
		updatedBox.setAlignment(Pos.CENTER_LEFT);
		updatedBox.setTranslateX(Settings.getVersionBoxOffsetX());
		updatedBox.setTranslateY(getAppHeight() -Settings.getVersionBoxOffsetY());
		updatedBox.setViewOrder(Settings.getViewOrderUiTop());
		getGameScene().addUINode(updatedBox);

		modalOverlay = new StackPane();
		modalOverlay.setMinSize(getAppWidth(), getAppHeight());
		modalOverlay.setMaxSize(getAppWidth(), getAppHeight());

		Rectangle dimBg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0,0,0,Settings.getUiDimBackgroundOpacity()));

		ImageView loadingSprite = new ImageView(image(Settings.getLinkToLoadingAnimation()));

		double loadingSize = Settings.getLoadingAnimViewSize();
		loadingSprite.setFitHeight(loadingSize);
		loadingSprite.setFitWidth(loadingSize);
		loadingSprite.setPreserveRatio(true);

		Text loadingText = getUIFactoryService().newText(getLocalizationService().getLocalizedString(Settings.getLangKeyLoadingText()),Color.WHITE,Settings.getFontSizeLoading());

		int columns = Settings.getLoadingAnimColumns();
		int frameWidth = Settings.getLoadingAnimFrameWidth();
		int frameHeight = Settings.getLoadingAnimFrameHeight();
		int totalFrames = Settings.getLoadingAnimTotalFrames();

		Timeline timeline = new Timeline();
		timeline.setCycleCount(Animation.INDEFINITE);

		for (int i = 0; i < totalFrames; i++)
		{
			int col = i % columns;
			int row = i / columns;
			double x = col * frameWidth;
			double y = row * frameHeight;

			Duration time = Duration.millis((1000.0 / totalFrames) * i);

			final int frameIndex = i;

			KeyFrame frame = new KeyFrame(time, e->{
				loadingSprite.setViewport(new Rectangle2D(x,y,frameWidth,frameHeight));

				if (frameIndex < 6)
				{
					loadingText.setText(getLocalizationService().getLocalizedString(Settings.getLangKeyLoadingText()) + ".");
				}
				else if (frameIndex < 12)
				{
					loadingText.setText(getLocalizationService().getLocalizedString(Settings.getLangKeyLoadingText()) + "..");
				}
				else
				{
					loadingText.setText(getLocalizationService().getLocalizedString(Settings.getLangKeyLoadingText()) + "...");
				}

			});
			timeline.getKeyFrames().add(frame);
		}

		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(Settings.getLoadingAnimDelaySeconds())));
		timeline.play();


		VBox contentBox = new VBox(Settings.getSpacingSmall());
		contentBox.setAlignment(Pos.CENTER);
		contentBox.getChildren().addAll(loadingSprite,loadingText);


		modalOverlay.getChildren().addAll(dimBg,contentBox);
		modalOverlay.setVisible(false);
		modalOverlay.setViewOrder(Settings.getViewOrderModal());
		getGameScene().addUINode(modalOverlay);
	}
	
	@Override
	protected void initGameVars(Map<String, Object> vars)
	{
		vars.put(Settings.getKeyScore(), Settings.getInitScore());
		vars.put(Settings.getKeyGeneralSpeed(), Settings.getInitSpeed());
		vars.put(Settings.getKeyBarrierSpeed(), Settings.getInitBarrierSpeed());
		vars.put(Settings.getKeyGameStarted(), Settings.getInitGameStarted());
		vars.put(Settings.getKeyLives(), Settings.getInitLives());
		vars.put(Settings.getKeyIsInvulnerable(), Settings.getInitIsInvulnerable());
		vars.put(Settings.getKeyIsGameOver(), Settings.getIsGameOver());
		vars.put(Settings.getKeyIsCrashing(), Settings.getInitPlayerIsCrashing());
		vars.put(Settings.getKeyIsBuffActive(), Settings.getInitIsBuffActive());
		vars.put(Settings.getKeyIsDebuffActive(), Settings.getInitIsDebuffActive());
		vars.put(Settings.getKeyPlaced(), Settings.getInitPlacedValue());
		vars.put(Settings.getKeyIsDebugging(), Settings.getInitIsDebugging());
	}
	
	
	@Override
	protected void initInput()
	{
		Input input = getInput();
		Preferences prefs = Preferences.userNodeForPackage(SaellyApp.class);

		// 1. Windowed (Fallback: F10)
		String savedWindowedStr = prefs.get("key_for_" + Settings.getActionWindowed(), Settings.getDefaultKeyWindowed());
		KeyCode windowedKey;
		try { windowedKey = KeyCode.valueOf(savedWindowedStr); }
		catch (IllegalArgumentException e) { windowedKey = KeyCode.F10; }

		input.addAction(new UserAction(Settings.getActionWindowed()) {
			@Override
			protected void onActionBegin() {
				if(windowManager != null) windowManager.switchMode(WindowMode.WINDOWED);
			}
		}, windowedKey);

		// 2. Borderless (Fallback: F11)
		String savedBorderlessStr = prefs.get("key_for_" + Settings.getActionBorderless(), Settings.getDefaultKeyBorderless());
		KeyCode borderlessKey;
		try { borderlessKey = KeyCode.valueOf(savedBorderlessStr); }
		catch (IllegalArgumentException e) { borderlessKey = KeyCode.F11; }

		input.addAction(new UserAction(Settings.getActionBorderless()) {
			@Override
			protected void onActionBegin() {
				if(windowManager != null) windowManager.switchMode(WindowMode.BORDERLESS);
			}
		}, borderlessKey);

		// 3. Fullscreen (Fallback: F12)
		String savedFullscreenStr = prefs.get("key_for_" + Settings.getActionFullscreen(), Settings.getDefaultKeyFullscreen());
		KeyCode fullscreenKey;
		try { fullscreenKey = KeyCode.valueOf(savedFullscreenStr); }
		catch (IllegalArgumentException e) { fullscreenKey = KeyCode.F12; }

		input.addAction(new UserAction(Settings.getActionFullscreen()) {
			@Override
			protected void onActionBegin() {
				if(windowManager != null) windowManager.switchMode(WindowMode.FULLSCREEN);
			}
		}, fullscreenKey);
		String savedJumpStr = prefs.get("key_for_" + Settings.getActionJump(), Settings.getDefaultKeyJump());
		KeyCode jumpKey;
		try {
			jumpKey = KeyCode.valueOf(savedJumpStr);
		} catch (IllegalArgumentException e) {
			jumpKey = KeyCode.SPACE;
		}

		onKeyDown(jumpKey, Settings.getActionJump(), () -> {
			if (getb(Settings.getKeyIsGameOver()) || getb(Settings.getKeyIsCrashing())) return;
			getGameWorld().getSingleton(EntityType.PLAYER).getComponent(PlayerComponent.class).jump();
		});

		String savedRestartStr = prefs.get("key_for_" + Settings.getActionRestart(), Settings.getDefaultKeyRestart());
		KeyCode restartKey;
		try {
			restartKey = KeyCode.valueOf(savedRestartStr);
		} catch (IllegalArgumentException e) {
			restartKey = KeyCode.ENTER;
		}

		onKeyDown(restartKey, Settings.getActionRestart(), () ->{
			if (getb(Settings.getKeyIsGameOver())){
				if (this.isFetchingData) return;
				if (nameInputField != null && currentRun != null)
				{

					if (gameOverBg != null) removeUINode(gameOverBg);
					if (gameOverBox != null) removeUINode(gameOverBox);

					this.isFetchingData = true;

					modalOverlay.setVisible(true);
					String spielerName = nameInputField.getText().trim();
					String finalName = spielerName.isEmpty() ? Settings.getDefaultPlayerName() : spielerName.toUpperCase();
					currentRun.setName(finalName);

					int rank = highscoreList.indexOf(currentRun) +1;

					highscoreL.safe(finalName, currentRun.getScore(),rank, ()->{
						this.isFetchingData = false;
						modalOverlay.setVisible(false);
						getGameController().startNewGame();
					});
				}
				else
				{
					if (gameOverBg != null) removeUINode(gameOverBg);
					if (gameOverBox != null) removeUINode(gameOverBox);
					getGameController().startNewGame();
				}
			}
		});

		//Debug
		getInput().addAction(new UserAction(Settings.getActionToggleDebug()) {
			@Override
			protected void onActionBegin()
			{
				boolean current = getb(Settings.getKeyIsDebugging());
				set(Settings.getKeyIsDebugging(), !current);
			}
		},KeyCode.H);
	}

	private void startHeartbeat()
	{
		highscoreL.pingServer(this::updateServerStatusUI);
		run(() ->
		{
		highscoreL.pingServer(this::updateServerStatusUI);
		}, Duration.minutes(Settings.getHeartbeatIntervalMinutes()));
	}

	public void gameOver()
	{

		if (getb(Settings.getKeyIsGameOver()) || getb(Settings.getKeyIsCrashing())) return;

		set(Settings.getKeyLives(), 0);
		set(Settings.getKeyIsCrashing(), true);


		set(Settings.getKeyGeneralSpeed(), 0.0);
		set(Settings.getKeyBarrierSpeed(), 0.0);
		if (backgroundMusic != null) getAudioPlayer().stopMusic(backgroundMusic);
		getAudioPlayer().playSound(getAssetLoader().loadSound(Settings.getLinkToGameOverSoundeffect()));

		this.isFetchingData = true;
		highscoreL.pingServer(this::updateServerStatusUI);
		highscoreL.fetchHighscores(fetchedList ->
		{
			this.highscoreList = fetchedList;
			this.isFetchingData = false;

			if (getb(Settings.getKeyIsGameOver()) && modalOverlay.isVisible())
			{
				buildAndShowGameOverUI();
				modalOverlay.setVisible(false);
			}
		});

		getGameTimer().runOnceAfter(() -> {
			set(Settings.getKeyIsGameOver(), true);

			if (gameOverMusic != null) getAudioPlayer().loopMusic(gameOverMusic);
			//Background noch anpassen!
			if (topBar != null) topBar.setVisible(false);

			if (this.isFetchingData)
			{
				modalOverlay.setVisible(true);
			}
			else
			{
				buildAndShowGameOverUI();
			}
		}, Duration.seconds(Settings.getGameOverDelaySeconds()));
	}

	private void buildAndShowGameOverUI()
	{
		String labelName = getLocalizationService().getLocalizedString(Settings.getLangKeyHighscoreName());
		String labelPoints = getLocalizationService().getLocalizedString(Settings.getLangKeyHighscorePoints());
		String labelHighscore = getLocalizationService().getLocalizedString(Settings.getLangKeyFinalScore());

		gameOverBg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, Settings.getGameOverDimOpacity()));
		gameOverBox = new VBox(Settings.getSpacingXxlarge());
		gameOverBox.setAlignment(Pos.CENTER);
		gameOverBox.setPrefSize(getAppWidth(), getAppHeight());
		gameOverBox.getStyleClass().add("game-over-screen");

		Text title = getUIFactoryService().newText("", Color.RED, Settings.getFontSizeGameoverTitle());
		title.textProperty().bind(localizedStringProperty(Settings.getLangKeyGameOver()));
		title.getStyleClass().add("game-over-title");

		this.currentRun = new ScoreEntry(Settings.getDefaultPlayerName(), geti(Settings.getKeyScore()), 0);
		highscoreList.add(currentRun);
		highscoreList.sort((a,b)-> Integer.compare(b.getScore(), a.getScore()));
		int index = highscoreList.indexOf(currentRun);
		int rank = index + 1;
		currentRun.setPlace(rank);

		VBox leaderboardBox = new VBox(Settings.getSpacingSmall());
		leaderboardBox.setAlignment(Pos.CENTER);
		leaderboardBox.getStyleClass().add("leaderboard-box");

		int start = index -Settings.getHighscoreDisplayOffsetStart();
		int end = index +Settings.getHighscoreDisplayOffsetEnd();

		if (start < 0)
		{
			end -= start;
			start = 0;
		}

		if (end >= highscoreList.size())
		{
			start -= (end -(highscoreList.size() -1));
			end = highscoreList.size() -1;
		}

		if (start < 0) start = 0; //Schutz falls bei Entwicklung weniger als 10 Einträge in der Liste vorhanden sind.

		for (int i = start; i <= end; i++)
		{
			if (i == index)
			{
				HBox playerSlot = new HBox(Settings.getSpacingXlarge());
				playerSlot.setAlignment(Pos.CENTER);

				Text rankText = getUIFactoryService().newText("#" + rank, Color.web(Settings.getColorHighlightHex()),Settings.getFontSizeGameoverRank());
				Text middleNameLabel = getUIFactoryService().newText(labelName + ": ", Color.web(Settings.getColorHighlightHex()),Settings.getFontSizeGameoverRank());

				this.nameInputField = new TextField(Settings.getDefaultPlayerName());
				nameInputField.getStyleClass().add("name-input-field");
				nameInputField.setPrefWidth(Settings.getNameInputPrefWidth());
				nameInputField.setAlignment(Pos.CENTER);

				nameInputField.textProperty().addListener((observable,oldVal,newVal)->{
					if (newVal.length() > Settings.getMaxPlayerNameLength()) nameInputField.setText(oldVal);
				});

				String formattedCurrentScore = String.format(Settings.getFormatGameoverScore(), geti(Settings.getKeyScore()));
				Text scoreText = getUIFactoryService().newText("", Color.web(Settings.getColorHighlightHex()), Settings.getFontSizeGameoverRank());
				scoreText.textProperty().bind(
						localizedStringProperty(Settings.getLangKeyFinalScore())
								.concat(": ")
								.concat(formattedCurrentScore)
								.concat(" ")
								.concat(localizedStringProperty(Settings.getLangKeyHighscorePoints()))
				);

				playerSlot.getChildren().addAll(rankText, middleNameLabel, nameInputField, scoreText);
				leaderboardBox.getChildren().add(playerSlot);
			}else
			{
				ScoreEntry entry = highscoreList.get(i);

				Color textColor = (i > index) ? Color.GRAY : Color.LIGHTGRAY;

				String formattedText = String.format("#%d  %s: %-5s  %s: %06d %s",
						i + 1, labelName, entry.getName(), labelHighscore, entry.getScore(), labelPoints);

				Text entryText = getUIFactoryService().newText(formattedText, textColor, Settings.getFontSizeGameoverEntry());
				leaderboardBox.getChildren().add(entryText);
			}
		}

		Text instruction = getUIFactoryService().newText("", Color.YELLOW, Settings.getFontSizeGameoverInstruct());
		instruction.textProperty().bind(localizedStringProperty(Settings.getLangKeyPressEnter()));

		gameOverBox.getChildren().addAll(title,leaderboardBox,instruction);

		addUINode(gameOverBg);
		addUINode(gameOverBox);

		nameInputField.requestFocus();
	}

	private void updateServerStatusUI(boolean isReachable)
	{
		if (this.isServerReachable != null && this.isServerReachable == isReachable) return;

		this.isServerReachable = isReachable;

		serverOnlineProp.set(isReachable);

		Color statusColor= isReachable ? Color.GREEN : Color.RED;

		if (statusIndicator != null) statusIndicator.setFill(statusColor);
		if (gameVersion != null) gameVersion.setFill(statusColor);
	}

	public void toggleMuteFromMenu() {
		if (muteButton != null) {
			muteButton.fire();
		}
	}
	
	private void safeBeforeExit()
	{
		//todo
		System.out.print(Settings.getMsgSafeExit());
	}
	
	private Stage getPrimaryStageSafely()
	{
		try
		{
			return getPrimaryStage();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private Parent getFxglGameRootSafely()
	{
		try
		{
			return getGameScene().getRoot();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public void changeGameLanguage(Language newLanguage)
	{
		FXGL.getLocalizationService().selectedLanguageProperty().set(newLanguage);
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		prefs.put(Settings.getPrefsKeyLanguage(), newLanguage.getName());
	}

	public javafx.beans.property.BooleanProperty serverOnlineProperty()
	{
		return serverOnlineProp;
	}

	public List<ScoreEntry> getHighscoreList()
	{
		return highscoreList;
	}
	public HighscoreLoader getHighscoreL() {return highscoreL;}
	
}