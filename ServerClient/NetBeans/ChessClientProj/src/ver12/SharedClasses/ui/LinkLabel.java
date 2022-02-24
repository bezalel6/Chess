package ver12.SharedClasses.ui;

import ver12.SharedClasses.Callbacks.VoidCallback;
import ver12.SharedClasses.FontManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LinkLabel extends JLabel {
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
}
