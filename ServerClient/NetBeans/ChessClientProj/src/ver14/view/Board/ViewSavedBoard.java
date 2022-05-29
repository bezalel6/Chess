package ver14.view.Board;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;

import java.util.ArrayList;
import java.util.Objects;

/**
 * represents a capture of a view board's state. used for saving positions and scrolling through game logs.
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

    @Override
    public String toString() {
        return super.toString();
    }
}
