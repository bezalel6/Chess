package ver27_minimax_levels.view_classes.dialogs_classes.dialog_objects;

import javax.swing.*;
import java.awt.*;

public class DialogInput extends JFormattedTextField implements DialogObject {
    public DialogInput() {
        setFont(font);
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public Object getKey() {
        return getText();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
