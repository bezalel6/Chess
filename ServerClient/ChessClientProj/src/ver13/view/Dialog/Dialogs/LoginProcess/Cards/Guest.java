package ver13.view.Dialog.Dialogs.LoginProcess.Cards;

import ver13.SharedClasses.LoginInfo;
import ver13.SharedClasses.LoginType;
import ver13.view.Dialog.Cards.CardHeader;
import ver13.view.Dialog.Dialog;

public class Guest extends LoginCard {
    public Guest(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Guest"), parentDialog, loginInfo, LoginType.GUEST);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
