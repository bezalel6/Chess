package ver10.view.Dialog.Dialogs;

import ver10.SharedClasses.FontManager;
import ver10.SharedClasses.ui.MyJButton;

import javax.swing.*;

public class BackOkPnl extends JPanel {
    private final MyJButton ok;
    private final MyJButton back;

    public BackOkPnl(BackOkInterface backOkInterface) {
        ok = new MyJButton(backOkInterface.getOkText(), FontManager.backOk, backOkInterface::onOk) {{
            setEnabled(false);
        }};
        back = new MyJButton(backOkInterface.getBackText(), FontManager.backOk, backOkInterface::onBack);
        add(back);
        add(ok);

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
