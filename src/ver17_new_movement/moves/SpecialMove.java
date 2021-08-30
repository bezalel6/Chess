package ver17_new_movement.moves;

abstract class SpecialMove extends Move {
    private SpecialMoveType moveType;

    public SpecialMove(Move move, SpecialMoveType moveType) {
        super(move);
        this.moveType = moveType;
    }

    public SpecialMoveType getMoveType() {
        return moveType;
    }
}

