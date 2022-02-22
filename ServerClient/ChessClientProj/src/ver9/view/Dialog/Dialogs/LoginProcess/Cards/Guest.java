package ver9.view.Dialog.Dialogs.LoginProcess.Cards;

import ver9.SharedClasses.LoginInfo;
import ver9.SharedClasses.LoginType;
import ver9.view.Dialog.Cards.CardHeader;
import ver9.view.Dialog.Dialog;

public class Guest extends LoginCard {
    public Guest(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Guest"), parentDialog, loginInfo, LoginType.GUEST);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
