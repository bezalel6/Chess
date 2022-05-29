package ver14.view.Dialog.Cards;

import ver14.view.Dialog.Dialog;

/**
 * Navigation card - used for creating dialog cards that are just navigation cards to other cards.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class NavigationCard extends DialogCard {
    /**
     * Instantiates a new Navigation card.
     *
     * @param cardHeader   the card header
     * @param parentDialog the parent dialog
     * @param linkTo       the link to
     */
    public NavigationCard(CardHeader cardHeader, Dialog parentDialog, DialogCard... linkTo) {
        super(cardHeader, parentDialog, null);
        for (DialogCard card : linkTo) {
            addNavigationTo(card);
        }
    }
}
