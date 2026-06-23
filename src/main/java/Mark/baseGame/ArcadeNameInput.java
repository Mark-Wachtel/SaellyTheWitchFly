package Mark.baseGame;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ArcadeNameInput extends HBox {

    private final int MAX_CHARS = Settings.getMaxPlayerNameLength();
    private final Text[] letters = new Text[MAX_CHARS];
    private final Text[] upArrows = new Text[MAX_CHARS];
    private final Text[] downArrows = new Text[MAX_CHARS];
    private int currentIndex = 0;

    private TranslateTransition bounceAnimUp;
    private TranslateTransition bounceAnimDown;

    public ArcadeNameInput() {
        setSpacing(Settings.getArcadeInputSpacingX());
        setAlignment(Pos.CENTER);
        setFocusTraversable(true);

        for (int i = 0; i < MAX_CHARS; i++) {

            upArrows[i] = new Text(Settings.getArcadeArrowUpText());
            upArrows[i].getStyleClass().add(Settings.getCssClassArcadeArrow());

            letters[i] = new Text(Settings.getArcadeDefaultLetter());
            letters[i].getStyleClass().add(Settings.getCssClassArcadeLetter());

            downArrows[i] = new Text(Settings.getArcadeArrowDownText());
            downArrows[i].getStyleClass().add(Settings.getCssClassArcadeArrow());

            VBox column = new VBox(Settings.getArcadeInputSpacingY(), upArrows[i], letters[i], downArrows[i]);
            column.setAlignment(Pos.CENTER);
            getChildren().add(column);
        }

        updateCursor();

        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                currentIndex = Math.max(0, currentIndex - 1);
                updateCursor();
                e.consume();
            } else if (e.getCode() == KeyCode.RIGHT) {
                currentIndex = Math.min(MAX_CHARS - 1, currentIndex + 1);
                updateCursor();
                e.consume();
            } else if (e.getCode() == KeyCode.UP) {
                cycleLetter(1);
                e.consume();
            } else if (e.getCode() == KeyCode.DOWN) {
                cycleLetter(-1);
                e.consume();
            } else if (e.getText().matches(Settings.getArcadeInputRegex())) {
                letters[currentIndex].setText(e.getText().toUpperCase());
                currentIndex = Math.min(MAX_CHARS - 1, currentIndex + 1);
                updateCursor();
                e.consume();
            }
        });
    }

    private void cycleLetter(int dir) {
        String allowed = Settings.getArcadeAllowedChars();
        char currentChar = letters[currentIndex].getText().charAt(0);

        int pos = allowed.indexOf(currentChar);

        pos += dir;

        if (pos >= allowed.length()) {
            pos = 0;
        }

        if (pos < 0) {
            pos = allowed.length() - 1;
        }

        letters[currentIndex].setText(String.valueOf(allowed.charAt(pos)));
    }

    private void updateCursor() {
        if (bounceAnimUp != null) bounceAnimUp.stop();
        if (bounceAnimDown != null) bounceAnimDown.stop();

        for (int i = 0; i < MAX_CHARS; i++) {
            upArrows[i].setVisible(false);
            downArrows[i].setVisible(false);
            upArrows[i].setTranslateY(0);
            downArrows[i].setTranslateY(0);
            letters[i].getStyleClass().remove(Settings.getCssClassArcadeLetterActive());
        }

        upArrows[currentIndex].setVisible(true);
        downArrows[currentIndex].setVisible(true);
        letters[currentIndex].getStyleClass().add(Settings.getCssClassArcadeLetterActive());

        bounceAnimUp = new TranslateTransition(Duration.millis(Settings.getArcadeAnimDurationMs()), upArrows[currentIndex]);
        bounceAnimUp.setByY(-Settings.getArcadeArrowBounceDist());
        bounceAnimUp.setCycleCount(Animation.INDEFINITE);
        bounceAnimUp.setAutoReverse(true);
        bounceAnimUp.play();

        bounceAnimDown = new TranslateTransition(Duration.millis(Settings.getArcadeAnimDurationMs()), downArrows[currentIndex]);
        bounceAnimDown.setByY(Settings.getArcadeArrowBounceDist());
        bounceAnimDown.setCycleCount(Animation.INDEFINITE);
        bounceAnimDown.setAutoReverse(true);
        bounceAnimDown.play();
    }

    public String getName() {
        StringBuilder sb = new StringBuilder();
        for (Text t : letters) sb.append(t.getText());
        return sb.toString();
    }
}