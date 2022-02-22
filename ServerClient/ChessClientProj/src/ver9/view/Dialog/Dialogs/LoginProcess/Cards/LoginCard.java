package ver9.view.Dialog.Dialogs.LoginProcess.Cards;

import ver9.SharedClasses.LoginInfo;
import ver9.SharedClasses.LoginType;
import ver9.view.Dialog.Cards.CardHeader;
import ver9.view.Dialog.Cards.DialogCard;
import ver9.view.Dialog.Dialog;

public abstract class LoginCard extends DialogCard {
    protected final LoginInfo loginInfo;
    protected final LoginType loginType;

    public LoginCard(CardHeader cardHeader, Dialog parentDialog, LoginInfo loginInfo, LoginType loginType) {
        super(cardHeader, parentDialog);
        this.loginInfo = loginInfo;
        this.loginType = loginType;
    }

    @Override
    public void onOk() {
        loginInfo.setLoginType(loginType);
        super.onOk();
    }
}
