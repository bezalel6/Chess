package ver14.view.Dialog.Selectables.Button;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.SharedClasses.UI.FontManager;
import ver14.view.Dialog.Selectables.Selectable;

import javax.swing.*;
import java.awt.*;

/**
 * represents a Normal selectable button.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class NormalButton extends MyJButton implements SelectableBtn {
    /**
     * The constant normalClr.
     */
    private final static Color normalClr = Color.WHITE;
    /**
     * The constant selectedClr.
     */
    private final static Color selectedClr = Color.orange;
    /**
     * The Value.
     */
    private final Selectable value;
    /**
     * The Selected.
     */
    private boolean selected = false;

    /**
     * Instantiates a new Normal button.
     *
     * @param value    the value
     * @param onSelect the on select
     */
    public NormalButton(Selectable value, Callback<Selectable> onSelect) {
        setVerticalTextPosition(SwingConstants.TOP);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setText(value.getText());
        setIcon(value.getIcon());
        setFont(FontManager.Dialogs.selectableBtn);
        this.value = value;
        setOnClick(() -> {
            selected = !selected;
            onSelect.callback(selected ? value : null);
        });
    }

    @Override
    public Selectable getValue() {
        return value;
    }

    public void select(boolean select) {
//        super.setSelected(e);
        if (select) {
            select();
        } else {
            unselect();
        }
    }

    /**
     * Select this button.
     */
    public void select() {
        selected = true;
        setBackground(selectedClr);
    }

    /**
     * Unselect this button.
     */
    public void unselect() {
        selected = false;
        setBackground(normalClr);
    }
}
