package ver8.view.Wishfull.dialogs.Navigation;

import ver8.SharedClasses.ui.MyJButton;
import ver8.view.Wishfull.dialogs.WinPnl;

import javax.swing.*;

public interface Navable {

    default void updateNavText() {
        setNavText(getNavText());
    }

    String getNavText();

    default AbstractButton navigationComp() {
        return new MyJButton(getNavText(), this::onNav);
    }

    default void onNav() {
        System.out.println("on default nav");
    }

    default void setNavText(String str) {
        navigationComp().setText(str);
    }

    WinPnl navTo();
}
