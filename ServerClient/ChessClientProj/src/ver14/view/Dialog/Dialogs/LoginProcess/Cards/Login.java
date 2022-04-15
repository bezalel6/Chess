package ver14.view.Dialog.Dialogs.LoginProcess.Cards;

import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.LoginType;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
import ver14.view.Dialog.Dialogs.LoginProcess.LoginProcess;
import ver14.view.IconManager.Size;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Set;

public class Login extends LoginCard {


    private ArrayList<Set<Integer>> removeAdapters = new ArrayList<>();

    public Login(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Login"), parentDialog, loginInfo, LoginType.LOGIN);

        addDialogComponent(new UsernamePnl(false, loginInfo, parentDialog));
        addDialogComponent(new PasswordPnl(false, loginInfo, parentDialog));
    }

    @Override
    public void shown() {
        super.shown();
        removeAdapters.add(parentDialog.keyAdapter().addAction(() -> {
            ((LoginProcess) parentDialog).getLoginInfo().initDebugLoginValues();
            onOk();
        }, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_F));
    }

    @Override
    public void onBack() {
        removeAdapters.forEach(parentDialog.keyAdapter()::removeAction);
        super.onBack();
    }

    @Override
    public Size getPreferredSize() {
        return new Size(300);
    }
}
