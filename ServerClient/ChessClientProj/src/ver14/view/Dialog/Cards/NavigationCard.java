package ver14.view.Dialog.Cards;

import ver14.view.Dialog.Dialog;
import ver14.view.IconManager.Size;

import java.awt.*;

public class NavigationCard extends DialogCard {
    public NavigationCard(CardHeader cardHeader, Dialog parentDialog, DialogCard... linkTo) {
        super(cardHeader, parentDialog, null);
        for (DialogCard card : linkTo) {
            addNavigationTo(card);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return Size.max(super.getPreferredSize());
    }
}
