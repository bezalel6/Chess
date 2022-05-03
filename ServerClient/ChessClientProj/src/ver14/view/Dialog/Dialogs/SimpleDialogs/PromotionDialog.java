package ver14.view.Dialog.Dialogs.SimpleDialogs;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.view.Dialog.Components.ListComponent;
import ver14.view.Dialog.DialogProperties;
import ver14.view.Dialog.Selectables.SelectablePiece;
import ver14.view.Dialog.WinPnl;

/**
 * Promotion dialog.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class PromotionDialog extends SimpleDialog {
    /**
     * The List.
     */
    private final PromotionList list;

    /**
     * Instantiates a new Promotion dialog.
     *
     * @param playerColor the player color
     */
    public PromotionDialog(PlayerColor playerColor) {
        super(new DialogProperties(new DialogProperties.DialogDetails("promotion", "promotion")));
//        MyJFrame.debugAdapter(this);
        list = new PromotionList(playerColor);
        delayedSetup(null, list);
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


    /**
     * Promotion list.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public class PromotionList extends ListComponent {

        /**
         * Instantiates a new Promotion list.
         *
         * @param playerColor the player color
         */
        public PromotionList(PlayerColor playerColor) {
            super(WinPnl.ALL_IN_ONE_ROW, null, PromotionDialog.this);
            addComponents(SelectablePiece.promotionPieces[playerColor.asInt]);
        }

        @Override
        protected void onSelected() {
            closeDialog();
        }
    }
}
