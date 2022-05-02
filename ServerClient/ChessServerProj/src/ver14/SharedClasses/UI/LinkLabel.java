package ver14.SharedClasses.UI;

import ver14.SharedClasses.Callbacks.VoidCallback;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Link label.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class LinkLabel extends MyLbl {
    /**
     * The constant normalClr.
     */
    private final static Color normalClr = Color.BLUE.darker();
    /**
     * The constant hoverClr.
     */
    private final static Color hoverClr = normalClr.brighter();

    /**
     * Instantiates a new Link label.
     *
     * @param text    the text
     * @param onClick the on click
     */
    public LinkLabel(String text, VoidCallback onClick) {
        super(text);
        setFont(FontManager.defaultLinkLbl);
        setForeground(normalClr);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.callback();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setForeground(hoverClr);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setForeground(normalClr);
            }
        });
    }


    /**
     * Sets text.
     *
     * @param text the text
     */
    @Override
    public void setText(String text) {
        super.setText(text);
        setToolTipText(text);
    }
}
