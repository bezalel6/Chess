package ver14.Sound;

import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;

/**
 * Sound manager.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class SoundManager {

    /**
     * plays after one of the players Promote.
     */
    public final Sound promotion = new Sound("promote", this);
    /**
     * plays after this player moved.
     */
    public final Sound selfMove = new Sound("self-move", this);
    /**
     * plays after the Opponent move.
     */
    public final Sound opponentMove = new Sound("opponent-move", this);
    /**
     * plays after one of the players Capture.
     */
    public final Sound capture = new Sound("capture", this);
    /**
     * plays after one of the players castle.
     */
    public final Sound castle = new Sound("castle", this);
    /**
     * The Check.
     */
    public final Sound check = new Sound("check", this);
    /**
     * The Game start.
     */
    public final Sound gameStart = new Sound("game-start", this);
    /**
     * The Game end.
     */
    public final Sound gameEnd = new Sound("game-end", this);
    /**
     * plays on the last Ten seconds of the timer
     */
    public final Sound tenSeconds = new Sound("ten-seconds", this);
    /**
     * is the Sound enabled.
     */
    private boolean soundEnabled = true;

    /**
     * Is sound enabled boolean.
     *
     * @return the boolean
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Sets sound enabled.
     *
     * @param soundEnabled the sound enabled
     */
    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    /**
     * play the right sound effect for the move played
     *
     * @param move  the move
     * @param myClr my clr
     */
    public void moved(Move move, PlayerColor myClr) {
        if (move.isCheck()) {
            check.play();
        } else if (move.isCapturing()) {
            capture.play();
        } else if (move.getMoveFlag().isCastling) {
            castle.play();
        } else if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
            promotion.play();
        } else if (move.getMovingColor() == myClr) {
            selfMove.play();
        } else opponentMove.play();

    }


}
