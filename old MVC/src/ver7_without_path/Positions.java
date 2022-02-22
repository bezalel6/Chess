package ver7_without_path;

import ver7_without_path.types.*;

public class Positions {
    private final String p = Piece.types.PAWN.ordinal() + "", n = Piece.types.KNIGHT.ordinal() + "", b = Piece.types.BISHOP.ordinal() + "", r = Piece.types.ROOK.ordinal() + "", k = Piece.types.KING.ordinal() + "", q = Piece.types.QUEEN.ordinal() + "";
    private final String w = Piece.colors.WHITE.ordinal() + "", bl = Piece.colors.BLACK.ordinal() + "";

    public static String[] piecesToPos(Piece[] pieces) {
        String[] ret = new String[pieces.length];
        for (int i = 0; i < pieces.length; i++) {
            ret[i] = pieceToStr(pieces[i]);
        }
        return ret;
    }

    private static String pieceToStr(Piece piece) {
        return piece.getPieceColor().ordinal() + "" + piece.getPieceType().ordinal() + "" + piece.getLoc().getStr();
    }

    public static Piece[] posToPieces(String[] strArr) {
        Piece[] ret = new Piece[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            ret[i] = strToPiece(strArr[i]);
        }
        return ret;
    }

    private static Piece strToPiece(String str) {
        Location loc = new Location(str.substring(2));
        Piece.colors color = Piece.colors.values()[str.charAt(0) - '0'];
        Piece.types type = Piece.types.values()[str.charAt(1) - '0'];
        switch (type) {
            case PAWN:
                return new Pawn(loc, color);
            case BISHOP:
                return new Bishop(loc, color);
            case KNIGHT:
                return new Knight(loc, color);
            case ROOK:
                return new Rook(loc, color);
            case QUEEN:
                return new Queen(loc, color);
            default:
                return new King(loc, color);
        }
    }


    public Piece[] startingPos() {
        return posToPieces(new String[]{
                w + r + "a1", w + n + "b1", w + b + "c1", w + q + "d1", w + k + "e1", w + b + "f1", w + n + "g1", w + r + "h1",
                w + p + "a2", w + p + "b2", w + p + "c2", w + p + "d2", w + p + "e2", w + p + "f2", w + p + "g2", w + p + "h2",

                bl + r + "a8", bl + n + "b8", bl + b + "c8", bl + q + "d8", bl + k + "e8", bl + b + "f8", bl + n + "g8", bl + r + "h8",
                bl + p + "a7", bl + p + "b7", bl + p + "c7", bl + p + "d7", bl + p + "e7", bl + p + "f7", bl + p + "g7", bl + p + "h7"
        });
    }

    public Piece[] readyToCastle() {
        return posToPieces(new String[]{
                w + r + "a1", w + k + "e1", w + r + "h1",
                w + p + "a2", w + p + "b2", w + p + "c2", w + p + "d2", w + p + "e2", w + p + "f2", w + p + "g2", w + p + "h2",

                bl + r + "a8", bl + k + "e8", bl + r + "h8",
                bl + p + "a7", bl + p + "b7", bl + p + "c7", bl + p + "d7", bl + p + "e7", bl + p + "f7", bl + p + "g7", bl + p + "h7"
        });
    }

    public Piece[] twoKings() {
        return posToPieces(new String[]{w + k + "a2", bl + k + "f3", bl + p + "d8"});
    }

    public Piece[] endgame() {
        return posToPieces(new String[]{w + k + "a2", bl + k + "f3", w + q + "e7", w + q + "d8"});
    }

}
