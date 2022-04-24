package ver14.SharedClasses.UI;

import ver14.SharedClasses.Callbacks.VoidCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * LinkLabel
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * LinkLabel -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * LinkLabel -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class LinkLabel extends MyLbl {
    private final static Color normalClr = Color.BLUE.darker();
    private final static Color hoverClr = normalClr.brighter();

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

    public static void main(String[] args) {
        new JFrame() {{
//            setLayout(new GridBagLayout());
            setSize(500, 500);
            add(new LinkLabel("hello", () -> {
            }));
            setVisible(true);
        }};
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        setToolTipText(text);
    }
}
