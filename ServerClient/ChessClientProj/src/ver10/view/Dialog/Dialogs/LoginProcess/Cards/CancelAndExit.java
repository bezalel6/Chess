package ver10.view.Dialog.Dialogs.LoginProcess.Cards;

import ver10.view.Dialog.Cards.CardHeader;
import ver10.view.Dialog.Dialog;
import ver10.SharedClasses.LoginInfo;
import ver10.SharedClasses.LoginType;

public class CancelAndExit extends LoginCard {
    public CancelAndExit(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Cancel & Exit"), parentDialog, loginInfo, LoginType.CANCEL);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
