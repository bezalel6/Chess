package ver14.view.Board;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;

import java.util.ArrayList;
import java.util.Objects;

/**
 * represents a capture of a view board's state. used for scrolling to previous position.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ViewSavedBoard extends Board {

    /**
     * The Saved squares.
     */
    public final ArrayList<SavedSquare> savedSquares;

    /**
     * Instantiates a new View saved board.
     *
     * @param boardPanel the board panel
     */
    public ViewSavedBoard(BoardPanel boardPanel) {
        savedSquares = new ArrayList<>();
        boardPanel.forEachBtn(btn -> savedSquares.add(new SavedSquare(btn)));
        assert savedSquares.stream().noneMatch(Objects::isNull);
    }

    /**
     * Disable all.
     */
    public void disableAll() {
        savedSquares.stream().parallel().forEach(sq -> sq.setEnabled(false));
    }

    /**
     * Saved square.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class SavedSquare {
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
}
