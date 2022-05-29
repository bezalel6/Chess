package ver14.SharedClasses.Game.Moves;


import ver14.SharedClasses.Callbacks.LazyHashSupplier;
import ver14.SharedClasses.Game.Evaluation.Evaluation;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;


/**
 * represents a "heavy" move. with a lot of info.
 * one of the fields it has is {@code intermediateMove} (aka Zwischenzug) which is a move to play before this move.
 * like moving the king in castling, or moving a pawn once in a double pawn push.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Move extends BasicMove implements Comparable<Move> {
    /**
     * The constant NOT_CAPTURING.
     */
    public static final PieceType NOT_CAPTURING = null;
    /**
     * an Intermediate move, like moving the rook in a castling.
     */
    BasicMove intermediateMove;
    private LazyHashSupplier<Long> createdListHashSupplier = () -> 0L;
    /**
     * The Move annotation.
     */
    private String moveAnnotation = null;
    /**
     * The Threefold status.
     */
    private ThreefoldStatus threefoldStatus;
    /**
     * the type of the piece this move captured.
     */
    private PieceType capturingPieceType;
    /**
     * the en passant location just created (this move is a double pawn push)
     */
    private Location enPassantLoc;
    /**
     * The Piece type this move is Promoting to.
     */
    private PieceType promotingTo;
    /**
     * Is this move reversible.
     */
    private boolean isReversible;
    /**
     * ths Move's evaluation.
     */
    private Evaluation moveEvaluation;
    /**
     * The Move flag.
     */
    private MoveFlag moveFlag;
    /**
     * The Moving player color.
     */
    private PlayerColor movingPlayerColor;
    /**
     * The castling rights this move disabled.
     */
    private byte disabledCastling;
    /**
     * The full move clock before making this move
     */
    private int prevFullMoveClock;
    /**
     * The half move clock before making this move
     */
    private int prevHalfMoveClock;

    /**
     * Instantiates a new Move.
     *
     * @param source             the source
     * @param destination        the destination
     * @param capturingPieceType the capturing piece type
     */
    public Move(Location source, Location destination, PieceType capturingPieceType) {
        this(source, destination);
        this.capturingPieceType = capturingPieceType;
    }

    /**
     * Instantiates a new Move.
     *
     * @param source      the source
     * @param destination the destination
     */
    public Move(Location source, Location destination) {
        super(source, destination);
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
        this.createdListHashSupplier = () -> other.createdListHashSupplier.get();
        this.moveAnnotation = other.moveAnnotation;
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
     * creates a Castling move.
     *
     * @param source      the source
     * @param destination the destination
     * @param side        the castling side
     * @return the castling move
     */
    public static Move castling(Location source, Location destination, CastlingRights.Side side) {
        Move move = new Move(source, destination);
        move.setMoveFlag(MoveFlag.CASTLING_FLAGS[side.asInt]);
        Location rookOrigin = Location.getLoc(source.row, side.rookStartingCol);
        Location rookDestination = Location.getLoc(source.row, side.castledRookCol);
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
     * Copy move.
     *
     * @param move the move
     * @return the move
     */
    public static Move copyMove(Move move) {
        return new Move(move);
    }

    public LazyHashSupplier<Long> getCreatedListHashSupplier() {
        return createdListHashSupplier;
    }

    public void setCreatedListHashSupplier(LazyHashSupplier<Long> createdListHashSupplier) {
        this.createdListHashSupplier = createdListHashSupplier;
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
     * Gets a byte with the bits of the castling it disabled set.
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
     * Is this move a check.
     *
     * @return <code>true</code> if this move is a check
     */
    public boolean isCheck() {
        return moveEvaluation != null && moveEvaluation.isCheck();
    }

    /**
     * Gets this move's evaluation.
     *
     * @return the move evaluation if one is set, null otherwise
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
     * Sets the evaluation of this move
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
     * Gets the type of piece this move captures.
     *
     * @return the piece if one is captured, null otherwise.
     */
    public PieceType getCapturingPieceType() {
        return capturingPieceType;
    }

    /**
     * Is this move reversible.
     *
     * @return <code>true</code> if this move is reversible
     * @see <a href="https://www.chessprogramming.org/Reversible_Moves">chessprogramming.org/Reversible_Moves</a>
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
     * is this move a capturing move.
     *
     * @return {@code true} if this is move captures a piece
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
     * checks source and destination equals and move flags equals.
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
     * Compare to another move.
     *
     * @param o the other move
     * @return the comparison result
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

    /**
     * Guess eval double.
     * used to estimate how good (or bad) a move can be,
     * in order to sort the moves before making any of them in some situations in the minimax
     *
     * @return the double
     */
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


    public ThreefoldStatus getThreefoldStatus() {
        return threefoldStatus;
    }


    public void setThreefoldStatus(ThreefoldStatus threefoldStatus) {
        this.threefoldStatus = threefoldStatus;
    }


    public String getGameStatusStr() {
        return moveEvaluation != null ? moveEvaluation.getGameStatus().getGameStatusType().annotation : "";
    }

    /**
     * Threefold status - represents a threefold draw status.
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
     * Move flag - which type of move this is.
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
         * Is this move flag a castling flag.
         */
        public final boolean isCastling;
        /**
         * if this is a castling flag, which side is it.
         */
        public final CastlingRights.Side castlingSide;

        /**
         * Instantiates a new Move flag.
         */
        MoveFlag() {
            this(null);
        }

        /**
         * Instantiates a new Move flag.
         *
         * @param side the side
         */
        MoveFlag(CastlingRights.Side side) {
            this.isCastling = side != null;
            this.castlingSide = side;
        }

        /**
         * checks if the flags are equals.
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