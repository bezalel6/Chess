package ver36_no_more_location.model_classes.moves;

import ver36_no_more_location.Location;
import ver36_no_more_location.model_classes.Board;
import ver36_no_more_location.model_classes.eval_classes.Evaluation;
import ver36_no_more_location.model_classes.pieces.Piece;
import ver36_no_more_location.model_classes.pieces.PieceType;

import java.util.Objects;


public class Move extends BasicMove implements Comparable<Move> {
    public static final PieceType NOT_CAPTURING = null;
    public static final int TEMP_CAPTURING = -2;
    MoveAnnotation moveAnnotation;
    BasicMove intermediateMove;
    private PieceType capturingPieceType;
    private Location enPassantLoc;
    private PieceType promotingTo;
    private boolean isReversible;
    private Evaluation moveEvaluation;
    private MoveFlag moveFlag;

    private int prevFullMoveClock;
    private int prevCastlingAbility;
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
    }

    public Move(Move other) {
        super(other);

        this.capturingPieceType = other.capturingPieceType;
        this.prevCastlingAbility = other.prevCastlingAbility;
        this.promotingTo = other.promotingTo;
        this.moveFlag = other.moveFlag;
        this.isReversible = other.isReversible;
        this.prevHalfMoveClock = other.prevHalfMoveClock;
        this.prevFullMoveClock = other.prevFullMoveClock;

        if (other.enPassantLoc != null)
            this.enPassantLoc = other.enPassantLoc;
        if (other.moveEvaluation != null)
            this.moveEvaluation = new Evaluation(other.moveEvaluation);
        if (other.intermediateMove != null)
            this.intermediateMove = new BasicMove(other.intermediateMove);

        this.moveAnnotation = new MoveAnnotation(other.moveAnnotation);

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
        if (move instanceof Castling)
            return new Castling((Castling) move);
        else
            return new Move(move);
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
    //endregion

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return capturingPieceType == move.capturingPieceType && promotingTo == move.promotingTo && isReversible == move.isReversible && Objects.equals(intermediateMove, move.intermediateMove) && Objects.equals(enPassantLoc, move.enPassantLoc) && moveFlag == move.moveFlag;
    }

    public MoveAnnotation getMoveAnnotation() {
        return moveAnnotation;
    }

    public String getAnnotation() {
        return moveAnnotation.toString();
    }

    public boolean isCapturing() {
        return capturingPieceType != NOT_CAPTURING;
    }

    public void setCapturing(PieceType pieceType) {
        this.capturingPieceType = pieceType;
        moveAnnotation.setCapture(capturingPieceType);
    }

    @Override
    public String toString() {
        return getAnnotation();
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

    public Location getEnPassantLoc() {
        return enPassantLoc;
    }

    public void setEnPassantLoc(Location epsnLoc) {
        enPassantLoc = epsnLoc;
    }

    public int getPrevCastlingAbility() {
        return prevCastlingAbility;
    }

    public void setPrevCastlingAbility(int prevCastlingAbility) {
        this.prevCastlingAbility = prevCastlingAbility;
    }

    public enum MoveFlag {NormalMove, EnPassant, DoublePawnPush, Promotion}

}