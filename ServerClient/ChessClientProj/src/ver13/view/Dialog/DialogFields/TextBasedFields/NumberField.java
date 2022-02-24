package ver13.view.Dialog.DialogFields.TextBasedFields;

import ver13.SharedClasses.RegEx;
import ver13.SharedClasses.Utils.StrUtils;
import ver13.view.Dialog.Components.Parent;
import ver13.view.Dialog.Dialogs.Header;

public class NumberField extends TextBasedField<Integer> {
    public NumberField(Header header, Parent parent) {
        super(header, parent, RegEx.Numbers);
    }

    @Override
    protected Integer getValue() {
        if (StrUtils.isEmpty(getTextFieldText()))
            return null;
        return Integer.parseInt(getTextFieldText());
    }

    @Override
    protected void setValue(Integer value) {
        textField.setText(value + "");
    }

}
