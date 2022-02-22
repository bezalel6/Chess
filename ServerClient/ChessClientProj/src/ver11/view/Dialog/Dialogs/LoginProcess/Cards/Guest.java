package ver11.view.Dialog.Dialogs.LoginProcess.Cards;

import ver11.SharedClasses.LoginInfo;
import ver11.SharedClasses.LoginType;
import ver11.view.Dialog.Cards.CardHeader;
import ver11.view.Dialog.Dialog;

public class Guest extends LoginCard {
    public Guest(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Guest"), parentDialog, loginInfo, LoginType.GUEST);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
