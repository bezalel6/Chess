package ver13_FEN;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;


public class BoardPanel extends JPanel {

    public BoardPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Look ma, I'm a square", JLabel.CENTER));
        setBorder(new LineBorder(Color.GRAY));
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }
}
    