package ver2;

import ver2.types.*;
import ver2.types.Path;

import java.awt.*;
import java.util.ArrayList;

public class Model {
    public static final int WHITE_PAWNS_START = 1,BLACK_PAWNS_START = 6;
    public static final int WHITE_PIECES_START = 0,BLACK_PIECES_START = 7;
    static final int ROWS = 8;
    static final int COLS = 8;
    static final int numOfPieces = 32;
    public static ArrayList<Piece> pieces;
    static String player;

    public Model() {
        initGame();
    }

    public static void initGame() {
        start();
    }
    public static void addPiece(Piece piece)
    {
        pieces.add(piece);
        View.updateAllCells(pieces);
    }

    public static ArrayList<Piece> getPieces() {
        return pieces;
    }


    public static void start() {
        loadPos(Positions.startingPos());
        for(Piece piece:pieces)
            piece.updateLegalMoves();
    }
    public static void loadPos(ArrayList<Piece> list)
    {
        pieces = new ArrayList<Piece>();
        pieces.addAll(list);
        View.updateAllCells(pieces);
    }
    public static Piece getKing(Piece.colors color)
    {
        for (Piece p : pieces) {
            if(p instanceof King&&p.getPieceColor()==color)
                return p;
        }
        return null;
    }
    public static boolean isThreatened(Piece piece)
    {
        for(Piece p : pieces)
        {
            if(!piece.isOnMyTeam(p))
            {
                if(p.legalMoves!=null)
                for(Object path : p.legalMoves)
                {
                    if(((Path) path).getLoc().isEqual(piece.getLoc()))
                        return true;
                }

            }
        }
        return false;
    }

