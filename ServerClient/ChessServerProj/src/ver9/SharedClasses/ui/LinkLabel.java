/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ver9.SharedClasses.ui;

import ver9.SharedClasses.Callbacks.VoidCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LinkLabel extends JLabel {
    private final static Color normalClr = Color.BLUE.darker();
    private final static Color hoverClr = normalClr.brighter();

    public LinkLabel(String text, VoidCallback onClick) {
        super(text);
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
