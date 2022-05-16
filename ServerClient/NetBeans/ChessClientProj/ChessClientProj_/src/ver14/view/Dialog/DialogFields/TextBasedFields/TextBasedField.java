package ver14.view.Dialog.DialogFields.TextBasedFields;

import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.Utils.RegEx;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.DialogFields.DialogField;
import ver14.view.Dialog.Dialogs.Header;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * a Text based field.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class TextBasedField<T> extends DialogField<T> {
    /**
     * The constant defaultTextFieldSize.
     */
    protected static final Dimension defaultTextFieldSize = new Dimension(100, 35);
    /**
     * The Text field.
     */
    protected final JTextField textField;
    /**
     * The Verify reg ex.
     */
    protected RegEx verifyRegEx;

    /**
     * Instantiates a new Text based field.
     *
     * @param str         the str
     * @param parent      the parent
     * @param verifyRegEx the verify reg ex
     */
    public TextBasedField(String str, Parent parent, RegEx verifyRegEx) {
        this(new Header(str, false), parent, verifyRegEx);
    }

    /**
     * Instantiates a new Text based field.
     *
     * @param dialogLabel the dialog label
     * @param parent      the parent
     * @param verifyRegEx the verify reg ex
     */
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

        setToolTipText("please dont inject sql");

    }

    /**
     * Style a provided text field according to this field's settings.
     *
     * @param textField the text field
     * @return the j text field
     */
    protected static JTextField styleTextField(JTextField textField) {
        textField.setForeground(Color.BLUE);
        textField.setPreferredSize(defaultTextFieldSize);
//        textField.setMinimumSize(defaultTextFieldSize);
//        textField.setMaximumSize(defaultTextFieldSize);
        textField.setFont(FontManager.Dialogs.dialogInput);
        return textField;
    }

    /**
     * Create text field.
     *
     * @return the created text field
     */
    protected JTextField createTextField() {
        return new JTextField();
    }

    /**
     * Gets text field text.
     *
     * @return the text field text
     */
    protected String getTextFieldText() {
        return textField.getText();
    }

    @Override
    protected boolean verifyField() {
        return verifyRegEx();
    }

    /**
     * Verify this field by its regex limitations.
     *
     * @return true if the field passed its verification, false otherwise.
     */
    protected boolean verifyRegEx() {
        return verifyRegEx.check(getValue() + "");
    }

    @Override
    public String errorDetails() {
        return verifyRegEx.getDetails();
    }
}
