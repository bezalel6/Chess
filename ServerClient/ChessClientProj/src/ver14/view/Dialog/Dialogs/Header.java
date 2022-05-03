package ver14.view.Dialog.Dialogs;

import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.UI.MyLbl;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.IconManager.Size;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Header - represents a header panel holding an icon, text, both, or none.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Header extends JPanel {
    /**
     * The constant insets.
     */
//    public final static Insets insets = WinPnl.insets;
    public final static Insets insets = new Insets(10, 10, 10, 10);
    /**
     * The constant maximumSize.
     */
    protected final static Size maximumSize = new Size(500);
    /**
     * The Lbl.
     */
    private final MyLbl lbl;
    /**
     * The Center.
     */
    private final boolean center;
    /**
     * The Icon.
     */
    private ImageIcon icon;
    /**
     * The Text.
     */
    private String text;

    /**
     * Instantiates a new Header.
     *
     * @param text the text
     */
    public Header(String text) {
        this(text, true);
    }

    /**
     * Instantiates a new Header.
     *
     * @param text   the text
     * @param center the center
     */
    public Header(String text, boolean center) {
        this(text, null, center);
    }

    /**
     * Instantiates a new Header.
     *
     * @param text   the text
     * @param icon   the icon
     * @param center the center
     */
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

    /**
     * Create header my lbl.
     *
     * @return the my lbl
     */
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

    /**
     * Instantiates a new Header.
     *
     * @param icon   the icon
     * @param center the center
     */
    public Header(ImageIcon icon, boolean center) {
        this(null, icon, center);
    }

    /**
     * Gets lbl.
     *
     * @return the lbl
     */
    public MyLbl getLbl() {
        return lbl;
    }

    /**
     * Space lbl icon.
     */
    public void spaceLblIcon() {
        lbl.setIconTextGap(50);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (lbl != null)
            lbl.setFont(font);
    }

    /**
     * Gets icon.
     *
     * @return the icon
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * Sets icon.
     *
     * @param icon the icon
     */
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
        lbl.setIcon(icon);
    }

    /**
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets text.
     *
     * @param s the s
     */
    public void setText(String s) {
        this.text = s;
        this.lbl.setText(text);
    }
}
