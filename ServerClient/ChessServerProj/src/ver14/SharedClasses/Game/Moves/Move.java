package ver14.SharedClasses.Game.Moves;


import ver14.SharedClasses.Game.Evaluation.Evaluation;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;


/**
 * Move - represents a "heavy" move. with a lot of info.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Move extends BasicMove implements Comparable<Move> {
    private static final PieceType NOT_CAPTURING = null;
    /**
     * The Intermediate move.
     */
    BasicMove intermediateMove;

    private MovesList creatorList = null;
    private String moveAnnotation = null;
    private ThreefoldStatus threefoldStatus;
    private PieceType capturingPieceType;
    private Location enPassantLoc;
    private PieceType promotingTo;
    private boolean isReversible;
    private Evaluation moveEvaluation;
    private MoveFlag moveFlag;
    private PlayerColor movingPlayerColor;
    private byte disabledCastling;
    private int prevFullMoveClock;
    private int prevHalfMoveClock;

    /**
     * Instantiates a new Move.
     *
     * @param movingFrom         the moving from
     * @param movingTo           the moving to
     * @param capturingPieceType the capturing piece type
     */
    public Move(Location movingFrom, Location movingTo, PieceType capturingPieceType) {
        this(movingFrom, movingTo);
        this.capturingPieceType = capturingPieceType;
    }

    /**
     * Instantiates a new Move.
     *
     * @param movingFrom the moving from
     * @param movingTo   the moving to
     */
    public Move(Location movingFrom, Location movingTo) {
        super(movingFrom, movingTo);
        this.movingPlayerColor = null;
        this.capturingPieceType = NOT_CAPTURING;
        this.intermediateMove = null;
        this.promotingTo = null;
        this.moveFlag = MoveFlag.NormalMove;
        this.disabledCastling = 0;
        this.threefoldStatus = ThreefoldStatus.NONE;
    }

    /**
     * Instantiates a new Move.
     *
     * @param threefoldStatus the threefold status
     */
    public Move(ThreefoldStatus threefoldStatus) {
        super(null, null);
        this.threefoldStatus = threefoldStatus;
    }

    /**
     * Instantiates a new Move.
     *
     * @param other the other
     */
    public Move(Move other) {
        super(other);
        this.moveAnnotation = other.moveAnnotation;
//        this.creatorList = other.creatorList;
        this.disabledCastling = other.disabledCastling;
        this.movingPlayerColor = other.movingPlayerColor;
        this.capturingPieceType = other.capturingPieceType;
        this.promotingTo = other.promotingTo;
        this.moveFlag = other.moveFlag;
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

    /**
     * Castling move.
     *
     * @param movingFrom the moving from
     * @param movingTo   the moving to
     * @param side       the side
     * @return the move
     */
    public static Move castling(Location movingFrom, Location movingTo, CastlingRights.Side side) {
        Move move = new Move(movingFrom, movingTo);
        move.setMoveFlag(MoveFlag.CASTLING_FLAGS[side.asInt]);
        Location rookOrigin = Location.getLoc(movingFrom.row, side.rookStartingCol);
        Location rookDestination = Location.getLoc(movingFrom.row, side.castledRookCol);
        assert rookOrigin != null && rookDestination != null;
        move.intermediateMove = new BasicMove(rookOrigin, rookDestination);
        return move;
    }

    /**
     * Threefold claim move.
     *
     * @return the move
     */
    public static Move threefoldClaim() {
        return new Move(ThreefoldStatus.CLAIMED);
    }

    /**
     * Flip move move.
     *
     * @param move the move
     * @return the move
     */
    public static Move flipMove(Move move) {
        Move ret = copyMove(move);
        ret.flip();
        if (ret.intermediateMove != null) {
            ret.intermediateMove.flip();
        }
        return ret;
    }

    /**
     * Copy move move.
     *
     * @param move the move
     * @return the move
     */
    public static Move copyMove(Move move) {
        return new Move(move);
    }

    /**
     * Gets creator list.
     *
     * @return the creator list
     */
    public MovesList getCreatorList() {
        return creatorList;
    }

    /**
     * Sets creator list.
     *
     * @param creatorList the creator list
     */
    public void setCreatorList(MovesList creatorList) {
        this.creatorList = creatorList;
    }

    /**
     * Sets move annotation.
     *
     * @param moveAnnotation the move annotation
     */
    public void setMoveAnnotation(String moveAnnotation) {
        this.moveAnnotation = moveAnnotation;
    }

    /**
     * Gets disabled castling.
     *
     * @return the disabled castling
     */
    public byte getDisabledCastling() {
        return disabledCastling;
    }

    /**
     * Sets disabled castling.
     *
     * @param disabledCastling the disabled castling
     */
    public void setDisabledCastling(byte disabledCastling) {
        this.disabledCastling = disabledCastling;
    }

    /**
     * Sets threefold option.
     */
    public void setThreefoldOption() {
        threefoldStatus = ThreefoldStatus.CAN_CLAIM;
    }

    /**
     * Gets moving color.
     *
     * @return the moving color
     */
    public PlayerColor getMovingColor() {
        return movingPlayerColor;
    }

    /**
     * Sets moving color.
     *
     * @param movingPlayerColor the moving player color
     */
    public void setMovingColor(PlayerColor movingPlayerColor) {
        this.movingPlayerColor = movingPlayerColor;
    }

    /**
     * Gets prev full move clock.
     *
     * @return the prev full move clock
     */
    public int getPrevFullMoveClock() {
        return prevFullMoveClock;
    }

    /**
     * Sets prev full move clock.
     *
     * @param prevFullMoveClock the prev full move clock
     */
    public void setPrevFullMoveClock(int prevFullMoveClock) {
        this.prevFullMoveClock = prevFullMoveClock;
    }

    /**
     * Gets prev half move clock.
     *
     * @return the prev half move clock
     */
    public int getPrevHalfMoveClock() {
        return prevHalfMoveClock;
    }

    /**
     * Sets prev half move clock.
     *
     * @param prevHalfMoveClock the prev half move clock
     */
    public void setPrevHalfMoveClock(int prevHalfMoveClock) {
        this.prevHalfMoveClock = prevHalfMoveClock;
    }

    /**
     * Gets promoting to.
     *
     * @return the promoting to
     */
    public PieceType getPromotingTo() {
        return promotingTo;
    }

    /**
     * Sets promoting to.
     *
     * @param promotingTo the promoting to
     */
    public void setPromotingTo(PieceType promotingTo) {
        this.moveFlag = MoveFlag.Promotion;
        this.promotingTo = promotingTo;
    }

    /**
     * Gets intermediate move.
     *
     * @return the intermediate move
     */
    public BasicMove getIntermediateMove() {
        return intermediateMove;
    }

    /**
     * Sets intermediate move.
     *
     * @param intermediateMove the intermediate move
     */
    public void setIntermediateMove(BasicMove intermediateMove) {
        this.intermediateMove = intermediateMove;
    }

    /**
     * Is check boolean.
     *
     * @return the boolean
     */
    public boolean isCheck() {
        return moveEvaluation != null && moveEvaluation.isCheck();
    }

    /**
     * Gets move evaluation.
     *
     * @return the move evaluation
     */
    public Evaluation getMoveEvaluation() {
        if (threefoldStatus == ThreefoldStatus.CLAIMED) {
            //fixme
            return new Evaluation(GameStatus.threeFoldRepetition(), PlayerColor.WHITE);
        }
        return moveEvaluation;
    }
    //endregion

    /**
     * Sets move evaluation.
     *
     * @param moveEvaluation the move evaluation
     */
    public void setMoveEvaluation(Evaluation moveEvaluation) {
        if (moveEvaluation != null) {
            this.moveEvaluation = new Evaluation(moveEvaluation);
        } else {
            this.moveEvaluation = null;
        }
    }

    /**
     * Gets capturing piece type.
     *
     * @return the capturing piece type
     */
    public PieceType getCapturingPieceType() {
        return capturingPieceType;
    }

    /**
     * Is reversible boolean.
     *
     * @return the boolean
     */
    public boolean isReversible() {
        return isReversible;
    }

    /**
     * Sets reversible.
     *
     * @param reversible the reversible
     */
    public void setReversible(boolean reversible) {
        isReversible = reversible;
    }


    /**
     * Is capturing boolean.
     *
     * @return the boolean
     */
    public boolean isCapturing() {
        return capturingPieceType != NOT_CAPTURING;
    }

    /**
     * Sets capturing.
     *
     * @param pieceType the piece type
     */
    public void setCapturing(PieceType pieceType) {
        this.capturingPieceType = pieceType;
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof BasicMove basicMove && super.equals(basicMove);

    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return getAnnotation();
    }

    /**
     * Gets annotation.
     *
     * @return the annotation
     */
    public String getAnnotation() {
        return moveAnnotation == null ? MoveAnnotation.basicAnnotate(this) : moveAnnotation;
    }

    /**
     * Strict equals boolean.
     *
     * @param move the move
     * @return the boolean
     */
    public boolean strictEquals(Move move) {
        return super.equals(move) && move.getMoveFlag().equals(this, move);
    }

    /**
     * Gets move flag.
     *
     * @return the move flag
     */
    public MoveFlag getMoveFlag() {
        return moveFlag;
    }

    /**
     * Sets move flag.
     *
     * @param moveFlag the move flag
     */
    public void setMoveFlag(MoveFlag moveFlag) {
        this.moveFlag = moveFlag;
    }

    /**
     * Compare to int.
     *
     * @param o the o
     * @return the int
     */
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
        if (moveFlag == MoveFlag.Promotion) {
            ret += 5 * promotingTo.value;
        } else if (moveFlag == MoveFlag.EnPassant) {
            ret += 0.00001;
        } else if (moveFlag.isCastling) {
            ret += 5;
        }
        ret /= 100000;
        if (moveEvaluation != null) {
            ret += moveEvaluation.getEval();
        }

        return ret;
    }

    /**
     * Gets en passant loc.
     *
     * @return the en passant loc
     */
    public Location getEnPassantLoc() {
        return enPassantLoc;
    }

    /**
     * Sets en passant loc.
     *
     * @param epsnLoc the epsn loc
     */
    public void setEnPassantLoc(Location epsnLoc) {
        enPassantLoc = epsnLoc;
    }


    /**
     * Gets threefold status.
     *
     * @return the threefold status
     */
    public ThreefoldStatus getThreefoldStatus() {
        return threefoldStatus;
    }

    /**
     * Sets threefold status.
     *
     * @param threefoldStatus the threefold status
     */
    public void setThreefoldStatus(ThreefoldStatus threefoldStatus) {
        this.threefoldStatus = threefoldStatus;
    }

    /**
     * Gets game status str.
     *
     * @return the game status str
     */
    public String getGameStatusStr() {
        return moveEvaluation != null ? moveEvaluation.getGameStatus().getGameStatusType().annotation : "";
    }

    /**
     * Threefold status - .
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public enum ThreefoldStatus {
        /**
         * None threefold status.
         */
        NONE,
        /**
         * Can claim threefold status.
         */
        CAN_CLAIM,
        /**
         * Claimed threefold status.
         */
        CLAIMED;
    }

    /**
     * Move flag - .
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public enum MoveFlag {
        /**
         * Normal move move flag.
         */
        NormalMove,
        /**
         * En passant move flag.
         */
        EnPassant,
        /**
         * Double pawn push move flag.
         */
        DoublePawnPush,
        /**
         * The Promotion.
         */
        Promotion {
            @Override
            public boolean equals(Move myMove, Move otherMove) {
                return super.equals(myMove, otherMove) && myMove.getPromotingTo() == otherMove.getPromotingTo();
            }
        },
        /**
         * Short castle move flag.
         */
        ShortCastle(CastlingRights.Side.KING),
        /**
         * Long castle move flag.
         */
        LongCastle(CastlingRights.Side.QUEEN);
        /**
         * The Castling flags.
         */
        public final static MoveFlag[] CASTLING_FLAGS;

        static {
            CASTLING_FLAGS = new MoveFlag[CastlingRights.Side.SIDES.length];
            for (CastlingRights.Side side : CastlingRights.Side.SIDES) {
                CASTLING_FLAGS[side.asInt] = side == CastlingRights.Side.KING ? ShortCastle : LongCastle;
            }
        }

        /**
         * The Is castling.
         */
        public final boolean isCastling;
        /**
         * The Castling side.
         */
        public final CastlingRights.Side castlingSide;

        MoveFlag() {
            this(null);
        }

        MoveFlag(CastlingRights.Side side) {
            this.isCastling = side != null;
            this.castlingSide = side;
        }

        /**
         * Equals boolean.
         *
         * @param myMove    the my move
         * @param otherMove the other move
         * @return the boolean
         */
        public boolean equals(Move myMove, Move otherMove) {
            return otherMove.getMoveFlag() == this;
        }

    }

}