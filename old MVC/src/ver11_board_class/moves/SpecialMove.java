package ver11_board_class.moves;

import ver11_board_class.Board;

abstract class SpecialMove extends Move {
    private SpecialMoveType moveType;

    public SpecialMove(Move move, SpecialMoveType moveType, String annotation, Board board) {
        super(new Move(move, board), annotation, board);
        this.moveType = moveType;
    }

    public SpecialMove(Move move, SpecialMoveType moveType, Board board) {
        super(move, board);
        this.moveType = moveType;
    }

}

