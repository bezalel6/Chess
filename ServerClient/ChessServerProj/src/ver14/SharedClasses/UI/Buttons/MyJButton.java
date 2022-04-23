package ver14.SharedClasses.UI.Buttons;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.awt.*;

/*
 * MyJButton
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * MyJButton -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * MyJButton -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

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

        addActionListener(l -> {
            onClick();
        });
    }

    public void setOnClick(VoidCallback onClick) {
        this.onClick = onClick;
    }

    @Override
    public void setText(String text) {
        super.setText(StrUtils.format(text));

    }

    private void onClick() {
        if (this.onClick != null)
            onClick.callback();
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
