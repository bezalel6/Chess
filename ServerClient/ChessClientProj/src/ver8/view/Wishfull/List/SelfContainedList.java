package ver8.view.Wishfull.List;

import ver8.SharedClasses.ui.MyJButton;
import ver8.view.ErrorPnl;
import ver8.view.Wishfull.dialogs.Header;
import ver8.view.Wishfull.dialogs.Navigation.BackOkInterface;
import ver8.view.Wishfull.dialogs.SelectedCallBack;
import ver8.view.Wishfull.dialogs.WinPnl;

import javax.swing.*;

public abstract class SelfContainedList extends ComponentsList implements BackOkInterface {
    protected final WinPnl container;
    protected final WinPnl inner;
    protected final ErrorPnl errorPnl;

    private final MyJButton navBtn;

    public SelfContainedList(ListType listType, SelectedCallBack onSelect, String header, boolean isVertical, Parent parent) {
        super(listType, onSelect, header);
        this.navBtn = new MyJButton(header, this::onNav);

        errorPnl = new ErrorPnl();
        container = new WinPnl(header, true) {
            {
                initBackOk(SelfContainedList.this);
            }

            @Override
            public void removeAllListItems() {
                inner.removeAllListItems();
            }
        };
        container.addToTopPnl(errorPnl, true);
        inner = new WinPnl(isVertical ? 1 : 3, new Header(""), true);
        container.add(inner, isVertical);

        setParent(parent);
    }

    @Override
    public AbstractButton navigationComp() {
        return navBtn;
    }
//
//    @Override
//    public void onNav() {
//        super.onNav();
//        parent.navigateTo(container);
//    }

    @Override
    public void setNavText(String str) {
        navBtn.setText(str);
    }

    @Override
    public WinPnl navTo() {
        return container;
    }

    @Override
    public boolean verify() {
        return super.verify() && isAnySelected();
    }

    @Override
    public boolean isVertical() {
        return true;
    }

    @Override
    public Container listContainer() {
        return inner;
    }

    protected void doneAdding() {

        container.doneAdding();
    }

    @Override
    public void onBack() {
        parent.switchToStart();
    }
}
