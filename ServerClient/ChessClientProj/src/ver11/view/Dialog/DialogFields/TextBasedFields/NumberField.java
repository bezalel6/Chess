package ver11.view.Dialog.DialogFields.TextBasedFields;

import ver11.SharedClasses.RegEx;
import ver11.SharedClasses.Utils.StrUtils;
import ver11.view.Dialog.Components.Parent;
import ver11.view.Dialog.Dialogs.Header;

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
