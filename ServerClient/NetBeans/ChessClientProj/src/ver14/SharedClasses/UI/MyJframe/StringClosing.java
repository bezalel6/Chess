package ver14.SharedClasses.UI.MyJframe;

import ver14.SharedClasses.UI.dialogs.ConfirmDialogs;

/**
 * represents a string input for closing a window.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface StringClosing extends Closing<String> {

    /**
     * Show input dialog.
     *
     * @return the input
     */
    default String show() {
        return ConfirmDialogs.confirm(null, title, header, "Enter closing reason", icon, initialValue());
    }

    /**
     * Check if the input should close the window.
     *
     * @param val the input value
     * @return <code>true</code> if the window should close
     */
    @Override
    default boolean checkClosingVal(String val) {
        return val != null;
    }

    /**
     * the initial value of the input field.
     *
     * @return the initial value of the input field
     */
    String initialValue();
}
