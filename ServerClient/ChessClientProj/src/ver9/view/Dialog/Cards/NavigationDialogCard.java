package ver9.view.Dialog.Cards;

import ver9.view.Dialog.Dialog;

public class NavigationDialogCard extends DialogCard {
    public NavigationDialogCard(CardHeader cardHeader, Dialog parentDialog, DialogCard... linkTo) {
        super(cardHeader, parentDialog, false);
        for (DialogCard card : linkTo) {
            addNavigationTo(card);
        }
    }
}
