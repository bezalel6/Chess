package ver6.view.List;

import ver6.SharedClasses.ui.MyJButton;
import ver6.view.dialogs.Header;
import ver6.view.dialogs.MyDialog;
import ver6.view.dialogs.SelectedCallBack;
import ver6.view.dialogs.WinPnl;

import javax.swing.*;

public abstract class DialogComponentsList extends ComponentsList {
    protected final WinPnl container;
    protected final MyDialog parentDialog;
    private final MyJButton navBtn;

    public DialogComponentsList(ListType listType, String header, MyDialog parentDialog, boolean isList, SelectedCallBack onSelect) {
        super(listType, onSelect, header);
        this.parentDialog = parentDialog;
        this.navBtn = new MyJButton(header);
        this.container = new WinPnl(isList ? 1 : 3, new Header(header), true);

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
