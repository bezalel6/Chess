package ver14.view.Board;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;

import java.util.ArrayList;
import java.util.Objects;

public class ViewSavedBoard extends Board {

    public final ArrayList<SavedSquare> savedSquares;

    public ViewSavedBoard(BoardPanel boardPanel) {
        savedSquares = new ArrayList<>();
        boardPanel.forEachBtn(btn -> savedSquares.add(new SavedSquare(btn)));
        assert savedSquares.stream().noneMatch(Objects::isNull);
    }

    public void disableAll() {
        savedSquares.stream().parallel().forEach(sq -> sq.setEnabled(false));
    }

    public static class SavedSquare {
        private final int btnState;
        private final ViewLocation loc;
        private final Piece piece;
        private boolean isEnabled;

        public SavedSquare(BoardButton btn) {
            this.btnState = btn.getBtnState();
            this.loc = btn.getBtnLoc();
            this.piece = btn.getPiece();
            this.isEnabled = btn.isEnabled();
        }


        public ViewLocation getLoc() {
            return loc;
        }

        public void setEnabled(boolean enabled) {
            isEnabled = enabled;
        }

        public void restore(BoardButton btn) {
            btn.setPiece(piece);
            btn.setStates(btnState);
            btn.setEnabled(isEnabled);
        }
    }
}
