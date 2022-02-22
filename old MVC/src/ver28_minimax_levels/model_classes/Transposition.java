package ver28_minimax_levels.model_classes;

import ver28_minimax_levels.model_classes.moves.MinimaxMove;

import java.util.ArrayList;
import java.util.Collections;

public class Transposition {
    private ArrayList<MinimaxMove> bottomMoves;
    private int maxDepth;

    public Transposition(MinimaxMove minimaxMove, int maxDepth) {
        bottomMoves = new ArrayList<>();
        bottomMoves.add(minimaxMove);
        this.maxDepth = maxDepth;
    }

    public void addMove(MinimaxMove minimaxMove) {
        bottomMoves.add(minimaxMove);
    }

    public ArrayList<MinimaxMove> getBottomMoves() {
//        Collections.sort(bottomMoves);
//        Collections.reverse(bottomMoves);
        return bottomMoves;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
