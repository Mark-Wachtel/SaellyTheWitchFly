package Mark.baseGame;

import javafx.scene.paint.Color;

public class Settings
{

    //FXGL keys
    private static final String KEY_GAME_STARTED = "gameStarted";
    private static final String KEY_BARRIER_SPEED = "barrierSpeed";
    private static final String KEY_IS_BUFF_ACTIVE = "isBuffActive";
    private static final String KEY_IS_INVULNERABLE = "isInvulnerable";
    private static final String KEY_LIVES = "lives";
    private static final String PREFS_KEY_LANGUAGE = "language";
    private static final String LANG_GERMAN = "GERMAN";
    private static final String LANG_ENGLISH = "ENGLISH";
    private static final String KEY_IS_DEBUGGING = "isDebugging";
    private static final String KEY_IS_GAME_OVER = "isGameOver";
    private static final String KEY_SCORE = "score";
    private static final String KEY_GENERAL_SPEED = "speed";
    private static final String KEY_IS_CRASHING = "isCrashing";
    private static final String KEY_IS_DEBUFF_ACTIVE = "isDebuffActive";
    private static final String KEY_PLACED = "placed";

    //Init
    private static final int INIT_SCORE = 0;
    private static final double INIT_SPEED = 0.0;
    private static final double INIT_BARRIER_SPEED = 0.0;
    private static final int INIT_LIVES = 1;
    private static final boolean INIT_GAME_STARTED = false;
    private static final boolean INIT_IS_INVULNERABLE = false;
    private static final boolean IS_GAME_OVER = false;
    private static final double INIT_PLAYER_SCALE = 0.8;
    private static final double INIT_BARRIER_SCALE = 0.5;
    private static final int INIT_PLAYER_SCALE_WIDTH = 254;
    private static final int INIT_PLAYER_SCALE_HEIGHT = 254;
    private static final double INIT_PLAYER_PIVOT_WIDTH = getInitPlayerScaleWidth() / 2.0;
    private static final double INIT_PLAYER_PIVOT_HEIGHT = getInitPlayerScaleHeight() / 2.0;
    private static final boolean INIT_PLAYER_IS_CRASHING = false;
    private static final boolean INIT_IS_BUFF_ACTIVE = false;
    private static final boolean INIT_IS_DEBUFF_ACTIVE = false;
    private static final boolean INIT_IS_DEBUGGING = false;

    //defaults
    private static final String DEFAULT_KEY_JUMP = "SPACE";
    private static final String DEFAULT_KEY_RESTART = "ENTER";
    private static final String DEFAULT_KEY_WINDOWED = "F10";
    private static final String DEFAULT_KEY_BORDERLESS = "F11";
    private static final String DEFAULT_KEY_FULLSCREEN = "F12";

    private static int placedValue = 1;

    //UI
    private static final String LINK_TO_HEART_UI_IMAGE = "ui/heart.png";
    private static final int HEART_UI_IMAGE_WIDTH = 32;
    private static final int HEART_UI_IMAGE_HEIGHT = 32;
    private static final String LINK_TO_UI_APPICON = "ui/ui_appIcon.png";
    private static final int APPICON_UI_IMAGE_WIDTH = 32;
    private static final int APPICON_UI_IMAGE_HEIGHT = 32;
    //Offsets
    private static final double CRASH_EFFECT_OFFSET_X = 0.0;
    private static final double CRASH_EFFECT_OFFSET_Y = -500.0;
    private static final double PICKUP_EFFECT_OFFSET_X = -100.0;
    private static final double PICKUP_EFFECT_OFFSET_Y = -500.0;
    private static final double SLOW_MOTION_OFFSET_X = 0.0;
    private static final double SLOW_MOTION_OFFSET_Y = 0.0;

    private static double gameSpeed = 30.0; //Die Geschwindigkeit in der unser Spiel abläuft. Einfluss auf z.B. Hintergrundbild Scrolling.
    private static double barrierSpeed = 130.0; //Die Geschwindigkeit in der sie Hindernisse von Rechts nach Links bewegen.
    private static final double IDLE_JUMP_HEIGHT = 20; //Die Höhe in Pixel die wir in der Idle Animation Hoch und Runter hüpfen.
    private static final double JUMP_MULTI = -350.0; //Die Springbeschleunigung. Positive Werte lassen Objekte auf der Y-Achse nach unten "fallen". In unserem Fall haben wir ein negativ Wert (-X) was bedeutet: Wir "schweben" 350 Pixel gegen die 900 Pixel Schwerkraft, die dauerhaft wirken.
    private static double gravity = 900.0; //Die Erdanziehungskraft, in unserem Fall wird der Spieler (oder jedes Objekt mit Anbindung an die Physik) jede Sekunde um 900px pro Sekunde nach unten Angezogen.
    private static int scoreMulti = 1; //Der Multiplikator der Punkte die unser Spieler für das passieren eines Hindernisses erhält, aktuell bekommt der Spieler 1 Punkt pro Hindernis. Dies kann gesteigert werden wenn der Spieler Power-Ups einsammelt (muss noch implementiert werden), z.B. +2 oder +10 Punkte pro Hindernis, eventuell steigert sich der Multiplikator auch mit der Geschwindigkeit).
    private static final double WINDOW_SCALE = 0.60; //Die Prozente wie groß das Fenster im Verhältnis zum Bildschirm ist. In unserem Fall 60%.
    private static final double ASPECT_RATIO = 16.0 / 9.0; //16 zu 9 Seitenverhältnis.
    private static final int STANDARD_WINDOW_WIDTH = 1280; //Standard Breite des Fensters.
    private static final int STANDARD_WINDOW_HEIGHT = 756; //Standard Höhe des Fensters inklusive Titelleiste. Hier 720 für das Spiel + 36 für die Titelleiste = 756px.
    private static final int STANDARD_WINDOW_HEIGHT_WITHOUT_TITLEBAR = 720; //Standard Höhe des Fensters ohne die Titelleiste.
    private static final double STANDARD_TITLEBAR_HEIGHT = 36.0; //Standard Höhe der Titelleiste.
    private static String windowTitle = "Saelly the Flying Witch"; //Titel der in der Titelleiste angezeigt wird. Nicht final damit der Titel z.B. auf Settings geändert werden kann.
    private static final String GAME_VERSION = "0.1.1 - Alpha 1"; //Version des Programmes/Spieles. Aktuell 0.0.1 - in den Startlöchern.
    private static final int LOGO_PREF_SIZE_WIDTH = 30; //Bevorzugte Breite des Logos.
    private static final int LOGO_PREF_SIZE_HEIGHT = 40; //Bevorzugte Höhe des Logos.
    private static final int LOGO_FIT_HEIGHT = 30; //Zwingt das Logo genau in 20 Pixel egal welche Ursprungsgröße es hatte. Wenn preserveRatio aktiviert ist wird die Breite automatisch propotional angepasst.
    private static final double MIN_SPAWN_HEIGHT_BARRIER = 50; //Minimale Spawnhöhe der Barriere.
    private static final double MAX_SPAWN_HEIGHT_BARRIER = 400; //Maximale Spawnhöhe der Barriere.
    private static double spawnDurationBarrier = 2; //Dauer in Sekunden in denen Barrieren gespawned werden.
    private static double spawnDurationBuffPowerup = 10; //Dauer in Sekunden in denen ein Powerup gespawned wird.
    private static int barriersToSpeedupTheGame = 50; //Wie viele Barriern werden benötigt bevor das Spiel schneller wird (Schneller zu Seite scrolled).

    //Powerups
    private static double invulnerableDurationInSeconds  = 2;
    private static double slowMotionBuffPowerupRatio = 0.5;
    private static int slowMotionBuffPowerupDurationSeconds = 8;
    private static int scoreX10DurationSeconds = 10;
    private static final int SCORE_X10_MULTI = 10;
    private static final int SLOW_MOTION_IMAGE_WIDTH = 40;
    private static final int SLOW_MOTION_IMAGE_HEIGHT = 40;
    private static final int INVULNERABLE_IMAGE_WIDTH = 40;
    private static final int INVULNERABLE_IMAGE_HEIGHT = 40;
    private static final int SCORE_X10_IMAGE_WIDTH = 40;
    private static final int SCORE_X10_IMAGE_HEIGHT = 40;
    private static final int EXTRA_LIFE_IMAGE_WIDTH = 40;
    private static final int EXTRA_LIFE_IMAGE_HEIGHT = 40;
    private static final int SCORE_POWERUP_MULTIPLIER_BONUS = 10;
    private static final int EXTRA_LIFE_POWERUP_BONUS = 1;


    private static final int PLAYER_START_X_POSITION = 300; //Startposition auf der X-Achse des Spieler's
    private static final int PLAYER_START_Y_POSITION = 300; //Startposition auf der Y-Achse des Spieler's
    private static final int PLAYER_COLLISION_BOX_WIDTH = 20; //Breite der Kollisionsbox Abfrage des Spieler's.
    private static final int PLAYER_COLLISION_BOX_HEIGHT = 20; //Höhe der Kollisionsbox Abfrage des Spieler's.
    private static final double PLAYER_MAX_FALL_SPEED = 400.0; //Maximale Fallgeschwindigkeit
    private static final double PLAYER_MAX_RISE_SPEED = -500.0; //Maximale Aufstiegsgeschwindigkeit


