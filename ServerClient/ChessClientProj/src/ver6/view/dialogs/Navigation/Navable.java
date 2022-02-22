package ver6.view.dialogs.Navigation;

import javax.swing.*;

public interface Navable {

    default void onNav() {

    }

    AbstractButton navigationComp();

    default void updateNavText() {
        setNavText(getNavText());
    }

    String getNavText();

    void setNavText(String str);

}
