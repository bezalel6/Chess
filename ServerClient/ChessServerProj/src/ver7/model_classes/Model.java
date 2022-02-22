package ver7.model_classes;


import ver7.SharedClasses.Location;
import ver7.SharedClasses.PlayerColor;
import ver7.SharedClasses.board_setup.Board;
import ver7.SharedClasses.board_setup.Square;
import ver7.SharedClasses.evaluation.Evaluation;
import ver7.SharedClasses.moves.BasicMove;
import ver7.SharedClasses.moves.CastlingRights;
import ver7.SharedClasses.moves.Move;
import ver7.SharedClasses.pieces.Piece;
import ver7.SharedClasses.pieces.PieceType;
import ver7.model_classes.eval_classes.Eval;
import ver7.model_classes.hashing.BoardHash;

import java.io.Serializable;
import java.util.ArrayList;
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

    public Model(String fen) {
        setup(fen);
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

    private void createNewEmptyLogicBoard() {
        board = new Board();
        pieces = new PiecesBBs[PlayerColor.NUM_OF_PLAYERS];
        attackedSquares = new Bitboard[PlayerColor.NUM_OF_PLAYERS];
        castlingRights = new CastlingRights();

        piecesCount = new int[PlayerColor.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
        for (PlayerColor playerColor : PlayerColor.PLAYER_COLORS) {
            Arrays.fill(piecesCount[playerColor.asInt()], 0);
            pieces[playerColor.asInt()] = new PiecesBBs(PieceType.NUM_OF_PIECE_TYPES);
            attackedSquares[playerColor.asInt()] = new Bitboard();
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
        piecesCount[piece.getPlayer().asInt()][piece.getPieceType().asInt()]++;
        getPieceBitBoard(piece).set(currentLoc, true);
        board.setPiece(currentLoc, piece);
    }

    private Bitboard getPieceBitBoard(Piece piece) {
        return getPlayersPieces(piece.getPlayer()).getBB(piece.getPieceType());
    }

    public PiecesBBs getPlayersPieces(PlayerColor playerColor) {
        return pieces[playerColor.asInt()];
    }

    public Model() {
        createNewEmptyLogicBoard();
    }

    public Model(Model other) {
        this.moveStack = new Stack<>();
        for (Move move : other.moveStack) {
            moveStack.push(Move.copyMove(move));
        }
        createNewEmptyLogicBoard();
        this.board = new Board(other.board);
        initPieces();
        this.currentPlayerColor = other.currentPlayerColor;
        this.fullMoveClock = other.fullMoveClock;
        this.halfMoveClock = other.halfMoveClock;
        this.castlingRights = new CastlingRights(other.castlingRights);

        this.enPassantActualLoc = other.enPassantActualLoc;
        this.enPassantTargetLoc = other.enPassantTargetLoc;
        this.finishSetup = other.finishSetup;
        this.boardHash = new BoardHash(this);
    }

    public static CastlingRights.Side getSideRelativeToKing(Model model, PlayerColor playerColor, Location rookLoc) {
        CastlingRights.Side ret = CastlingRights.Side.QUEEN;
        Location kingLoc = model.getKing(playerColor);
        if (kingLoc.getCol() < rookLoc.getCol() * Piece.getDifference(playerColor)) {
            ret = CastlingRights.Side.KING;
        }
        return ret;
    }

    private void setAttackedSquares() {
//        for (PlayerColor playerColor : PlayerColor.PLAYER_COLORS) {
//            attackedSquares[playerColor.asInt()] = AttackedSquares.getAttackedSquares(this, playerColor);
//        }
    }

    public Bitboard getAttackedSquares(PlayerColor attackingPlayerColor) {
        Bitboard genNew = AttackedSquares.getAttackedSquares(this, attackingPlayerColor);
//        Bitboard old = attackedSquares[attackingPlayerColor.asInt()];
//        assert old.equals(genNew);
        return genNew;
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
            int row = enPassantTargetLoc.getRow();
            int diff = (row == (Piece.getStartingRow(PlayerColor.WHITE) + 3)) ? Piece.getDifference(PlayerColor.WHITE) : Piece.getDifference(PlayerColor.BLACK);
            row -= diff;
            this.enPassantActualLoc = Location.getLoc(row, Location.col(enPassantTargetLoc));
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

    public String getFenStr() {
        return FEN.generateFEN(this);
    }

    public Move findMove(BasicMove basicMove) {
        for (Move move : generateAllMoves()) {
            if (basicMove.equals(move)) {
                return Move.copyMove(move);
            }
        }
        return null;
    }

    public ArrayList<Move> generateAllMoves() {
        return MoveGenerator.generateMoves(this).getCleanList();
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
        return piecesCount[playerColor.asInt()][pieceType.asInt()];
    }

    public int bothPlayersNumOfPieces(PieceType pieceType) {
        return getNumOfPieces(PlayerColor.WHITE, pieceType) + getNumOfPieces(PlayerColor.BLACK, pieceType);
    }

    public boolean isInCheck() {
        return isInCheck(currentPlayerColor);
    }

    public boolean isInCheck(PlayerColor playerColor) {
//        long hash = getBoardHash().getFullHash();
//        hash = Zobrist.combineHashes(hash, Zobrist.playerHash(player));
//        if (isInCheckHashMap.containsKey(hash)) {
//            return isInCheckHashMap.get(hash);
//        }
        boolean ret = isThreatened(getKing(playerColor), PlayerColor.getOpponent(playerColor));
//        isInCheckHashMap.put(hash, ret);
        return ret;
    }

    public boolean isThreatened(Location loc, PlayerColor threateningPlayerColor) {
        return getAttackedSquares(threateningPlayerColor).isSet(loc);

        //working slow
//        return MoveGenerator.generateMoves(this, false).stream()
//                .anyMatch(move -> move.getMovingTo().equals(loc));
    }

    public Location getKing() {
        return getKing(currentPlayerColor);
    }

    public Location getKing(PlayerColor playerColor) {
        Bitboard k = getPieceBitBoard(playerColor, PieceType.KING);
        return k.isEmpty() ? null : k.getSetLocs().get(0);
    }

    public Bitboard getPieceBitBoard(PlayerColor playerColor, PieceType pieceType) {
        return getPlayersPieces(playerColor).getBB(pieceType);
    }

    public boolean anyLegalMove(PlayerColor playerColor) {
//        Bitboard[] playersPieces = getPlayersPieces(playerColor);
        //todo can return true if one piece can move to any
        return !generateAllMoves(playerColor).isEmpty();
    }

    public ArrayList<Move> generateAllMoves(PlayerColor player) {
        ArrayList<Move> ret = new ArrayList<>();
        if (player == currentPlayerColor) {
            ret = generateAllMoves();
        }
        return ret;
    }

//    public void applyMove(String moveStr) {
//        for (Move move : generateAllMoves()) {
//            if (move.getAnnotation().equals(moveStr)) {
//                applyMove(move);
//                return;
//            }
//        }
//        System.out.println("didnt find move str to play");
//
//    }

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
        PlayerColor piecePlayerColor = piece.getPlayer();

        move.getMoveAnnotation().setDetailedAnnotation(move, piece.getPieceType());
        move.setPrevFullMoveClock(fullMoveClock);
        move.setPrevHalfMoveClock(halfMoveClock);
        move.setReversible(piece);

        makeIntermediateMove(move);

        //region move instances
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
        //endregion

        if (currentPlayerColor == PlayerColor.BLACK)
            fullMoveClock++;

        boolean didDisableCastling = false;

        CastlingRights disabling = null;
        if (castlingRights.hasAny(piecePlayerColor)) {
            if (piece.getPieceType() == PieceType.KING) {
                disabling = new CastlingRights(castlingRights);
                castlingRights.disableCastling(piecePlayerColor);
                didDisableCastling = true;
            } else if (piece.getPieceType() == PieceType.ROOK) {
                didDisableCastling = true;
                CastlingRights.Side side = getSideRelativeToKing(this, piecePlayerColor, movingTo);
                disabling = new CastlingRights(castlingRights);
                castlingRights.disableCastling(piecePlayerColor, side);
            }
        }
        if (move.isCapturing()) {
            Piece otherPiece = board.getPiece(movingTo);
            assert otherPiece != null && otherPiece.getPieceType() != PieceType.KING;
            if (otherPiece.getPieceType() == PieceType.ROOK) {
                PlayerColor capClr = otherPiece.getPlayer();
                if (castlingRights.isEnabled(capClr, getSideRelativeToKing(this, capClr, movingTo))) {
                    didDisableCastling = true;
                    CastlingRights.Side side = getSideRelativeToKing(this, otherPiece.getPlayer(), movingTo);
                    disabling = new CastlingRights(castlingRights);
                    castlingRights.disableCastling(otherPiece.getPlayer(), side);
                }
            }
            delPiece(otherPiece, movingTo);
        }
        updatePieceLoc(piece, movingFrom, movingTo);

        move.setPrevCastlingAbilities(disabling);
        if (didDisableCastling) {
            move.setNotReversible();
        }

        moveStack.push(move);

        if (move.isReversible()) {
//            if (checkRep())
//                move.setThreefoldOption();
            setHalfMoveClock(move.getPrevHalfMoveClock() + 1);

        } else {
            setHalfMoveClock(0);
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

    private boolean checkRep() {
        if (moveStack.size() < 8)
            return false;

        Move[] chainList = new Move[24];
        Arrays.fill(chainList, null);
        short c = 0;
        l:
        for (int j = 0; j < moveStack.size(); j++) {
            Move currentMove = moveStack.get(j);
            if (!currentMove.isReversible()) {
                break;
            }
            for (int i = 0; i < chainList.length; i++) {
                Move currentLink = chainList[i];
                if (currentLink == null)
                    continue;
                if (currentMove.getMovingTo().equals(currentLink.getMovingFrom())) {
                    if (currentMove.getMovingFrom().equals(currentLink.getMovingTo())) {
                        if (--c == 0) {
                            return true;
                        }
                        chainList[i] = null;
                        continue l;
                    }
                    currentLink.setMovingFrom(currentMove.getMovingFrom());
                    continue l;
                }
            }
            for (int i = 0; i < 24; i++) {
                if (chainList[i] == null) {
                    chainList[i] = Move.copyMove(currentMove);
                    ++c;
                    continue l;
                }
            }
            break;
        }
        return false;
    }

    private void verifyBitBoardsAndMat() {
        for (Square square : board) {
            if (!square.isEmpty()) {
                Piece piece = square.getPiece();
                assert getPieceBitBoard(piece).isSet(square.getLoc());
            }
        }
        for (Piece piece : Piece.ALL_PIECES) {
            getPieceBitBoard(piece).getSetLocs().forEach(loc -> {
                assert board.getPiece(loc).equals(piece);
            });
        }

    }

    public void undoMove() {
        Move move = moveStack.pop();

        setFullMoveClock(move.getPrevFullMoveClock());
        setHalfMoveClock(move.getPrevHalfMoveClock());

        CastlingRights prevMoveCastlingRights = move.getPrevCastlingAbilities();
        if (prevMoveCastlingRights != null) {
            castlingRights = new CastlingRights(prevMoveCastlingRights);
        }

        Location movingFrom = move.getMovingTo();
        Location movingTo = move.getMovingFrom();

        Piece piece = board.getPiece(movingFrom, true);
        PlayerColor piecePlayerColor = piece.getPlayer();
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
            PlayerColor opponent = PlayerColor.getOpponent(piecePlayerColor);
            Piece otherPiece = Piece.getPiece(move.getCapturingPieceType(), opponent);
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
        piecesCount[piece.getPlayer().asInt()][piece.getPieceType().asInt()]--;
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
        currentPlayerColor = PlayerColor.getOpponent(currentPlayerColor);
    }

    public int[] getPiecesCount(PlayerColor playerColor) {
        return piecesCount[playerColor.asInt()];
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

    private void setBoardHash() {
        boardHash.setAll(this);
    }

    public BoardHash hashBoard() {
        return new BoardHash(this);
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
