package ver14.view.Dialog.Dialogs.LoginProcess.Cards;

import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.LoginType;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialog;

public class CancelAndExit extends LoginCard {
    public CancelAndExit(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Cancel & Exit"), parentDialog, loginInfo, LoginType.CANCEL);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
