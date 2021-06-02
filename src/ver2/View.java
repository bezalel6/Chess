package ver2;


import ver2.types.Path;
import ver2.types.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class View {
    static final int ROWS = 8;
    static final int COLS = 8;
    static JButton[][] btnMat;
    static String player;

    public View() {
        initGame();
    }

    public static void initGame() {
        player = "White";
        createGui();
    }

    public static void createGui() {
        JFrameResizing win = new JFrameResizing();
        JPanel middle = new JPanel();

        win.setSize(1000, 1000);
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.setLocationRelativeTo(null);

        btnMat = new JButton[ROWS][COLS];
        middle.setLayout(new GridLayout(ROWS, COLS));
        for (int i = 0; i < ROWS; i++) {
            isBlack = !isBlack;
            for (int j = 0; j < COLS; j++) {
                btnMat[i][j] = new JButton();
                JButton btn = btnMat[i][j];

                btn.setFont(new Font(null, 1, 50));
                btn.setActionCommand(i + "" + j);
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        clicked(new Location(e.getActionCommand().charAt(0)-'0', e.getActionCommand().charAt(1)-'0'));
                    }
                });
                btn.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == 3) { // if right click
                            rightClicked(btn,new Location( btn.getActionCommand().charAt(0)-'0', btn.getActionCommand().charAt(1)-'0'));
                        } else {

                        }
                    }
                });
                btn.setFocusable(false);

                middle.add(btn);
            }
        }
        resetBackground();
        win.add(middle);
        win.setVisible(true);

    }
    static Color highlightColor = Color.CYAN;
    private static void rightClicked(JButton btn, Location location) {
        if(btn.getBackground()==highlightColor)
            btn.setBackground(null);
        else btn.setBackground(highlightColor);
    }

    public static void updateAllCells(ArrayList<Piece> arr) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                btnMat[i][j].setIcon(null);
                btnMat[i][j].setDisabledIcon(null);
                btnMat[i][j].setEnabled(false);

            }
        }
        for (int i = 0; i < arr.size(); i++) {
            Piece piece = arr.get(i);
            ImageIcon icon = new ImageIcon(View.class.getResource("/Assets/"+piece.getColorString()+"/"+piece.getType()+".png"));
            JButton btn =btnMat[arr.get(i).getLoc().getRow()][arr.get(i).getLoc().getCol()];
            btn.setIcon(icon);
            btn.setDisabledIcon(icon);
            btn.setEnabled(true);
        }
        refreshIconSizes();
    }
    static float cmp = 0.7f;
    public static void refreshIconSizes()
    {
        for (int i = 0; i < Controller.ROWS; i++) {
            for (int j = 0; j < Controller.COLS; j++) {
                JButton btn = btnMat[i][j];
                ImageIcon image = (ImageIcon) btn.getIcon();
                if(image==null)continue;
                image = new ImageIcon(image.getImage().getScaledInstance((int)(btn.getHeight()*cmp), (int)(btn.getHeight()*cmp), Image.SCALE_SMOOTH));
                btn.setIcon(image);
                btn.setDisabledIcon(image);

            }
        }
    }

    static boolean isBlack = true;
    static Color brown = new Color(79, 60, 33, 255);
    static Color white = new Color(222, 213, 187);

    static void resetBackground() {
        for (int i = 0, row = 0; row < ROWS; row++, i++) {
            isBlack = !isBlack;
            for (int j = 0, cell = 0; cell < COLS; cell++, j++) {
                if (isBlack) btnMat[i][j].setBackground(brown);
                else btnMat[i][j].setBackground(white);
                isBlack = !isBlack;
            }
        }

    }
   static  boolean isFirstClick = true;
    public static String convertToAnn(Location loc){
    return (char)('a'+loc.getCol())+""+loc.getRow();
    }
    public static void clicked(Location loc) {
        System.out.println("clicked on " +convertToAnn(loc));
        enableAllSquares(!isFirstClick);
        if(!isFirstClick)
        {
            isFirstClick=true;
            resetBackground();
            Controller.move(loc);
            return;
        }
        isFirstClick=false;
        ArrayList<Path> list = Controller.clicked(loc);
        if (list != null) {
            enableSquare(loc,true);
            for (int i = 0; i < list.size(); i++) {
                Location currentLoc = list.get(i).getLoc();

                enableSquare(currentLoc,true);
                highlightSquare(loc,blue);
                if (list.get(i).isCapturing()){
                    highlightSquare(currentLoc, red);}
                else {
                    highlightSquare(currentLoc, yellow);
                }
            }
        }
        else isFirstClick=true;

    }

    static Color red = Color.red, yellow = Color.yellow,blue = Color.BLUE;

    public static void highlightSquare(Location loc, Color color) {
        btnMat[loc.getRow()][loc.getCol() ].setBackground(color);
    }
    public static void enableSquare(Location loc, boolean bool)
    {
     btnMat[loc.getRow()][loc.getCol()].setEnabled(bool);
    }
    public static void enableAllSquares( boolean bool)
    {
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                btnMat[i][j].setEnabled(bool);
            }
        }
    }

}
