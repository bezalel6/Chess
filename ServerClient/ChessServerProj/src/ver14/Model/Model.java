package ver14.Model;


import ver14.Model.Eval.Eval;
import ver14.Model.MoveGenerator.GenerationSettings;
import ver14.Model.MoveGenerator.MoveGenerator;
import ver14.Model.hashing.BoardHash;
import ver14.SharedClasses.Game.BoardSetup.Board;
import ver14.SharedClasses.Game.BoardSetup.Square;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.evaluation.Evaluation;
import ver14.SharedClasses.Game.moves.BasicMove;
import ver14.SharedClasses.Game.moves.CastlingRights;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Game.pieces.Piece;
import ver14.SharedClasses.Game.pieces.PieceType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Stack;

public class Model implements Serializable {
    private final static String startingPos = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private boolean finishSetup = false;
    private Stack<Move> moveStack;
    private Board board;
    private PiecesBBs[] pieces;
    private Bitboard[] attackedSquares;
    private int[][] piecesCount;
    private PlayerColor currentPlayerColor;
    private int halfMoveClock, fullMoveClock;
    private CastlingRights castlingRights;
    private Location enPassantTargetLoc;
    private Location enPassantActualLoc;
    private BoardHash boardHash;

    public Model() {
        createNewEmptyLogicBoard();
    }

