package ver14.Model;


import ver14.Model.Eval.Eval;
import ver14.Model.Hashing.BoardHash;
import ver14.Model.MoveGenerator.GenerationSettings;
import ver14.Model.MoveGenerator.MoveGenerator;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Square;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.Moves.BasicMove;
import ver14.SharedClasses.Game.Moves.CastlingRights;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.Moves.MovesList;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Stack;


/**
 * Model - handles all game logic.
 */
public class Model implements Serializable {
    /**
     * a concatenating string builder of the main (= not searches) game progress.
     * done in the pgn format(Portable Game Notation).
     * used for searching inside the book
     *
     * @see ver14.Model.Eval.Book
     */
    private StringBuilder pgnBuilder = new StringBuilder();
    /**
     * stack to keep track of the applied moves
     */
    private Stack<Move> moveStack;
    /**
     * the current position board
     */
    private Board board;
    /**
     * bitboards to keep track of pieces locations by their types.
     */
    private PiecesBBs[] pieces;
    /**
     * keeping track of the number of pieces by piece type and color
     */
    private int[][] piecesCount;
    /**
     * the current player to move
     */
    private PlayerColor currentPlayerColor;
    /**
     * number of plies since the last reversible move. used for the fifty move rule
     *
     * @see <a href="https://www.chessprogramming.org/Halfmove_Clock">more info about the fifty move rule</a>
     */
    private int halfMoveClock;
    /**
     * number of the full moves in game. incremented after each black's moves
     *
     * @see <a href="www.chessprogramming.org/Forsyth-Edwards_Notation#:~:text=The%20number%20of%20the%20full%20moves%20in%20a%20game.%20It%20starts%20at%201%2C%20and%20is%20incremented%20after%20each%20Black%27s%20move">...</a>.
     */
    private int fullMoveCounter;
    /**
     * current position castling rights for both sides
     */
    private CastlingRights castlingRights;
    /**
     * where the en passant capture is done(somewhere on the 3rd or 6th row)
     */
    private Location enPassantTargetLoc;
    /**
     * where an en passant captured piece is actually on(somewhere on the 4th or 5th row)
     */
    private Location enPassantActualLoc;
    /**
     * current position hash
     */
    private BoardHash boardHash;
    /**
     * the position this model first setup
     */
    private String loadedFen = null;

    /**
     * Instantiates a new Model.
     *
     * @param other the other
     */
    public Model(Model other) {
        this(other.genFenStr());
    }

    /**
     * Instantiates a new Model.
     *
     * @param fen the fen
     */
    public Model(String fen) {
        setup(fen);
    }

    /**
     * Generates a fen string of the current position.
     *
     * @return the fen
     */
    public String genFenStr() {
        return FEN.generateFEN(this);
    }

    /**
     * Sets .
     *
     * @param fen the fen
     */
    public void setup(String fen) {
        createNewEmptyLogicBoard();
        if (fen == null)
            fen = Board.startingFen;
        moveStack = new Stack<>();
        FEN.loadFEN(fen, this);
        initPieces();
        this.boardHash = new BoardHash(this);
        if (!StrUtils.isEmpty(fen) && !fen.equals(Board.startingFen))
            this.pgnBuilder = new StringBuilder("n");

    }

