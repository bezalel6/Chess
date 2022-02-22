package ver11.view.Dialog.Cards;

import ver11.view.Dialog.Dialog;

public class NavigationDialogCard extends DialogCard {
    public NavigationDialogCard(CardHeader cardHeader, Dialog parentDialog, DialogCard... linkTo) {
        super(cardHeader, parentDialog, null);
        for (DialogCard card : linkTo) {
            addNavigationTo(card);
        }
    }
}
