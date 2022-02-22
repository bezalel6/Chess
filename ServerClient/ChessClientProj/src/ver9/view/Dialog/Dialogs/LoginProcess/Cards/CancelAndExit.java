package ver9.view.Dialog.Dialogs.LoginProcess.Cards;

import ver9.SharedClasses.LoginInfo;
import ver9.SharedClasses.LoginType;
import ver9.view.Dialog.Cards.CardHeader;
import ver9.view.Dialog.Dialog;

public class CancelAndExit extends LoginCard {
    public CancelAndExit(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Cancel & Exit"), parentDialog, loginInfo, LoginType.CANCEL);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
