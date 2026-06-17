package Mark.baseGame;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.localization.Language;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.prefs.Preferences;

public class CustomMainMenuScene extends FXGLMenu {
    private WindowMode currentWindowMode = WindowMode.WINDOWED;
    private Language currentLanguage;

    private final VBox menuBox;
    private final VBox optionsBox;
    private final VBox controlsBox;
    private final VBox languagesBox;
    private final VBox leaderboardBox;
    private final VBox creditsBox;
    private final VBox exitBox;
    private final VBox corruptedBox;
    private Button btnExit;
    private boolean hasCheckedCorruption = false;
    private double lastMusicVol = Settings.getDefaultRestoreMusicVolume();
    private double lastSoundVol = Settings.getDefaultRestoreSoundVolume();

    private Button lastHoveredButton = null; //memory so tick sound dosen't repeat spammy

    public CustomMainMenuScene() {
        super(MenuType.MAIN_MENU);
        URL cssUrl = getClass().getResource(Settings.getLinkToCss());
        if (cssUrl != null) {
            getContentRoot().getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println(Settings.getMsgErrCssMain());
        }

        Preferences preferences = Preferences.userNodeForPackage(SaellyApp.class);
        String savedLang = preferences.get(Settings.getPrefsKeyLanguage(), Settings.getLangGerman());

        if (savedLang.equals(Settings.getLangEnglish())) {
            currentLanguage = Language.ENGLISH;
        } else {
            currentLanguage = Language.GERMAN;
        }

        ImageView backgroundImage = new ImageView(FXGL.image(Settings.getLinkToBackgroundImage()));
        backgroundImage.setFitWidth(getAppWidth());
        backgroundImage.setFitHeight(getAppHeight());

        Rectangle dimOverlay = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, Settings.getMainMenuDimOpacity()));

        StackPane bg = new StackPane();
        bg.setPrefSize(getAppWidth(), getAppHeight());

        //click and tick sounds on buttons
        bg.addEventFilter(MouseEvent.MOUSE_PRESSED, e ->
        {
            Node target = (Node) e.getTarget();
            while (target != null) {
                if (target instanceof Button) {
                    playClickSound();
                    break;
                }
                target = target.getParent();
            }
        });

        bg.addEventFilter(MouseEvent.MOUSE_MOVED, e ->
        {
            Node target = (Node) e.getTarget();
            Button currentHover = null;

            while (target != null) {
                if (target instanceof Button) {
                    currentHover = (Button) target;
                    break;
                }
                target = target.getParent();
            }

            if (currentHover != null && currentHover != lastHoveredButton)
            {
                playTickSound();
                lastHoveredButton = currentHover;
            }
            else if (currentHover == null)
            {
                lastHoveredButton = null;
            }
        });

        CustomTitleBar titleBar = new CustomTitleBar(Settings.getLinkToLogo());

        titleBar.setOnCloseClicked(() -> FXGL.getGameController().exit());
        StackPane.setAlignment(titleBar, Pos.TOP_CENTER);

        Text gameTitle = new Text();
        gameTitle.getStyleClass().add(Settings.getCssClassMagicalTitle());
        gameTitle.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyGameTitle()));

        TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(Settings.getMenuTitleFloatDur()), gameTitle);
        floatAnim.setByY(Settings.getMenuTitleFloatY());
        floatAnim.setAutoReverse(true);
        floatAnim.setCycleCount(Animation.INDEFINITE);
        floatAnim.play();
        StackPane.setAlignment(gameTitle, Pos.TOP_CENTER);
        StackPane.setMargin(gameTitle, new Insets(Settings.getMenuTitleMarginTop(), 0, 0, 0));

        Circle statusIndicator = new Circle(Settings.getStatusIndicatorRadius());
        Text gameVersion = FXGL.getUIFactoryService().newText("", Color.WHITE, Settings.getFontSizeVersion());

        gameVersion.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyServerVersion()).concat(" ").concat(FXGL.getSettings().getVersion()));

        SaellyApp app = (SaellyApp) FXGL.getAppCast();

        statusIndicator.fillProperty().bind(
                Bindings.when(app.serverOnlineProperty())
                        .then(Settings.getColorServerOnline())
                        .otherwise(Settings.getColorServerOffline())
        );
        gameVersion.fillProperty().bind(statusIndicator.fillProperty());

        // Layouting Corner Box
        VBox leftCornerBox = new VBox(Settings.getSpacingMedium());
        leftCornerBox.setAlignment(Pos.BOTTOM_LEFT);
        leftCornerBox.setPickOnBounds(false);
        Texture studioLogo = FXGL.texture(Settings.getLinkToLogo(), Settings.getMenuCornerLogoWidth(), Settings.getMenuCornerLogoWidth());
        HBox updatedBox = new HBox(Settings.getSpacingMedium(), statusIndicator, gameVersion);
        updatedBox.setAlignment(Pos.CENTER_LEFT);
        leftCornerBox.getChildren().addAll(studioLogo, updatedBox);
        StackPane.setAlignment(leftCornerBox, Pos.BOTTOM_LEFT);
        StackPane.setMargin(leftCornerBox, new Insets(0, 0, Settings.getMenuCornerMarginBottom(), Settings.getVersionBoxOffsetX()));

        //mute button
        int muteSize = Settings.getIconSizeMute();
        Texture soundOnIcon = FXGL.texture(Settings.getLinkToUiSoundUnmutedImage(), muteSize, muteSize);
        Texture soundOffIcon = FXGL.texture(Settings.getLinkToUiSoundMutedImage(), muteSize, muteSize);

        Button btnMute = new Button("", FXGL.getSettings().getGlobalMusicVolume() > 0 ? soundOnIcon : soundOffIcon);
        btnMute.getStyleClass().add(Settings.getCssClassMuteBtn());
        btnMute.setFocusTraversable(false);

        btnMute.setOnAction(e ->
        {
            Preferences prefs = Preferences.userNodeForPackage(SaellyApp.class);
            double currentVol = FXGL.getSettings().getGlobalMusicVolume();

            if (currentVol > 0)
            {

                this.lastMusicVol = currentVol;
                this.lastSoundVol = FXGL.getSettings().getGlobalSoundVolume();

                FXGL.getSettings().setGlobalMusicVolume(0.0);
                FXGL.getSettings().setGlobalSoundVolume(0.0);
            }
            else
            {
                if (this.lastMusicVol <= 0.0) this.lastMusicVol = Settings.getDefaultRestoreMusicVolume();
                if (this.lastSoundVol <= 0.0) this.lastSoundVol = Settings.getDefaultRestoreSoundVolume();

                FXGL.getSettings().setGlobalMusicVolume(this.lastMusicVol);
                FXGL.getSettings().setGlobalSoundVolume(this.lastSoundVol);

                prefs.putDouble(Settings.getPrefsKeyMusicVol(), this.lastMusicVol);
                prefs.putDouble(Settings.getPrefsKeySoundVol(), this.lastSoundVol);
            }
        });

        FXGL.getSettings().globalMusicVolumeProperty().addListener((obs, oldVal, newVal) ->
        {
            if (newVal.doubleValue() > 0)
            {
                btnMute.setGraphic(soundOnIcon);
            }
            else
            {
                btnMute.setGraphic(soundOffIcon);
            }
        });

        double animDur = Settings.getMuteBtnAnimDurationMs();
        double scaleHover = Settings.getMuteBtnScaleHover();
        double scalePress = Settings.getMuteBtnScalePress();
        double scaleNormal = Settings.getMuteBtnScaleNormal();

        btnMute.setOnMouseEntered(e -> playScaleAnim(btnMute, scaleHover, animDur));
        btnMute.setOnMouseExited(e -> playScaleAnim(btnMute, scaleNormal, animDur));
        btnMute.setOnMousePressed(e -> playScaleAnim(btnMute, scalePress, animDur));
        btnMute.setOnMouseReleased(e -> playScaleAnim(btnMute, scaleHover, animDur));

        StackPane.setAlignment(btnMute, Pos.TOP_LEFT);
        btnMute.setTranslateX(getAppWidth() - Settings.getMuteBtnOffset());
        btnMute.setTranslateY(getAppHeight() - Settings.getMuteBtnOffset());

        //main menu
        menuBox = new VBox(Settings.getMenuBoxSpacing());
        menuBox.setAlignment(Pos.CENTER);

        //neu game
        Button btnNewGame = new Button();
        btnNewGame.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnNewGame.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuNewGame()));
        btnNewGame.setOnAction(e ->
        {
            if (app != null)
            {
                app.setGameUIVisibility(true);
                app.playGameMusic();
            }
            fireNewGame();
        });

        Button btnOptions = new Button();
        btnOptions.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnOptions.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuOptions()));

        Button btnLeaderboard = new Button();
        btnLeaderboard.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnLeaderboard.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuLeaderboard()));

        Button btnCredits = new Button();
        btnCredits.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnCredits.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuCredits()));

        btnExit = new Button();
        btnExit.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnExit.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuExit()));

        menuBox.getChildren().addAll(btnNewGame, btnOptions, btnLeaderboard, btnCredits, btnExit);

        //option menu
        optionsBox = new VBox(Settings.getMenuBoxSpacing());
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setVisible(false);

        Text textMusic = new Text();
        textMusic.getStyleClass().add(Settings.getCssClassMagicalText());
        textMusic.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuMusic()));

        Slider musicSlider = new Slider(0, 1, FXGL.getSettings().getGlobalMusicVolume());
        musicSlider.setMaxWidth(Settings.getMenuSliderMaxWidth());
        musicSlider.getStyleClass().add(Settings.getCssClassSlider());
        FXGL.getSettings().globalMusicVolumeProperty().bindBidirectional(musicSlider.valueProperty());
        musicSlider.valueProperty().addListener((obs, oldV, newV) -> preferences.putDouble(Settings.getPrefsKeyMusicVol(), newV.doubleValue()));

        Text textSound = new Text();
        textSound.getStyleClass().add(Settings.getCssClassMagicalText());
        textSound.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuSound()));

        Slider soundSlider = new Slider(0, 1, FXGL.getSettings().getGlobalSoundVolume());
        soundSlider.setMaxWidth(Settings.getMenuSliderMaxWidth());
        soundSlider.getStyleClass().add(Settings.getCssClassSlider());
        FXGL.getSettings().globalSoundVolumeProperty().bindBidirectional(soundSlider.valueProperty());
        soundSlider.valueProperty().addListener((obs, oldV, newV) -> preferences.putDouble(Settings.getPrefsKeySoundVol(), newV.doubleValue()));

        Button btnLang = new Button();
        btnLang.getStyleClass().add(Settings.getCssClassMagicalBtn());

        btnLang.textProperty().bind(Bindings.createStringBinding(() ->
        {
            String label = FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeyMenuLanguage());
            Language currentLang = FXGL.getLocalizationService().selectedLanguageProperty().get();
            String langName = (currentLang == Language.GERMAN) ? "Deutsch" : "English";
            return label + ": " + langName;
        }, FXGL.getLocalizationService().selectedLanguageProperty()));

        Button btnWindowMode = new Button();
        btnWindowMode.getStyleClass().add(Settings.getCssClassMagicalBtn());
        Runnable updateModeText = () ->
        {
            String modeI18nKey = "";
            switch (currentWindowMode)
            {
                case WINDOWED:
                    modeI18nKey = Settings.getLangKeyMenuWindowed();
                    break;
                case BORDERLESS:
                    modeI18nKey = Settings.getLangKeyMenuBorderless();
                    break;
                case FULLSCREEN:
                    modeI18nKey = Settings.getLangKeyMenuFullscreen();
                    break;
            }

            String label = FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeyMenuMode());
            String value = FXGL.getLocalizationService().getLocalizedString(modeI18nKey);
            btnWindowMode.setText(label + ": " + value);
        };

        updateModeText.run();
        btnWindowMode.setOnAction(e ->
        {
            cycleWindowMode();
            updateModeText.run();
        });

        FXGL.getLocalizationService().selectedLanguageProperty().addListener((obs, o, n) -> updateModeText.run());

        Button btnControls = new Button();
        btnControls.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuControls()));
        btnControls.getStyleClass().add(Settings.getCssClassMagicalBtn());

        Button btnBackOpt = new Button();
        btnBackOpt.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuBack()));
        btnBackOpt.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnBackOpt.setOnAction(e ->
        {
            optionsBox.setVisible(false);
            menuBox.setVisible(true);
        });

        optionsBox.getChildren().addAll(textMusic, musicSlider, textSound, soundSlider, btnControls, btnLang, btnWindowMode, btnBackOpt);

        //control menu
        controlsBox = new VBox(Settings.getMenuBoxSpacing());
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setVisible(false);

        Text textControlsTitle = new Text();
        textControlsTitle.getStyleClass().add(Settings.getCssClassMagicalText());
        textControlsTitle.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuControlsTitle()));

        VBox scrollContent = new VBox(Settings.getSpacingXlarge());
        scrollContent.setAlignment(Pos.CENTER);
        scrollContent.getChildren().addAll(
                createKeybindRow(Settings.getLangKeyMenuJump(), Settings.getActionJump(), Settings.getDefaultKeyJump()),
                createKeybindRow(Settings.getLangKeyMenuRestart(), Settings.getActionRestart(), Settings.getDefaultKeyRestart()),
                createKeybindRow(Settings.getLangKeyMenuWindowed(), Settings.getActionWindowed(), Settings.getDefaultKeyWindowed()),
                createKeybindRow(Settings.getLangKeyMenuBorderless(), Settings.getActionBorderless(), Settings.getDefaultKeyBorderless()),
                createKeybindRow(Settings.getLangKeyMenuFullscreen(), Settings.getActionFullscreen(), Settings.getDefaultKeyFullscreen())
        );

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(Settings.getMenuScrollHeight());
        scrollPane.setMaxHeight(Settings.getMenuScrollHeight());
        scrollPane.getStyleClass().add(Settings.getCssClassScrollPane());

        Button btnBackControls = new Button();
        btnBackControls.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuBack()));
        btnBackControls.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnBackControls.setOnAction(e ->
        {
            controlsBox.setVisible(false);
            optionsBox.setVisible(true);
        });

        btnControls.setOnAction(e ->
        {
            optionsBox.setVisible(false);
            controlsBox.setVisible(true);
        });

        controlsBox.getChildren().addAll(textControlsTitle, scrollPane, btnBackControls);

        //language menu
        languagesBox = new VBox(Settings.getMenuBoxSpacing());
        languagesBox.setAlignment(Pos.CENTER);
        languagesBox.setVisible(false);

        Text textLanguagesTitle = new Text();
        textLanguagesTitle.getStyleClass().add(Settings.getCssClassMagicalText());
        textLanguagesTitle.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuLanguageTitle()));

        VBox langScrollContent = new VBox(Settings.getSpacingXlarge());
        langScrollContent.setAlignment(Pos.CENTER);

        Button btnLangDE = new Button();
        btnLangDE.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuLangDe()));
        btnLangDE.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnLangDE.setOnAction(e -> selectLanguage(Language.GERMAN));

        Button btnLangEN = new Button();
        btnLangEN.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuLangEn()));
        btnLangEN.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnLangEN.setOnAction(e -> selectLanguage(Language.ENGLISH));

        langScrollContent.getChildren().addAll(btnLangDE, btnLangEN);

        ScrollPane langScrollPane = new ScrollPane(langScrollContent);
        langScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        langScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        langScrollPane.setFitToWidth(true);
        langScrollPane.setPrefHeight(Settings.getMenuScrollHeight());
        langScrollPane.setMaxHeight(Settings.getMenuScrollHeight());
        langScrollPane.getStyleClass().add(Settings.getCssClassScrollPane());

        Button btnBackLang = new Button();
        btnBackLang.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuBack()));
        btnBackLang.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnBackLang.setOnAction(e ->
        {
            languagesBox.setVisible(false);
            optionsBox.setVisible(true);
        });

        btnLang.setOnAction(e ->
        {
            optionsBox.setVisible(false);
            languagesBox.setVisible(true);
        });

        languagesBox.getChildren().addAll(textLanguagesTitle, langScrollPane, btnBackLang);

        //leaderboard menu
        leaderboardBox = new VBox(Settings.getMenuBoxSpacing());
        leaderboardBox.setAlignment(Pos.CENTER);
        leaderboardBox.setVisible(false);

        Text textLeaderboardTitle = new Text();
        textLeaderboardTitle.getStyleClass().add(Settings.getCssClassMagicalTitle());
        textLeaderboardTitle.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuLeaderboardTitle()));

        VBox leaderboardContent = new VBox(Settings.getSpacingMedium());
        leaderboardContent.setAlignment(Pos.CENTER);

        ScrollPane leaderboardScroll = new ScrollPane(leaderboardContent);
        leaderboardScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leaderboardScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leaderboardScroll.setFitToWidth(true);
        leaderboardScroll.setPrefHeight(Settings.getMenuScrollHeight());
        leaderboardScroll.setMaxHeight(Settings.getMenuScrollHeight());
        leaderboardScroll.getStyleClass().add(Settings.getCssClassScrollPane());

        Button btnBackLeaderboard = new Button();
        btnBackLeaderboard.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuBack()));
        btnBackLeaderboard.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnBackLeaderboard.setOnAction(e ->
        {
            leaderboardBox.setVisible(false);
            menuBox.setVisible(true);
        });

        leaderboardBox.getChildren().addAll(textLeaderboardTitle, leaderboardScroll, btnBackLeaderboard);

        //credits menu
        creditsBox = new VBox(Settings.getMenuBoxSpacing());
        creditsBox.setAlignment(Pos.CENTER);
        creditsBox.setVisible(false);

        Text textCredits = new Text();
        textCredits.getStyleClass().add(Settings.getCssClassCreditsText());
        textCredits.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuCreditsText()));

        Button btnBackCred = new Button();
        btnBackCred.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuBack()));
        btnBackCred.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnBackCred.setOnAction(e ->
        {
            creditsBox.setVisible(false);
            menuBox.setVisible(true);
        });

        creditsBox.getChildren().addAll(textCredits, btnBackCred);

        //exit dialog
        exitBox = new VBox(Settings.getMenuBoxSpacing());
        exitBox.setAlignment(Pos.CENTER);
        exitBox.setVisible(false);

        Text textExitConfirm = new Text();
        textExitConfirm.getStyleClass().add(Settings.getCssClassMagicalText());
        textExitConfirm.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuExitConfirm()));

        HBox exitBtnRow = new HBox(Settings.getSpacingXlarge());
        exitBtnRow.setAlignment(Pos.CENTER);

        Button btnYes = new Button();
        btnYes.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuYes()));
        btnYes.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnYes.setOnAction(e -> FXGL.getGameController().exit());

        Button btnNo = new Button();
        btnNo.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuNo()));
        btnNo.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnNo.setOnAction(e ->
        {
            exitBox.setVisible(false);
            menuBox.setVisible(true);
        });

        exitBtnRow.getChildren().addAll(btnYes, btnNo);
        exitBox.getChildren().addAll(textExitConfirm, exitBtnRow);

        corruptedBox = new VBox(Settings.getMenuBoxSpacing());
        corruptedBox.setAlignment(Pos.CENTER);
        corruptedBox.setVisible(false);

        Text textCorrupted = new Text();
        textCorrupted.getStyleClass().add(Settings.getCssClassMagicalText());
        textCorrupted.setTextAlignment(TextAlignment.CENTER);

        Text textCorruptedButton = new Text();
        textCorruptedButton.getStyleClass().add(Settings.getCssClassMagicalText());
        textCorruptedButton.setTextAlignment(TextAlignment.CENTER);

        textCorrupted.textProperty().bind(Bindings.createStringBinding(() ->
        {
            HighscoreLoader hl = app.getHighscoreL();
            String timestamp = (hl != null) ? hl.getLastSyncTimestamp() : "";
            return FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeyCorruptedFile()) + "\n" + FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeyLastSync()) + " " + timestamp;
        }, FXGL.getLocalizationService().selectedLanguageProperty()));

        Button btnCorruptedOk = new Button();
        btnCorruptedOk.setText(textCorruptedButton.getText());
        btnCorruptedOk.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnCorruptedOk.setOnAction(e ->
        {
            corruptedBox.setVisible(false);
            menuBox.setVisible(true);
        });

        corruptedBox.getChildren().addAll(textCorrupted, btnCorruptedOk);

        //layouting
        btnOptions.setOnAction(e ->
        {
            menuBox.setVisible(false);
            optionsBox.setVisible(true);
        });

        btnLeaderboard.setOnAction(e ->
        {
            refreshLeaderboard(leaderboardContent, app);
            menuBox.setVisible(false);
            leaderboardBox.setVisible(true);
        });

        btnCredits.setOnAction(e ->
        {
            menuBox.setVisible(false);
            creditsBox.setVisible(true);
        });

        btnExit.setOnAction(e ->
        {
            e.consume();
            menuBox.setVisible(false);
            exitBox.setVisible(true);
        });

        StackPane.setMargin(menuBox, new Insets(Settings.getMenuBoxMarginTop(), 0, 0, 0));
        StackPane.setMargin(optionsBox, new Insets(Settings.getMenuBoxMarginTop(), 0, 0, 0));
        StackPane.setMargin(creditsBox, new Insets(Settings.getMenuBoxMarginTop(), 0, 0, 0));
        StackPane.setMargin(controlsBox, new Insets(Settings.getMenuBoxMarginTop(), 0, 0, 0));
        StackPane.setMargin(languagesBox, new Insets(Settings.getMenuBoxMarginTop(), 0, 0, 0));
        StackPane.setMargin(leaderboardBox, new Insets(Settings.getMenuBoxMarginTop(), 0, 0, 0));
        StackPane.setMargin(exitBox, new Insets(Settings.getMenuBoxMarginTop(), 0, 0, 0));
        StackPane.setMargin(corruptedBox, new Insets(Settings.getMenuBoxMarginTop(), 0, 0, 0));

        menuBox.setPickOnBounds(false);
        optionsBox.setPickOnBounds(false);
        creditsBox.setPickOnBounds(false);
        controlsBox.setPickOnBounds(false);
        languagesBox.setPickOnBounds(false);
        leaderboardBox.setPickOnBounds(false);
        exitBox.setPickOnBounds(false);
        corruptedBox.setPickOnBounds(false);


        bg.getChildren().addAll(titleBar, leftCornerBox, btnMute, menuBox, optionsBox, controlsBox, languagesBox, leaderboardBox, creditsBox, exitBox, corruptedBox, gameTitle);
        getContentRoot().getChildren().addAll(backgroundImage, dimOverlay, bg);

        //esc-listener
        getContentRoot().sceneProperty().addListener((obs, oldScene, newScene) ->
        {
            if (newScene != null)
            {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, e ->
                {
                    if (e.getCode() == KeyCode.ESCAPE)
                    {
                        boolean handled = handleEscapeBack();

                        if (!handled)
                        {
                            if (exitBox.isVisible())
                            {
                                exitBox.setVisible(false);
                                menuBox.setVisible(true);
                            }
                            else
                            {
                                menuBox.setVisible(false);
                                exitBox.setVisible(true);
                            }
                        }
                        e.consume();
                    }
                });
            }
        });
    }

    public void activateMainMenu()
    {
        SaellyApp app = (SaellyApp) FXGL.getAppCast();
            app.setGameUIVisibility(false);
            app.playMainMenuMusic();

            if (!hasCheckedCorruption)
            {
                hasCheckedCorruption = true;
                if (app.getHighscoreL() != null && app.getHighscoreL().isFileCorruptedAndRestored())
                {
                    menuBox.setVisible(false);
                    corruptedBox.setVisible(true);
                }
            }
    }


    private void playClickSound()
    {
        try
        {
            FXGL.getAudioPlayer().playSound(FXGL.getAssetLoader().loadSound(Settings.getLinkToClickSound()));
        }
        catch (Exception e)
        {
            System.err.println(Settings.getErrMsgButtonClickSound() + e.getMessage());
        }
    }

    private void playTickSound()
    {
        try
        {
            FXGL.getAudioPlayer().playSound(FXGL.getAssetLoader().loadSound(Settings.getLinkToButtonTickSound()));
        }
        catch (Exception e)
        {
            System.err.println(Settings.getErrMsgButtonTickSound() + e.getMessage());
        }
    }

    private void refreshLeaderboard(VBox container, SaellyApp app)
    {
        container.getChildren().clear();
        List<ScoreEntry> scores = app.getHighscoreList();

        if (scores == null || scores.isEmpty())
        {
            Text noEntries = new Text();
            noEntries.getStyleClass().add(Settings.getCssClassMagicalText());
            noEntries.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuNoEntries()));
            container.getChildren().add(noEntries);
        }
        else
        {
            for (int i = 0; i < scores.size(); i++)
            {
                ScoreEntry entry = scores.get(i);
                String formattedText = String.format(Settings.getFormatLeaderboardEntry(), (i + 1), entry.getName(), entry.getScore());
                Text entryText = new Text();
                entryText.getStyleClass().add(Settings.getCssClassMagicalText());
                entryText.setText(formattedText);
                container.getChildren().add(entryText);
            }
        }
    }

    @Override
    public void onEnteredFrom(Scene prevState)
    {
        super.onEnteredFrom(prevState);

        if (btnExit != null)
        {
            btnExit.setDisable(true);
            PauseTransition shield = new PauseTransition(Duration.millis(100));
            shield.setOnFinished(ev -> btnExit.setDisable(false));
            shield.play();
        }

        if (menuBox != null) menuBox.setVisible(true);
        if (exitBox != null) exitBox.setVisible(false);
        if (optionsBox != null) optionsBox.setVisible(false);
        if (controlsBox != null) controlsBox.setVisible(false);
        if (languagesBox != null) languagesBox.setVisible(false);
        if (leaderboardBox != null) leaderboardBox.setVisible(false);
        if (creditsBox != null) creditsBox.setVisible(false);
        if (corruptedBox != null) corruptedBox.setVisible(false);
    }

    protected boolean handleEscapeBack()
    {
        if (controlsBox.isVisible())
        {
            controlsBox.setVisible(false);
            optionsBox.setVisible(true);
            return true;
        }
        else if (languagesBox.isVisible())
        {
            languagesBox.setVisible(false);
            optionsBox.setVisible(true);
            return true;
        }
        else if (leaderboardBox.isVisible())
        {
            leaderboardBox.setVisible(false);
            menuBox.setVisible(true);
            return true;
        }
        else if (optionsBox.isVisible())
        {
            optionsBox.setVisible(false);
            menuBox.setVisible(true);
            return true;
        }
        else if (creditsBox.isVisible())
        {
            creditsBox.setVisible(false);
            menuBox.setVisible(true);
            return true;
        }
        else if (exitBox.isVisible())
        {
            exitBox.setVisible(false);
            menuBox.setVisible(true);
            return true;
        }
        return false;
    }

    private void playScaleAnim(Button btn, double scaleTo, double durationMs)
    {
        ScaleTransition st = new ScaleTransition(Duration.millis(durationMs), btn);
        st.setToX(scaleTo);
        st.setToY(scaleTo);
        st.play();
    }

    private void selectLanguage(Language lang)
    {
        currentLanguage = lang;
        FXGL.getLocalizationService().selectedLanguageProperty().unbind();
        FXGL.getLocalizationService().selectedLanguageProperty().set(currentLanguage);

        Preferences prefs = Preferences.userNodeForPackage(SaellyApp.class);
        prefs.put(Settings.getPrefsKeyLanguage(), currentLanguage.getName());
    }

    private void cycleWindowMode()
    {
        WindowMode[] modes = WindowMode.values();
        int nextIndex = (currentWindowMode.ordinal() + 1) % modes.length;
        currentWindowMode = modes[nextIndex];
        applyWindowMode(currentWindowMode);
    }

    private void applyWindowMode(WindowMode mode)
    {
        switch (mode)
        {
            case FULLSCREEN:
                FXGL.getPrimaryStage().setFullScreen(true);
                break;
            case WINDOWED:
                FXGL.getPrimaryStage().setFullScreen(false);
                FXGL.getPrimaryStage().setWidth(getAppWidth());
                FXGL.getPrimaryStage().setHeight(getAppHeight());
                FXGL.getPrimaryStage().centerOnScreen();
                break;
            case BORDERLESS:
                FXGL.getPrimaryStage().setFullScreen(false);
                javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getBounds();
                FXGL.getPrimaryStage().setX(bounds.getMinX());
                FXGL.getPrimaryStage().setY(bounds.getMinY());
                FXGL.getPrimaryStage().setWidth(bounds.getWidth());
                FXGL.getPrimaryStage().setHeight(bounds.getHeight());
                break;
        }
    }

    private HBox createKeybindRow(String langKey, String actionName, String defaultKeyName)
    {
        Text label = new Text();
        label.getStyleClass().add(Settings.getCssClassMagicalText());
        label.textProperty().bind(FXGL.localizedStringProperty(langKey));

        Preferences prefs = Preferences.userNodeForPackage(SaellyApp.class);
        Button btnKey = new Button();
        btnKey.getStyleClass().add(Settings.getCssClassMagicalBtn());

        Runnable updateBtnText = () ->
        {
            String currentKey = prefs.get(Settings.getPrefsKeyBindingPrefix() + actionName, defaultKeyName);
            String displayKey = currentKey;

            if (currentKey.equals("SPACE")) displayKey = FXGL.getLocalizationService().getLocalizedString("key.space");
            else if (currentKey.equals("ENTER"))
                displayKey = FXGL.getLocalizationService().getLocalizedString("key.enter");

            if (displayKey.contains("Missing_key")) displayKey = currentKey;

            btnKey.setText(displayKey);
        };

        updateBtnText.run();
        FXGL.getLocalizationService().selectedLanguageProperty().addListener((obs, o, n) -> updateBtnText.run());

        btnKey.setOnAction(e ->
        {
            btnKey.setText(FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeyMenuWaiting()));
            btnKey.getStyleClass().add(Settings.getCssClassBtnWaiting());

            EventHandler<KeyEvent> keyListener = new EventHandler<>()
            {
                @Override
                public void handle(KeyEvent event)
                {
                    KeyCode newKey = event.getCode();

                    if (newKey == KeyCode.ESCAPE)
                    {
                        updateBtnText.run();
                        btnKey.getStyleClass().remove(Settings.getCssClassBtnWaiting());
                        getContentRoot().getScene().removeEventFilter(KeyEvent.KEY_PRESSED, this);
                        event.consume();
                        return;
                    }

                    UserAction action = FXGL.getInput().getActionByName(actionName);
                    FXGL.getInput().rebind(action, newKey);

                    prefs.put(Settings.getPrefsKeyBindingPrefix() + actionName, newKey.name());

                    updateBtnText.run();
                    btnKey.getStyleClass().remove(Settings.getCssClassBtnWaiting());

                    getContentRoot().getScene().removeEventFilter(KeyEvent.KEY_PRESSED, this);
                    event.consume();
                }
            };
            getContentRoot().getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyListener);
        });

        HBox row = new HBox(Settings.getMenuKeybindRowSpacing(), label, btnKey);
        row.setAlignment(Pos.CENTER);
        return row;
    }
}