package ver12.view.Dialog.Dialogs.LoginProcess.Cards;

import ver12.SharedClasses.LoginInfo;
import ver12.SharedClasses.LoginType;
import ver12.view.Dialog.Cards.CardHeader;
import ver12.view.Dialog.Dialog;

public class Guest extends LoginCard {
    public Guest(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Guest"), parentDialog, loginInfo, LoginType.GUEST);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
