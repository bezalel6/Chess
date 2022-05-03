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
     * Gets value.
     *
     * @return the value
     */
    Selectable getValue();

    /**
     * Select.
     *
     * @param e the e
     */
    void select(boolean e);

    /**
     * Comp component.
     *
     * @return the component
     */
    default Component comp() {
        return (Component) this;
    }
}
