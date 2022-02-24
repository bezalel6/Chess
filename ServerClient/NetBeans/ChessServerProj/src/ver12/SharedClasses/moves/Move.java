package ver12.SharedClasses.moves;


import ver12.SharedClasses.Location;
import ver12.SharedClasses.PlayerColor;
import ver12.SharedClasses.evaluation.Evaluation;
import ver12.SharedClasses.evaluation.GameStatus;
import ver12.SharedClasses.pieces.Piece;
import ver12.SharedClasses.pieces.PieceType;


public class Move extends BasicMove implements Comparable<Move> {
    private static final PieceType NOT_CAPTURING = null;
    MoveAnnotation moveAnnotation;
    BasicMove intermediateMove;
    private ThreefoldStatus threefoldStatus;
    private PieceType capturingPieceType;
    private Location enPassantLoc;
    private PieceType promotingTo;
    private boolean isReversible;
    private Evaluation moveEvaluation;
    private MoveFlag moveFlag;
    private PlayerColor movingPlayerColor = null;
    private int prevFullMoveClock;
    private CastlingRights prevCastlingAbilities;
    private int prevHalfMoveClock;

    public Move(Location movingFrom, Location movingTo, PieceType capturingPieceType) {
        this(movingFrom, movingTo);
        this.capturingPieceType = capturingPieceType;
    }

    public Move(Location movingFrom, Location movingTo) {
        super(movingFrom, movingTo);
        this.capturingPieceType = NOT_CAPTURING;
        this.moveAnnotation = new MoveAnnotation(this);
        this.intermediateMove = null;
        this.promotingTo = null;
        this.moveFlag = MoveFlag.NormalMove;
        this.threefoldStatus = ThreefoldStatus.NONE;
    }

    public Move(ThreefoldStatus threefoldStatus) {
        super(null, null);
        this.threefoldStatus = threefoldStatus;
        this.moveAnnotation = new MoveAnnotation(this);
    }

    public Move(Move other) {
        super(other);
        this.movingPlayerColor = other.movingPlayerColor;
        this.capturingPieceType = other.capturingPieceType;
        this.prevCastlingAbilities = other.prevCastlingAbilities;
        this.promotingTo = other.promotingTo;
        this.moveFlag = other.moveFlag;
        this.isReversible = other.isReversible;
        this.prevHalfMoveClock = other.prevHalfMoveClock;
        this.prevFullMoveClock = other.prevFullMoveClock;
        this.threefoldStatus = other.threefoldStatus;

        if (other.enPassantLoc != null)
            this.enPassantLoc = other.enPassantLoc;
        if (other.moveEvaluation != null)
            this.moveEvaluation = new Evaluation(other.moveEvaluation);
        if (other.intermediateMove != null)
            this.intermediateMove = new BasicMove(other.intermediateMove);

        this.moveAnnotation = new MoveAnnotation(other.moveAnnotation);

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
        Move ret;
        if (move instanceof Castling)
            ret = new Castling((Castling) move);
        else
            ret = new Move(move);
        return ret;
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
        this.moveFlag = MoveFlag.Promotion;
        this.promotingTo = promotingTo;
        moveAnnotation.setPromotion(promotingTo);
    }

    public MoveFlag getMoveFlag() {
        return moveFlag;
    }

    public void setMoveFlag(MoveFlag moveFlag) {
        this.moveFlag = moveFlag;
    }

    public BasicMove getIntermediateMove() {
        return intermediateMove;
    }

    public void setIntermediateMove(BasicMove intermediateMove) {
        this.intermediateMove = intermediateMove;
    }
    //endregion

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

    public void setMoveEvaluation(Evaluation moveEvaluation) {
        if (moveEvaluation != null) {
            this.moveEvaluation = new Evaluation(moveEvaluation);
            moveAnnotation.setGameStatus(moveEvaluation.getGameStatus());
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
        isReversible = !(isCapturing() || movingPiece.getPieceType() == PieceType.PAWN);
    }

    public boolean isCapturing() {
        return capturingPieceType != NOT_CAPTURING;
    }

    public void setCapturing(PieceType pieceType) {
        this.capturingPieceType = pieceType;
        moveAnnotation.setCapture(capturingPieceType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return super.equals(move);
    }

    @Override
    public String toString() {
        return getAnnotation();
    }

    public String getAnnotation() {
        return moveAnnotation.toString();
    }

    public MoveAnnotation getMoveAnnotation() {
        return moveAnnotation;
    }

    @Override
    public int compareTo(Move o) {
        if (moveEvaluation != null) {
            if (o.moveEvaluation != null) {
                return Double.compare(moveEvaluation.getEval(), o.moveEvaluation.getEval());
            }
            return 1;
        }
        if (o.moveEvaluation != null) {
            return -1;
        }
        return Double.compare(guessEval(this), guessEval(o));
    }

    private static double guessEval(Move move) {
        double ret = 0;
//        int capturingPieceType = move.getCapturingPieceType();
//        int movingPieceType = move.getMovingPieceType();
//        if (Piece.isValidPieceType(capturingPieceType)) {
//            ret += 10 * Piece.getPieceWorth(movingPieceType) - Piece.getPieceWorth(capturingPieceType);
//        }
        if (move.moveFlag == MoveFlag.Promotion) {
            ret += 5 * move.promotingTo.value;
        } else if (move.moveFlag == MoveFlag.EnPassant) {
            ret += 0.00001;
        } else if (move instanceof Castling) {
            ret += 10;
        }
        ret -= 100000;
        return ret;
    }

    public Location getEnPassantLoc() {
        return enPassantLoc;
    }

    public void setEnPassantLoc(Location epsnLoc) {
        enPassantLoc = epsnLoc;
    }

    public CastlingRights getPrevCastlingAbilities() {
        return prevCastlingAbilities;
    }

    public void setPrevCastlingAbilities(CastlingRights prevCastlingAbilities) {

        this.prevCastlingAbilities = prevCastlingAbilities;
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

    public enum ThreefoldStatus {
        NONE, CAN_CLAIM, CLAIMED;


    }

    public enum MoveFlag {NormalMove, EnPassant, DoublePawnPush, Promotion}

}