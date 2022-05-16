package ver14.view.Dialog.Dialogs.Promotion;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.view.Dialog.DialogDetails;
import ver14.view.Dialog.DialogProperties;
import ver14.view.Dialog.SimpleDialog;
import ver14.view.Dialog.Selectables.SelectablePiece;

/**
 * represents a Promotion dialog for choosing a piece to promote to.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Promotion extends SimpleDialog {
    /**
     * The List.
     */
    private final PromotionList list;

    /**
     * Instantiates a new Promotion dialog.
     *
     * @param playerColor the player color
     */
    public Promotion(PlayerColor playerColor) {
        super(new DialogProperties(new DialogDetails("promotion", "promotion")));
//        MyJFrame.debugAdapter(this);
        list = new PromotionList(this, playerColor);
        setup(null, list);
    }

//    /**
//     * The entry point of application.
//     *
//     * @param args the input arguments
//     */
//    public static void main(String[] args) {
//        new PromotionDialog(PlayerColor.WHITE).start();
//    }

    /**
     * Gets the dialog's result.
     *
     * @return the chosen piece type
     */
    public Piece getResult() {
        return ((SelectablePiece) list.getResult()).piece();
    }


}
