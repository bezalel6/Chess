package ver6.view.dialogs.Navigation;

import ver6.SharedClasses.FontManager;
import ver6.SharedClasses.ui.MyJButton;

import javax.swing.*;

public class BackOkPnl extends JPanel {
    private final MyJButton ok;
    private final MyJButton back;

    public BackOkPnl(BackOkInterface backOkInterface) {
        ok = new MyJButton("Ok", FontManager.backOk, backOkInterface::onOk) {{
            setEnabled(false);
        }};
        back = new MyJButton("Back", FontManager.backOk, backOkInterface::onBack);
        add(back);
        add(ok);
    }

    public void enableOk(boolean enable) {
        ok.setEnabled(enable);
    }
}
