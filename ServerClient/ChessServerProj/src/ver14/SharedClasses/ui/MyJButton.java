package ver14.SharedClasses.ui;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.awt.*;

public class MyJButton extends JButton {

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
        addActionListener(e -> onClick.callback());
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
    }

    public MyJButton(String text, Font font) {
        this(text);
        setFont(font);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
    }


    public int getMinSize() {
        return Math.min(getHeight(), getWidth());
    }
}
