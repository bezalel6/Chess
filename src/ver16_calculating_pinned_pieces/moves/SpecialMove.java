package ver16_calculating_pinned_pieces.moves;

abstract class SpecialMove extends Move {
    private SpecialMoveType moveType;

    public SpecialMove(Move move, SpecialMoveType moveType) {
        super(move);
        this.moveType = moveType;
    }

}

