package ver14.view.Dialog.Cards;

import ver14.view.Dialog.Dialog;

public class NavigationCard extends DialogCard {
    public NavigationCard(CardHeader cardHeader, Dialog parentDialog, DialogCard... linkTo) {
        super(cardHeader, parentDialog, null);
        for (DialogCard card : linkTo) {
            addNavigationTo(card);
        }
    }
}
