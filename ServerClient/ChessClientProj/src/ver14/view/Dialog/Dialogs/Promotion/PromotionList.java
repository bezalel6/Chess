package ver14.view.Dialog.Dialogs.Promotion;

import ver14.SharedClasses.Game.PlayerColor;
import ver14.view.Dialog.Components.ListComponent;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.SelectablePiece;
import ver14.view.Dialog.WinPnl;

/**
 * represents a list of all the pieces a player can promote to.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class PromotionList extends ListComponent {

    private final Promotion promotion;

    /**
     * Instantiates a new Promotion list.
     *
     * @param playerColor the color the pieces should be in.
     */
    public PromotionList(Promotion promotion, PlayerColor playerColor) {
        super(WinPnl.ALL_IN_ONE_ROW, new Header("Select a piece to promote to"), promotion);
        this.promotion = promotion;
        addComponents(SelectablePiece.promotionPieces[playerColor.asInt]);
    }

    @Override
    protected void onSelected() {
        promotion.closeDialog();
    }
}
