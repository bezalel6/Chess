package ver24_flip_board.model_classes.eval_classes;

import ver24_flip_board.moves.MinimaxMove;
import ver24_flip_board.moves.Move;

import java.util.ArrayList;
import java.util.Collections;

public class ExtendedEvaluation extends Evaluation {
    private final ArrayList<MinimaxMove> minimaxMoves;
    private final Move rootMove;
    private boolean initializedMoves = false;

    public ExtendedEvaluation(Evaluation other, Move rootMove) {
        super(other);
        this.rootMove = rootMove;
        this.minimaxMoves = new ArrayList<>();
    }

    public Move getRootMove() {
        return rootMove;
    }

    public boolean isInitializedMoves() {
        return initializedMoves;
    }

    public void addOrOverwriteMove(MinimaxMove minimaxMove) {
        initializedMoves = true;
        int index = -1;
        for (int i = 0, minimaxMovesSize = minimaxMoves.size(); i < minimaxMovesSize; i++) {
            MinimaxMove move = minimaxMoves.get(i);
            if (move.getMove().equals(minimaxMove.getMove())) {
                index = i;
            }
        }
        minimaxMove = new MinimaxMove(minimaxMove);
        if (index != -1)
            minimaxMoves.set(index, minimaxMove);
        else minimaxMoves.add(minimaxMove);
    }

    public ArrayList<MinimaxMove> getMinimaxMoves() {
        return minimaxMoves;
    }

    public ArrayList<Move> getMoves() {
        ArrayList<Move> ret = new ArrayList<>();
        for (MinimaxMove minimaxMove : minimaxMoves)
            ret.add(minimaxMove.getMove());
        return ret;
    }

    public void sortMoves(boolean ascendingOrder) {
        Collections.sort(minimaxMoves);
        if (!ascendingOrder)
            Collections.reverse(minimaxMoves);
    }


}
