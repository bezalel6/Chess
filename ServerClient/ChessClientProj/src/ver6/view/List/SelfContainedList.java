package ver6.view.List;

import ver6.SharedClasses.ui.MyJButton;
import ver6.view.dialogs.Header;
import ver6.view.dialogs.Navigation.BackOkInterface;
import ver6.view.dialogs.SelectedCallBack;
import ver6.view.dialogs.WinPnl;

import javax.swing.*;

public abstract class SelfContainedList extends ComponentsList implements BackOkInterface {
    protected final WinPnl container;
    protected final WinPnl inner;
    private final MyJButton navBtn;

    public SelfContainedList(ListType listType, SelectedCallBack onSelect, String header, boolean isVertical, Parent parent) {
        super(listType, onSelect, header);
        this.navBtn = new MyJButton(header, this::onNav);

        container = new WinPnl(header, true) {
            {
                initBackOk(SelfContainedList.this);
            }

            @Override
            public void removeAllListItems() {
//                super.removeAllListItems();
                inner.removeAllListItems();
            }
        };
        inner = new WinPnl(isVertical ? 1 : 3, new Header(""), true);
        container.add(inner, isVertical);

        setParent(parent);


    }

    @Override
    public void onNav() {
        super.onNav();
        parent.navigateTo(container);
    }

    @Override
    public AbstractButton navigationComp() {
        return navBtn;
    }

    @Override
    public void setNavText(String str) {
        navBtn.setText(str);
    }

    protected void doneAdding() {

        container.doneAdding();
    }

    @Override
    public boolean isVertical() {
        return true;
    }

    @Override
    public Container listContainer() {
        return inner;
    }

    @Override
    public void onBack() {
        parent.switchToStart();
    }
}