    private static final int BARRIER_SCALE_WIDTH = 200;
    private static final int BARRIER_SCALE_HEIGHT = 200;


    //Links zu Bildern, Musik und Sounds
    private static final String LINK_TO_BACKGROUND_MUSIC = "bgMusic.mp3"; //Name als String
    private static final String LINK_TO_MAIN_MENU_MUSIC = "menuMusic.mp3";
    private static final String LINK_TO_CLICK_SOUND = "buttonClick.wav";
    private static final String LINK_TO_BUTTON_TICK_SOUND = "buttonTick.wav";
    private static final String LINK_TO_BACKGROUND_IMAGE = "gameBackgroundLoopable.png"; //Name als String
    private static final String LINK_TO_IMAGE_PLAYER_IDLE = "player/SaellyJump.png"; //Name als String
    private static final String LINK_TO_IMAGE_PLAYER_JUMP = "player/SaellyJump.png"; //Name als String
    private static final String LINK_TO_SOUND_BARRIER_PASSED = "barrierPassed.wav";
    private static final String LINK_TO_JUMP_SOUND = "Jump.wav";
    private static final String LINK_TO_SLOW_MOTION_PICKUP_SOUND = "pickup.wav";
    private static final String LINK_TO_SLOW_MOTION_ACTIVE_SOUND = "SlowMotionActiveSound.wav";
    private static final String LINK_TO_SLOW_MOTION_IMAGE = "powerups/slowMotion.png";
    private static final String LINK_TO_SCOREX10_PICKUP_SOUND = "pickup.wav";
    private static final String LINK_TO_SCOREX10_ACTIVE_SOUND = "ScoreX10ActiveSound.wav";
    private static final String LINK_TO_SCOREX10_IMAGE = "powerups/x10Score.png";
    private static final String LINK_TO_INVULNERABLE_PICKUP_SOUND = "pickup.wav";
    private static final String LINK_TO_INVULNERABLE_ACTIV_SOUND = "InvulnerableActiveSound.wav";
    private static final String LINK_TO_INVULNERABLE_IMAGE = "powerups/invulnerable.png";
    private static final String LINK_TO_EXTRA_LIVES_PICKUP_SOUND = "pickup.wav";
    private static final String LINK_TO_EXTRA_LIVES_ACTIV_SOUND = "ExtraLivesActivSound.wav";
    private static final String LINK_TO_EXTRA_LIVES_IMAGE = "powerups/extraLife.png";
    private static final String LINK_TO_TOP_BARRIER_IMAGE = "barriers/topBarrier.png";
    private static final String LINK_TO_LOGO = "ui/ui_appIcon.png"; //Link
    private static final String LINK_TO_FONT_EAGLE = "EagleLake.ttf"; //Link
    private static final String LINK_TO_MENU_MUSIC = "menuMusic.mp3";
    private static final String LINK_TO_GAME_OVER_MUSIC = "gameOverMusic.mp3";
    private static final String LINK_TO_GAME_OVER_SOUNDEFFECT = "gameOverSound.wav";
    private static final String LINK_TO_CRASH_EFFECT_ANIMATION = "effects/crashEffect.png";
    private static final String LINK_TO_UI_SOUND_MUTED_IMAGE = "ui/ui_sound_muted_image.png";
    private static final String LINK_TO_UI_SOUND_UNMUTED_IMAGE = "ui/ui_sound_unmute_image.png";
    private static final String LINK_TO_INVULNERABLE_ANIMATION = "effects/invulnerableEffect.png";
    private static final String LINK_TO_SLOW_MOTION_ANIMATION = "effects/slowMotionEffect.png";
    private static final String LINK_TO_PICKUP_ANIMATION = "effects/pickupEffect.png";
    private static final String LINK_TO_LOADING_ANIMATION = "ui/ui_loadingAnimation.png";
    private static final String LINK_TO_INIT_LOADINGSCREEN_BACKGROUND_IMAGE = "gameBackgroundLoopable.png";
    private static final String LINK_TO_UI_INTRO_BACKGROUND_IMAGE = "gameBackgroundLoopable.png";
    private static final String LINK_TO_CSS = "/Mark/baseGame/styles.css";

    //Buff Settings
    private static final int BUFF_ANIM_FRAMES_PER_ROW = 5;
    private static final int BUFF_ANIM_FRAME_WIDTH = 307;
    private static final int BUFF_ANIM_FRAME_HEIGHT = 1024;
    private static final double BUFF_ANIM_DURATION_SECONDS = 0.5;
    private static final int BUFF_ANIM_START_FRAME = 0;
    private static final int BUFF_ANIM_END_FRAME = 4;
    private static final double BUFF_SCALE_FACTOR = 1.5;

    //MainClass
    private static final double POWERUP_SPAWN_CHANCE = 0.1;
    private static final double MAX_TPF_THRESHOLD = 0.1;
    private static final double NORMAL_TPF_FALLBACK = 0.016;
    private static final int ICON_SIZE_MUTE = 128;
    private static final int ICON_SIZE_BUFF = 40;
    private static final double UI_DIM_BACKGROUND_OPACITY = 0.65;
    private static final double GAME_OVER_DIM_OPACITY = 0.7;
    private static final String DEFAULT_PLAYER_NAME = "AAAAA";
    private static final int MAX_PLAYER_NAME_LENGTH = 5;
    private static final double VIEWPORT_OFFSET_Y = 30.0;
    private static final double TOP_BAR_PADDING_TOP = 40.0;
    private static final double TOP_BAR_PADDING_SIDE = 20.0;
    private static final double MUTE_BTN_OFFSET = 130.0;
    private static final double VERSION_BOX_OFFSET_X = 20.0;
    private static final double VERSION_BOX_OFFSET_Y = 40.0;
    private static final int FONT_SIZE_SCORE = 24;
    private static final int FONT_SIZE_LIVES = 28;
    private static final int FONT_SIZE_VERSION = 14;
    private static final int FONT_SIZE_LOADING = 40;
    private static final int FONT_SIZE_GAMEOVER_TITLE = 44;
    private static final int FONT_SIZE_GAMEOVER_RANK = 22;
    private static final int FONT_SIZE_GAMEOVER_ENTRY = 16;
    private static final int FONT_SIZE_GAMEOVER_INSTRUCT = 18;
    private static final double MUTE_BTN_ANIM_DURATION_MS = 150.0;
    private static final double MUTE_BTN_SCALE_HOVER = 1.1;
    private static final double MUTE_BTN_SCALE_NORMAL = 1.0;
    private static final double MUTE_BTN_SCALE_PRESS = 0.9;
    private static final double DEFAULT_RESTORE_VOLUME = 0.5;
    private static final String FORMAT_LIVES = "x %d";
    private static final String FORMAT_GAMEOVER_SCORE = "%06d";

    //Language keys
    private static final String LANG_KEY_GAME_TITLE = "game.title";
    private static final String LANG_KEY_SERVER_VERSION = "server.version";
    private static final String LANG_KEY_LOADING_TEXT = "menu.loadingText";
    private static final String LANG_KEY_HIGHSCORE_NAME = "highscore.name";
    private static final String LANG_KEY_HIGHSCORE_POINTS = "highscorePoints.name";
    private static final String LANG_KEY_INGAME_POINTS = "ingame.points";
    private static final String LANG_KEY_FINAL_SCORE = "menu.final_score";
    private static final String LANG_KEY_GAME_OVER = "menu.game_over";
    private static final String LANG_KEY_PRESS_ENTER = "menu.press_enter";
    private static final String LANG_KEY_CORRUPTED_FILE = "dialog.corrupted_file";
    private static final String LANG_KEY_LAST_SYNC = "lastCloudSync.dialog.corrupted_file";
    private static final String LANG_KEY_MENU_RESUME = "menu.resume";
    private static final String LANG_KEY_MENU_OPTIONS = "menu.options";
    private static final String LANG_KEY_MENU_CREDITS = "menu.credits";
    private static final String LANG_KEY_MENU_EXIT = "menu.exit";
    private static final String LANG_KEY_MENU_MUSIC = "menu.music";
    private static final String LANG_KEY_MENU_SOUND = "menu.sound";
    private static final String LANG_KEY_MENU_LANGUAGE = "menu.language";
    private static final String LANG_KEY_MENU_MODE = "menu.mode";
    private static final String LANG_KEY_MENU_BACK = "menu.back";
    private static final String LANG_KEY_MENU_CONTROLS = "menu.controls";
    private static final String LANG_KEY_MENU_CONTROLS_TITLE = "menu.controls_title";
    private static final String LANG_KEY_MENU_WAITING = "menu.waiting";
    private static final String LANG_KEY_MENU_JUMP = "menu.jump";
    private static final String LANG_KEY_MENU_RESTART = "menu.restart";
    private static final String LANG_KEY_MENU_WINDOWED = "menu.windowed";
    private static final String LANG_KEY_MENU_BORDERLESS = "menu.borderless";
    private static final String LANG_KEY_MENU_FULLSCREEN = "menu.fullscreen";
    private static final String LANG_KEY_MENU_CREDITS_TEXT = "menu.credits_text";
    private static final String LANG_KEY_MENU_LANGUAGE_TITLE = "menu.language_title";
    private static final String BUNDLE_PATH_TEXTS = "assets.properties.texts";
    private static final String LANG_KEY_MENU_MAINMENU = "menu.mainmenu";
    private static final String LANG_KEY_MENU_LEADERBOARD = "menu.leaderboard";
    private static final String LANG_KEY_MENU_LEADERBOARD_TITLE = "menu.leaderboard_title";
    private static final String LANG_KEY_MENU_LANG_DE = "menu.lang_de";
    private static final String LANG_KEY_MENU_LANG_EN = "menu.lang_en";
    private static final String LANG_KEY_MENU_NO_ENTRIES = "menu.no_entries";
    private static final String LANG_KEY_MENU_EXIT_CONFIRM = "menu.exit_confirm";
    private static final String LANG_KEY_MENU_YES = "menu.yes";
    private static final String LANG_KEY_MENU_NO = "menu.no";
    private static final String LANG_KEY_MENU_NEWGAME = "menu.new_game";

