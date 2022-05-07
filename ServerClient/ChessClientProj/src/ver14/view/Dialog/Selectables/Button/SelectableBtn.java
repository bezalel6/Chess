package ver14.view.Dialog.Selectables.Button;

import ver14.view.Dialog.Selectables.Selectable;

import java.awt.*;

/**
 * represents a button with a {@link Selectable} value.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface SelectableBtn {
    /**
     * Gets the selectable value of this button.
     *
     * @return the selectable value of this button.
     */
    Selectable getValue();

    /**
     * set the selection state of this button.
     *
     * @param select true to select this button and false to unselect it
     */
    void select(boolean select);

    /**
     * get the visual {@link Component} of this button.
     *
     * @return the component
     */
    default Component comp() {
        return this instanceof Component c?c :null;
    }
}
