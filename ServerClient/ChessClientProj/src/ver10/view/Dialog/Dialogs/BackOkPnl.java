package ver10.view.Dialog.Dialogs;

import ver10.SharedClasses.FontManager;
import ver10.SharedClasses.Utils.StrUtils;
import ver10.SharedClasses.ui.MyJButton;

import javax.swing.*;

public class BackOkPnl extends JPanel {
    private MyJButton ok;
    private MyJButton back;

    public BackOkPnl(BackOkInterface backOk) {
        if (backOk.getBackText() != null) {
            back = new MyJButton(StrUtils.uppercase(backOk.getBackText()), FontManager.backOk, backOk::onBack);
            add(back);
        }
        if (backOk.getOkText() != null) {
            ok = new MyJButton(StrUtils.uppercase(backOk.getOkText()), FontManager.backOk, backOk::onOk) {{
                setEnabled(false);
            }};
            add(ok);
        }


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