    private static final int SPACING_SMALL = 5;
    private static final int SPACING_MEDIUM = 8;
    private static final int SPACING_LARGE = 10;
    private static final int SPACING_XLARGE = 15;
    private static final int SPACING_XXLARGE = 20;
    private static final int STATUS_INDICATOR_RADIUS = 6;
    private static final int NAME_INPUT_PREF_WIDTH = 120;
    private static final int VIEW_ORDER_BACKGROUND = -1;
    private static final int VIEW_ORDER_TITLEBAR = -1000;
    private static final int VIEW_ORDER_MODAL = -2000;
    private static final int VIEW_ORDER_UI_TOP = -3000;
    private static final String COLOR_HIGHLIGHT_HEX = "#ff9e00";
    private static final String ACTION_WINDOWED = "Windowed";
    private static final String ACTION_BORDERLESS = "Borderless";
    private static final String ACTION_FULLSCREEN = "Fullscreen";
    private static final String ACTION_JUMP = "jump";
    private static final String ACTION_RESTART = "Restart Game";
    private static final String ACTION_TOGGLE_DEBUG = "toggle hitboxes";
    private static final double HEARTBEAT_INTERVAL_MINUTES = 30.0;
    private static final double GAME_OVER_DELAY_SECONDS = 1.0;
    private static final double BUFF_EFFECT_DURATION_SECONDS = 1.0;
    private static final double CORRUPTED_DIALOG_DELAY_SECONDS = 0.5;
    private static final int HIGHSCORE_DISPLAY_OFFSET_START = 2;
    private static final int HIGHSCORE_DISPLAY_OFFSET_END = 7;
    private static final double POWERUP_PADDING_MULTIPLIER = 0.5;
    private static final double LOADING_ANIM_DELAY_SECONDS = 1.0;
    private static final String ACTION_TOGGLE_MENU = "Toggle Menu";
    private static final String HITBOX_NAME_BODY = "Body";
    private static final String MSG_ERR_CSS_MAIN = "WARNUNG: CSS Datei nicht gefunden! Prüfe den Pfad in den Settings: ";
    private static final String MSG_ERR_CSS_LOADING = "CSS Datei für LoadingScene nicht gefunden!";
    // --- Konsolen-Texte ---
    private static final String MSG_ERR_SLEEP = "Verzögerung unterbrochen: ";
    private static final String MSG_ERR_ICON = "icon can't be forced! ";
    private static final String MSG_ERR_STAGE = "Stage not available...";
    private static final String MSG_ERR_ROOT = "FXGL Game Root not available...";
    private static final String MSG_ERR_SCENE = "Scene not available...";
    private static final String MSG_SAFE_EXIT = "Safe and Exit...";
    //Intro Scene
    private static final double INTRO_FADE_DURATION_SECONDS = 3.0;
    //Loading Scene: animation settings
    private static final int LOADING_ANIM_FRAME_WIDTH = 1440;
    private static final int LOADING_ANIM_FRAME_HEIGHT = 1440;
    private static final int LOADING_ANIM_COLUMNS = 5;
    private static final int LOADING_ANIM_TOTAL_FRAMES = 18;
    private static final double LOADING_ANIM_DURATION_MILLIS = 60.0;
    //Loading Scene: UI scaling
    private static final double LOADING_ANIM_VIEW_SIZE = 300.0;
    private static final double LOADING_TEXT_SIZE = 22.0;
    private static final double LOADING_DOTS_DURATION_SECONDS = 0.5;
    private static final double LOADING_BAR_WIDTH_RATIO = 0.6;
    private static final double LOADING_BAR_HEIGHT = 14.0;
    private static final double LOADING_VBOX_SPACING = 25.0;
    //Startup Scene
    private static final String LINK_TO_STARTUP_BACKGROUND = "/assets/textures/gameBackgroundLoopable.png";
    private static final String LINK_TO_STUDIO_LOGO = "/assets/textures/mwGamesLogo.png";
    private static final String ERROR_MSG_STARTUP_BG = "Fehler: Hintergrundbild konnte nicht geladen werden...";
    private static final String ERROR_MSG_STARTUP_LOGO = "Fehler: Studio-Logo konnte nicht geladen werden!";
    //Custom Title Bar
    private static final int TITLEBAR_PADDING = 8;
    private static final int TITLEBAR_FONT_SIZE = 24;
    private static final double TITLEBAR_LOGO_ANIM_DURATION = 0.2;
    private static final double TITLEBAR_LOGO_CLICK_DURATION = 0.05;
    private static final int TITLEBAR_CLOSE_BTN_SIZE = 20;
    private static final double TITLEBAR_CLOSE_BREATHING_DURATION = 1.2;
    private static final double TITLEBAR_CLOSE_HOVER_DURATION = 0.3;
    private static final double TITLEBAR_CLOSE_FADE_OUT_DURATION = 0.8;
    private static final String ERROR_MSG_TITLEBAR_LOGO = "Logo can't load...";
    private static final String BASE_PATH_TEXTURES = "/assets/textures/";
    //Effect component settings
    private static final String LINK_TO_CRASH_EFFECT = "effects/crashEffect.png";
    private static final int CRASH_EFFECT_COLS = 5;
    private static final int CRASH_EFFECT_FRAME_WIDTH = 307; // Hier kannst du später deinen Bug fixen!
    private static final int CRASH_EFFECT_FRAME_HEIGHT = 1024;
    private static final int CRASH_EFFECT_NUM_FRAMES = 5;
    private static final double CRASH_EFFECT_DURATION_SEC = 0.5;
    private static final double CRASH_EFFECT_TARGET_WIDTH = 128.0;
    private static final double CRASH_EFFECT_TARGET_HEIGHT = 128.0;
    private static final String LINK_TO_PICKUP_EFFECT = "effects/pickupEffect.png";
    private static final int PICKUP_EFFECT_COLS = 4;
    private static final int PICKUP_EFFECT_FRAME_WIDTH = 307;
    private static final int PICKUP_EFFECT_FRAME_HEIGHT = 1024;
    private static final int PICKUP_EFFECT_NUM_FRAMES = 5;
    private static final double PICKUP_EFFECT_DURATION_SEC = 0.3;
    private static final double PICKUP_EFFECT_TARGET_WIDTH = 128.0;
    private static final double PICKUP_EFFECT_TARGET_HEIGHT = 128.0;
    private static final int INVULNERABLE_ANIMATION_COLS = 5;
    private static final int INVULNERABLE_ANIMATION_FRAME_WIDTH = 307;
    private static final int INVULNERABLE_ANIMATION_FRAME_HEIGHT = 1024;
    private static final int INVULNERABLE_ANIMATION_NUM_FRAMES = 5;
    private static final double INVULNERABLE_ANIMATION_DURATION_SEC = 0.5;
    private static final double INVULNERABLE_ANIMATION_TARGET_WIDTH = 128.0;
    private static final double INVULNERABLE_ANIMATION_TARGET_HEIGHT = 128.0;
    private static final int SLOW_MOTION_ANIMATION_COLS = 5;
    private static final int SLOW_MOTION_ANIMATION_FRAME_WIDTH = 307;
    private static final int SLOW_MOTION_ANIMATION_FRAME_HEIGHT = 1024;
    private static final int SLOW_MOTION_ANIMATION_NUM_FRAMES = 5;
    private static final double SLOW_MOTION_ANIMATION_DURATION_SEC = 0.5;
    private static final double SLOW_MOTION_ANIMATION_TARGET_WIDTH = 128.0;
    private static final double SLOW_MOTION_ANIMATION_TARGET_HEIGHT = 128.0;
    //ExitCoordinator
    private static final String ERROR_MSG_SAVE_FAILED = "Error: Highscore or settings can't be safed!";
    //Highscore loader
    private static final String HIGHSCORE_SERVER_URL = "http://217.160.103.10/highscore.php"; // Hier ist deine echte Strato-IP!
    private static final String HIGHSCORE_SECRET_KEY = "kJg5JG7?@jh6%fh=";
    private static final String HIGHSCORE_PHP_PWD = "@kqo2kd9?ks{!";
    private static final long HTTP_TIMEOUT_SECONDS = 3;
    private static final long PING_TIMEOUT_SECONDS = 2;
    private static final String SAVE_DIRECTORY_NAME = ".saellyWitchFly";
    private static final String LOCAL_HIGHSCORE_FILE_NAME = "local_highscores.saelly";
    private static final String METADATA_FILE_NAME = "sync_metadata.saelly";
    private static final String LANG_KEY_UNKNOWN = "unknow"; // Aus deinem FXGL Localization Service
    private static final String SERVER_RESPONSE_SUCCESS = "Saved successfully!";

