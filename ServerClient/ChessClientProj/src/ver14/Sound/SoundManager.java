package ver14.Sound;

import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.moves.Move;

public class SoundManager {

    public final Sound promotion = new Sound("promote", this);
    public final Sound selfMove = new Sound("self-move", this);
    public final Sound opponentMove = new Sound("opponent-move", this);
    public final Sound capture = new Sound("capture", this);
    public final Sound castle = new Sound("castle", this);
    public final Sound check = new Sound("check", this);
    public final Sound gameStart = new Sound("game-start", this);
    public final Sound gameEnd = new Sound("game-end", this);

    private boolean soundEnabled = true;

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public void moved(Move move, PlayerColor myClr) {
        if (move.isCapturing()) {
            capture.play();
        } else if (move.isCheck()) {
            check.play();
        } else if (move.getMoveFlag().isCastling) {
            castle.play();
        } else if (move.getMoveFlag() == Move.MoveType.Promotion) {
            promotion.play();
        } else if (move.getMovingColor() == myClr) {
            selfMove.play();
        } else opponentMove.play();

    }


}
