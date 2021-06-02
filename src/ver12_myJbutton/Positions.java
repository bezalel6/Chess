package ver12_myJbutton;

import ver12_myJbutton.types.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Positions {
    private final static String p = Piece.types.PAWN.ordinal() + "", n = Piece.types.KNIGHT.ordinal() + "", b = Piece.types.BISHOP.ordinal() + "", r = Piece.types.ROOK.ordinal() + "", k = Piece.types.KING.ordinal() + "", q = Piece.types.QUEEN.ordinal() + "";
    private final static String w = Piece.Player.WHITE.ordinal() + "", bl = Piece.Player.BLACK.ordinal() + "";
    private static String[] start = new String[]{"Starting Position,",
            w + r + "a1", w + n + "b1", w + b + "c1", w + q + "d1", w + k + "e1", w + b + "f1", w + n + "g1", w + r + "h1",
            w + p + "a2", w + p + "b2", w + p + "c2", w + p + "d2", w + p + "e2", w + p + "f2", w + p + "g2", w + p + "h2",

            bl + r + "a8", bl + n + "b8", bl + b + "c8", bl + q + "d8", bl + k + "e8", bl + b + "f8", bl + n + "g8", bl + r + "h8",
            bl + p + "a7", bl + p + "b7", bl + p + "c7", bl + p + "d7", bl + p + "e7", bl + p + "f7", bl + p + "g7", bl + p + "h7"
    };
    private static String[] castle = new String[]{"Ready To Castle Position,",
            w + r + "a1", w + k + "e1", w + r + "h1",
            w + p + "a2", w + p + "b2", w + p + "c2", w + p + "d2", w + p + "e2", w + p + "f2", w + p + "g2", w + p + "h2",

            bl + r + "a8", bl + k + "e8", bl + r + "h8",
            bl + p + "a7", bl + p + "b7", bl + p + "c7", bl + p + "d7", bl + p + "e7", bl + p + "f7", bl + p + "g7", bl + p + "h7"
    };
    private static String[] two_kings = new String[]{"Two Kings Position,", w + k + "a2", bl + k + "f3", bl + p + "d4"};
    private static String[] end = new String[]{"Endgame Position,", w + k + "a2", bl + k + "f3", w + q + "e7", w + q + "d8"};
    public static String[][] getAllPositions = {start, end, castle, two_kings};
    private final boolean t = true, f = false;

    public static ArrayList<String[]> getAllPositionsNamesAndIndexes() {
        ArrayList<String[]> ret = new ArrayList();
        for (int i = 0; i < getAllPositions.length; i++) {
            String[] row = getAllPositions[i];
            ret.add(new String[]{row[0].substring(0, row[0].length() - 1), i + ""});
        }
        return ret;
    }

    public static String[] piecesToPos(Piece[] board) {
        String[] ret = new String[board.length];
        for (int i = 0; i < board.length; i++) {
            ret[i] = board[i] == null ? null : pieceToStr(board[i], board[i].getHasMoved());
        }
        return ret;
    }

    public static String pieceToStr(Piece piece, boolean hasPieceMoved) {
        Location pieceLoc = piece.getLoc();
        return piece.getPieceColor().ordinal() + "" + piece.getPieceType().ordinal() + "" + pieceLoc.toString() + (hasPieceMoved ? 1 : 0);
    }

    public static Piece[] posToPieces(String[] strArr) {
        if (strArr[0].indexOf(',') != -1) {
            strArr = Arrays.copyOfRange(strArr, 1, strArr.length);
        }
        Piece[] ret = new Piece[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            ret[i] = strArr[i] == null ? null : strToPiece(strArr[i]);
        }
        return ret;
    }

    public static Piece strToPiece(String str) {
        Location loc = new Location(str.substring(2, 4));
        boolean hasMoved = false;
        if (str.length() >= 5)
            hasMoved = str.charAt(4) == '1';
        Piece.Player player = Piece.Player.values()[Integer.parseInt(str.charAt(0) + "")];
        Piece.types type = Piece.types.values()[Integer.parseInt(str.charAt(1) + "")];
        switch (type) {
            case PAWN:
                return new Pawn(loc, player, hasMoved);
            case BISHOP:
                return new Bishop(loc, player, hasMoved);
            case KNIGHT:
                return new Knight(loc, player, hasMoved);
            case ROOK:
                return new Rook(loc, player, hasMoved);
            case QUEEN:
                return new Queen(loc, player, hasMoved);
            default:
                return new King(loc, player, hasMoved);
        }
    }


    public Piece[] startingPos() {
        return posToPieces(start);
    }


}