    //Error massages
    private static final String ERR_MSG_SERVER_TIMEOUT = "[]Server not reachable... timeout...";
    private static final String ERR_MSG_SERVER_ERROR = "[]Server-error: ";
    private static final String ERR_MSG_SERVER_OFFLINE = "[]Timout... server offline...";
    private static final String ERR_MSG_DIRECT_SAVE_FAILED = "[]Direktes Online-Speichern fehlgeschlagen: ";
    private static final String ERR_MSG_NO_HASH = "[]No hash found!";
    private static final String ERR_MSG_DATA_MANIPULATED = "[]Data manipulated!";
    private static final String ERR_MSG_FILE_CORRUPTED = "[]File corrupted or decryption failed. Resetting...";
    private static final String ERR_MSG_SAVE_LOCAL_FAILED = "[]Can't safe highscore locally: ";
    private static final String ERR_MSG_ENCRYPT_FAILED = "[]Error enrypting/saving: ";
    private static final String ERR_MSG_CREATE_DIR_FAILED = "[]Error creating directory for highscore datei: ";
    private static final String ERR_MSG_SAVE_SYNC_TIME_FAILED = "[]can't safe sync-time: ";
    private static final String ERR_MSG_MAIN_MENU_MUSIC = "[MainMenu] Can't load menu music.";
    private static final String ERR_MSG_BUTTON_CLICK_SOUND = "[SoundEffects] Can't load 'buttonClick.wav':";
    private static final String ERR_MSG_BUTTON_TICK_SOUND = "[SoundEffects] Can't load 'buttonTick.wav':";

    //Dummy Daten
    private static final String[] DUMMY_NAMES = {"Merlin", "Gandalf", "Harry", "Arthur"};
    private static final int[] DUMMY_SCORES = {6, 3, 1, 1};
    //HitboxDebugger
    private static final double HITBOX_STROKE_WIDTH = 2.0;
    private static final Color HITBOX_COLOR_COLLIDING = Color.GREEN;
    private static final Color HITBOX_COLOR_PLAYER = Color.BLUE;
    private static final Color HITBOX_COLOR_BARRIER = Color.RED;
    private static final Color HITBOX_COLOR_BUFF = Color.WHITE;
    private static final Color HITBOX_COLOR_DEBUFF = Color.BLACK;
    //WindowManager
    private static final double WINDOW_RESIZE_TOLERANCE = 1.0;
    //Custom Pause Menu
    private static final String CSS_CLASS_PAUSE_BG = "pause-menu-bg";
    private static final String CSS_CLASS_MAGICAL_TITLE = "magical-title";
    private static final String CSS_CLASS_MAGICAL_BTN = "magical-button";
    private static final String CSS_CLASS_MUTE_BTN = "mute-button";
    private static final String CSS_CLASS_SLIDER = "magical-slider";
    private static final String CSS_CLASS_SCROLL_PANE = "magical-scroll-pane";
    private static final String CSS_CLASS_BTN_WAITING = "magical-button-waiting";
    private static final String CSS_CLASS_CREDITS_TEXT = "credits-text";
    private static final String PREFS_KEY_MUSIC_VOL = "musicVolume";
    private static final String PREFS_KEY_SOUND_VOL = "soundVolume";
    private static final String PREFS_KEY_BINDING_PREFIX = "key_for_";
    private static final double MENU_TITLE_FONT_SIZE = 60.0;
    private static final double MENU_TITLE_FLOAT_Y = -15.0;
    private static final double MENU_TITLE_FLOAT_DUR = 2.0;
    private static final double MENU_TITLE_MARGIN_TOP = 50.0;
    private static final double MENU_CORNER_LOGO_WIDTH = 120.0;
    private static final double MENU_CORNER_MARGIN_BOTTOM = 60.0;
    private static final double MENU_CORNER_OFFSET_Y_CALC = 120.0;
    private static final double MENU_BOX_SPACING = 20.0;
    private static final double MENU_BOX_MARGIN_TOP = 240.0;
    private static final double MENU_SLIDER_MAX_WIDTH = 300.0;
    private static final double MENU_SCROLL_HEIGHT = 350.0;
    private static final double MENU_KEYBIND_ROW_SPACING = 30.0;
    private static final int MENU_TEXT_FONT_SIZE = 20;
    private static final String CSS_CLASS_MAGICAL_TEXT = "magical-text";
    private static final Color COLOR_SERVER_ONLINE = Color.GREEN;
    private static final Color COLOR_SERVER_OFFLINE = Color.RED;
    private static final String FORMAT_LEADERBOARD_ENTRY = "#%d  %-5s : %06d";
    //CustomMainMenu
    private static final double DEFAULT_RESTORE_MUSIC_VOLUME = 0.5;
    private static final double DEFAULT_RESTORE_SOUND_VOLUME = 0.5;
    private static final double MAIN_MENU_DIM_OPACITY = 0.6; //60%

    // Animationen für Saelly
    private static final int PLAYER_ANIM_FRAMES_PER_ROW = 5;
    private static final double PLAYER_ANIM_DURATION_SECONDS = 1.0;
    private static final int PLAYER_ANIM_IDLE_START = 0;
    private static final int PLAYER_ANIM_IDLE_END = 4;
    private static final int PLAYER_ANIM_JUMP_START = 5;
    private static final int PLAYER_ANIM_JUMP_END = 9;
    private static final double PLAYER_JUMP_ANIM_THRESHOLD = -10.0;// Start animation at jumpspeed
    private static final double PHYSICS_LAG_THRESHOLD = 0.05;
    private static final double PHYSICS_TARGET_NORMAL_TPF = 0.016;//ca. 60 FPS
    private static final double PLAYER_ANIM_TPF_LIMIT = 0.02;
    private static final double PLAYER_IDLE_ANIM_SPEED = 5.0;

    //FXGL Keys getter
    public static String getKeyGameStarted() { return KEY_GAME_STARTED; }
    public static String getKeyBarrierSpeed() { return KEY_BARRIER_SPEED; }
    public static String getKeyIsBuffActive() { return KEY_IS_BUFF_ACTIVE; }
    public static String getKeyIsInvulnerable() { return KEY_IS_INVULNERABLE; }
    public static String getKeyLives() { return KEY_LIVES; }
    public static String getPrefsKeyLanguage() { return PREFS_KEY_LANGUAGE; }
    public static String getLangGerman() { return LANG_GERMAN; }
    public static String getLangEnglish() { return LANG_ENGLISH; }
    public static String getKeyIsGameOver() { return KEY_IS_GAME_OVER; }
    public static String getKeyScore() { return KEY_SCORE; }
    public static String getKeyGeneralSpeed() { return KEY_GENERAL_SPEED; }
    public static String getKeyIsCrashing() { return  KEY_IS_CRASHING;}

    //Init-Getter
    public static int getInitScore()
    {
        return INIT_SCORE;
    }
    public static double getInitSpeed()
    {
        return INIT_SPEED;
    }
    public static boolean getIsGameOver()
    {
        return IS_GAME_OVER;
    }
    public static double getInitBarrierSpeed()
    {
        return INIT_BARRIER_SPEED;
    }
    public static int getInitLives()
    {
        return INIT_LIVES;
    }
    public static boolean getInitGameStarted()
    {
        return INIT_GAME_STARTED;
    }
    public static boolean getInitIsInvulnerable()
    {
        return INIT_IS_INVULNERABLE;
    }
    public static double getInitPlayerScale()
    {
        return INIT_PLAYER_SCALE;
    }
    public static double getInitBarrierScale()
    {
        return INIT_BARRIER_SCALE;
    }
    public static int getInitPlayerScaleWidth()
    {
        return INIT_PLAYER_SCALE_WIDTH;
    }
    public static int getInitPlayerScaleHeight()
    {
        return INIT_PLAYER_SCALE_HEIGHT;
    }
    public static double getInitPlayerPivotWidth()
    {
        return INIT_PLAYER_PIVOT_WIDTH;
    }
    public static double getInitPlayerPivotHeight()
    {
        return INIT_PLAYER_PIVOT_HEIGHT;
    }
    public static boolean getInitPlayerIsCrashing()
    {
        return INIT_PLAYER_IS_CRASHING;
    }
    public static boolean getInitIsBuffActive()
    {
        return INIT_IS_BUFF_ACTIVE;
    }
    public static boolean getInitIsDebuffActive()
    {
        return INIT_IS_DEBUFF_ACTIVE;
    }
    public static int getInitPlacedValue() {
        return placedValue;
    }
    //defaults
    public static String getDefaultKeyJump() { return DEFAULT_KEY_JUMP; }
    public static String getDefaultKeyRestart() { return DEFAULT_KEY_RESTART; }
    public static String getDefaultKeyWindowed() { return DEFAULT_KEY_WINDOWED; }
    public static String getDefaultKeyBorderless() { return DEFAULT_KEY_BORDERLESS; }
    public static String getDefaultKeyFullscreen() { return DEFAULT_KEY_FULLSCREEN; }
    //UI
    public static String getLinkToHeartUiImage()
    {
        return LINK_TO_HEART_UI_IMAGE;
    }
    public static int getHeartUiImageWidth()
    {
        return HEART_UI_IMAGE_WIDTH;
    }
    public static int getHeartUiImageHeight()
    {
        return HEART_UI_IMAGE_HEIGHT;
    }
    public static boolean getInitIsDebugging()
    {
        return INIT_IS_DEBUGGING;
    }
    public static String getLinkToUiAppicon()
    {
        return LINK_TO_UI_APPICON;
    }
    public static int getAppiconUiImageWidth()
    {
        return APPICON_UI_IMAGE_WIDTH;
    }
    public static int getAppiconUiImageHeight()
    {
        return APPICON_UI_IMAGE_HEIGHT;
    }
    public static double getCrashEffectOffsetX() { return CRASH_EFFECT_OFFSET_X; }
    public static double getCrashEffectOffsetY() { return CRASH_EFFECT_OFFSET_Y; }
    public static double getPickupEffectOffsetX() { return PICKUP_EFFECT_OFFSET_X; }
    public static double getPickupEffectOffsetY() { return PICKUP_EFFECT_OFFSET_Y; }
    public static double getSlowMotionOffsetX() { return SLOW_MOTION_OFFSET_X; }
    public static double getSlowMotionOffsetY() { return SLOW_MOTION_OFFSET_Y; }

