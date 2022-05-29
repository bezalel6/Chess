package ver14.SharedClasses.Game.Evaluation;

import java.io.Serializable;

/**
 * represents the Game status type.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum GameStatusType implements Serializable {
    /**
     * Tie.
     */
    TIE("½½", "Tie"),
    /**
     * a player is in check.
     */
    CHECK("+"),
    /**
     * the Game goes on.
     */
    GAME_GOES_ON(""),
    /**
     * a player won.
     */
    WIN_OR_LOSS("#", "Won"),
    /**
     * the game is unfinished.
     */
    UNFINISHED("...");

    /**
     * The game status annotation.
     */
    public final String annotation;
    /**
     * game over str
     */
    public final String gameOverStr;

    /**
     * Instantiates a new Game status type.
     *
     * @param annotation the annotation
     */
    GameStatusType(String annotation) {
        this(annotation, annotation);
    }

    /**
     * Instantiates a new Game status type.
     *
     * @param annotation  the annotation
     * @param gameOverStr the game over str
     */
    GameStatusType(String annotation, String gameOverStr) {
        this.annotation = annotation;
        this.gameOverStr = gameOverStr;
    }


    /**
     * Is game over.
     *
     * @return <code>true</code> if is game over. false otherwise
     */
    public boolean isGameOver() {
        return this == TIE || this == WIN_OR_LOSS || this == UNFINISHED;
    }
}
