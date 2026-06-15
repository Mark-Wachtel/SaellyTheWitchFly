package Mark.baseGame;

import com.almasb.fxgl.app.scene.StartupScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class CustomStartupScene extends StartupScene
{

    public CustomStartupScene(int width, int height)
    {
        super(width,height);

        getContentRoot().getChildren().clear();

        var backgroundImage = getClass().getResourceAsStream(Settings.getLinkToStartupBackground());
        if (backgroundImage != null)
        {
            Image image = new Image(backgroundImage);
            ImageView backgroundView = new ImageView(image);

            backgroundView.setFitWidth(width);
            backgroundView.setFitHeight(height);
            getContentRoot().getChildren().add(backgroundView);
        }
        else
        {
            System.err.println(Settings.getErrorMsgStartupBg());
        }


        var imageStream = getClass().getResourceAsStream(Settings.getLinkToStudioLogo());

        if (imageStream != null) {
            Image image = new Image(imageStream);
            ImageView logoView = new ImageView(image);

            logoView.setTranslateX(width / 2.0 - image.getWidth() / 2.0);
            logoView.setTranslateY(height / 2.0 - image.getHeight() / 2.0);

            getContentRoot().getChildren().add(logoView);
        } else {
            System.err.println(Settings.getErrorMsgStartupLogo());
        }
    }

}
