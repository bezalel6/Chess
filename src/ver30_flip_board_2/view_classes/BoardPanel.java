package ver30_flip_board_2.view_classes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

public class BoardPanel extends JPanel implements ActionListener {

    int deg = 0;
    private boolean flipped;

    public BoardPanel(boolean flipped) {
        this.flipped = flipped;
    }

    public void flip() {
        flipped = !flipped;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
//        if (flipped) {
//            Graphics2D g2d = (Graphics2D) g;
//            AffineTransform affineTransform = new AffineTransform();
//            affineTransform.scale(-1, -1);
//            affineTransform.translate(-getWidth(), -getHeight());
//            g2d.transform(affineTransform);
//        }
//        g2d.dispose();
    }


    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
