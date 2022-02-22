package ver5;

import ver5.types.Piece;

public class Positions {
    private final int p = Piece.PAWN, n = Piece.KNIGHT, b = Piece.BISHOP, r = Piece.ROOK, k = Piece.KING, q = Piece.QUEEN;
    private final int w = Piece.WHITE, bl = Piece.BLACK;

    public String[] startingPos = {
            w + "" + r + "a1", w + "" + n + "b1", w + "" + b + "c1", w + "" + q + "d1", w + "" + k + "e1", w + "" + b + "f1", w + "" + n + "g1", w + "" + r + "h1",
            w + "" + p + "a2", w + "" + p + "b2", w + "" + p + "c2", w + "" + p + "d2", w + "" + p + "e2", w + "" + p + "f2", w + "" + p + "g2", w + "" + p + "h2",

            bl + "" + r + "a8", bl + "" + n + "b8", bl + "" + b + "c8", bl + "" + q + "d8", bl + "" + k + "e8", bl + "" + b + "f8", bl + "" + n + "g8", bl + "" + r + "h8",
            bl + "" + p + "a7", bl + "" + p + "b7", bl + "" + p + "c7", bl + "" + p + "d7", bl + "" + p + "e7", bl + "" + p + "f7", bl + "" + p + "g7", bl + "" + p + "h7"
    };
    public String[] readyToCastle = {
            w + "" + r + "a1", w + "" + k + "e1", w + "" + r + "h1",
            w + "" + p + "a2", w + "" + p + "b2", w + "" + p + "c2", w + "" + p + "d2", w + "" + p + "e2", w + "" + p + "f2", w + "" + p + "g2", w + "" + p + "h2",

            bl + "" + r + "a8", bl + "" + k + "e8", bl + "" + r + "h8",
            bl + "" + p + "a7", bl + "" + p + "b7", bl + "" + p + "c7", bl + "" + p + "d7", bl + "" + p + "e7", bl + "" + p + "f7", bl + "" + p + "g7", bl + "" + p + "h7"
    };
    public String[] twoKings = {w + "" + k + "a2", bl + "" + k + "f3",bl+""+p+"d8"};
    public String[] endgame = {w + "" + k + "a2", bl + "" + k + "f3",w+""+q+"e7",w+""+q+"d8"};


}
