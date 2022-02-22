package ver5.SharedClasses.ui;

import ver5.SharedClasses.Callback;
import ver5.SharedClasses.StrUtils;

import javax.swing.*;
import java.awt.*;

public class MyJButton extends JButton {

    public MyJButton(String text, Callback callback) {
        this(text);
        setOnClick(callback);
    }

    public MyJButton(String text) {
        text = StrUtils.uppercaseFirstLetters(text);
        setText(text);
        setFocusable(false);
    }

    public void setOnClick(Callback onClick) {
        addActionListener(e -> onClick.callback());
    }

    @Override
    public void setText(String text) {
        super.setText(StrUtils.uppercaseFirstLetters(text));

    }

    public MyJButton() {
        this("");
    }

    public MyJButton(String text, Font font, Callback onClick, JPanel addTo) {
        this(text, font, onClick);
        addTo.add(this);
    }

    public MyJButton(String text, Font font, Callback onClick) {
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
