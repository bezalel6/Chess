package ver14.SharedClasses.Game.moves;


import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.evaluation.Evaluation;
import ver14.SharedClasses.Game.evaluation.GameStatus;
import ver14.SharedClasses.Game.pieces.Piece;
import ver14.SharedClasses.Game.pieces.PieceType;


public class Move extends BasicMove implements Comparable<Move> {
    private static final PieceType NOT_CAPTURING = null;
    BasicMove intermediateMove;

    private String moveAnnotation = null;
    private ThreefoldStatus threefoldStatus;
    private PieceType capturingPieceType;
    private Location enPassantLoc;
    private PieceType promotingTo;
    private boolean isReversible;
    private Evaluation moveEvaluation;
    private MoveType moveType;
    private PlayerColor movingPlayerColor;
    private byte disabledCastling;
    private int prevFullMoveClock;
    private int prevHalfMoveClock;

    public Move(Location movingFrom, Location movingTo, PieceType capturingPieceType) {
        this(movingFrom, movingTo);
        this.capturingPieceType = capturingPieceType;
    }

    public Move(Location movingFrom, Location movingTo) {
        super(movingFrom, movingTo);
        this.movingPlayerColor = null;
        this.capturingPieceType = NOT_CAPTURING;
        this.intermediateMove = null;
        this.promotingTo = null;
        this.moveType = MoveType.NormalMove;
        this.disabledCastling = 0;
        this.threefoldStatus = ThreefoldStatus.NONE;
    }

    public Move(ThreefoldStatus threefoldStatus) {
        super(null, null);
        this.threefoldStatus = threefoldStatus;
    }

    public Move(Move other) {
        super(other);
        this.disabledCastling = other.disabledCastling;
        this.movingPlayerColor = other.movingPlayerColor;
        this.capturingPieceType = other.capturingPieceType;
        this.promotingTo = other.promotingTo;
        this.moveType = other.moveType;
        this.isReversible = other.isReversible;
        this.prevHalfMoveClock = other.prevHalfMoveClock;
        this.prevFullMoveClock = other.prevFullMoveClock;
        this.threefoldStatus = other.threefoldStatus;
        this.enPassantLoc = other.enPassantLoc;

        if (other.moveEvaluation != null)
            this.moveEvaluation = new Evaluation(other.moveEvaluation);
        if (other.intermediateMove != null)
            this.intermediateMove = new BasicMove(other.intermediateMove);

    }

    public static Move castling(Location movingFrom, Location movingTo, CastlingRights.Side side) {
        Move move = new Move(movingFrom, movingTo);
        move.setMoveFlag(MoveType.CASTLING_FLAGS[side.asInt]);
        Location rookOrigin = Location.getLoc(movingFrom.row, side.rookStartingCol);
        Location rookDestination = Location.getLoc(movingFrom.row, side.castledRookCol);
        assert rookOrigin != null && rookDestination != null;
        move.intermediateMove = new BasicMove(rookOrigin, rookDestination);
        return move;
    }

    public static Move threefoldClaim() {
        return new Move(ThreefoldStatus.CLAIMED);
    }

    public static Move flipMove(Move move) {
        Move ret = copyMove(move);
        ret.flip();
        if (ret.intermediateMove != null) {
            ret.intermediateMove.flip();
        }
        return ret;
    }

    public static Move copyMove(Move move) {
        return new Move(move);
    }

    public void setMoveAnnotation(String moveAnnotation) {
        this.moveAnnotation = moveAnnotation;
    }

    public byte getDisabledCastling() {
        return disabledCastling;
    }

    public void setDisabledCastling(byte disabledCastling) {
        this.disabledCastling = disabledCastling;
    }

    public void setThreefoldOption() {
        threefoldStatus = ThreefoldStatus.CAN_CLAIM;
    }

    public PlayerColor getMovingColor() {
        return movingPlayerColor;
    }

    public void setMovingColor(PlayerColor movingPlayerColor) {
        this.movingPlayerColor = movingPlayerColor;
    }

    public int getPrevFullMoveClock() {
        return prevFullMoveClock;
    }

    public void setPrevFullMoveClock(int prevFullMoveClock) {
        this.prevFullMoveClock = prevFullMoveClock;
    }

    public int getPrevHalfMoveClock() {
        return prevHalfMoveClock;
    }

    public void setPrevHalfMoveClock(int prevHalfMoveClock) {
        this.prevHalfMoveClock = prevHalfMoveClock;
    }

    public PieceType getPromotingTo() {
        return promotingTo;
    }

    public void setPromotingTo(PieceType promotingTo) {
        this.moveType = MoveType.Promotion;
        this.promotingTo = promotingTo;
    }

