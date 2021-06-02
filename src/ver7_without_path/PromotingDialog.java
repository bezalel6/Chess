package ver7_without_path;

import ver7_without_path.types.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PromotingDialog extends JDialog implements ActionListener {
    private JButton arr[];
    private ImageIcon wn, wb, wr, wq, bn, bb, br, bq;
    private int result;
    private float btnIconRatio = 0.5f;

    public PromotingDialog(Frame parent) {
        super(parent, "", true);
        loadIcons();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout());
        arr = new JButton[4];
        for (int i = 0; i < 4; i++) {
            arr[i] = new JButton();
            arr[i].setFocusable(false);
        }
        arr[0].setActionCommand(Piece.types.KNIGHT.ordinal() + "");
        arr[1].setActionCommand(Piece.types.BISHOP.ordinal() + "");
        arr[3].setActionCommand(Piece.types.QUEEN.ordinal() + "");
        arr[2].setActionCommand(Piece.types.ROOK.ordinal() + "");

        for (JButton btn : arr) {
            btn.addActionListener(this);
            panel.add(btn);
        }


        getContentPane().add(panel);
        pack();
    }

    private void loadIcons() {
        wn = loadImage("White/Knight");
        wb = loadImage("White/Bishop");
        wr = loadImage("White/Rook");
        wq = loadImage("White/Queen");

        bn = loadImage("Black/Knight");
        bb = loadImage("Black/Bishop");
        br = loadImage("Black/Rook");
        bq = loadImage("Black/Queen");
    }

    public ImageIcon loadImage(String fileName) {
        ImageIcon ret = new ImageIcon(View.class.getResource("/Assets/" + fileName + ".png"));
        return ret;
    }

    public void refreshIconSizes() {
        for (JButton btn : arr) {
            ImageIcon image = (ImageIcon) btn.getIcon();
            image = new ImageIcon(image.getImage().getScaledInstance((int) (btn.getHeight() * btnIconRatio), (int) (btn.getHeight() * btnIconRatio), Image.SCALE_SMOOTH));
            btn.setIcon(image);
            btn.setDisabledIcon(image);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        String res = source.getActionCommand();
        result = res.charAt(0) - '0';
        dispose();
    }

    public int run(Piece.colors color) {
        setIcons(color);
        refreshIconSizes();
        this.setVisible(true);
        return result;
    }

    private void setIcons(Piece.colors color) {
        if (color == Piece.colors.WHITE) {
            arr[0].setIcon(wn);
            arr[1].setIcon(wb);
            arr[2].setIcon(wr);
            arr[3].setIcon(wq);
        } else {
            arr[0].setIcon(bn);
            arr[1].setIcon(bb);
            arr[2].setIcon(br);
            arr[3].setIcon(bq);
        }
    }
}