    private void createNewEmptyLogicBoard() {
        board = new Board();
        pieces = new PiecesBBs[PlayerColor.NUM_OF_PLAYERS];
        castlingRights = new CastlingRights();
        piecesCount = new int[PlayerColor.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
        for (PlayerColor playerColor : PlayerColor.PLAYER_COLORS) {
            Arrays.fill(piecesCount[playerColor.asInt], 0);
            pieces[playerColor.asInt] = new PiecesBBs(PieceType.NUM_OF_PIECE_TYPES);
        }
    }

    private void initPieces() {
        for (Square square : board) {
            if (!square.isEmpty()) {
                Piece piece = square.getPiece();
                addPiece(piece, square.getLoc());
            }
        }
    }

    private void addPiece(Piece piece, Location currentLoc) {
        if (piece == null) return;
        piecesCount[piece.playerColor.asInt][piece.pieceType.asInt]++;
        getPieceBitBoard(piece).set(currentLoc, true);
        board.setPiece(currentLoc, piece);

    }

    private Bitboard getPieceBitBoard(Piece piece) {
        return getPlayersPieces(piece.playerColor).getBB(piece.pieceType);
    }

    /**
     * Gets players pieces.
     *
     * @param playerColor the player color
     * @return the players pieces
     */
    public PiecesBBs getPlayersPieces(PlayerColor playerColor) {
        return pieces[playerColor.asInt];
    }

    /**
     * Instantiates a new Model.
     */
    public Model() {
        createNewEmptyLogicBoard();
    }

    /**
     * Gets side relative to king.
     *
     * @param model       the model
     * @param playerColor the player color
     * @param rookLoc     the rook loc
     * @return the side relative to king
     */
    public static CastlingRights.Side getSideRelativeToKing(Model model, PlayerColor playerColor, Location rookLoc) {
        CastlingRights.Side ret = CastlingRights.Side.QUEEN;
        Location kingLoc = model.getKing(playerColor);
        if (kingLoc.col < rookLoc.col) {
            ret = CastlingRights.Side.KING;
        }
        return ret;
    }

    /**
     * Sets loaded fen.
     *
     * @param loadedFen the loaded fen
     */
    public void setLoadedFen(String loadedFen) {
        this.loadedFen = loadedFen;
    }

    /**
     * Gets pgn.
     *
     * @return the pgn
     */
    public String getPGN() {
        return StrUtils.clean(pgnBuilder.toString());
    }

    private void setBoardHash() {
//        if (HashManager.hashBoard)
        boardHash.setAll(this);
    }

    private void setAttackedSquares() {
//        for (PlayerColor playerColor : PlayerColor.PLAYER_COLORS) {
//            attackedSquares[playerColor.asInt] = AttackedSquares.getAttackedSquares(this, playerColor);
//    }

    }

    /**
     * Gets castling rights.
     *
     * @return the castling rights
     */
    public CastlingRights getCastlingRights() {
        return castlingRights;
    }

    /**
     * Sets castling abilities.
     *
     * @param castlingRights the castling rights
     */
    public void setCastlingAbilities(CastlingRights castlingRights) {
        this.castlingRights = castlingRights;
    }

    /**
     * Gets en passant target loc.
     *
     * @return the en passant target loc
     */
    public Location getEnPassantTargetLoc() {
        return enPassantTargetLoc;
    }

    /**
     * Sets en passant target loc.
     *
     * @param enPassantTargetLoc the en passant target loc
     */
    public void setEnPassantTargetLoc(Location enPassantTargetLoc) {
        this.enPassantTargetLoc = enPassantTargetLoc;
    }

    /**
     * Sets en passant target loc.
     *
     * @param enPassantTargetLocStr the en passant target loc str
     */
    public void setEnPassantTargetLoc(String enPassantTargetLocStr) {
        if (StrUtils.isEmpty(enPassantTargetLocStr) || enPassantTargetLocStr.replaceAll("\\s+", "").equalsIgnoreCase("-")) {
            this.enPassantTargetLoc = null;
        } else {
            this.enPassantTargetLoc = Location.getLoc(enPassantTargetLocStr);
            int row = enPassantTargetLoc.row;
            int diff = (row == (PlayerColor.WHITE.startingRow + 3 * PlayerColor.WHITE.diff)) ? PlayerColor.WHITE.diff : PlayerColor.BLACK.diff;
            row += diff;
            this.enPassantActualLoc = Location.getLoc(row, enPassantTargetLoc.col);
        }
    }

    /**
     * Gets en passant actual loc.
     *
     * @return the en passant actual loc
     */
    public Location getEnPassantActualLoc() {
        return enPassantActualLoc;
    }

    /**
     * Sets en passant actual loc.
     *
     * @param enPassantActualLoc the en passant actual loc
     */
    public void setEnPassantActualLoc(Location enPassantActualLoc) {
        this.enPassantActualLoc = enPassantActualLoc;
    }

    /**
     * Gets half move clock.
     *
     * @return the half move clock
     */
    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    /**
     * Sets half move clock.
     *
     * @param num the num
     */
    public void setHalfMoveClock(int num) {
        this.halfMoveClock = num;
    }

    /**
     * Gets full move clock.
     *
     * @return the full move clock
     */
    public int getFullMoveCounter() {
        return fullMoveCounter;
    }

    /**
     * Sets full move clock.
     *
     * @param fullMoveCounter the full move clock
     */
    public void setFullMoveCounter(int fullMoveCounter) {
        this.fullMoveCounter = fullMoveCounter;
    }

    /**
     * Find move move.
     *
     * @param basicMove the basic move
     * @return the move
     */
    public Move findMove(BasicMove basicMove) {
//        basicMove.flip();
        for (Move move : generateAllMoves()) {
            if (basicMove.equals(move)) {
                return Move.copyMove(move);
            }
        }
        return null;
    }

    /**
     * Generate all moves model moves list.
     *
     * @return the model moves list
     */
    public ModelMovesList generateAllMoves() {
        return MoveGenerator.generateMoves(this);
    }

    /**
     * Both players num of pieces int.
     *
     * @param arr the arr
     * @return the int
     */
    public int bothPlayersNumOfPieces(PieceType[] arr) {
        return getNumOfPieces(PlayerColor.WHITE, arr) + getNumOfPieces(PlayerColor.BLACK, arr);
    }

    /**
     * Gets num of pieces.
     *
     * @param playerColor the player color
     * @param arr         the arr
     * @return the num of pieces
     */
    public int getNumOfPieces(PlayerColor playerColor, PieceType[] arr) {
        int ret = 0;
        for (PieceType pieceType : arr) {
            ret += getNumOfPieces(playerColor, pieceType);
        }
        return ret;
    }

    /**
     * Gets num of pieces.
     *
     * @param playerColor the player color
     * @param pieceType   the piece type
     * @return the num of pieces
     */
    public int getNumOfPieces(PlayerColor playerColor, PieceType pieceType) {
        return piecesCount[playerColor.asInt][pieceType.asInt];
    }

    /**
     * Both players num of pieces int.
     *
     * @param pieceType the piece type
     * @return the int
     */
    public int bothPlayersNumOfPieces(PieceType pieceType) {
        return getNumOfPieces(PlayerColor.WHITE, pieceType) + getNumOfPieces(PlayerColor.BLACK, pieceType);
    }

    /**
     * Is in check boolean.
     *
     * @return the boolean
     */
    public boolean isInCheck() {
        return isInCheck(currentPlayerColor);
    }

    /**
     * Is in check boolean.
     *
     * @param playerColor the player color
     * @return the boolean
     */
    public boolean isInCheck(PlayerColor playerColor) {
        return isThreatened(getKing(playerColor), playerColor.getOpponent());

    }

    /**
     * Is threatened boolean.
     *
     * @param loc               the loc
     * @param threateningPlayer the threatening player
     * @return the boolean
     */
    public boolean isThreatened(Location loc, PlayerColor threateningPlayer) {
        return AttackedSquares.isAttacked(this, loc, threateningPlayer);
    }

    /**
     * Gets king.
     *
     * @param playerColor the player color
     * @return the king
     */
    public Location getKing(PlayerColor playerColor) {
        Bitboard k = getPieceBitBoard(playerColor, PieceType.KING);
        return k.getLastSetLoc();
    }

    /**
     * Gets piece bit board.
     *
     * @param playerColor the player color
     * @param pieceType   the piece type
     * @return the piece bit board
     */
    public Bitboard getPieceBitBoard(PlayerColor playerColor, PieceType pieceType) {
        return getPlayersPieces(playerColor).getBB(pieceType);
    }

    private void Assert(boolean b, Object... message) {
        if (!b) {
            String div = "\n\n\n";

            String str = (moveStack.isEmpty() ? "" : "last move: " + moveStack.peek());
            str += "\n";
            str += StrUtils.splitArr(message);
            str += "\n";
            str += "assertion failed";
            str += div;
            str += genFenStr();
            str += div;
            str += board.toString();
            throw new AssertionError(str);
        }
    }

    /**
     * Gets king.
     *
     * @return the king
     */
    public Location getKing() {
        return getKing(currentPlayerColor);
    }

    /**
     * Any legal move boolean.
     *
     * @param playerColor the player color
     * @return the boolean
     */
    public boolean anyLegalMove(PlayerColor playerColor) {
        assert playerColor == currentPlayerColor;

        return !MoveGenerator.generateMoves(this, GenerationSettings.ANY_LEGAL).isEmpty();
    }

    /**
     * Gets current player.
     *
     * @return the current player
     */
    public PlayerColor getCurrentPlayer() {
        return currentPlayerColor;
    }

    /**
     * Sets current player.
     *
     * @param currentPlayerColor the current player color
     */
    public void setCurrentPlayer(PlayerColor currentPlayerColor) {
        this.currentPlayerColor = currentPlayerColor;
    }

    /**
     * Apply move.
     *
     * @param move the move
     */
    public void applyMove(Move move) {
        Location movingFrom = move.getMovingFrom();
        Location movingTo = move.getMovingTo();

        Piece piece = board.getPiece(movingFrom, true);
        PlayerColor piecePlayerColor = piece.playerColor;

        move.setPrevFullMoveClock(fullMoveCounter);
        move.setPrevHalfMoveClock(halfMoveClock);

        makeIntermediateMove(move);

        if (move.getMoveFlag() == Move.MoveFlag.DoublePawnPush) {
            enPassantTargetLoc = (move.getEnPassantLoc());
            enPassantActualLoc = (movingTo);
        } else {
            enPassantTargetLoc = enPassantActualLoc = null;
        }

        if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
            PieceType promotingTo = move.getPromotingTo();
            delPiece(piece, movingFrom);
            Piece newPiece = Piece.getPiece(promotingTo, piecePlayerColor);
            addPiece(newPiece, movingFrom);
            piece = newPiece;
        }

        if (currentPlayerColor == PlayerColor.BLACK)
            fullMoveCounter++;

        byte disabled = 0;
        if (castlingRights.hasAny(piecePlayerColor)) {
            if (piece.pieceType == PieceType.KING) {
                disabled |= castlingRights.disableCastling(piecePlayerColor);
            } else if (piece.pieceType == PieceType.ROOK) {
                CastlingRights.Side side = getSideRelativeToKing(this, piecePlayerColor, movingTo);
                disabled |= castlingRights.disableCastling(piecePlayerColor, side);
            }
        }

        if (move.isCapturing()) {
            Piece otherPiece = board.getPiece(movingTo);
//            Assert(otherPiece != null, "eating on empty stomach", move);
//            Location lastSet = getKing(otherPiece.playerColor);
//            Assert(otherPiece.pieceType != PieceType.KING, "eating a freaking king", move);
            if (otherPiece.pieceType == PieceType.ROOK) {
                PlayerColor capClr = otherPiece.playerColor;
                CastlingRights.Side side = getSideRelativeToKing(this, capClr, movingTo);
                if (castlingRights.isEnabled(capClr, side)) {
                    disabled |= castlingRights.disableCastling(otherPiece.playerColor, side);
                }
            }
            delPiece(otherPiece, movingTo);
        }
        updatePieceLoc(piece, movingFrom, movingTo);

        move.setDisabledCastling(disabled);
        boolean incHalfMoveClock = !(move.isCapturing() || (piece.pieceType == PieceType.PAWN || move.getMoveFlag() == Move.MoveFlag.Promotion));

        move.setReversible(incHalfMoveClock && disabled == 0);

        moveStack.push(move);

        if (incHalfMoveClock) {
            this.halfMoveClock++;
        } else {
            this.halfMoveClock = 0;
        }
        switchTurn();
//        setBoardHash();

//        setAttackedSquares();

    }

