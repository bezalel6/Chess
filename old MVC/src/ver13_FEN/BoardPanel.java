package ver13_FEN;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {

    public BoardPanel() {
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
        return new Dimension(300, 300);
    }
}
