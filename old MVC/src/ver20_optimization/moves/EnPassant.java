package ver20_optimization.moves;

public class EnPassant extends Move {
    public EnPassant(Move move, int capturingPieceHash) {
        super(move, capturingPieceHash);
    }

    public EnPassant(EnPassant move) {
        super(move);
    }
}
