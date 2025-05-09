package ver14.view.Board;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;

/**
 * represents a Saved square on the saved board.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class SavedSquare {
    /**
     * The Btn state.
     */
    private final int btnState;
    /**
     * The Loc.
     */
    private final ViewLocation loc;
    /**
     * The Piece.
     */
    private final Piece piece;
    /**
     * The Is enabled.
     */
    private boolean isEnabled;

    /**
     * Instantiates a new Saved square.
     *
     * @param btn the btn
     */
    public SavedSquare(BoardButton btn) {
        this.btnState = btn.getBtnState();
        this.loc = btn.getBtnLoc();
        this.piece = btn.getPiece();
        this.isEnabled = btn.isEnabled();
    }

    /**
     * Gets loc.
     *
     * @return the loc
     */
    public ViewLocation getLoc() {
        return loc;
    }

    /**
     * Sets enabled.
     *
     * @param enabled the enabled
     */
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    /**
     * Restore.
     *
     * @param btn the btn
     */
    public void restore(BoardButton btn) {
        btn.setPiece(piece);
        btn.setStates(btnState);
        btn.setEnabled(isEnabled);
    }
}
