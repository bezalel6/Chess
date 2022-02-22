package ver10.view.Dialog.Dialogs.LoginProcess.Cards;

import ver10.view.Dialog.Cards.CardHeader;
import ver10.view.Dialog.Cards.DialogCard;
import ver10.view.Dialog.Dialog;
import ver10.SharedClasses.LoginInfo;
import ver10.SharedClasses.LoginType;

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
