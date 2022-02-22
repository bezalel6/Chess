package ver6.view.dialogs.game_select.OptionalFields;

import ver6.SharedClasses.Callbacks.VoidCallback;
import ver6.SharedClasses.ui.MyJButton;

import javax.swing.*;

public abstract class OptionalField extends JPanel {
    private MyJButton btn;
    private JCheckBox jCheckBox;

    public OptionalField(VoidCallback onClick, String btnTxt) {
        btn = new MyJButton(btnTxt, () -> {
            jCheckBox.setSelected(true);
            onClick.callback();
        });
        jCheckBox = new JCheckBox() {{
            setSelected(false);
            addChangeListener(e -> {
                enableCheckBox();
            });
            addActionListener(e -> {
                enableCheckBox();
            });
        }};
        enableCheckBox();
        add(btn);
        add(jCheckBox);
    }

    private void enableCheckBox() {
        jCheckBox.setEnabled(jCheckBox.isSelected());
    }

    public boolean isCheckBox() {
        return jCheckBox.isSelected();
    }

    public void setCheckBox(boolean b) {
        jCheckBox.setSelected(b);
    }
}
