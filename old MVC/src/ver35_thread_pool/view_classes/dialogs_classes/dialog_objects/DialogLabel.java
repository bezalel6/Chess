package ver35_thread_pool.view_classes.dialogs_classes.dialog_objects;

import javax.swing.*;
import java.awt.*;

public class DialogLabel extends JLabel implements DialogObject {

    private ImageIcon icon;

    public DialogLabel(String text, ImageIcon icon) {
        super(text);
        setIcon(icon);
        setFont(font);

    }

    public DialogLabel(ImageIcon icon) {
        this("", icon);
    }


    public DialogLabel(String text) {
        this(text, null);
    }

    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public Object getKey() {
        return null;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
