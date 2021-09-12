package ver22_eval_captures.moves;

import ver22_eval_captures.Error;
import ver22_eval_captures.Location;

public class DoublePawnPush extends Move {
    private Location enPassantTargetSquare;


    public DoublePawnPush(Move move, Location enPassantTargetSquare) {
        super(move);
        this.enPassantTargetSquare = enPassantTargetSquare;
    }

    public DoublePawnPush(DoublePawnPush move) {
        super(move);
        this.enPassantTargetSquare = move.enPassantTargetSquare;
    }

//    void copyConstructor(Move move) {
//        super.copyConstructor(move);
//        if (move instanceof DoublePawnPush) {
//            enPassantTargetSquare = new Location(((DoublePawnPush) move).enPassantTargetSquare);
//        } else Error.error("");
//    }

    public Location getEnPassantTargetSquare() {
        return enPassantTargetSquare;
    }
}