    /**
     * Undo move.
     *
     * @param move the move
     */
    public void undoMove(Move move) {
        moveStack.pop();

        fullMoveCounter = (move.getPrevFullMoveClock());
        halfMoveClock = (move.getPrevHalfMoveClock());

        castlingRights.enable(move.getDisabledCastling());

        Location movingFrom = move.getMovingTo();
        Location movingTo = move.getMovingFrom();

        Piece piece = board.getPiece(movingFrom, true);
        PlayerColor piecePlayerColor = piece.playerColor;
        if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
            delPiece(piece, movingFrom);
            Piece oldPiece = Piece.getPiece(PieceType.PAWN, piecePlayerColor);
            addPiece(oldPiece, movingFrom);
            piece = oldPiece;
        }
        enPassantActualLoc = null;
        enPassantTargetLoc = null;
        if (!moveStack.empty()) {
            Move prevMove = moveStack.peek();
            if (prevMove.getMoveFlag() == Move.MoveFlag.DoublePawnPush) {
                setEnPassantTargetLoc(prevMove.getEnPassantLoc());
                setEnPassantActualLoc(prevMove.getMovingTo());
            }
        } else {
            setEnPassantTargetLoc(FEN.extractEnPassantTargetLoc(loadedFen));
        }
        updatePieceLoc(piece, movingFrom, movingTo);