    //Getter
    public static double getGameSpeed()
    {
        return gameSpeed;
    }
    public static double getBarrierSpeed()
    {
        return barrierSpeed;
    }
    public static double getIdleJumpHeight()
    {
        return IDLE_JUMP_HEIGHT;
    }
    public static int getScoreMulti()
    {
        return scoreMulti;
    }
    public static int getPlayerStartXPosition()
    {
        return PLAYER_START_X_POSITION;
    }
    public static int getPlayerStartYPosition()
    {
        return PLAYER_START_Y_POSITION;
    }
    public static int getPlayerCollisionBoxWidth()
    {
        return PLAYER_COLLISION_BOX_WIDTH;
    }
    public static int getPlayerCollisionBoxHeight()
    {
        return PLAYER_COLLISION_BOX_HEIGHT;
    }
    public static double getPlayerMaxFallSpeed()
    {
        return PLAYER_MAX_FALL_SPEED;
    }
    public static double getPlayerMaxRiseSpeed()
    {
        return PLAYER_MAX_RISE_SPEED;
    }
    public static double getJumpMulti()
    {
        return JUMP_MULTI;
    }
    public static double getGravity()
    {
        return gravity;
    }
    public static double getWindowScale()
    {
        return WINDOW_SCALE;
    }
    public static int getStandardWindowWidth()
    {
        return STANDARD_WINDOW_WIDTH;
    }
    public static int getStandardWindowHeightWithoutTitlebar()
    {
        return STANDARD_WINDOW_HEIGHT_WITHOUT_TITLEBAR;
    }
    public static double getStandardTitlebarHeight()
    {
        return STANDARD_TITLEBAR_HEIGHT;
    }
    public static double getAspectRatio()
    {
        return ASPECT_RATIO;
    }
    public static int getStandardWindowHeight()
    {
        return STANDARD_WINDOW_HEIGHT;
    }
    public static String getWindowTitle()
    {
        return windowTitle;
    }
    public static String getGameVersion()
    {
        return GAME_VERSION;
    }
    public static int getLogoPrefSizeWidth()
    {
        return LOGO_PREF_SIZE_WIDTH;
    }
    public static int getLogoPrefSizeHeight()
    {
        return LOGO_PREF_SIZE_HEIGHT;
    }
    public static int getLogoFitHeight()
    {
        return LOGO_FIT_HEIGHT;
    }
    public static double getMinSpawnHeightBarrier()
    {
        return MIN_SPAWN_HEIGHT_BARRIER;
    }
    public static double getMaxSpawnHeightBarrier()
    {
        return MAX_SPAWN_HEIGHT_BARRIER;
    }
    public static double getSpawnDurationBarrier()
    {
        return spawnDurationBarrier;
    }
    public static double getSpawnDurationBuffPowerup()
    {
        return spawnDurationBuffPowerup;
    }
    public static int getBarriersToSpeedupTheGame()
    {
        return barriersToSpeedupTheGame;
    }
    public static int getBarrierScaleWidth()
    {
        return BARRIER_SCALE_WIDTH;
    }
    public static int getBarrierScaleHeight()
    {
        return BARRIER_SCALE_HEIGHT;
    }

    //BuffPowerups
    public static double getInvulnerableDurationInSeconds()
    {
        return invulnerableDurationInSeconds;
    }
    public static int getScoreX10Multi()
    {
        return SCORE_X10_MULTI;
    }
    public static double getSlowMotionBuffPowerupRatio()
    {
        return slowMotionBuffPowerupRatio;
    }
    public static int getSlowMotionBuffPowerupDurationSeconds()
    {
        return slowMotionBuffPowerupDurationSeconds;
    }
    public static int getScoreX10DurationSeconds()
    {
        return scoreX10DurationSeconds;
    }
    public static int getSlowMotionImageWidth()
    {
        return SLOW_MOTION_IMAGE_WIDTH;
    }
    public static int getSlowMotionImageHeight()
    {
        return SLOW_MOTION_IMAGE_HEIGHT;
    }
    public static int getInvulnerableImageWidth()
    {
        return INVULNERABLE_IMAGE_WIDTH;
    }
    public static int getInvulnerableImageHeight()
    {
        return INVULNERABLE_IMAGE_HEIGHT;
    }
    public static int getScoreX10ImageWidth()
    {
        return SCORE_X10_IMAGE_WIDTH;
    }
    public static int getScoreX10ImageHeight()
    {
        return SCORE_X10_IMAGE_HEIGHT;
    }
    public static int getExtraLifeImageWidth()
    {
        return EXTRA_LIFE_IMAGE_WIDTH;
    }
    public static int getExtraLifeImageHeight()
    {
        return EXTRA_LIFE_IMAGE_HEIGHT;
    }
    public static int getScorePowerupMultiplierBonus() { return SCORE_POWERUP_MULTIPLIER_BONUS; }
    public static int getExtraLifePowerupBonus() { return EXTRA_LIFE_POWERUP_BONUS; }
    public static String getLinkToInitLoadingScreenBackground()
    {
        return LINK_TO_INIT_LOADINGSCREEN_BACKGROUND_IMAGE;
    }


    //Links zu Bildern, Musik und Sounds - Getter
    public static String getLinkToBackgroundMusic()
    {
        return LINK_TO_BACKGROUND_MUSIC;
    }
    public static String getLinkToClickSound() { return LINK_TO_CLICK_SOUND; }
    public static String getLinkToButtonTickSound() {return LINK_TO_BUTTON_TICK_SOUND;}
    public static String getLinkToMainMenuMusic() {return LINK_TO_MAIN_MENU_MUSIC; }
    public static String getLinkToBackgroundImage()
    {
        return LINK_TO_BACKGROUND_IMAGE;
    }
    public static String getLinkToImagePlayerIdle()
    {
        return LINK_TO_IMAGE_PLAYER_IDLE;
    }
    public static String getLinkToImagePlayerJump()
    {
        return LINK_TO_IMAGE_PLAYER_JUMP;
    }
    public static String getLinkToSoundBarrierPassed()
    {
        return LINK_TO_SOUND_BARRIER_PASSED;
    }
    public static String getLinkToJumpSound()
    {
        return LINK_TO_JUMP_SOUND;
    }
    public static String getLinkToSlowMotionPickupSound()
    {
        return LINK_TO_SLOW_MOTION_PICKUP_SOUND;
    }
    public static String getLinkToSlowMotionActiveSound()
    {
        return LINK_TO_SLOW_MOTION_ACTIVE_SOUND;
    }
    public static String getLinkToSlowMotionImage()
    {
        return LINK_TO_SLOW_MOTION_IMAGE;
    }
    public static String getLinkToScorex10PickupSound()
    {
        return LINK_TO_SCOREX10_PICKUP_SOUND;
    }
    public static String getLinkToScorex10ActiveSound()
    {
        return LINK_TO_SCOREX10_ACTIVE_SOUND;
    }
    public static String getLinkToScorex10Image()
    {
        return LINK_TO_SCOREX10_IMAGE;
    }
    public static String getLinkToInvulnerablePickupSound()
    {
        return LINK_TO_INVULNERABLE_PICKUP_SOUND;
    }
    public static String getLinkToInvulnerableActivSound()
    {
        return LINK_TO_INVULNERABLE_ACTIV_SOUND;
    }
    public static String getLinkToInvulnerableImage()
    {
        return LINK_TO_INVULNERABLE_IMAGE;
    }
    public static String getLinkToExtraLivesPickupSound()
    {
        return LINK_TO_EXTRA_LIVES_PICKUP_SOUND;
    }
    public static String getLinkToExtraLivesActivSound()
    {
        return LINK_TO_EXTRA_LIVES_ACTIV_SOUND;
    }
    public static String getLinkToExtraLivesImage()
    {
        return LINK_TO_EXTRA_LIVES_IMAGE;
    }
    public static String getLinkToTopBarrierImage()
    {
        return LINK_TO_TOP_BARRIER_IMAGE;
    }
    public static String getLinkToLogo()
    {
        return LINK_TO_LOGO;
    }
    public static String getLinkToFontEagle()
    {
        return LINK_TO_FONT_EAGLE;
    }
    public static String getLinkToMenuMusic()
    {
        return LINK_TO_MENU_MUSIC;
    }
    public static String getLinkToGameOverMusic()
    {
        return LINK_TO_GAME_OVER_MUSIC;
    }
    public static String getLinkToGameOverSoundeffect()
    {
        return LINK_TO_GAME_OVER_SOUNDEFFECT;
    }
    public static String getLinkToCrashEffectAnimation()
    {
        return LINK_TO_CRASH_EFFECT_ANIMATION;
    }
    public static String getLinkToUiSoundMutedImage()
    {
        return LINK_TO_UI_SOUND_MUTED_IMAGE;
    }
    public static String getLinkToUiSoundUnmutedImage()
    {
        return LINK_TO_UI_SOUND_UNMUTED_IMAGE;
    }
    public static String getLinkToInvulnerableAnimation()
    {
        return LINK_TO_INVULNERABLE_ANIMATION;
    }
    public static String getLinkToSlowMotionAnimation()
    {
        return LINK_TO_SLOW_MOTION_ANIMATION;
    }
    public static String getLinkToPickupAnimation()
    {
        return LINK_TO_PICKUP_ANIMATION;
    }
    public static String getLinkToLoadingAnimation()
    {
        return LINK_TO_LOADING_ANIMATION;
    }
    public static String getLinkToUiIntroBackgroundImage()
    {
        return LINK_TO_UI_INTRO_BACKGROUND_IMAGE;
    }
    public static String getLinkToCss() { return LINK_TO_CSS; }