    public BasicMove getIntermediateMove() {
        return intermediateMove;
    }

    public void setIntermediateMove(BasicMove intermediateMove) {
        this.intermediateMove = intermediateMove;
    }

    public boolean isCheck() {
        return moveEvaluation != null && moveEvaluation.isCheck();
    }

    public Evaluation getMoveEvaluation() {
        if (threefoldStatus == ThreefoldStatus.CLAIMED) {
            //fixme
            return new Evaluation(GameStatus.threeFoldRepetition(), PlayerColor.WHITE);
        }
        return moveEvaluation;
    }
    //endregion

    public void setMoveEvaluation(Evaluation moveEvaluation) {
        if (moveEvaluation != null) {
            this.moveEvaluation = new Evaluation(moveEvaluation);
        } else {
            this.moveEvaluation = null;
        }
    }

    public PieceType getCapturingPieceType() {
        return capturingPieceType;
    }

    public boolean isReversible() {
        return isReversible;
    }

    public void setReversible(Piece movingPiece) {
        isReversible = !(isCapturing() || movingPiece.pieceType == PieceType.PAWN);
    }

    public boolean isCapturing() {
        return capturingPieceType != NOT_CAPTURING;
    }

    public void setCapturing(PieceType pieceType) {
        this.capturingPieceType = pieceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof BasicMove basicMove && super.equals(basicMove);

    }

    @Override
    public String toString() {
        return getAnnotation();
    }

    public String getAnnotation() {
        return moveAnnotation == null ? MoveAnnotation.basicAnnotate(this) : moveAnnotation;
    }

    @Override
    public int compareTo(Move o) {
        if (moveEvaluation != null) {
            if (o.moveEvaluation != null)
                return Double.compare(moveEvaluation.getEval(), o.moveEvaluation.getEval());
            return 1;
        }
        if (o.moveEvaluation != null)
            return -1;
        return Double.compare(guessEval(), o.guessEval());
    }

    private double guessEval() {
        double ret = 0;
//        if (disabledCastling != 0 && movingPlayerColor != null) {
//            int penalty = 100000;
//            ret += (CastlingRights.whosCastling(disabledCastling) == movingPlayerColor && !getMoveFlag().isCastling) ? -penalty : penalty;
//        }

        if (capturingPieceType != NOT_CAPTURING) {
            ret += 10 - capturingPieceType.value;
            if (moveEvaluation != null && moveEvaluation.isCheck())
                ret += 100000000;
        }
        if (moveType == MoveType.Promotion) {
            ret += 5 * promotingTo.value;
        } else if (moveType == MoveType.EnPassant) {
            ret += 0.00001;
        } else if (moveType.isCastling) {
            ret += 5;
        }
        ret /= 100000;
        if (moveEvaluation != null) {
            ret += moveEvaluation.getEval();
        }

        return ret;
    }

    public MoveType getMoveFlag() {
        return moveType;
    }

    public void setMoveFlag(MoveType moveType) {
        this.moveType = moveType;
    }

    public Location getEnPassantLoc() {
        return enPassantLoc;
    }

    public void setEnPassantLoc(Location epsnLoc) {
        enPassantLoc = epsnLoc;
    }

    public void setNotReversible() {
        isReversible = false;
    }

    public ThreefoldStatus getThreefoldStatus() {
        return threefoldStatus;
    }

    public void setThreefoldStatus(ThreefoldStatus threefoldStatus) {
        this.threefoldStatus = threefoldStatus;
    }

    public String getGameStatusStr() {
        return moveEvaluation != null ? moveEvaluation.getGameStatus().getGameStatusType().annotation : "";
    }

    public enum ThreefoldStatus {
        NONE, CAN_CLAIM, CLAIMED;
    }

    public enum MoveType {
        NormalMove, EnPassant, DoublePawnPush, Promotion, ShortCastle(CastlingRights.Side.KING), LongCastle(CastlingRights.Side.QUEEN);
        public final static MoveType[] CASTLING_FLAGS;

        static {
            CASTLING_FLAGS = new MoveType[CastlingRights.Side.SIDES.length];
            for (CastlingRights.Side side : CastlingRights.Side.SIDES) {
                CASTLING_FLAGS[side.asInt] = side == CastlingRights.Side.KING ? ShortCastle : LongCastle;
            }
        }

        public final boolean isCastling;
        public final CastlingRights.Side castlingSide;

        MoveType() {
            this(null);
        }

        MoveType(CastlingRights.Side side) {
            this.isCastling = side != null;
            this.castlingSide = side;
        }
    }

}