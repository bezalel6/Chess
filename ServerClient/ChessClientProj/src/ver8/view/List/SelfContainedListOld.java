package ver8.view.List;

import ver8.SharedClasses.ui.MyJButton;
import ver8.view.OldDialogs.Header;
import ver8.view.OldDialogs.Navigation.BackOkInterface;
import ver8.view.OldDialogs.SelectedCallBack;
import ver8.view.OldDialogs.VerifiedComponent;
import ver8.view.OldDialogs.WinPnl;

import javax.swing.*;

public abstract class SelfContainedListOld extends OldComponentsList implements BackOkInterface, VerifiedComponent {
    protected final WinPnl container;
    protected final WinPnl inner;
    private final MyJButton navBtn;

    public SelfContainedListOld(ListType listType, SelectedCallBack onSelect, String header, boolean isVertical, Parent parent) {
        super(listType, onSelect, header);
        this.navBtn = new MyJButton(header, this::onNav);
        container = new WinPnl(header, true) {
            {
                initBackOk(SelfContainedListOld.this);
            }

            @Override
            public void removeAllListItems() {
//                super.removeAllListItems();
                inner.removeAllListItems();
            }
        };
        inner = new WinPnl(isVertical ? 1 : 3, new Header("", true), true);
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

    @Override
    public void onOk() {

    }

    @Override
    public boolean verify() {
        return false;
    }
}
