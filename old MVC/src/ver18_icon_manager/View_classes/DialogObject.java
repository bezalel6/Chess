package ver18_icon_manager.View_classes;

import javax.swing.*;

public class DialogObject {
    private String text;
    private ImageIcon icon;
    private int key;

    public DialogObject(String text, ImageIcon icon, int key) {
        this.text = text;
        this.icon = icon;
        this.key = key;
    }

    public DialogObject(ImageIcon icon, int key) {
        text = "";
        this.icon = icon;
        this.key = key;
    }

    public DialogObject(String text, int key) {
        icon = null;
        this.text = text;
        this.key = key;
    }

    public DialogObject(String text, ImageIcon icon) {
        this.text = text;
        this.icon = icon;
    }

    public int getKey() {
        return key;
    }


    public String getText() {
        return text;
    }


    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
}