    //Buff Settings getter
    public static int getBuffAnimFramesPerRow()
    {
        return BUFF_ANIM_FRAMES_PER_ROW;
    }
    public static int getBuffAnimFrameWidth()
    {
        return BUFF_ANIM_FRAME_WIDTH;
    }
    public static int getBuffAnimFrameHeight()
    {
        return BUFF_ANIM_FRAME_HEIGHT;
    }
    public static int getBuffAnimStartFrame()
    {
        return BUFF_ANIM_START_FRAME;
    }
    public static int getBuffAnimEndFrame()
    {
        return BUFF_ANIM_END_FRAME;
    }
    public static double getBuffAnimDurationSeconds()
    {
        return BUFF_ANIM_DURATION_SECONDS;
    }
    public static double getBuffScaleFactor()
    {
        return BUFF_SCALE_FACTOR;
    }


    public static String getKeyIsDebuffActive() { return KEY_IS_DEBUFF_ACTIVE; }
    public static String getKeyPlaced() { return KEY_PLACED; }
    public static String getActionToggleMenu() { return ACTION_TOGGLE_MENU; }
    public static String getHitboxNameBody() { return HITBOX_NAME_BODY; }
    public static String getMsgErrSleep() { return MSG_ERR_SLEEP; }
    public static String getMsgErrIcon() { return MSG_ERR_ICON; }
    public static String getMsgErrStage() { return MSG_ERR_STAGE; }
    public static String getMsgErrRoot() { return MSG_ERR_ROOT; }
    public static String getMsgErrScene() { return MSG_ERR_SCENE; }
    public static String getMsgSafeExit() { return MSG_SAFE_EXIT; }
    public static double getPowerupPaddingMultiplier() { return POWERUP_PADDING_MULTIPLIER; }
    public static double getLoadingAnimDelaySeconds() { return LOADING_ANIM_DELAY_SECONDS; }

    //Language keys
    public static String getLangKeyGameTitle() {return LANG_KEY_GAME_TITLE;}
    public static String getLangKeyServerVersion() { return LANG_KEY_SERVER_VERSION; }
    public static String getLangKeyLoadingText() { return LANG_KEY_LOADING_TEXT; }
    public static String getLangKeyHighscoreName() { return LANG_KEY_HIGHSCORE_NAME; }
    public static String getLangKeyHighscorePoints() { return LANG_KEY_HIGHSCORE_POINTS; }
    public static String getLangKeyFinalScore() { return LANG_KEY_FINAL_SCORE; }
    public static String getLangKeyGameOver() { return LANG_KEY_GAME_OVER; }
    public static String getLangKeyPressEnter() { return LANG_KEY_PRESS_ENTER; }
    public static String getLangKeyCorruptedFile() { return LANG_KEY_CORRUPTED_FILE; }
    public static String getLangKeyLastSync() { return LANG_KEY_LAST_SYNC; }
    public static String getBundlePathTexts() { return BUNDLE_PATH_TEXTS; }
    public static String getLangKeyMenuResume() { return LANG_KEY_MENU_RESUME; }
    public static String getLangKeyMenuOptions() { return LANG_KEY_MENU_OPTIONS; }
    public static String getLangKeyMenuCredits() { return LANG_KEY_MENU_CREDITS; }
    public static String getLangKeyMenuExit() { return LANG_KEY_MENU_EXIT; }
    public static String getLangKeyMenuMusic() { return LANG_KEY_MENU_MUSIC; }
    public static String getLangKeyMenuSound() { return LANG_KEY_MENU_SOUND; }
    public static String getLangKeyMenuLanguage() { return LANG_KEY_MENU_LANGUAGE; }
    public static String getLangKeyMenuMode() { return LANG_KEY_MENU_MODE; }
    public static String getLangKeyMenuBack() { return LANG_KEY_MENU_BACK; }
    public static String getLangKeyMenuControls() { return LANG_KEY_MENU_CONTROLS; }
    public static String getLangKeyMenuControlsTitle() { return LANG_KEY_MENU_CONTROLS_TITLE; }
    public static String getLangKeyMenuWaiting() { return LANG_KEY_MENU_WAITING; }
    public static String getLangKeyMenuJump() { return LANG_KEY_MENU_JUMP; }
    public static String getLangKeyMenuRestart() { return LANG_KEY_MENU_RESTART; }
    public static String getLangKeyMenuWindowed() { return LANG_KEY_MENU_WINDOWED; }
    public static String getLangKeyMenuBorderless() { return LANG_KEY_MENU_BORDERLESS; }
    public static String getLangKeyMenuFullscreen() { return LANG_KEY_MENU_FULLSCREEN; }
    public static String getLangKeyMenuCreditsText() { return LANG_KEY_MENU_CREDITS_TEXT; }
    public static String getLangKeyMenuLanguageTitle() { return LANG_KEY_MENU_LANGUAGE_TITLE; }
    public static String getLangKeyMenuMainmenu() { return LANG_KEY_MENU_MAINMENU; }
    public static String getLangKeyMenuLeaderboard() { return LANG_KEY_MENU_LEADERBOARD; }
    public static String getLangKeyMenuLeaderboardTitle() { return LANG_KEY_MENU_LEADERBOARD_TITLE; }
    public static String getLangKeyMenuLangDe() { return LANG_KEY_MENU_LANG_DE; }
    public static String getLangKeyMenuLangEn() { return LANG_KEY_MENU_LANG_EN; }
    public static String getLangKeyMenuNoEntries() { return LANG_KEY_MENU_NO_ENTRIES; }
    public static String getLangKeyMenuExitConfirm() { return LANG_KEY_MENU_EXIT_CONFIRM; }
    public static String getLangKeyMenuYes() { return LANG_KEY_MENU_YES; }
    public static String getLangKeyMenuNo() { return LANG_KEY_MENU_NO; }
    public static String getLangKeyMenuNewGame() {return LANG_KEY_MENU_NEWGAME;}

