package ver14.view.Dialog.Dialogs;

import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.ui.MyLbl;
import ver14.view.IconManager.Size;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Header extends JPanel {
    //    public final static Insets insets = WinPnl.insets;
    public final static Insets insets = new Insets(10, 10, 10, 10);
    protected final static Size maximumSize = new Size(500);
    private final MyLbl lbl;
    private final boolean center;
    private ImageIcon icon;
    private String text;

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
        setToolTipText(lbl.getText());

        add(lbl);

        Border border = getBorder();
        Border margin = new EmptyBorder(insets);
        setBorder(new CompoundBorder(border, margin));

        setMaximumSize(maximumSize);
    }

    protected MyLbl createHeader() {
        return new MyLbl(text, icon, center ? SwingConstants.CENTER : SwingConstants.LEFT) {
            {
                setFont(FontManager.Dialogs.dialog);
            }

            @Override
            public void setText(String text) {
                super.setText(text);
                setToolTipText(text);
            }
        };
    }

    public Header(ImageIcon icon, boolean center) {
        this(null, icon, center);
    }

    public MyLbl getLbl() {
        return lbl;
    }

    public void spaceLblIcon() {
        lbl.setIconTextGap(50);
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

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
        lbl.setIcon(icon);
    }

    public String getText() {
        return text;
    }

    public void setText(String s) {
        this.text = s;
        this.lbl.setText(text);
    }
}
