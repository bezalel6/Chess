package ver14.SharedClasses.ui;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.awt.*;

public class MyJButton extends JButton {

    private String ogTxt = null;
    private VoidCallback ogOnClick = null;
    private VoidCallback onClick = null;

    public MyJButton(String text, VoidCallback callback) {
        this(text);
        setOnClick(callback);
    }

    public MyJButton(String text) {
        text = StrUtils.format(text);
        setText(text);
        setFocusable(false);
    }

    public void setOnClick(VoidCallback onClick) {
        this.onClick = onClick;
    }

    @Override
    public void setText(String text) {
        super.setText(StrUtils.format(text));

    }

    public MyJButton() {
        this("");
    }

    public MyJButton(String text, Font font, VoidCallback onClick, JPanel addTo) {
        this(text, font, onClick);
        addTo.add(this);
    }

    public MyJButton(String text, Font font, VoidCallback onClick) {
        this(text, font);
        setOnClick(onClick);
        addActionListener(l -> {
            onClick();
        });
    }

    public MyJButton(String text, Font font) {
        this(text);
        setFont(font);
    }

    private void onClick() {
        if (this.onClick != null)
            onClick.callback();
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
    }

    public int getMinSize() {
        return Math.min(getHeight(), getWidth());
    }

    public void replaceWithCancel(VoidCallback onCancelled) {
        replaceText("Cancel");
        ogOnClick = onClick;
        setOnClick(onCancelled);
    }

    private void replaceText(String replacement) {
        ogTxt = getText();
        setText(replacement);
    }

    public void resetState(boolean e) {
        setEnabled(e);
        if (ogTxt != null && ogOnClick != null) {
            setText(ogTxt);
            onClick = ogOnClick;
            ogOnClick = null;
            ogTxt = null;
        }

    }
}
