package ver8.view.Wishfull.dialogs;

import ver8.view.Wishfull.List.ComponentsList;
import ver8.view.Wishfull.dialogs.Navigation.Navable;

public interface DialogComponent extends Navable, VerifiedComponent {
    @Override
    default void onNav() {
        parent().navigateTo(this);
    }

    @Override
    default WinPnl navTo() {
        return (WinPnl) this;
    }

    ComponentsList.Parent parent();
}

