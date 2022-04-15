package ver14.view.Dialog.Dialogs.LoginProcess.Cards;

import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.LoginType;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.IconManager.Size;

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


    @Override
    public Size getPreferredSize() {
        return new Size(300);
    }
}
