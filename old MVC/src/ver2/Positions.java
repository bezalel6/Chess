package ver2;

import ver2.types.*;

import java.util.ArrayList;

public class Positions {
    public static ArrayList<Piece> startingPos()
    {
        ArrayList<Piece> ret = new ArrayList<Piece>();

        //White:

        ret.add(new Rook(new Location("a1"), Piece.colors.White));
        ret.add(new Rook(new Location("h1"), Piece.colors.White));

        ret.add(new Knight(new Location("b1"), Piece.colors.White));
        ret.add(new Knight(new Location("g1"), Piece.colors.White));

        ret.add(new Bishop(new Location("c1"), Piece.colors.White));
        ret.add(new Bishop(new Location("f1"), Piece.colors.White));

        ret.add(new Queen(new Location("d1"), Piece.colors.White));
        ret.add(new King(new Location("e1"), Piece.colors.White));

        for (int i = 0; i < 8; i++) {
            char col = 'a';
            col+=i;
            ret.add(new Pawn(new Location(col+""+2), Piece.colors.White));

        }

        //Black
        ret.add(new Rook(new Location("a8"), Piece.colors.Black));
        ret.add(new Rook(new Location("h8"), Piece.colors.Black));

        ret.add(new Knight(new Location("b8"), Piece.colors.Black));
        ret.add(new Knight(new Location("g8"), Piece.colors.Black));

        ret.add(new Bishop(new Location("c8"), Piece.colors.Black));
        ret.add(new Bishop(new Location("f8"), Piece.colors.Black));

        ret.add(new Queen(new Location("d8"), Piece.colors.Black));
        ret.add(new King(new Location("e8"), Piece.colors.Black));

        for (int i = 0; i < 8; i++) {
            char col = 'a';
            col+=i;
            ret.add(new Pawn(new Location(col+""+7), Piece.colors.Black));

        }

        return ret;
    }
}
