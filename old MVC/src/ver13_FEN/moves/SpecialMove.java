package ver13_FEN.moves;

abstract class SpecialMove extends Move {
    private SpecialMoveType moveType;

    public SpecialMove(Move move, SpecialMoveType moveType) {
        super(move);
        this.moveType = moveType;
    }

}

