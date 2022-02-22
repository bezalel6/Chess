package ver8.view.Dialog.Dialogs.LoginProcess.Cards;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.LoginType;
import ver8.view.Dialog.Cards.CardHeader;
import ver8.view.Dialog.Dialog;

public class CancelAndExit extends LoginCard {
    public CancelAndExit(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Cancel & Exit"), parentDialog, loginInfo, LoginType.CANCEL);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
