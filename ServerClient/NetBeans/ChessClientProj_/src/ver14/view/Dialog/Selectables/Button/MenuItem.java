package ver14.view.Dialog.Selectables.Button;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.UI.FontManager;
import ver14.view.Dialog.Selectables.Selectable;

import javax.swing.*;
import java.awt.*;

/**
 * represents a Menu item with a selectable value.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MenuItem extends JMenuItem implements SelectableBtn {
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
     * Instantiates a new Menu item.
     *
     * @param value    the value
     * @param onSelect the on select
     */
    public MenuItem(Selectable value, Callback<Selectable> onSelect) {
//        setVerticalTextPosition(SwingConstants.TOP);
//        setHorizontalTextPosition(SwingConstants.CENTER);

        setText(value.getText());
        setIcon(value.getIcon());
        setFont(FontManager.small);
        this.value = value;

        addActionListener(e -> {

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
    public void select(boolean select) {
        if (select) {
            select();
        } else {
            unselect();
        }
    }

    /**
     * Select.
     */
    public void select() {
        selected = true;
        setBackground(selectedClr);
    }

    /**
     * Unselect.
     */
    public void unselect() {
        selected = false;
        setBackground(normalClr);
    }
}
