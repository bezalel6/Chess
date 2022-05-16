package ver14.SharedClasses.UI.MyJframe;

import javax.swing.*;

/**
 * represents a user input confirmation for closing a window.
 *
 * @param <T> the input type
 */
public interface Closing<T> {
    /**
     * The constant title.
     */
    String title = "Exit Confirmation";
    /**
     * The constant header.
     */
    String header = "Are You Sure You Want To Exit?";
    /**
     * The constant icon.
     */
    ImageIcon icon = null;

    /**
     * show the input dialog and if the input verifies, close the window.
     */
    default void tryClose() {
        T t = show();
        if (checkClosingVal(t))
            closing(t);
    }

    /**
     * Show the input dialog.
     *
     * @return the input value
     */
    T show();

    /**
     * check an input value supplied by the user.
     *
     * @param val the value
     * @return <code>true</code> if the window can close
     */
    boolean checkClosingVal(T val);

    /**
     * close the window.
     *
     * @param val the value the user submitted in the input dialog
     */
    void closing(T val);
}