    public static int getSpacingSmall() { return SPACING_SMALL; }
    public static int getSpacingMedium() { return SPACING_MEDIUM; }
    public static int getSpacingLarge() { return SPACING_LARGE; }
    public static int getSpacingXlarge() { return SPACING_XLARGE; }
    public static int getSpacingXxlarge() { return SPACING_XXLARGE; }
    public static int getStatusIndicatorRadius() { return STATUS_INDICATOR_RADIUS; }
    public static int getNameInputPrefWidth() { return NAME_INPUT_PREF_WIDTH; }
    public static int getViewOrderBackground() { return VIEW_ORDER_BACKGROUND; }
    public static int getViewOrderTitlebar() { return VIEW_ORDER_TITLEBAR; }
    public static int getViewOrderModal() { return VIEW_ORDER_MODAL; }
    public static int getViewOrderUiTop() { return VIEW_ORDER_UI_TOP; }
    public static String getColorHighlightHex() { return COLOR_HIGHLIGHT_HEX; }
    public static String getActionWindowed() { return ACTION_WINDOWED; }
    public static String getActionBorderless() { return ACTION_BORDERLESS; }
    public static String getActionFullscreen() { return ACTION_FULLSCREEN; }
    public static String getActionJump() { return ACTION_JUMP; }
    public static String getActionRestart() { return ACTION_RESTART; }
    public static String getActionToggleDebug() { return ACTION_TOGGLE_DEBUG; }
    public static double getHeartbeatIntervalMinutes() { return HEARTBEAT_INTERVAL_MINUTES; }
    public static double getGameOverDelaySeconds() { return GAME_OVER_DELAY_SECONDS; }
    public static double getBuffEffectDurationSeconds() { return BUFF_EFFECT_DURATION_SECONDS; }
    public static double getCorruptedDialogDelaySeconds() { return CORRUPTED_DIALOG_DELAY_SECONDS; }
    public static int getHighscoreDisplayOffsetStart() { return HIGHSCORE_DISPLAY_OFFSET_START; }
    public static int getHighscoreDisplayOffsetEnd() { return HIGHSCORE_DISPLAY_OFFSET_END; }
    public static double getViewportOffsetY() { return VIEWPORT_OFFSET_Y; }
    public static double getTopBarPaddingTop() { return TOP_BAR_PADDING_TOP; }
    public static double getTopBarPaddingSide() { return TOP_BAR_PADDING_SIDE; }
    public static double getMuteBtnOffset() { return MUTE_BTN_OFFSET; }
    public static double getVersionBoxOffsetX() { return VERSION_BOX_OFFSET_X; }
    public static double getVersionBoxOffsetY() { return VERSION_BOX_OFFSET_Y; }
    public static int getFontSizeScore() { return FONT_SIZE_SCORE; }
    public static int getFontSizeLives() { return FONT_SIZE_LIVES; }
    public static int getFontSizeVersion() { return FONT_SIZE_VERSION; }
    public static int getFontSizeLoading() { return FONT_SIZE_LOADING; }
    public static int getFontSizeGameoverTitle() { return FONT_SIZE_GAMEOVER_TITLE; }
    public static int getFontSizeGameoverRank() { return FONT_SIZE_GAMEOVER_RANK; }
    public static int getFontSizeGameoverEntry() { return FONT_SIZE_GAMEOVER_ENTRY; }
    public static int getFontSizeGameoverInstruct() { return FONT_SIZE_GAMEOVER_INSTRUCT; }
    public static double getMuteBtnAnimDurationMs() { return MUTE_BTN_ANIM_DURATION_MS; }
    public static double getMuteBtnScaleHover() { return MUTE_BTN_SCALE_HOVER; }
    public static double getMuteBtnScaleNormal() { return MUTE_BTN_SCALE_NORMAL;}
    public static double getMuteBtnScalePress() { return MUTE_BTN_SCALE_PRESS; }
    public static double getDefaultRestoreVolume() { return DEFAULT_RESTORE_VOLUME; }
    public static String getLangKeyIngamePoints() { return LANG_KEY_INGAME_POINTS; }
    public static String getFormatLives() { return FORMAT_LIVES; }
    public static String getFormatGameoverScore() { return FORMAT_GAMEOVER_SCORE; }
    public static double getPowerupSpawnChance() { return POWERUP_SPAWN_CHANCE; }
    public static double getMaxTpfThreshold() { return MAX_TPF_THRESHOLD; }
    public static double getNormalTpfFallback() { return NORMAL_TPF_FALLBACK; }
    public static int getIconSizeMute() { return ICON_SIZE_MUTE; }
    public static int getIconSizeBuff() { return ICON_SIZE_BUFF; }
    public static double getUiDimBackgroundOpacity() { return UI_DIM_BACKGROUND_OPACITY; }
    public static double getGameOverDimOpacity() { return GAME_OVER_DIM_OPACITY; }
    public static String getDefaultPlayerName() { return DEFAULT_PLAYER_NAME; }
    public static int getMaxPlayerNameLength() { return MAX_PLAYER_NAME_LENGTH; }
    public static double getIntroFadeDurationSeconds() { return INTRO_FADE_DURATION_SECONDS; }
    public static int getLoadingAnimFrameWidth() { return LOADING_ANIM_FRAME_WIDTH; }
    public static int getLoadingAnimFrameHeight() { return LOADING_ANIM_FRAME_HEIGHT; }
    public static int getLoadingAnimColumns() { return LOADING_ANIM_COLUMNS; }
    public static int getLoadingAnimTotalFrames() { return LOADING_ANIM_TOTAL_FRAMES; }
    public static double getLoadingAnimDurationMillis() { return LOADING_ANIM_DURATION_MILLIS; }
    public static double getLoadingAnimViewSize() { return LOADING_ANIM_VIEW_SIZE; }
    public static double getLoadingTextSize() { return LOADING_TEXT_SIZE; }
    public static double getLoadingDotsDurationSeconds() { return LOADING_DOTS_DURATION_SECONDS; }
    public static double getLoadingBarWidthRatio() { return LOADING_BAR_WIDTH_RATIO; }
    public static double getLoadingBarHeight() { return LOADING_BAR_HEIGHT; }
    public static double getLoadingVBoxSpacing() { return LOADING_VBOX_SPACING; }
    public static String getLinkToStartupBackground() { return LINK_TO_STARTUP_BACKGROUND; }
    public static String getLinkToStudioLogo() { return LINK_TO_STUDIO_LOGO; }
    public static String getErrorMsgStartupBg() { return ERROR_MSG_STARTUP_BG; }
    public static String getErrorMsgStartupLogo() { return ERROR_MSG_STARTUP_LOGO; }
    public static int getTitlebarPadding() { return TITLEBAR_PADDING; }
    public static int getTitlebarFontSize() { return TITLEBAR_FONT_SIZE; }
    public static double getTitlebarLogoAnimDuration() { return TITLEBAR_LOGO_ANIM_DURATION; }
    public static double getTitlebarLogoClickDuration() { return TITLEBAR_LOGO_CLICK_DURATION; }
    public static int getTitlebarCloseBtnSize() { return TITLEBAR_CLOSE_BTN_SIZE; }
    public static double getTitlebarCloseBreathingDuration() { return TITLEBAR_CLOSE_BREATHING_DURATION; }
    public static double getTitlebarCloseHoverDuration() { return TITLEBAR_CLOSE_HOVER_DURATION; }
    public static double getTitlebarCloseFadeOutDuration() { return TITLEBAR_CLOSE_FADE_OUT_DURATION; }
    public static String getErrorMsgTitlebarLogo() { return ERROR_MSG_TITLEBAR_LOGO; }
    public static String getBasePathTextures() { return BASE_PATH_TEXTURES; }
    public static String getLinkToCrashEffect() { return LINK_TO_CRASH_EFFECT; }
    public static int getCrashEffectCols() { return CRASH_EFFECT_COLS; }
    public static int getCrashEffectFrameWidth() { return CRASH_EFFECT_FRAME_WIDTH; }
    public static int getCrashEffectFrameHeight() { return CRASH_EFFECT_FRAME_HEIGHT; }
    public static int getCrashEffectNumFrames() { return CRASH_EFFECT_NUM_FRAMES; }
    public static double getCrashEffectDurationSec() { return CRASH_EFFECT_DURATION_SEC; }
    public static double getCrashEffectTargetWidth() { return CRASH_EFFECT_TARGET_WIDTH; }
    public static double getCrashEffectTargetHeight() { return CRASH_EFFECT_TARGET_HEIGHT; }
    public static String getLinkToPickupEffect() { return LINK_TO_PICKUP_EFFECT; }
    public static int getPickupEffectCols() { return PICKUP_EFFECT_COLS; }
    public static int getPickupEffectFrameWidth() { return PICKUP_EFFECT_FRAME_WIDTH; }
    public static int getPickupEffectFrameHeight() { return PICKUP_EFFECT_FRAME_HEIGHT; }
    public static int getPickupEffectNumFrames() { return PICKUP_EFFECT_NUM_FRAMES; }
    public static double getPickupEffectDurationSec() { return PICKUP_EFFECT_DURATION_SEC; }
    public static double getPickupEffectTargetWidth() { return PICKUP_EFFECT_TARGET_WIDTH; }
    public static double getPickupEffectTargetHeight() { return PICKUP_EFFECT_TARGET_HEIGHT; }
    public static int getInvulnerableAnimationCols() { return INVULNERABLE_ANIMATION_COLS; }
    public static int getInvulnerableAnimationFrameWidth() { return INVULNERABLE_ANIMATION_FRAME_WIDTH; }
    public static int getInvulnerableAnimationFrameHeight() { return INVULNERABLE_ANIMATION_FRAME_HEIGHT; }
    public static int getInvulnerableAnimationNumFrames() { return INVULNERABLE_ANIMATION_NUM_FRAMES; }
    public static double getInvulnerableAnimationDurationSec() { return INVULNERABLE_ANIMATION_DURATION_SEC; }
    public static double getInvulnerableAnimationTargetWidth() { return INVULNERABLE_ANIMATION_TARGET_WIDTH; }
    public static double getInvulnerableAnimationTargetHeight() { return INVULNERABLE_ANIMATION_TARGET_HEIGHT; }
    public static int getSlowMotionAnimationCols() { return SLOW_MOTION_ANIMATION_COLS; }
    public static int getSlowMotionAnimationFrameWidth() { return SLOW_MOTION_ANIMATION_FRAME_WIDTH; }
    public static int getSlowMotionAnimationFrameHeight() { return SLOW_MOTION_ANIMATION_FRAME_HEIGHT; }
    public static int getSlowMotionAnimationNumFrames() { return SLOW_MOTION_ANIMATION_NUM_FRAMES; }
    public static double getSlowMotionAnimationDurationSec() { return SLOW_MOTION_ANIMATION_DURATION_SEC; }
    public static double getSlowMotionAnimationTargetWidth() { return SLOW_MOTION_ANIMATION_TARGET_WIDTH; }
    public static double getSlowMotionAnimationTargetHeight() { return SLOW_MOTION_ANIMATION_TARGET_HEIGHT; }
    public static String getErrorMsgSaveFailed() { return ERROR_MSG_SAVE_FAILED; }
    public static String getHighscoreServerUrl() { return HIGHSCORE_SERVER_URL; }
    public static String getHighscoreSecretKey() { return HIGHSCORE_SECRET_KEY; }
    public static String getHighscorePhpPwd() { return HIGHSCORE_PHP_PWD; }
    public static long getHttpTimeoutSeconds() { return HTTP_TIMEOUT_SECONDS; }
    public static long getPingTimeoutSeconds() { return PING_TIMEOUT_SECONDS; }
    public static String getSaveDirectoryName() { return SAVE_DIRECTORY_NAME; }
    public static String getLocalHighscoreFileName() { return LOCAL_HIGHSCORE_FILE_NAME; }
    public static String getMetadataFileName() { return METADATA_FILE_NAME; }
    public static String getLangKeyUnknown() { return LANG_KEY_UNKNOWN; }
    public static String getServerResponseSuccess() { return SERVER_RESPONSE_SUCCESS; }

