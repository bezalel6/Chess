package ver6.view.List;

import ver6.view.dialogs.Selectable;

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