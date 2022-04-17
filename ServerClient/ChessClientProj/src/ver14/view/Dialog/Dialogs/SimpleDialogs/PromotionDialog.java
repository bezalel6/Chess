package ver14.view.Dialog.Dialogs.SimpleDialogs;

import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.pieces.Piece;
import ver14.view.Dialog.Components.ListComponent;
import ver14.view.Dialog.Dialogs.DialogProperties.Properties;
import ver14.view.Dialog.Selectables.SelectablePiece;
import ver14.view.Dialog.WinPnl;

public class PromotionDialog extends SimpleDialog {
    private final PromotionList list;

    public PromotionDialog(PlayerColor playerColor) {
        super(new Properties(new Properties.Details("promotion", "promotion")));
        list = new PromotionList(playerColor);
        delayedSetup(null, list);
    }

    public Piece getResult() {
        return ((SelectablePiece) list.getResult()).piece();
    }


    public class PromotionList extends ListComponent {

        public PromotionList(PlayerColor playerColor) {
            super(WinPnl.ALL_IN_ONE_ROW, null, PromotionDialog.this);
            addComponents(SelectablePiece.promotionPieces[playerColor.asInt]);
        }

        @Override
        protected void onSelected() {
            tryOk(false);
        }
    }
}
