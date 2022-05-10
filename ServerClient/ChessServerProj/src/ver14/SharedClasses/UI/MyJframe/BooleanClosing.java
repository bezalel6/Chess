package ver14.SharedClasses.UI.MyJframe;

import ver14.SharedClasses.UI.dialogs.ConfirmDialogs;

/**
 * represents a boolean input closing dialog. with yes\no options
 */
public interface BooleanClosing extends Closing<Boolean> {

    /**
     * Show the input dialog.
     *
     * @return <code>true</code> if the user selected yes
     */
    default Boolean show() {
        return ConfirmDialogs.confirm(null, title, header, icon);
    }


    @Override
    default boolean checkClosingVal(Boolean val) {
        return val;
    }

    @Override
    default void closing(Boolean val) {
        if (val)
            closing();
    }

    /**
     * the user selected 'yes' and the window should close
     */
    void closing();
}
