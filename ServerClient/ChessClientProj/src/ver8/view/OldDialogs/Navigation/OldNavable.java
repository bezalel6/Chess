package ver8.view.OldDialogs.Navigation;

import ver8.SharedClasses.ui.MyJButton;
import ver8.view.OldDialogs.MyDialog;
import ver8.view.OldDialogs.WinPnl;

import java.awt.*;

public class OldNavable extends WinPnl {
    private final MyDialog parentDialog;
    private final String navText;
    private MyJButton navBtn = null;

    public OldNavable(String header, MyDialog parentDialog, String navText) {
        super(header);
        this.parentDialog = parentDialog;
        this.navText = navText;
    }

    public Component navigationComp() {
        return navBtn;
    }

    @Override
    public void doneAdding() {
        super.doneAdding();
        navBtn = new MyJButton("", () -> parentDialog.switchTo(this));
        resetNavBtn("");
    }

    public void resetNavBtn(String adding) {
        navBtn.setText(navText + adding);
    }
}
