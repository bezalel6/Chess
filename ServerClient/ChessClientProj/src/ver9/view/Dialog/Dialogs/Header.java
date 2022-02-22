package ver9.view.Dialog.Dialogs;

import ver9.SharedClasses.FontManager;
import ver9.SharedClasses.StrUtils;
import ver9.view.Dialog.WinPnl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Header extends JPanel {
    public final static Insets insets = WinPnl.insets;

    private final String txt;
    private final JLabel lbl;
    private final ImageIcon icon;
    private final boolean center;

    public Header(String txt) {
        this(txt, true);
    }

    public Header(String txt, boolean center) {
        this(txt, null, center);
    }

    public Header(String txt, ImageIcon icon, boolean center) {
        super(new BorderLayout());
        this.txt = StrUtils.format(txt);
        this.icon = icon;
        this.center = center;

        lbl = createHeader();
        add(lbl);

        Border border = getBorder();
        Border margin = new EmptyBorder(insets);
        setBorder(new CompoundBorder(border, margin));
    }

    protected JLabel createHeader() {
        return new JLabel(txt, icon, center ? SwingConstants.CENTER : SwingConstants.LEFT) {{
            setFont(FontManager.normal);
        }};
    }

    public Header(ImageIcon icon, boolean center) {
        this(null, icon, center);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (lbl != null)
            lbl.setFont(font);
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public String getTxt() {
        return txt;
    }

}
