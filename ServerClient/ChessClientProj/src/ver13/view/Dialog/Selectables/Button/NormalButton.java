package ver13.view.Dialog.Selectables.Button;

import ver13.SharedClasses.Callbacks.Callback;
import ver13.SharedClasses.FontManager;
import ver13.SharedClasses.ui.MyJButton;
import ver13.view.Dialog.Selectables.Selectable;

import javax.swing.*;
import java.awt.*;

public class NormalButton extends MyJButton implements SelectableBtn {
    private final static Color normalClr = Color.WHITE;
    private final static Color selectedClr = Color.orange;
    private final Selectable value;
    private boolean selected = false;

    public NormalButton(Selectable value, Callback<Selectable> onSelect) {
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

    @Override
    public Selectable getValue() {
        return value;
    }

    @Override
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