    //Error massages
    public static String getErrMsgServerTimeout() { return ERR_MSG_SERVER_TIMEOUT; }
    public static String getErrMsgServerError() { return ERR_MSG_SERVER_ERROR; }
    public static String getErrMsgServerOffline() { return ERR_MSG_SERVER_OFFLINE; }
    public static String getErrMsgDirectSaveFailed() { return ERR_MSG_DIRECT_SAVE_FAILED; }
    public static String getErrMsgNoHash() { return ERR_MSG_NO_HASH; }
    public static String getErrMsgDataManipulated() { return ERR_MSG_DATA_MANIPULATED; }
    public static String getErrMsgFileCorrupted() { return ERR_MSG_FILE_CORRUPTED; }
    public static String getErrMsgSaveLocalFailed() { return ERR_MSG_SAVE_LOCAL_FAILED; }
    public static String getErrMsgEncryptFailed() { return ERR_MSG_ENCRYPT_FAILED; }
    public static String getErrMsgCreateDirFailed() { return ERR_MSG_CREATE_DIR_FAILED; }
    public static String getErrMsgSaveSyncTimeFailed() { return ERR_MSG_SAVE_SYNC_TIME_FAILED; }
    public static String getErrMsgMainMenuMusic() {return ERR_MSG_MAIN_MENU_MUSIC;}
    public static String getErrMsgButtonClickSound() {return ERR_MSG_BUTTON_CLICK_SOUND;}
    public static String getErrMsgButtonTickSound() {return ERR_MSG_BUTTON_TICK_SOUND;}

    public static String[] getDummyNames() { return DUMMY_NAMES; }
    public static int[] getDummyScores() { return DUMMY_SCORES; }
    public static String getKeyIsDebugging() { return KEY_IS_DEBUGGING; }
    public static double getHitboxStrokeWidth() { return HITBOX_STROKE_WIDTH; }
    public static Color getHitboxColorColliding() { return HITBOX_COLOR_COLLIDING; }
    public static Color getHitboxColorPlayer() { return HITBOX_COLOR_PLAYER; }
    public static Color getHitboxColorBarrier() { return HITBOX_COLOR_BARRIER; }
    public static Color getHitboxColorBuff() { return HITBOX_COLOR_BUFF; }
    public static Color getHitboxColorDebuff() { return HITBOX_COLOR_DEBUFF; }
    public static double getPhysicsLagThreshold() { return PHYSICS_LAG_THRESHOLD; }
    public static double getPhysicsTargetNormalTpf() { return PHYSICS_TARGET_NORMAL_TPF; }
    public static double getPlayerAnimTpfLimit() { return PLAYER_ANIM_TPF_LIMIT; }
    public static double getPlayerIdleAnimSpeed() { return PLAYER_IDLE_ANIM_SPEED; }
    public static double getWindowResizeTolerance() { return WINDOW_RESIZE_TOLERANCE; }
    public static String getMsgErrCssMain() { return MSG_ERR_CSS_MAIN; }
    public static String getMsgErrCssLoading() { return MSG_ERR_CSS_LOADING; }
    public static String getCssClassPauseBg() { return CSS_CLASS_PAUSE_BG; }
    public static String getCssClassMagicalTitle() { return CSS_CLASS_MAGICAL_TITLE; }
    public static String getCssClassMagicalBtn() { return CSS_CLASS_MAGICAL_BTN; }
    public static String getCssClassMuteBtn() { return CSS_CLASS_MUTE_BTN; }
    public static String getCssClassSlider() { return CSS_CLASS_SLIDER; }
    public static String getCssClassScrollPane() { return CSS_CLASS_SCROLL_PANE; }
    public static String getCssClassBtnWaiting() { return CSS_CLASS_BTN_WAITING; }
    public static String getCssClassCreditsText() { return CSS_CLASS_CREDITS_TEXT; }
    public static String getPrefsKeyMusicVol() { return PREFS_KEY_MUSIC_VOL; }
    public static String getPrefsKeySoundVol() { return PREFS_KEY_SOUND_VOL; }
    public static String getPrefsKeyBindingPrefix() { return PREFS_KEY_BINDING_PREFIX; }
    public static double getMenuTitleFontSize() { return MENU_TITLE_FONT_SIZE; }
    public static double getMenuTitleFloatY() { return MENU_TITLE_FLOAT_Y; }
    public static double getMenuTitleFloatDur() { return MENU_TITLE_FLOAT_DUR; }
    public static double getMenuTitleMarginTop() { return MENU_TITLE_MARGIN_TOP; }
    public static double getMenuCornerLogoWidth() { return MENU_CORNER_LOGO_WIDTH; }
    public static double getMenuCornerMarginBottom() { return MENU_CORNER_MARGIN_BOTTOM; }
    public static double getMenuCornerOffsetYCalc() { return MENU_CORNER_OFFSET_Y_CALC; }
    public static double getMenuBoxSpacing() { return MENU_BOX_SPACING; }
    public static double getMenuBoxMarginTop() { return MENU_BOX_MARGIN_TOP; }
    public static double getMenuSliderMaxWidth() { return MENU_SLIDER_MAX_WIDTH; }
    public static double getMenuScrollHeight() { return MENU_SCROLL_HEIGHT; }
    public static double getMenuKeybindRowSpacing() { return MENU_KEYBIND_ROW_SPACING; }
    public static int getMenuTextFontSize() { return MENU_TEXT_FONT_SIZE; }
    public static String getCssClassMagicalText() { return CSS_CLASS_MAGICAL_TEXT; }
    public static Color getColorServerOnline() { return COLOR_SERVER_ONLINE; }
    public static Color getColorServerOffline() { return COLOR_SERVER_OFFLINE; }
    public static String getFormatLeaderboardEntry() {return FORMAT_LEADERBOARD_ENTRY; }
    public static double getMainMenuDimOpacity() {return MAIN_MENU_DIM_OPACITY;}
    public static double getDefaultRestoreMusicVolume() {return DEFAULT_RESTORE_MUSIC_VOLUME;}
    public static double getDefaultRestoreSoundVolume() {return DEFAULT_RESTORE_SOUND_VOLUME;}

    //Player animation getter
    public static int getPlayerAnimFramesPerRow() { return PLAYER_ANIM_FRAMES_PER_ROW; }
    public static double getPlayerAnimDurationSeconds() { return PLAYER_ANIM_DURATION_SECONDS; }
    public static int getPlayerAnimIdleStart() { return PLAYER_ANIM_IDLE_START; }
    public static int getPlayerAnimIdleEnd() { return PLAYER_ANIM_IDLE_END; }
    public static int getPlayerAnimJumpStart() { return PLAYER_ANIM_JUMP_START; }
    public static int getPlayerAnimJumpEnd() { return PLAYER_ANIM_JUMP_END; }
    public static double getPlayerJumpAnimThreshold() { return PLAYER_JUMP_ANIM_THRESHOLD; }

    //Setter
    public static void setGameSpeed(double gameSpeed)
    {
        Settings.gameSpeed = gameSpeed;
    }
    public static void setBarrierSpeed(double barrierSpeed)
    {
        Settings.barrierSpeed = barrierSpeed;
    }
    public static void setGravity(double gravity)
    {
        Settings.gravity = gravity;
    }
    public static void setScoreMulti(int scoreMulti)
    {
        Settings.scoreMulti = scoreMulti;
    }
    public static void setWindowTitle(String windowTitle)
    {
        Settings.windowTitle = windowTitle;
    }
    public static void setSpawnDurationBarrier(double spawnDurationBarrier)
    {
        Settings.spawnDurationBarrier = spawnDurationBarrier;
    }
    public static void setBarriersToSpeedupTheGame(int barriersToSpeedupTheGame)
    {
        Settings.barriersToSpeedupTheGame = barriersToSpeedupTheGame;
    }
    public static void setPlacedValue(int placedValue)
    {
        Settings.placedValue = placedValue;
    }

}
