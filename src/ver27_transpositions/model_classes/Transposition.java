package ver27_transpositions.model_classes;

import ver27_transpositions.model_classes.moves.Move;

import java.util.ArrayList;

public class Transposition {
    private ArrayList<Move> possibleMoves;
    private int depth;

    public Transposition(Transposition other) {
        this.possibleMoves = other.getPossibleMoves();
        this.depth = other.depth;
    }

    public Transposition(ArrayList<Move> possibleMoves) {
        setPossibleMoves(possibleMoves);
    }

    public ArrayList<Move> getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(ArrayList<Move> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

}
