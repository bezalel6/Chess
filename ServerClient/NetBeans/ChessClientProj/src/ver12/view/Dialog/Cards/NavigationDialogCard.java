package ver12.view.Dialog.Cards;

import ver12.view.Dialog.Dialog;

public class NavigationDialogCard extends DialogCard {
    public NavigationDialogCard(CardHeader cardHeader, Dialog parentDialog, DialogCard... linkTo) {
        super(cardHeader, parentDialog, null);
        for (DialogCard card : linkTo) {
            addNavigationTo(card);
        }
    }
}