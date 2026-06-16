package Mark.baseGame;

import com.almasb.fxgl.app.scene.*;

public class SceneFactory extends com.almasb.fxgl.app.scene.SceneFactory
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
    public FXGLMenu newGameMenu() {
        return new CustomPauseMenuScene();
    }

    @Override
    public FXGLMenu newMainMenu() {return new CustomMainMenuScene();}
}
