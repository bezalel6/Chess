package ver6.view.dialogs;

import ver6.SharedClasses.StrUtils;

import javax.swing.*;

public class Header {
    public final String txt;
    public final ImageIcon icon;

    public Header(String txt) {
        this(txt, null);
    }

    public Header(ImageIcon icon) {
        this(null, icon);
    }

    public Header(String txt, ImageIcon icon) {
        this.txt = StrUtils.uppercaseFirstLetters(txt);
        this.icon = icon;
    }
}
