package ver10.view.Dialog.Dialogs.LoginProcess.Cards;

import ver10.view.Dialog.Cards.CardHeader;
import ver10.view.Dialog.Dialog;
import ver10.SharedClasses.LoginInfo;
import ver10.SharedClasses.LoginType;

public class Guest extends LoginCard {
    public Guest(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Guest"), parentDialog, loginInfo, LoginType.GUEST);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
