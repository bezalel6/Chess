package ver12.view.Dialog.Dialogs.LoginProcess.Cards;

import ver12.SharedClasses.LoginInfo;
import ver12.SharedClasses.LoginType;
import ver12.view.Dialog.Cards.CardHeader;
import ver12.view.Dialog.Dialog;

public class CancelAndExit extends LoginCard {
    public CancelAndExit(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Cancel & Exit"), parentDialog, loginInfo, LoginType.CANCEL);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
