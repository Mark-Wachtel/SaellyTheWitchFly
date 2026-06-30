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
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class CustomPauseMenuScene extends FXGLMenu {

    private final SaellyApp APP = (SaellyApp) FXGL.getAppCast();

    private WindowMode currentWindowMode = WindowMode.WINDOWED;
    private Language currentLanguage;

    private final StackPane mainRootPane = new StackPane();

    private VBox menuBox;
    private VBox optionsBox;
    private VBox controlsBox;
    private VBox languagesBox;
    private VBox leaderboardBox;
    private VBox creditsBox;
    private VBox exitBox;

    private Button lastHoveredButton = null;

    public CustomPauseMenuScene()
    {
        super(MenuType.GAME_MENU);

        initLanguageConfiguration();
        setupArcadeInputFilters();
        buildVisualBackground();

        initGameTitle();
        initLeftCornerStatus();
        initMuteButton();

        Preferences preferences = Preferences.userNodeForPackage(SaellyApp.class);

        buildMenuBox();
        buildOptionsBox(preferences);
        buildControlsBox();
        buildLanguagesBox();
        buildLeaderboardBox();
        buildCreditsBox();
        buildExitBox();

        assembleLayout();
    }

    private void initLanguageConfiguration() {
        Preferences preferences = Preferences.userNodeForPackage(SaellyApp.class);
        String savedLang = preferences.get(Settings.getPrefsKeyLanguage(), Settings.getLangGerman());
        currentLanguage = savedLang.equals(Settings.getLangEnglish()) ? Language.ENGLISH : Language.GERMAN;
    }

    private void setupArcadeInputFilters() {
        this.getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
                Node focusOwner = getRoot().getScene().getFocusOwner();
                if (focusOwner instanceof Button) {
                    ((Button) focusOwner).fire();
                    playClickSound();
                    event.consume();
                }
            } else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
                List<Node> focusableNodes = getVisibleFocusableNodes((Parent) getRoot());
                if (focusableNodes.isEmpty()) return;

                Node currentFocus = getRoot().getScene().getFocusOwner();
                int currentIndex = focusableNodes.indexOf(currentFocus);

                if (event.getCode() == KeyCode.DOWN) {
                    currentIndex++;
                    if (currentIndex >= focusableNodes.size()) currentIndex = 0;
                } else {
                    currentIndex--;
                    if (currentIndex < 0) currentIndex = focusableNodes.size() - 1;
                }

                focusableNodes.get(currentIndex).requestFocus();
                playTickSound();
                event.consume();
            }
        });

        mainRootPane.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
            Node target = (Node) e.getTarget();
            while (target != null) {
                if (target instanceof Button) {
                    playClickSound();
                    break;
                }
                target = target.getParent();
            }
        });

        mainRootPane.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_MOVED, e -> {
            Node target = (Node) e.getTarget();
            Button currentHover = null;
            while (target != null) {
                if (target instanceof Button) {
                    currentHover = (Button) target;
                    break;
                }
                target = target.getParent();
            }

            if (currentHover != null && currentHover != lastHoveredButton) {
                playTickSound();
                lastHoveredButton = currentHover;
                currentHover.requestFocus();
            } else if (currentHover == null) {
                lastHoveredButton = null;
            }
        });
    }

    private void buildVisualBackground() {
        mainRootPane.setPrefSize(getAppWidth(), getAppHeight());
        mainRootPane.getStyleClass().add(Settings.getCssClassPauseBg());
        getContentRoot().getChildren().add(mainRootPane);
    }

    private void initGameTitle() {
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
        mainRootPane.getChildren().add(gameTitle);
    }

    private void initLeftCornerStatus() {
        VBox leftCornerBox = new VBox(Settings.getSpacingMedium());
        leftCornerBox.setAlignment(Pos.BOTTOM_LEFT);
        leftCornerBox.setPickOnBounds(false);
        leftCornerBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);

        Texture studioLogo = FXGL.texture(Settings.getLinkToLogo());
        studioLogo.setFitWidth(Settings.getMenuCornerLogoWidth());
        studioLogo.setPreserveRatio(true);

        Circle statusIndicator = new Circle(Settings.getStatusIndicatorRadius());
        Text gameVersion = FXGL.getUIFactoryService().newText("", Color.TRANSPARENT, Settings.getFontSizeVersion());

        gameVersion.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyServerVersion()).concat(" ").concat(FXGL.getSettings().getVersion()));

        statusIndicator.fillProperty().bind(Bindings.when(APP.serverOnlineProperty()).then(Settings.getColorServerOnline()).otherwise(Settings.getColorServerOffline()));
        gameVersion.fillProperty().bind(statusIndicator.fillProperty());

        HBox updatedBox = new HBox(Settings.getSpacingMedium(), statusIndicator, gameVersion);
        updatedBox.setAlignment(Pos.CENTER_LEFT);
        leftCornerBox.getChildren().addAll(studioLogo, updatedBox);

        StackPane.setAlignment(leftCornerBox, Pos.BOTTOM_LEFT);
        StackPane.setMargin(leftCornerBox, new Insets(0, 0, Settings.getMenuCornerMarginBottom(), Settings.getVersionBoxOffsetX()));
        mainRootPane.getChildren().add(leftCornerBox);
    }

    private void initMuteButton() {
        int muteSize = Settings.getIconSizeMute();
        Texture soundOnIcon = FXGL.texture(Settings.getLinkToUiSoundUnmutedImage(), muteSize, muteSize);
        Texture soundOffIcon = FXGL.texture(Settings.getLinkToUiSoundMutedImage(), muteSize, muteSize);

        Button btnMute = new Button("", FXGL.getSettings().getGlobalMusicVolume() > 0 ? soundOnIcon : soundOffIcon);
        btnMute.getStyleClass().add(Settings.getCssClassMuteBtn());
        btnMute.setFocusTraversable(false);

        btnMute.setOnAction(e -> APP.toggleMuteFromMenu());

        FXGL.getSettings().globalMusicVolumeProperty().addListener((obs, oldVal, newVal) -> {
            btnMute.setGraphic(newVal.doubleValue() > 0 ? soundOnIcon : soundOffIcon);
        });

        double animDur = Settings.getMuteBtnAnimDurationMs();
        btnMute.setOnMouseEntered(e -> playScaleAnim(btnMute, Settings.getMuteBtnScaleHover(), animDur));
        btnMute.setOnMouseExited(e -> playScaleAnim(btnMute, Settings.getMuteBtnScaleNormal(), animDur));
        btnMute.setOnMousePressed(e -> playScaleAnim(btnMute, Settings.getMuteBtnScalePress(), animDur));
        btnMute.setOnMouseReleased(e -> playScaleAnim(btnMute, Settings.getMuteBtnScaleHover(), animDur));

        StackPane.setAlignment(btnMute, Pos.TOP_LEFT);
        btnMute.setTranslateX(getAppWidth() - Settings.getMuteBtnOffset());
        btnMute.setTranslateY(getAppHeight() - Settings.getMuteBtnOffset());
        mainRootPane.getChildren().add(btnMute);
    }

    private void buildMenuBox() {
        menuBox = new VBox(Settings.getMenuBoxSpacing());
        menuBox.setAlignment(Pos.CENTER);

        Button btnResume = new Button();
        btnResume.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnResume.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuResume()));
        btnResume.setOnAction(e -> {
            closeMenuWithAnimation(() -> {
                APP.setGameUIVisibility(true);
                APP.playGameMusic();
                fireResume();
            });
        });

        Button btnOptions = new Button();
        btnOptions.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnOptions.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuOptions()));
        btnOptions.setOnAction(e -> switchMenuVisibility(menuBox, optionsBox));

        Button btnLeaderboard = new Button();
        btnLeaderboard.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnLeaderboard.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuLeaderboard()));

        Button btnCredits = new Button();
        btnCredits.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnCredits.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuCredits()));
        btnCredits.setOnAction(e -> switchMenuVisibility(menuBox, creditsBox));

        Button btnMainMenu = new Button();
        btnMainMenu.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnMainMenu.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuMainmenu()));

        btnMainMenu.setOnAction(e -> {
            e.consume();
            closeMenuWithAnimation(() -> {
                Scene mainMenuTarget = FXGL.getSceneService().getMainMenuScene().orElse(null);
                APP.getSceneTransitionCoordinator().transition(() -> {
                    APP.stopAllMusic();
                    APP.setGameUIVisibility(false);
                    FXGL.getSceneService().getMainMenuScene().ifPresent(scene -> {
                        if (scene instanceof CustomMainMenuScene mainMenu) {
                            mainMenu.activateMainMenu();
                        }
                    });
                    FXGL.getGameController().gotoMainMenu();
                }, mainMenuTarget);
            });
        });

        Button btnExit = new Button();
        btnExit.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnExit.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuExit()));
        btnExit.setOnAction(e -> switchMenuVisibility(menuBox, exitBox));

        menuBox.getChildren().addAll(btnResume, btnOptions, btnLeaderboard, btnCredits, btnMainMenu, btnExit);

        btnLeaderboard.setOnAction(e -> {
            Node scrollPane = leaderboardBox.getChildren().stream().filter(n -> n instanceof ScrollPane).findFirst().orElse(null);
            if (scrollPane instanceof ScrollPane) {
                refreshLeaderboard((VBox) ((ScrollPane) scrollPane).getContent(), APP);
            }
            switchMenuVisibility(menuBox, leaderboardBox);
        });
    }

    private void buildOptionsBox(Preferences preferences) {
        optionsBox = new VBox(Settings.getMenuBoxSpacing());
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setVisible(false);

        Text textMusic = new Text();
        textMusic.getStyleClass().add(Settings.getCssClassMagicalText());
        textMusic.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuMusic()));

        Slider musicSlider = new Slider(Settings.getVolumeSliderMin(), Settings.getVolumeSliderMax(), FXGL.getSettings().getGlobalMusicVolume());
        musicSlider.setMaxWidth(Settings.getMenuSliderMaxWidth());
        musicSlider.getStyleClass().add(Settings.getCssClassSlider());
        FXGL.getSettings().globalMusicVolumeProperty().bindBidirectional(musicSlider.valueProperty());
        musicSlider.valueProperty().addListener((obs, oldV, newV) -> preferences.putDouble(Settings.getPrefsKeyMusicVol(), newV.doubleValue()));

        Text textSound = new Text();
        textSound.getStyleClass().add(Settings.getCssClassMagicalText());
        textSound.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuSound()));

        Slider soundSlider = new Slider(Settings.getVolumeSliderMin(), Settings.getVolumeSliderMax(), FXGL.getSettings().getGlobalSoundVolume());
        soundSlider.setMaxWidth(Settings.getMenuSliderMaxWidth());
        soundSlider.getStyleClass().add(Settings.getCssClassSlider());
        FXGL.getSettings().globalSoundVolumeProperty().bindBidirectional(soundSlider.valueProperty());
        soundSlider.valueProperty().addListener((obs, oldV, newV) -> preferences.putDouble(Settings.getPrefsKeySoundVol(), newV.doubleValue()));

        Button btnLang = new Button();
        btnLang.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnLang.textProperty().bind(Bindings.createStringBinding(() -> {
            String label = FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeyMenuLanguage());
            Language currentLang = FXGL.getLocalizationService().selectedLanguageProperty().get();
            String langName = (currentLang == Language.GERMAN) ? Settings.getDisplayLangGerman() : Settings.getDisplayLangEnglish();
            return String.format(Settings.getFormatMenuLabelValue(), label, langName);
        }, FXGL.getLocalizationService().selectedLanguageProperty()));
        btnLang.setOnAction(e -> switchMenuVisibility(optionsBox, languagesBox));

        Button btnWindowMode = new Button();
        btnWindowMode.getStyleClass().add(Settings.getCssClassMagicalBtn());
        Runnable updateModeText = () -> {
            String modeI18nKey = switch (currentWindowMode) {
                case WINDOWED -> Settings.getLangKeyMenuWindowed();
                case BORDERLESS -> Settings.getLangKeyMenuBorderless();
                case FULLSCREEN -> Settings.getLangKeyMenuFullscreen();
            };
            String label = FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeyMenuMode());
            String value = FXGL.getLocalizationService().getLocalizedString(modeI18nKey);
            btnWindowMode.setText(String.format(Settings.getFormatMenuLabelValue(), label, value));
        };
        updateModeText.run();
        btnWindowMode.setOnAction(e -> {
            cycleWindowMode();
            updateModeText.run();
        });
        FXGL.getLocalizationService().selectedLanguageProperty().addListener((obs, o, n) -> updateModeText.run());

        Button btnControls = new Button();
        btnControls.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuControls()));
        btnControls.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnControls.setOnAction(e -> switchMenuVisibility(optionsBox, controlsBox));

        Button btnBackOpt = new Button();
        btnBackOpt.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuBack()));
        btnBackOpt.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnBackOpt.setOnAction(e -> switchMenuVisibility(optionsBox, menuBox));

        optionsBox.getChildren().addAll(textMusic, musicSlider, textSound, soundSlider, btnControls, btnLang, btnWindowMode, btnBackOpt);
    }

    private void buildControlsBox() {
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
        btnBackControls.setOnAction(e -> switchMenuVisibility(controlsBox, optionsBox));

        controlsBox.getChildren().addAll(textControlsTitle, scrollPane, btnBackControls);
    }

    private void buildLanguagesBox() {
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
        btnBackLang.setOnAction(e -> switchMenuVisibility(languagesBox, optionsBox));

        languagesBox.getChildren().addAll(textLanguagesTitle, langScrollPane, btnBackLang);
    }

    private void buildLeaderboardBox() {
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
        btnBackLeaderboard.setOnAction(e -> switchMenuVisibility(leaderboardBox, menuBox));

        leaderboardBox.getChildren().addAll(textLeaderboardTitle, leaderboardScroll, btnBackLeaderboard);
    }

    private void buildCreditsBox() {
        creditsBox = new VBox(Settings.getMenuBoxSpacing());
        creditsBox.setAlignment(Pos.CENTER);
        creditsBox.setVisible(false);

        Text textCredits = new Text();
        textCredits.getStyleClass().add(Settings.getCssClassCreditsText());
        textCredits.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuCreditsText()));

        Button btnBackCred = new Button();
        btnBackCred.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuBack()));
        btnBackCred.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnBackCred.setOnAction(e -> switchMenuVisibility(creditsBox, menuBox));

        creditsBox.getChildren().addAll(textCredits, btnBackCred);
    }

    private void buildExitBox() {
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
        btnYes.setOnAction(e -> {
            SceneTransitionCoordinator ec = APP.getSceneTransitionCoordinator();
            if (ec != null) ec.requestExit(() ->FXGL.getGameController().exit());
            else FXGL.getGameController().exit();
        });

        Button btnNo = new Button();
        btnNo.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuNo()));
        btnNo.getStyleClass().add(Settings.getCssClassMagicalBtn());
        btnNo.setOnAction(e -> switchMenuVisibility(exitBox, menuBox));

        exitBtnRow.getChildren().addAll(btnYes, btnNo);
        exitBox.getChildren().addAll(textExitConfirm, exitBtnRow);
    }

    private void switchMenuVisibility(VBox dynamicToHide, VBox dynamicToShow) {
        mainRootPane.setDisable(true);

        SceneTransitionCoordinator coordinator = ((SaellyApp) FXGL.getAppCast()).getSceneTransitionCoordinator();

        if (coordinator != null) {
            coordinator.fadeOut(dynamicToHide, Duration.seconds(0.15), () -> {
                dynamicToHide.setVisible(false);
                dynamicToHide.setOpacity(1.0);

                coordinator.fadeIn(dynamicToShow, Duration.seconds(0.15), () -> {
                    mainRootPane.setDisable(false);
                    Platform.runLater(() -> {
                        List<Node> nodes = getVisibleFocusableNodes((Parent) getRoot());
                        if (!nodes.isEmpty()) nodes.getFirst().requestFocus();
                    });
                });
            });
        } else {
            dynamicToHide.setVisible(false);
            dynamicToShow.setVisible(true);
            mainRootPane.setDisable(false);
            Platform.runLater(() -> {
                List<Node> nodes = getVisibleFocusableNodes((Parent) getRoot());
                if (!nodes.isEmpty()) nodes.getFirst().requestFocus();
            });
        }
    }

    private void assembleLayout() {
        Insets margins = new Insets(Settings.getMenuBoxMarginTop(), 0, 0, 0);
        VBox[] boxes = {menuBox, optionsBox, controlsBox, languagesBox, leaderboardBox, creditsBox, exitBox};

        for (VBox box : boxes) {
            StackPane.setMargin(box, margins);
            box.setPickOnBounds(false);
            mainRootPane.getChildren().add(box);
        }
    }

    public void activatePauseMenu()
    {
        if (APP != null) {
            APP.setGameUIVisibility(false);
            APP.playPauseMenuMusic();
        }
    }

    private void playClickSound() {
        try {
            FXGL.getAudioPlayer().playSound(FXGL.getAssetLoader().loadSound(Settings.getLinkToClickSound()));
        } catch (Exception e) {
            System.err.println(Settings.getErrMsgButtonClickSound() + e.getMessage());
        }
    }

    private void playTickSound() {
        try {
            FXGL.getAudioPlayer().playSound(FXGL.getAssetLoader().loadSound(Settings.getLinkToButtonTickSound()));
        } catch (Exception e) {
            System.err.println(Settings.getErrMsgButtonTickSound() + e.getMessage());
        }
    }

    private void refreshLeaderboard(VBox container, SaellyApp app) {
        container.getChildren().clear();
        List<ScoreEntry> scores = app.getHighscoreList();

        if (scores == null || scores.isEmpty()) {
            Text noEntries = new Text();
            noEntries.getStyleClass().add(Settings.getCssClassMagicalText());
            noEntries.textProperty().bind(FXGL.localizedStringProperty(Settings.getLangKeyMenuNoEntries()));
            container.getChildren().add(noEntries);
        } else {
            for (int i = 0; i < scores.size(); i++) {
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

        VBox[] boxes = {optionsBox, controlsBox, languagesBox, leaderboardBox, creditsBox, exitBox};
        for (VBox box : boxes) if (box != null) box.setVisible(false);
        if (menuBox != null) menuBox.setVisible(true);

        mainRootPane.setDisable(false);
        APP.getSceneTransitionCoordinator().popIn(mainRootPane, Duration.seconds(0.25), null);

        Platform.runLater(() -> {
            List<Node> nodes = getVisibleFocusableNodes((Parent) getRoot());
            if (!nodes.isEmpty()) nodes.getFirst().requestFocus();
        });
    }

    public void closeMenuWithAnimation(Runnable onFinished) {
        mainRootPane.setDisable(true);
        APP.getSceneTransitionCoordinator().popOut(mainRootPane, Duration.seconds(0.15), onFinished);
    }

    public boolean handleEscapeBack()
    {
        if (controlsBox.isVisible()) {
            switchMenuVisibility(controlsBox, optionsBox);
            return true;
        } else if (languagesBox.isVisible()) {
            switchMenuVisibility(languagesBox, optionsBox);
            return true;
        } else if (leaderboardBox.isVisible()) {
            switchMenuVisibility(leaderboardBox, menuBox);
            return true;
        } else if (optionsBox.isVisible()) {
            switchMenuVisibility(optionsBox, menuBox);
            return true;
        } else if (creditsBox.isVisible()) {
            switchMenuVisibility(creditsBox, menuBox);
            return true;
        } else if (exitBox.isVisible()) {
            switchMenuVisibility(exitBox, menuBox);
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
            case FULLSCREEN -> FXGL.getPrimaryStage().setFullScreen(true);
            case WINDOWED -> {
                FXGL.getPrimaryStage().setFullScreen(false);
                FXGL.getPrimaryStage().setWidth(getAppWidth());
                FXGL.getPrimaryStage().setHeight(getAppHeight());
                FXGL.getPrimaryStage().centerOnScreen();
            }
            case BORDERLESS -> {
                FXGL.getPrimaryStage().setFullScreen(false);
                javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getBounds();
                FXGL.getPrimaryStage().setX(bounds.getMinX());
                FXGL.getPrimaryStage().setY(bounds.getMinY());
                FXGL.getPrimaryStage().setWidth(bounds.getWidth());
                FXGL.getPrimaryStage().setHeight(bounds.getHeight());
            }
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

            if (currentKey.equals(Settings.getRawKeySpace())) {
                displayKey = FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeySpace());
            } else if (currentKey.equals(Settings.getRawKeyEnter())) {
                displayKey = FXGL.getLocalizationService().getLocalizedString(Settings.getLangKeyEnter());
            }

            if (displayKey.contains(Settings.getFallbackMissingKey())) {
                displayKey = currentKey;
            }
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

    private List<Node> getVisibleFocusableNodes(Parent parent) {
        List<Node> nodes = new ArrayList<>();
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (!child.isVisible()) continue;

            if ((child instanceof Button || child instanceof Slider) && !child.isDisabled() && child.isFocusTraversable()) {
                nodes.add(child);
            } else if (child instanceof Parent) {
                nodes.addAll(getVisibleFocusableNodes((Parent) child));
            }
        }
        return nodes;
    }
}