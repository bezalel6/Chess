package ver8.view.Wishfull.dialogs;

import ver8.SharedClasses.StrUtils;

import javax.swing.*;

public class Header {
    public final String txt;
    public final ImageIcon icon;

    public Header(String txt) {
        this(txt, null);
    }

    public Header(String txt, ImageIcon icon) {
        this.txt = StrUtils.format(txt);
        this.icon = icon;
    }

    public Header(ImageIcon icon) {
        this(null, icon);
    }
}
