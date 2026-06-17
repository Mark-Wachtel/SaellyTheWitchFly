package Mark.baseGame;

import com.almasb.fxgl.app.scene.*;

public class CustomSceneFactory extends SceneFactory
{

    @Override
    public LoadingScene newLoadingScene()
    {
        return new CustomLoadingScene();
    }

    @Override
    public StartupScene newStartup(int width, int height)
    {
        return new CustomStartupScene(width,height);
    }

    @Override
    public IntroScene newIntro()
    {
        return new CustomIntroScene();
    }

    @Override
    public FXGLMenu newGameMenu()
    {
        CustomPauseMenuScene pauseMenu = new CustomPauseMenuScene();
        pauseMenu.getRoot().visibleProperty().addListener((obs, oldVal, newVal) ->
        {
            if (newVal)
            {
                pauseMenu.activatePauseMenu();
            }
        });
        return pauseMenu;
    }

    @Override
    public FXGLMenu newMainMenu()
    {
        CustomMainMenuScene mainMenu = new CustomMainMenuScene();
        mainMenu.activateMainMenu();
        return mainMenu;
    }
}
