package ver14.view.Dialog.BackOk;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;

/**
 * represents a navigation panel for back ok navigating.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class BackOkPnl extends JPanel {
    /**
     * The Ok.
     */
    private MyJButton ok;
    /**
     * The Back.
     */
    private MyJButton back;

    /**
     * Instantiates a new Back ok pnl.
     *
     * @param backOk the back ok
     */
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

    /**
     * Create btn my j button.
     *
     * @param str     the str
     * @param onClick the on click
     * @return the my j button
     */
    public static MyJButton createBtn(String str, VoidCallback onClick) {
        return new MyJButton(StrUtils.uppercase(str), FontManager.Dialogs.dialog, onClick) {{
            setFocusable(true);
        }};
    }

    /**
     * Gets ok.
     *
     * @return the ok
     */
    public MyJButton getOk() {
        return ok;
    }

    /**
     * Gets back.
     *
     * @return the back
     */
    public MyJButton getBack() {
        return back;
    }

    /**
     * Enable ok.
     *
     * @param enable the enable
     */
    public void enableOk(boolean enable) {
        if (ok != null)
            ok.setEnabled(enable);
//        if (enable)
//            ok.requestFocus();
    }

    /**
     * Enable back.
     *
     * @param enable the enable
     */
    public void enableBack(boolean enable) {
        if (back != null)
            back.setEnabled(enable);
    }

}
