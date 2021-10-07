package ver27_transpositions.model_classes;

import ver27_transpositions.model_classes.moves.Move;

import java.util.ArrayList;

public class Transposition {
    private ArrayList<Move> possibleMoves;

    public Transposition(Transposition other) {
        setPossibleMoves(other.getPossibleMoves());
    }

    public Transposition(ArrayList<Move> possibleMoves) {
        setPossibleMoves(possibleMoves);
    }

    public ArrayList<Move> getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(ArrayList<Move> possibleMoves) {
        this.possibleMoves = new ArrayList<>(possibleMoves);
    }

}