        if (move.isCapturing()) {
            Piece otherPiece = Piece.getPiece(move.getCapturingPieceType(), piecePlayerColor.getOpponent());
            addPiece(otherPiece, movingFrom);
        }
        makeIntermediateMove(move, true);

        switchTurn();

//        setBoardHash();
//        setAttackedSquares();
    }

    private void updatePieceLoc(Piece piece, Location movingFrom, Location movingTo) {
        Bitboard bitboard = getPieceBitBoard(piece);

        bitboard.set(movingFrom, false);
        bitboard.set(movingTo, true);

        board.setPiece(movingTo, piece);
        board.setSquareEmpty(movingFrom);
    }

    private void delPiece(Piece piece, Location currentLoc) {
        piecesCount[piece.playerColor.asInt][piece.pieceType.asInt]--;
        getPieceBitBoard(piece).set(currentLoc, false);
        board.setSquareEmpty(currentLoc);
    }

    private void makeIntermediateMove(Move move) {
        makeIntermediateMove(move, false);
    }

    /**
     * Gets move stack.
     *
     * @return the move stack
     */
    public Stack<Move> getMoveStack() {
        return moveStack;
    }

    private void makeIntermediateMove(Move move, boolean flip) {
        BasicMove intermediateMove = move.getIntermediateMove();
        if (intermediateMove != null) {
            if (flip) {
                intermediateMove = BasicMove.getFlipped(intermediateMove);
            }
            movePiece(intermediateMove);
        }
    }

    /**
     * Switch turn.
     */
    public void switchTurn() {
        currentPlayerColor = currentPlayerColor.getOpponent();
    }

    /**
     * Get pieces count int [ ].
     *
     * @param playerColor the player color
     * @return the int [ ]
     */
    public int[] getPiecesCount(PlayerColor playerColor) {
        return piecesCount[playerColor.asInt];
    }

    private void movePiece(BasicMove basicMove) {
        movePiece(basicMove.getMovingFrom(), basicMove.getMovingTo());
    }

    private void movePiece(Location movingFrom, Location movingTo) {
//        Assert(isSquareEmpty(movingTo), "move piece is not intended for captures. prob smn wrong with castling", getSquare(movingFrom), getSquare(movingTo));//not intended for captures
        Piece piece = board.getPiece(movingFrom, true);
        updatePieceLoc(piece, movingFrom, movingTo);
    }

    /**
     * Print board.
     */
    public void printBoard() {
        System.out.println(this);
    }

    /**
     * Is square empty boolean.
     *
     * @param loc the loc
     * @return the boolean
     */
    public boolean isSquareEmpty(Location loc) {
        return getSquare(loc).isEmpty();
    }

    /**
     * Gets square.
     *
     * @param loc the loc
     * @return the square
     */
    public Square getSquare(Location loc) {
        return board.getSquare(loc);
    }

    /**
     * Make move.
     *
     * @param move the move
     */
    public void makeMove(Move move) {
        MovesList moves = MoveGenerator.generateMoves(this, GenerationSettings.ANNOTATE);
        Move finalMove = move;
        move = moves.stream().filter(m -> m.strictEquals(finalMove)).findAny().orElse(null);
        if (move == null) {
            throw new Error();
        }
        String str = move.getAnnotation() + " ";
        applyMove(move);
        move.setMoveEvaluation(Eval.getEvaluation(this));
        pgnBuilder.append(str);
//        System.out.println(pgnBuilder);

    }

    /**
     * Gets logic board.
     *
     * @return the logic board
     */
    public Board getLogicBoard() {
        return board;
    }

    /**
     * Get pieces pieces b bs [ ].
     *
     * @return the pieces b bs [ ]
     */
    public PiecesBBs[] getPieces() {
        return pieces;
    }

    /**
     * Gets board hash.
     *
     * @return the board hash
     */
    public BoardHash getBoardHash() {
//        return new BoardHash(this);
        return boardHash;
    }

    /**
     * Is same player boolean.
     *
     * @param loc1 the loc 1
     * @param loc2 the loc 2
     * @return the boolean
     */
    public boolean isSamePlayer(Location loc1, Location loc2) {
        if (board.isSquareEmpty(loc1) || board.isSquareEmpty(loc2))
            return false;
        return board.getPiece(loc1).isOnMyTeam(board.getPiece(loc2));
    }

    @Override
    public String toString() {
        return board.toString();
    }
}
