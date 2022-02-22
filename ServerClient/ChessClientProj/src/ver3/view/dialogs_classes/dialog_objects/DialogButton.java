package ver3.view.dialogs_classes.dialog_objects;


import ver3.view.MyJButton;

import javax.swing.*;
import java.awt.*;


public class DialogButton extends MyJButton implements DialogObject {
    private ImageIcon icon;
    private Object key;

    public DialogButton(Object key, String text, ImageIcon icon) {
        super(text);
        this.key = key;
        this.icon = icon;
        setFont(font);
    }

    public DialogButton(Object key, ImageIcon icon) {
        this(key, "", icon);
    }

    public DialogButton(Object key, String text) {
        this(key, text, null);
    }


    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
