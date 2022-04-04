package ver14.view.Dialog.Dialogs;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.ui.MyJButton;

import javax.swing.*;

public class BackOkPnl extends JPanel {
    private MyJButton ok;
    private MyJButton back;

    public BackOkPnl(BackOkInterface backOk) {
        if (backOk.getBackText() != null) {
            back = createBtn(backOk.getBackText(), backOk::onBack);
            add(back);
        }
        if (backOk.getOkText() != null) {
            ok = createBtn(StrUtils.uppercase(backOk.getOkText()), backOk::onOk);
            ok.setEnabled(false);
            add(ok);
        }


    }

    public static MyJButton createBtn(String str, VoidCallback onClick) {
        return new MyJButton(StrUtils.uppercase(str), FontManager.Dialogs.dialog, onClick) {{
            setFocusable(true);
        }};
    }

    public MyJButton getOk() {
        return ok;
    }

    public MyJButton getBack() {
        return back;
    }

    public void enableOk(boolean enable) {
        ok.setEnabled(enable);
//        if (enable)
//            ok.requestFocus();
    }

    public void enableBack(boolean enable) {
        back.setEnabled(enable);
    }

}
