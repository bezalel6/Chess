package ver14.view.Dialog.DialogFields.TextBasedFields;

import ver14.SharedClasses.Utils.RegEx;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.Header;

public class TextField extends TextBasedField<String> {
    public TextField(String str, Parent parent, RegEx verifyRegEx) {
        super(str, parent, verifyRegEx);
    }

    public TextField(Header dialogLabel, Parent parent, RegEx verifyRegEx) {
        super(dialogLabel, parent, verifyRegEx);
    }

    @Override
    protected String getValue() {
        return getTextFieldText();
    }

    @Override
    public void setValue(String value) {
        textField.setText(value);
    }
}

