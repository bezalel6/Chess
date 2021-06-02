package ver1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Model {
    static final int ROWS = 8;
    static final int COLS = 8;
    static Piece[] pieces = new Piece[32];
    static JButton[][] btnMat;
    static String player;

    public Model() {
        initGame();
    }

    public static void initGame() {
        player = "White";
    }

    public static void createGui() {
        JFrame win = new JFrame();
        JPanel top = new JPanel();
        JPanel middle = new JPanel();
        JPanel bottom = new JPanel();

        win.setSize(1000, 1000);
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.setLocationRelativeTo(null);

        btnMat = new JButton[ROWS][COLS];
        middle.setLayout(new GridLayout(ROWS, COLS));
        int index = 32;
        for (int i = 0; i < ROWS; i++) {
            isBlack = !isBlack;
            for (int j = 0; j < COLS; j++) {
                Location loc = new Location(i, j);

                if (i < 2 || i > 5) {

                    index--;
                    btnMat[i][j] = new JButton();

                    pieces[index] = createPiece(loc, btnMat[i][j]);
                    //btnMat[i][j].setText(pieces[index].toString());

                    btnMat[i][j].setIcon(pieces[index].getImage());
                } else {
                    btnMat[i][j] = new JButton();
                    btnMat[i][j].setIcon(null);
                }

                btnMat[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        clicked((JButton) e.getSource());
                    }
                });

                btnMat[i][j].setFocusable(false);

                middle.add(btnMat[i][j]);
            }
        }
        disableAllEmptyCells();
        resetBackground();
        win.add(middle);
        win.setVisible(true);
    }

    static boolean isBlack = false;
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
        disableUnmovable();

    }

    static Piece createPiece(Location loc, JButton btn) {
        String color = "White", type = "Pawn";
        int row = loc.getRow(), col = loc.getCol();
        if (row == 1 || row == 6) {
            type = "Pawn";
            if (row == 6) {
                color = "White";
            } else {
                color = "Black";
            }
        } else if (row == 0 || row == 7) {
            type = "Rook";
            if (col == 0 || col == 7) {
                if (row == 0) color = "Black";
                else color = "White";
            } else if (col == 1 || col == 6) {
                type = "Knight";
                if (row == 0) color = "Black";
                else color = "White";
            } else if (col == 2 || col == 5) {
                type = "Bishop";
                if (row == 0) color = "Black";
                else color = "White";
            } else if (col == 3) {
                type = "King";
                if (row == 0) color = "Black";
                else color = "White";
            } else if (col == 4) {
                type = "Queen";
                if (row == 0) color = "Black";
                else color = "White";
            }
        }

        return new Piece(color, type, loc, btn);
    }

    static Location getBtnMatLocation(JButton btn) {
        Location loc = new Location(-1,-1);
        for (int i = 0, row = 0; row < ROWS; row++, i++)
            for (int j = 0, cell = 0; cell < COLS; cell++, j++)
                if (btn == btnMat[row][cell]) {
                    return new Location(row, cell);
                }
        return loc;
    }


    static boolean isFirstClick = true;
    static JButton prevButton;
    static Location prevLocation;

    static void clicked(JButton btn) {
        Location loc = getBtnMatLocation(btn);
        Piece piece = getPiece(btn);

        if (isFirstClick) {
            prevLocation = loc;
            prevButton = btn;
            for (int i = 0; i < ROWS; i++) {

                for (int j = 0; j < COLS; j++) {
                    Location moveTo = new Location(i, j);
                    if (canMove(piece, moveTo)) {
                        enableCell(moveTo, true);
                        btnMat[i][j].setBackground(Color.YELLOW);
                    }
                    else  {
                        if(!piece.getEaten())
                        enableCell(moveTo,false);

                    }
                }
            }
        }
        else {

            Piece piece1 = getPiece(prevButton);
            if(piece!=null&&piece1!=null) {
                 piece.setType(piece1.getType()); piece.setImage(piece1.getImage()); piece.setColor(piece1.getColor());//בשביל שהוא לא יהפוך למי שהוא אכל

                piece.setEaten();
            }
             piece1.setLoc(loc);

            piece1.setBtn(btn);



            btnMat[prevLocation.getRow()][prevLocation.getCol()].setIcon(null);
            btnMat[prevLocation.getRow()][prevLocation.getCol()] = new JButton();

            btnMat[loc.getRow()][loc.getCol()].setIcon(piece1.getImage());
            disableAllEmptyCells();
            resetBackground();




        }
        isFirstClick = !isFirstClick;

    }


    static Piece getPiece(JButton btn) {
        for (int i = 0; i < pieces.length; i++) {

            if (pieces[i].getBtn() == btn) return pieces[i];
        }
        return null;
    }

    static Piece getPiece(Location loc) {
        for (int i = 0; i < pieces.length; i++) {

            if (pieces[i].getLoc().getRow() == loc.getRow() && pieces[i].getLoc().getCol() == loc.getCol())
                return pieces[i];
        }
        return null;
    }

    static boolean canMove(Piece piece, Location loc) {
        boolean res = false;


        Piece piece1 = getPiece(prevButton);


        String type = piece.getType();
        if (piece.getLoc().getCol() == loc.getCol() && piece.getLoc().getRow() == loc.getRow())
            return false;
        Piece destinationPiece = getPiece(loc);
        if (destinationPiece != null && piece.getColor() == destinationPiece.getColor()) {
            return false;
        }

        switch(type) {
            case "Pawn":{
                res =  canPawnMove(piece,loc);break;}
            case "Rook":{
                res =  canRookMove(piece, loc);break;}
            case "Knight":{
                res =  canKnightMove(piece, loc);break;}
            case "Bishop":{
                res =  canBishopMove(piece, loc);break;}
            case "Queen":{
                res =  canQueenMove(piece, loc);break;}
            case "King":{
                res =  canKingMove(piece, loc);break;}

        }


        return res;
    }
    static void disableUnmovable()
    {
        for (int k = 0; k < pieces.length; k++) {
            boolean canMove=false;
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j <COLS ; j++) {
                    Location loc = new Location(i,j);
                    if(canMove(pieces[k],loc))canMove=true;
                }
            }
            enableCell(pieces[k].getLoc(),canMove);
        }

    }
    
    
    static boolean canPawnMove(Piece piece, Location destination) {
        int multiplyByMinus = 1, firstRow = piece.getFirstRow();
        if (piece.getColor() == "White") {
            multiplyByMinus = -1;
        }
        if (destination.getCol() == piece.getLoc().getCol()&&getPiece(destination)==null) {
            if (destination.getRow() == piece.getLoc().getRow() + 1 * multiplyByMinus)
                return true;

            if (piece.getLoc().getRow() == firstRow && destination.getRow() == piece.getLoc().getRow() + 2 * multiplyByMinus)
                return true;
        }
        if(piece.getColor()=="White") {
            if (getPiece(destination) != null && piece.getLoc().getCol() == destination.getCol() + 1 && piece.getLoc().getRow() == destination.getRow() + 1)
                return true;
            if (getPiece(destination) != null && piece.getLoc().getCol() == destination.getCol() - 1 && piece.getLoc().getRow() == destination.getRow() + 1)
                return true;
        }
        else{
            if (getPiece(destination) != null && piece.getLoc().getCol() == destination.getCol() + 1 && piece.getLoc().getRow() == destination.getRow() - 1)
                return true;
            if (getPiece(destination) != null && piece.getLoc().getCol() == destination.getCol() - 1 && piece.getLoc().getRow() == destination.getRow() - 1)
                return true;
        }

            return false;
    }

    static boolean canRookMove(Piece piece, Location destination) {
        boolean res=false;
        int Pcol = piece.getLoc().getCol(),Dcol = destination.getCol();
        int Prow = piece.getLoc().getRow(),Drow = destination.getRow();
        if ( Dcol== Pcol ||Drow == Prow) {
            res=true;
            if(Drow == Prow) {
                if (Dcol>Pcol)
                for (int i = Pcol; i < Dcol; i++) {
                    if (getPiece(btnMat[Prow][i]) != null)
                        res = false;

                }
                else
                    for (int i = Dcol; i < Pcol; i++) {
                        if (getPiece(btnMat[Prow][i]) != null)
                            res = false;

                    }

            }
            if(Dcol == Pcol) {
                if(Drow>Prow)
                for (int i = Prow; i < Drow; i++) {
                    if (getPiece(btnMat[i][Dcol]) != null)
                        res = false;

                }
                else
                    for (int i = Drow; i < Prow; i++) {
                        if (getPiece(btnMat[i][Dcol]) != null)
                            res = false;

                    }
            }


        }

        return res;
    }
    static boolean canKnightMove(Piece piece, Location destination) {
        int PCol = piece.getLoc().getCol(), DCol = destination.getCol();
        int PRow = piece.getLoc().getRow(), DRow = destination.getRow();

        if (PCol == DCol + 1 || PCol == DCol - 1)
            if (PRow == DRow + 2 || PRow == DRow - 2)
                return true;
        if (PCol == DCol + 2 || PCol == DCol - 2)
            if (PRow == DRow + 1 || PRow == DRow - 1)
                return true;

        return false;
    }

    static boolean canQueenMove(Piece piece, Location destination) {
        return (canBishopMove(piece, destination) || canRookMove(piece, destination));
    }

    static boolean canBishopMove(Piece piece, Location destination) {
        int PCol = piece.getLoc().getCol(), DCol = destination.getCol();
        int PRow = piece.getLoc().getRow(), DRow = destination.getRow();

        for (int i = 0; i < ROWS; i++) {
            if (PCol == DCol + i && PRow == DRow + i)
                return true;
        }
        for (int i = 0; i < ROWS; i++) {
            if (PCol == DCol - i && PRow == DRow - i)
                return true;
        }
        for (int i = 0; i < ROWS; i++) {
            if (PCol == DCol + i && PRow == DRow - i)
                return true;
        }
        for (int i = 0; i < ROWS; i++) {
            if (PCol == DCol - i && PRow == DRow + i)
                return true;
        }


        return false;
    }

    static boolean canKingMove(Piece piece, Location destination) {
        int PCol = piece.getLoc().getCol(), DCol = destination.getCol();
        int PRow = piece.getLoc().getRow(), DRow = destination.getRow();

        if (PCol == DCol && PRow == DRow + 1
        ||PCol == DCol + 1 && PRow == DRow
        ||PCol == DCol && PRow == DRow - 1
        ||PCol == DCol - 1 && PRow == DRow
        ||PCol == DCol + 1 && PRow == DRow + 1
        ||PCol == DCol - 1 && PRow == DRow - 1
        ||PCol == DCol + 1 && PRow == DRow - 1
        ||PCol == DCol - 1 && PRow == DRow + 1)
            return true;

        return false;
    }


    static void disableAllEmptyCells() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (btnMat[i][j].getIcon() == null) enableCell(new Location(i, j), false);
                else enableCell(new Location(i, j), true);
            }
        }
    }

    static void enableCell(Location loc, boolean bool) {
        btnMat[loc.getRow()][loc.getCol()].setEnabled(bool);
    }

    static void enableAllCells(boolean bool) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                btnMat[i][j].setEnabled(bool);
            }
        }
    }

    public static void colorWinLocs(ArrayList<Location> winLocList) {
        for (int i = 0; i < winLocList.size(); i++) {
            Location loc = winLocList.get(i);
            btnMat[loc.getRow()][loc.getCol()].setBackground(Color.YELLOW);
        }
    }
}
