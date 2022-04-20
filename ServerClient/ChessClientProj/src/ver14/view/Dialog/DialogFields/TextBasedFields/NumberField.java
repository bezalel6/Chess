package ver14.view.Dialog.DialogFields.TextBasedFields;

import ver14.SharedClasses.RegEx;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.Header;

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
    public void setValue(Integer value) {
        textField.setText(value + "");
    }

}
