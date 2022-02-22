package ver5.view.dialogs.game_select.selectable;

import ver5.SharedClasses.PlayerColor;
import ver5.SharedClasses.SavedGame;
import ver5.SharedClasses.board_setup.Board;
import ver5.view.GameIconsGenerator;
import ver5.view.dialogs.Selectable;

import javax.swing.*;
import java.util.Objects;

public class SelectableGame implements Selectable {
    public final SavedGame SavedGame;
    private final String txt;
    private ImageIcon icon = null;

    public SelectableGame(SavedGame savedGame) {
        this.SavedGame = savedGame;
        PlayerColor joiningPlayerColor = savedGame.gameSettings.getPlayerColor() == PlayerColor.NO_PLAYER ? PlayerColor.NO_PLAYER : savedGame.gameSettings.getPlayerColor().getOpponent();
        if (savedGame.gameSettings.getFen() == null)
            txt = "%s %s (%s)".formatted(savedGame.creatorUsername, joiningPlayerColor == PlayerColor.NO_PLAYER ? "Random" : joiningPlayerColor, savedGame.gameId);
        else {
            Board board = new Board();
            board.fenSetup(savedGame.gameSettings.getFen());
//            txt = board.toString();
            txt = "";
            icon = GameIconsGenerator.generate(board);
        }
    }

    public static SelectableGame[] createArr(SavedGame[] arr) {
        SelectableGame[] ret = new SelectableGame[arr.length];
        for (int i = 0; i < arr.length; i++) {
            SavedGame SavedGame = arr[i];
            ret[i] = new SelectableGame(SavedGame);
        }
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectableGame that = (SelectableGame) o;
        return Objects.equals(SavedGame, that.SavedGame) && Objects.equals(txt, that.txt);
    }

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public String getText() {
        return txt;
    }
}
