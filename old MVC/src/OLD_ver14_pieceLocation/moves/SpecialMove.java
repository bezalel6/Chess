package OLD_ver14_pieceLocation.moves;

abstract class SpecialMove extends Move {
    private SpecialMoveType moveType;

    public SpecialMove(Move move, SpecialMoveType moveType) {
        super(move);
        this.moveType = moveType;
    }

}
