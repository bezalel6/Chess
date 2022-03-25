package ver14.view.Dialog.Selectables.Button;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.ui.MyJButton;
import ver14.view.Dialog.Selectables.Selectable;

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
        setFont(FontManager.Dialogs.selectableBtn);
        this.value = value;
        setOnClick(() -> {
            onSelect.callback(selected ? value : null);
        });
    }

    @Override
    public Selectable getValue() {
        return value;
    }

    @Override
    public void setSelected(boolean e) {
//        super.setSelected(e);
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
