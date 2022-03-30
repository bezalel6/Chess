package ver14.view.Dialog.Dialogs;

import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.ui.MyJButton;

import javax.swing.*;

public class BackOkPnl extends JPanel {
    private MyJButton ok;
    private MyJButton back;

    public BackOkPnl(BackOkInterface backOk) {
        if (backOk.getBackText() != null) {
            back = new MyJButton(StrUtils.uppercase(backOk.getBackText()), FontManager.Dialogs.dialog, backOk::onBack);
            add(back);
        }
        if (backOk.getOkText() != null) {
            ok = new MyJButton(StrUtils.uppercase(backOk.getOkText()), FontManager.Dialogs.dialog, backOk::onOk) {{
                setEnabled(false);
            }};
            add(ok);
        }


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