    public static Piece getPiece(Location loc)
    {
        for (Piece p : pieces) {
            if(p.getLoc().isEqual(loc))
                return p;
        }
        return null;
    }
    static Location prev = null;
    public static ArrayList<Path> getMovableSquares(Location loc) {
        prev = loc;
        Piece m = getPiece(loc);
        if(m==null)
        {
            return null;
        }
        return m.canMoveTo();
    }
    public static void move(Location newLoc)
    {
        Piece prevPiece = getPiece(prev);
        int index = getPiecesIndex(newLoc);
        if(index!=-1&&getPiecesIndex(prev)!=index)
        pieces.remove(index);
        prevPiece.setLoc(newLoc);
        View.updateAllCells(pieces);
        for(Piece p : pieces) {
            p.updateLegalMoves();
            if (isThreatened(p))
                View.highlightSquare(p.getLoc(), Color.green);
        }
    }
    static int getPiecesIndex(Location loc)
    {
        for (Piece p : pieces) {
            if(p.getLoc().isEqual(loc))
                return pieces.indexOf(p);
        }
        return -1;
    }
    //can shit move
//    static boolean canPawnMove(Piece piece, Location destination) {
//        int multiplyByMinus = 1, firstRow = piece.getFirstRow();
//        if (piece.getColor() == master.colors.White) {
//            multiplyByMinus = -1;
//        }
//        if (destination.getCol() == piece.getLoc().getCol()&&getPiece(destination)==null) {
//            if (destination.getRow() == piece.getLoc().getRow() + 1 * multiplyByMinus)
//                return true;
//
//            if (piece.getLoc().getRow() == firstRow && destination.getRow() == piece.getLoc().getRow() + 2 * multiplyByMinus)
//                return true;
//        }
//        if(piece.getColor()==master.colors.White) {
//            if (getPiece(destination) != null && piece.getLoc().getCol() == destination.getCol() + 1 && piece.getLoc().getRow() == destination.getRow() + 1)
//                return true;
//            if (getPiece(destination) != null && piece.getLoc().getCol() == destination.getCol() - 1 && piece.getLoc().getRow() == destination.getRow() + 1)
//                return true;
//        }
//        else{
//            if (getPiece(destination) != null && piece.getLoc().getCol() == destination.getCol() + 1 && piece.getLoc().getRow() == destination.getRow() - 1)
//                return true;
//            if (getPiece(destination) != null && piece.getLoc().getCol() == destination.getCol() - 1 && piece.getLoc().getRow() == destination.getRow() - 1)
//                return true;
//        }
//
//            return false;
//    }
//
//    static boolean canRookMove(Piece piece, Location destination) {
//        boolean res=false;
//        int Pcol = piece.getLoc().getCol(),Dcol = destination.getCol();
//        int Prow = piece.getLoc().getRow(),Drow = destination.getRow();
//        if(Dcol==7&&Pcol==7)
//        {
//            System.out.println("7,7");
//        }
//        if ( Dcol== Pcol ||Drow == Prow) {
//            res=true;
//            if(Drow == Prow) {
//                if (Dcol>Pcol)
//                for (int i = Pcol; i < Dcol; i++) {
//                    if (getPiece(mat[Prow][i]) != null)
//                        res = false;
//
//                }
//                else
//                    for (int i = Dcol; i < Pcol; i++) {
//                        if (getPiece(mat[Prow][i]) != null)
//                            res = false;
//
//                    }
//
//            }
//            if(Dcol == Pcol) {
//                if(Drow>Prow)
//                for (int i = Prow; i < Drow; i++) {
//                    if (getPiece(mat[i][Dcol]) != null)
//                        res = false;
//
//                }
//                else
//                    for (int i = Drow; i < Prow; i++) {
//                        if (getPiece(mat[i][Dcol]) != null)
//                            res = false;
//
//                    }
//            }
//
//
//        }
//
//        return res;
//    }
//    static boolean canKnightMove(Piece piece, Location destination) {
//        int PCol = piece.getLoc().getCol(), DCol = destination.getCol();
//        int PRow = piece.getLoc().getRow(), DRow = destination.getRow();
//
//        if (PCol == DCol + 1 || PCol == DCol - 1)
//            if (PRow == DRow + 2 || PRow == DRow - 2)
//                return true;
//        if (PCol == DCol + 2 || PCol == DCol - 2)
//            if (PRow == DRow + 1 || PRow == DRow - 1)
//                return true;
//
//        return false;
//    }
//
//    static boolean canQueenMove(Piece piece, Location destination) {
//        return (canBishopMove(piece, destination) || canRookMove(piece, destination));
//    }
//
//    static boolean canBishopMove(Piece piece, Location destination) {
//        int PCol = piece.getLoc().getCol(), DCol = destination.getCol();
//        int PRow = piece.getLoc().getRow(), DRow = destination.getRow();
//
//        for (int i = 0; i < ROWS; i++) {
//            if (PCol == DCol + i && PRow == DRow + i)
//                return true;
//        }
//        for (int i = 0; i < ROWS; i++) {
//            if (PCol == DCol - i && PRow == DRow - i)
//                return true;
//        }
//        for (int i = 0; i < ROWS; i++) {
//            if (PCol == DCol + i && PRow == DRow - i)
//                return true;
//        }
//        for (int i = 0; i < ROWS; i++) {
//            if (PCol == DCol - i && PRow == DRow + i)
//                return true;
//        }
//
//
//        return false;
//    }
//
//    static boolean canKingMove(Piece piece, Location destination) {
//        int PCol = piece.getLoc().getCol(), DCol = destination.getCol();
//        int PRow = piece.getLoc().getRow(), DRow = destination.getRow();
//
//        if (PCol == DCol && PRow == DRow + 1
//        ||PCol == DCol + 1 && PRow == DRow
//        ||PCol == DCol && PRow == DRow - 1
//        ||PCol == DCol - 1 && PRow == DRow
//        ||PCol == DCol + 1 && PRow == DRow + 1
//        ||PCol == DCol - 1 && PRow == DRow - 1
//        ||PCol == DCol + 1 && PRow == DRow - 1
//        ||PCol == DCol - 1 && PRow == DRow + 1)
//            return true;
//
//        return false;
//    }
//
}


