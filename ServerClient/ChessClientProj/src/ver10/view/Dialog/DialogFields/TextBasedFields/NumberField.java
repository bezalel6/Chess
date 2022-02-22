package ver10.view.Dialog.DialogFields.TextBasedFields;

import ver10.SharedClasses.RegEx;
import ver10.SharedClasses.Utils.StrUtils;
import ver10.view.Dialog.Components.Parent;
import ver10.view.Dialog.Dialogs.Header;

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
