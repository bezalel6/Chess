package ver8.view.List;

import ver8.SharedClasses.ui.MyJButton;
import ver8.view.OldDialogs.Header;
import ver8.view.OldDialogs.MyDialog;
import ver8.view.OldDialogs.SelectedCallBack;
import ver8.view.OldDialogs.WinPnl;

import javax.swing.*;

public abstract class DialogOldComponentsList extends OldComponentsList {
    protected final WinPnl container;
    protected final MyDialog parentDialog;
    private final MyJButton navBtn;

    public DialogOldComponentsList(ListType listType, String header, MyDialog parentDialog, boolean isList, SelectedCallBack onSelect) {
        super(listType, onSelect, header);
        this.parentDialog = parentDialog;
        this.navBtn = new MyJButton(header);
        this.container = new WinPnl(isList ? 1 : 3, new Header(header, true), true);

    }

    @Override
    public Container listContainer() {
        return container;
    }

    @Override
    protected void updated() {
        super.updated();
        parentDialog.repackWin();
    }

    @Override
    public void onNav() {
        parentDialog.switchTo(container);
    }

    @Override
    public AbstractButton navigationComp() {
        return navBtn;
    }

    @Override
    public void setNavText(String str) {
        navBtn.setText(str);
    }
}
