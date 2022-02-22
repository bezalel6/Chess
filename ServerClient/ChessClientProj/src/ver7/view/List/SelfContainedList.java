package ver7.view.List;

import ver7.SharedClasses.ui.MyJButton;
import ver7.view.ErrorPnl;
import ver7.view.dialogs.Header;
import ver7.view.dialogs.Navigation.BackOkInterface;
import ver7.view.dialogs.SelectedCallBack;
import ver7.view.dialogs.WinPnl;

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

    @Override
    public boolean checkVerification() {
        return errorPnl != null && errorPnl.verify(this);
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
