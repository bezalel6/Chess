package ver11.view.Dialog.Dialogs;

import ver11.SharedClasses.FontManager;
import ver11.SharedClasses.Utils.StrUtils;
import ver11.view.Dialog.WinPnl;
import ver11.view.IconManager.Size;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Header extends JPanel {
    public final static Insets insets = WinPnl.insets;
    protected final static Size maximumSize = new Size(500);
    private final String text;
    private final JLabel lbl;
    private final ImageIcon icon;
    private final boolean center;

    public Header(String text) {
        this(text, true);
    }

    public Header(String text, boolean center) {
        this(text, null, center);
    }

    public Header(String text, ImageIcon icon, boolean center) {
        super(new BorderLayout());
        this.text = StrUtils.htmlNewLines(StrUtils.format(text));
        this.icon = icon;
        this.center = center;

        lbl = createHeader();
        add(lbl);

        Border border = getBorder();
        Border margin = new EmptyBorder(insets);
        setBorder(new CompoundBorder(border, margin));

        setMaximumSize(maximumSize);
    }

    protected JLabel createHeader() {
        return new JLabel(text, icon, center ? SwingConstants.CENTER : SwingConstants.LEFT) {{
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

    public String getText() {
        return text;
    }

}
