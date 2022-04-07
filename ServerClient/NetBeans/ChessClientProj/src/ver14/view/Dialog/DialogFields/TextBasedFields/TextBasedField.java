package ver14.view.Dialog.DialogFields.TextBasedFields;

import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.RegEx;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.DialogFields.DialogField;
import ver14.view.Dialog.Dialogs.Header;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public abstract class TextBasedField<T> extends DialogField<T> {
    protected static final Dimension defaultTextFieldSize = new Dimension(250, 20);
    protected final JTextField textField;
    protected RegEx verifyRegEx;

    public TextBasedField(String str, Parent parent, RegEx verifyRegEx) {
        this(new Header(str, false), parent, verifyRegEx);
    }

    public TextBasedField(Header dialogLabel, Parent parent, RegEx verifyRegEx) {
        super(dialogLabel, parent);
        this.verifyRegEx = verifyRegEx;
        this.textField = styleTextField(createTextField());
        this.textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//                onUpdate();
            }
        });
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    parent.tryOk(true);
            }
        });
        addMainComp(this.textField);

    }

    protected static JTextField styleTextField(JTextField textField) {
        textField.setForeground(Color.BLUE);
        textField.setPreferredSize(defaultTextFieldSize);
        textField.setFont(FontManager.Dialogs.dialogInput);
        return textField;
    }

    protected JTextField createTextField() {
        return new JTextField();
    }

    protected String getTextFieldText() {
        return textField.getText();
    }

    @Override
    protected boolean verifyField() {
        return verifyRegEx();
    }

    protected boolean verifyRegEx() {
        return verifyRegEx.check(getValue() + "");
    }

    @Override
    public String errorDetails() {
        return verifyRegEx.getDetails();
    }
}
