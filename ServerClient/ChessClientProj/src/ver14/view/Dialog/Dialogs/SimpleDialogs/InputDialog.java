package ver14.view.Dialog.Dialogs.SimpleDialogs;

import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.Arg.ArgType;
import ver14.SharedClasses.Utils.ArrUtils;
import ver14.view.Dialog.Dialogs.DialogProperties.Properties;


public class InputDialog extends CustomDialog {
    public InputDialog(Properties properties) {
        this(properties, ArgType.Text);
    }

    /**
     * @param properties set msg on properties for input directions
     * @param inputType
     */
    public InputDialog(Properties properties, ArgType inputType) {
        super(properties, new Arg(inputType, properties.argConfig()));
    }

    public String getInput() {
        return ArrUtils.exists(getResults(), 0) + "";
    }
}
