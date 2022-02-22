package ver11.view.Dialog.Dialogs.LoginProcess.Cards;

import ver11.SharedClasses.LoginInfo;
import ver11.SharedClasses.LoginType;
import ver11.view.Dialog.Cards.CardHeader;
import ver11.view.Dialog.Dialog;

public class CancelAndExit extends LoginCard {
    public CancelAndExit(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Cancel & Exit"), parentDialog, loginInfo, LoginType.CANCEL);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
