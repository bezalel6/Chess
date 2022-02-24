package ver13.view.Dialog.Dialogs.LoginProcess.Cards;

import ver13.SharedClasses.LoginInfo;
import ver13.SharedClasses.LoginType;
import ver13.view.Dialog.Cards.CardHeader;
import ver13.view.Dialog.Dialog;

public class CancelAndExit extends LoginCard {
    public CancelAndExit(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Cancel & Exit"), parentDialog, loginInfo, LoginType.CANCEL);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
