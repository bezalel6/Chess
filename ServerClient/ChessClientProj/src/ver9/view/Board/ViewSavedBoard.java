package ver9.view.Board;

import ver9.SharedClasses.Location;
import ver9.SharedClasses.board_setup.Board;
import ver9.SharedClasses.pieces.Piece;

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
        private final ArrayList<BoardButton.State> btnStates;
        private final Location loc;
        private final Piece piece;
        private boolean isEnabled;

        public SavedSquare(BoardButton btn) {
            this.btnStates = new ArrayList<>(btn.getBtnStates());
            this.loc = btn.getBtnLoc();
            this.piece = btn.getPiece();
            this.isEnabled = btn.isEnabled();
        }


        public Location getLoc() {
            return loc;
        }

        public void setEnabled(boolean enabled) {
            isEnabled = enabled;
        }

        public void restore(BoardButton btn) {
            btn.setPiece(piece);
            btn.setStates(btnStates);
            btn.setEnabled(isEnabled);
        }
    }
}