    private void createNewEmptyLogicBoard() {
        board = new Board();
        pieces = new PiecesBBs[PlayerColor.NUM_OF_PLAYERS];
        attackedSquares = new Bitboard[PlayerColor.NUM_OF_PLAYERS];
        castlingRights = new CastlingRights();
        piecesCount = new int[PlayerColor.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
        for (PlayerColor playerColor : PlayerColor.PLAYER_COLORS) {
            Arrays.fill(piecesCount[playerColor.asInt], 0);
            pieces[playerColor.asInt] = new PiecesBBs(PieceType.NUM_OF_PIECE_TYPES);
            attackedSquares[playerColor.asInt] = new Bitboard();
        }
    }

    public Model(Model other) {
        this(startingPos);
        other.moveStack.forEach(this::applyMove);
//        this.moveStack = new Stack<>();
//        for (Move move : other.moveStack) {
//            moveStack.push(Move.copyMove(move));
//        }
//        createNewEmptyLogicBoard();
//        this.board = new Board(other.board);
//        initPieces();
//        this.currentPlayerColor = other.currentPlayerColor;
//        this.fullMoveClock = other.fullMoveClock;
//        this.halfMoveClock = other.halfMoveClock;
//        this.castlingRights = new CastlingRights(other.castlingRights);
//
//        this.enPassantActualLoc = other.enPassantActualLoc;
//        this.enPassantTargetLoc = other.enPassantTargetLoc;
//        this.finishSetup = other.finishSetup;
//        this.boardHash = new BoardHash(this);
    }

    public Model(String fen) {
        setup(fen);
    }

    public static CastlingRights.Side getSideRelativeToKing(Model model, PlayerColor playerColor, Location rookLoc) {
        CastlingRights.Side ret = CastlingRights.Side.QUEEN;
        Location kingLoc = model.getKing(playerColor);
        if (kingLoc.col < rookLoc.col) {
            ret = CastlingRights.Side.KING;
        }
        return ret;
    }

    public String genFenStr() {
        return FEN.generateFEN(this);
    }

    public void setup(String fen) {
        createNewEmptyLogicBoard();
        if (fen == null)
            fen = startingPos;
        moveStack = new Stack<>();
        FEN.loadFEN(fen, this);
        initPieces();
        this.boardHash = new BoardHash(this);
        finishSetup = true;
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

    private void setBoardHash() {
//        if (HashManager.hashBoard)
        boardHash.setAll(this);
    }

    private void setAttackedSquares() {
//        for (PlayerColor playerColor : PlayerColor.PLAYER_COLORS) {
//            attackedSquares[playerColor.asInt] = AttackedSquares.getAttackedSquares(this, playerColor);
//    }

    }

    public CastlingRights getCastlingRights() {
        return castlingRights;
    }

    public void setCastlingAbilities(CastlingRights castlingRights) {
        this.castlingRights = castlingRights;
    }

    public Location getEnPassantTargetLoc() {
        return enPassantTargetLoc;
    }

    public void setEnPassantTargetLoc(Location enPassantTargetLoc) {
        this.enPassantTargetLoc = enPassantTargetLoc;
    }

    public void setEnPassantTargetLoc(String enPassantTargetLocStr) {
        if (enPassantTargetLocStr.replaceAll("\\s+", "").equalsIgnoreCase("-")) {
            this.enPassantTargetLoc = null;
        } else {
            this.enPassantTargetLoc = Location.getLoc(enPassantTargetLocStr);
            int row = enPassantTargetLoc.row;
            int diff = (row == (PlayerColor.WHITE.startingRow + 3)) ? PlayerColor.WHITE.diff : PlayerColor.BLACK.diff;
            row -= diff;
            this.enPassantActualLoc = Location.getLoc(row, enPassantTargetLoc.col);
        }
    }

    public Location getEnPassantActualLoc() {
        return enPassantActualLoc;
    }

    public void setEnPassantActualLoc(Location enPassantActualLoc) {
        this.enPassantActualLoc = enPassantActualLoc;
    }

    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    public void setHalfMoveClock(int num) {
        this.halfMoveClock = num;
    }

    public int getFullMoveClock() {
        return fullMoveClock;
    }

    public void setFullMoveClock(int fullMoveClock) {
        this.fullMoveClock = fullMoveClock;
    }

    public Move findMove(BasicMove basicMove) {
//        basicMove.flip();
        for (Move move : generateAllMoves()) {
            if (basicMove.equals(move)) {
                return Move.copyMove(move);
            }
        }
        return null;
    }

    public ModelMovesList generateAllMoves() {
        return MoveGenerator.generateMoves(this);
    }

    public int bothPlayersNumOfPieces(PieceType[] arr) {
        return getNumOfPieces(PlayerColor.WHITE, arr) + getNumOfPieces(PlayerColor.BLACK, arr);
    }

    public int getNumOfPieces(PlayerColor playerColor, PieceType[] arr) {
        int ret = 0;
        for (PieceType pieceType : arr) {
            ret += getNumOfPieces(playerColor, pieceType);
        }
        return ret;
    }

    public int getNumOfPieces(PlayerColor playerColor, PieceType pieceType) {
        return piecesCount[playerColor.asInt][pieceType.asInt];
    }

    public int bothPlayersNumOfPieces(PieceType pieceType) {
        return getNumOfPieces(PlayerColor.WHITE, pieceType) + getNumOfPieces(PlayerColor.BLACK, pieceType);
    }

    public boolean isInCheck() {
        return isInCheck(currentPlayerColor);
    }

    public boolean isInCheck(PlayerColor playerColor) {

        return isThreatened(getKing(playerColor), playerColor.getOpponent());

    }

    public boolean isThreatened(Location loc, PlayerColor threateningPlayer) {
//        return
//        return AttackedSquares.getAttackedSquares(this, threateningPlayer).isSet(loc);
        return AttackedSquares.isAttacked(this, loc, threateningPlayer);
    }

    public Location getKing() {
        return getKing(currentPlayerColor);
    }

    public Location getKing(PlayerColor playerColor) {
        Bitboard k = getPieceBitBoard(playerColor, PieceType.KING);

        return k.isEmpty() ? null : k.getLastSetLoc();
    }

    public Bitboard getPieceBitBoard(PlayerColor playerColor, PieceType pieceType) {
        return getPlayersPieces(playerColor).getBB(pieceType);
    }

    public PiecesBBs getPlayersPieces(PlayerColor playerColor) {
        return pieces[playerColor.asInt];
    }

    public boolean anyLegalMove(PlayerColor playerColor) {
//        Bitboard[] playersPieces = getPlayersPieces(playerColor);

        return playerColor == currentPlayerColor && !MoveGenerator.generateMoves(this, GenerationSettings.anyLegalMove).isEmpty();

        //todo can return true if one piece can move to any
//        return !generateAllMoves(playerColor).isEmpty();
    }

    public ModelMovesList generateAllMoves(PlayerColor player) {

        if (player == currentPlayerColor) {
            return generateAllMoves();
        }
        return new ModelMovesList(null, null);
    }

    public PlayerColor getCurrentPlayer() {
        return currentPlayerColor;
    }

    public void setCurrentPlayer(PlayerColor currentPlayerColor) {
        this.currentPlayerColor = currentPlayerColor;
    }

    public void applyMove(Move move) {
        Location movingFrom = move.getMovingFrom();
        Location movingTo = move.getMovingTo();

        Piece piece = board.getPiece(movingFrom, true);
        PlayerColor piecePlayerColor = piece.playerColor;

        move.setPrevFullMoveClock(fullMoveClock);
        move.setPrevHalfMoveClock(halfMoveClock);
        move.setReversible(piece);

        makeIntermediateMove(move);

        if (move.getMoveFlag() == Move.MoveFlag.DoublePawnPush) {
            setEnPassantTargetLoc(move.getEnPassantLoc());
            setEnPassantActualLoc(movingTo);
        } else {
            setEnPassantTargetLoc((Location) null);
            setEnPassantActualLoc(null);
        }
        if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
            PieceType promotingTo = move.getPromotingTo();
            delPiece(piece, movingFrom);
            Piece newPiece = Piece.getPiece(promotingTo, piecePlayerColor);
            addPiece(newPiece, movingFrom);
            piece = newPiece;
        }

        if (currentPlayerColor == PlayerColor.BLACK)
            fullMoveClock++;

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
            assert otherPiece != null && otherPiece.pieceType != PieceType.KING;
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
        if (disabled != 0) {
            move.setNotReversible();
        }

        moveStack.push(move);

        if (move.isReversible()) {
            this.halfMoveClock++;
        } else {
            this.halfMoveClock = 0;
        }
        switchTurn();
        setBoardHash();

        setAttackedSquares();

        if (isInCheck()) {
            Evaluation e = new Evaluation(currentPlayerColor);
            e.getGameStatus().setInCheck(getKing(currentPlayerColor));
            move.setMoveEvaluation(e);
        }
    }


    public void undoMove() {
        Move move = moveStack.pop();

        setFullMoveClock(move.getPrevFullMoveClock());
        setHalfMoveClock(move.getPrevHalfMoveClock());

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
        setEnPassantTargetLoc((Location) null);
        setEnPassantActualLoc(null);
        if (!moveStack.empty()) {
            Move prevMove = moveStack.peek();
            if (prevMove.getMoveFlag() == Move.MoveFlag.DoublePawnPush) {
                setEnPassantTargetLoc(prevMove.getEnPassantLoc());
                setEnPassantActualLoc(prevMove.getMovingTo());
            }
        }
        updatePieceLoc(piece, movingFrom, movingTo);

        if (move.isCapturing()) {
            Piece otherPiece = Piece.getPiece(move.getCapturingPieceType(), piecePlayerColor.getOpponent());
            addPiece(otherPiece, movingFrom);
        }
        makeIntermediateMove(move, true);

        switchTurn();

        setBoardHash();
        setAttackedSquares();
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

    public void switchTurn() {
        currentPlayerColor = currentPlayerColor.getOpponent();
    }

    public int[] getPiecesCount(PlayerColor playerColor) {
        return piecesCount[playerColor.asInt];
    }

    private void movePiece(BasicMove basicMove) {
        movePiece(basicMove.getMovingFrom(), basicMove.getMovingTo());
    }

    private void movePiece(Location movingFrom, Location movingTo) {
        assert isSquareEmpty(movingTo);//not intended for captures
        Piece piece = board.getPiece(movingFrom, true);
        updatePieceLoc(piece, movingFrom, movingTo);
    }

    public void printBoard() {
        System.out.println(this);
    }

    public boolean isSquareEmpty(Location loc) {
        return getSquare(loc).isEmpty();
    }

    public void makeMove(Move move) {
        applyMove(move);
        move.setMoveEvaluation(Eval.getEvaluation(this));
    }

    public Board getLogicBoard() {
        return board;
    }

    public PiecesBBs[] getPieces() {
        return pieces;
    }

    public BoardHash getBoardHash() {
//        return new BoardHash(this);
        return boardHash;
    }

    public boolean isSamePlayer(Location loc1, Location loc2) {
        if (board.isSquareEmpty(loc1) || board.isSquareEmpty(loc2))
            return false;
        return board.getPiece(loc1).isOnMyTeam(board.getPiece(loc2));
    }

    @Override
    public String toString() {
        return board.toString();
    }

    public Square getSquare(Location loc) {
        return board.getSquare(loc);
    }
}
