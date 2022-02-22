package ver8.view.Wishfull.List;

import ver8.view.Wishfull.dialogs.Selectable;

import javax.swing.*;
import java.awt.*;

public class MenuItem extends JMenuItem implements ListItem {

    public MenuItem(Selectable selectable) {
        super(selectable.getText());
    }

    @Override
    public Component comp() {
        return this;
    }
}
