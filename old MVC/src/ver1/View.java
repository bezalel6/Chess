package ver1;

import javax.swing.*;
import java.awt.*;

public class View {
    static final int ROWS =8;
    static final int COLS= 8;
    static JButton[][] btnMat;
    public View() {
    }
    public static void createGui()
    {
        JFrame win = new JFrame();
        JPanel top = new JPanel();
        JPanel middle = new JPanel();
        JPanel bottom = new JPanel();

        win.setSize(500,500);
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.setLocationRelativeTo(null);

        btnMat = new JButton[ROWS][COLS];
        middle.setLayout(new GridLayout(ROWS,COLS));
        boolean isBlack = false;
        for (int i = 0; i <ROWS ; i++) {
            isBlack=!isBlack;
            for (int j = 0; j < COLS; j++) {
                if(i<2||i>5) {
                    btnMat[i][j] = new JButton("1");
                }
                else {
                    btnMat[i][j] = new JButton("");
                }

                if(isBlack) btnMat[i][j].setBackground(Color.GRAY);
                else btnMat[i][j].setBackground(Color.white);

                isBlack=!isBlack;

                btnMat[i][j].setFocusable(false);
                middle.add(btnMat[i][j]);

            }
        }
        win.add(middle);
        win.setVisible(true);
    }

}
