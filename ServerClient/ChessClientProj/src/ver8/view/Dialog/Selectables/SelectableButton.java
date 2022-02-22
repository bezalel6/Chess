package ver8.view.Dialog.Selectables;

import ver8.SharedClasses.Callbacks.Callback;
import ver8.SharedClasses.FontManager;
import ver8.SharedClasses.ui.MyJButton;

import javax.swing.*;
import java.awt.*;

public class SelectableButton extends MyJButton {
    private final static Color normalClr = Color.WHITE;
    private final static Color selectedClr = Color.orange;
    private final Selectable value;
    private boolean selected = false;

    public SelectableButton(Selectable value, Callback<Selectable> onSelect) {
        setVerticalTextPosition(SwingConstants.TOP);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setText(value.getText());
        setIcon(value.getIcon());
        setFont(FontManager.defaultSelectableBtn);
        this.value = value;
        setOnClick(() -> {
            if (selected) {
                onSelect.callback(null);
            } else {
                onSelect.callback(value);
            }
        });
    }

    public Selectable getValue() {
        return value;
    }

    public void setSelected(boolean e) {
        if (e) {
            select();
        } else {
            unselect();
        }
    }

    public void select() {
        selected = true;
        setBackground(selectedClr);
    }

    public void unselect() {
        selected = false;
        setBackground(normalClr);
    }
}
