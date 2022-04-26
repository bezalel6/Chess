package ver14.view.Dialog.Dialogs.LoginProcess.Cards;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Login.LoginType;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialog;

public class Guest extends LoginCard {
    public Guest(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Guest"), parentDialog, loginInfo, LoginType.GUEST);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
