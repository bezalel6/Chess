package ver14.view.Dialog.Dialogs.SimpleDialogs;

import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.Arg.ArgType;
import ver14.SharedClasses.Utils.ArrUtils;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.DialogProperties;
import ver14.view.IconManager.Size;

import java.awt.*;


/**
 * represents a simple Input dialog.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class InputDialog extends CustomDialog {
    /**
     * Instantiates a new Input dialog.
     *
     * @param dialogProperties the properties
     */
    public InputDialog(DialogProperties dialogProperties) {
        this(dialogProperties, ArgType.Text);
    }

    /**
     * Instantiates a new Input dialog.
     *
     * @param dialogProperties set msg on properties for input directions
     * @param inputType        the input type
     */
    public InputDialog(DialogProperties dialogProperties, ArgType inputType) {
        super(dialogProperties, new Arg(inputType, dialogProperties.argConfig()));
    }

    /**
     * Gets input.
     *
     * @return the input
     */
    public String getInput() {
        String str = (String) ArrUtils.exists(getResults(), 0);
        if (StrUtils.isEmpty(str)) {
            return null;
        }
        return str;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Size(450);

    }

}
