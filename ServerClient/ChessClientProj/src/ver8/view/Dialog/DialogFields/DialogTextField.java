package ver8.view.Dialog.DialogFields;

import ver8.SharedClasses.RegEx;
import ver8.view.Dialog.Dialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public abstract class DialogTextField extends DialogField {
    protected static final Dimension defaultTextFieldSize = new Dimension(250, 20);
    public final RegEx verifyRegEx;
    protected final JTextField textField;
    private final boolean useDontAddRegex;

    protected DialogTextField(String dialogLabel, Dialog parent, RegEx verifyRegEx, boolean useDontAddRegex) {
        super(dialogLabel, parent);
        this.verifyRegEx = verifyRegEx;
        this.useDontAddRegex = useDontAddRegex;
        this.textField = styleTextField(createTextField());
        this.textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onInputUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onInputUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onInputUpdate();
            }
        });
        addMainComp(this.textField);

    }

    protected static JTextField styleTextField(JTextField textField) {
        textField.setForeground(Color.BLUE);
        textField.setPreferredSize(defaultTextFieldSize);
        return textField;
    }

    protected JTextField createTextField() {
        return new JTextField();
    }

    @Override
    protected boolean verifyField() {
        return verifyRegEx();
    }

    protected boolean verifyRegEx() {
        return verifyRegEx.check(getValue(), useDontAddRegex);
    }

    
    public String getValue() {
        return textField.getText();
    }

    @Override
    public String errorDetails() {
        return verifyRegEx.getDetails(useDontAddRegex);
    }
}

