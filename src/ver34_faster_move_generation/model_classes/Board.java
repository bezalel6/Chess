package ver34_faster_move_generation.model_classes;

import ver34_faster_move_generation.Controller;
import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.MyError;
import ver34_faster_move_generation.Player;
import ver34_faster_move_generation.model_classes.eval_classes.Eval;
import ver34_faster_move_generation.model_classes.eval_classes.Evaluation;
import ver34_faster_move_generation.model_classes.moves.*;
import ver34_faster_move_generation.model_classes.pieces.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static ver34_faster_move_generation.model_classes.pieces.Piece.*;

public class Board implements Iterable<Square[]> {

    public static final ConcurrentHashMap<Long, ArrayList<Move>> movesGenerationHashMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, Boolean> isInCheckHashMap = new ConcurrentHashMap<>();
    private final FEN fen;
    private final Eval boardEval;
    private final Stack<Move> moveStack;
    private ArrayList<Long> repetitionHashList;
    private Square[][] logicMat;
    private ConcurrentHashMap<Location, Piece>[] pieces;
    private King[] kingsArr;
    private int[][] piecesCount;
    private int currentPlayer;
    private int halfMoveClock, fullMoveClock;
    private Location enPassantTargetLoc;
    private Location enPassantActualLoc;
    private CastlingAbility castlingAbility;
    private BoardHash boardHash;

    //create empty board
    public Board() {
        this("8/8/8/8/8/8/8/8 w - - 0 1");
    }

    public Board(String fenStr) {
        boardEval = new Eval(this);
        this.fen = new FEN(fenStr, this);
        repetitionHashList = new ArrayList<>();
        moveStack = new Stack<>();
        createNewLogicBoard(fen);
        initPiecesArrays();
        loadMat();
        this.boardHash = new BoardHash(this);
    }

    public Board(Board other) {
        this.fen = new FEN(this);
        this.boardEval = new Eval(this);
        this.moveStack = new Stack<>();
        for (Move move : other.moveStack) {
            moveStack.push(Move.copyMove(move));
        }
        this.repetitionHashList = new ArrayList<>(other.repetitionHashList);
        initPiecesArrays();

        createNewEmptyLogicBoard();
        loadMat(other.logicMat, true);

        this.currentPlayer = other.currentPlayer;
        this.fullMoveClock = other.fullMoveClock;
        this.halfMoveClock = other.halfMoveClock;

        this.enPassantActualLoc = new Location(other.enPassantActualLoc);
        this.enPassantTargetLoc = new Location(other.enPassantTargetLoc);

        this.castlingAbility = new CastlingAbility(other.castlingAbility);
        this.boardHash = new BoardHash(this);
    }

    private void createNewEmptyLogicBoard() {
        logicMat = new Square[Controller.ROWS][Controller.COLS];
        for (int i = 0; i < logicMat.length; i++) {
            for (int j = 0; j < logicMat[i].length; j++) {
                logicMat[i][j] = new Square(new Location(i, j));
            }
        }
    }


    public CastlingAbility getCastlingAbility() {
        return castlingAbility;
    }

    private void loadMat() {
        loadMat(logicMat, false);
    }

    private void loadMat(Square[][] board, boolean copyPiece) {
        for (Square[] row : board)
            for (Square square : row)
                if (!square.isEmpty()) {
                    Piece piece = square.getPiece();
                    Location startingLoc = new Location(piece.getStartingLoc());
                    if (copyPiece) {
                        piece = Piece.copyPiece(piece);
                    }
                    setPiece(piece.getLoc(), piece);
                    pieces[piece.getPieceColor()].put(startingLoc, piece);

                    if (piece instanceof King) {
                        kingsArr[piece.getPieceColor()] = (King) piece;
                    }

                    piecesCount[piece.getPieceColor()][piece.getPieceType()]++;
                }
    }

    private void initPiecesArrays() {
        kingsArr = new King[NUM_OF_PLAYERS];
        pieces = new ConcurrentHashMap[NUM_OF_PLAYERS];
        piecesCount = new int[NUM_OF_PLAYERS][NUM_OF_PIECE_TYPES];
        for (int i = 0; i < NUM_OF_PLAYERS; i++) {
            Arrays.fill(piecesCount[i], 0);
            pieces[i] = new ConcurrentHashMap<>();
        }
    }

    private void createNewLogicBoard(FEN fen) {
        createNewEmptyLogicBoard();
        fen.loadFEN();
        castlingAbility = new CastlingAbility(fen.getCastlingAbilityStr());
        currentPlayer = fen.getInitialPlayerToMove();
    }

    public Location getEnPassantTargetLoc() {
        return enPassantTargetLoc;
    }

    public void setEnPassantTargetLoc(Location enPassantTargetLoc) {
        this.enPassantTargetLoc = enPassantTargetLoc;
        if (enPassantTargetLoc != null)
            this.enPassantTargetLoc = new Location(enPassantTargetLoc);
    }

    public void setEnPassantTargetLoc(String enPassantTargetLocStr) {
        if (enPassantTargetLocStr.replaceAll("\\s+", "").equalsIgnoreCase("-")) {
            this.enPassantTargetLoc = null;
        } else {
            this.enPassantTargetLoc = new Location(enPassantTargetLocStr);
            int row = enPassantTargetLoc.getRow();
            int diff = (row == (STARTING_ROW[Player.WHITE] + 3)) ? Piece.getDifference(Player.WHITE) : Piece.getDifference(Player.BLACK);
            row -= diff;
            this.enPassantActualLoc = new Location(row, enPassantTargetLoc.getCol());
        }
    }

    public Location getEnPassantActualLoc() {
        return enPassantActualLoc;
    }

    public void setEnPassantActualLoc(Location enPassantActualLoc) {
        this.enPassantActualLoc = enPassantActualLoc;
        if (enPassantActualLoc != null)
            this.enPassantActualLoc = new Location(enPassantActualLoc);
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

    public Eval getEval() {
        return boardEval;
    }

    public Evaluation getEvaluation() {
        return getEvaluation(currentPlayer);
    }

    public Evaluation getEvaluation(int player) {
        return boardEval.getEvaluation(player);
    }


    public FEN getFen() {
        return fen;
    }

    public String getFenStr() {
        return fen.generateFEN();
    }

    private void replacePiece(Piece newPiece, Piece oldPiece) {
        assert newPiece != null && oldPiece != null;
        Location pieceLoc = oldPiece.getLoc();
        int oldPlayer = oldPiece.getPieceColor();
        int newPlayer = newPiece.getPieceColor();
        pieces[oldPlayer].remove(oldPiece.getStartingLoc());
        pieces[newPlayer].put(newPiece.getStartingLoc(), newPiece);
        setPiece(pieceLoc, newPiece);
    }

    public void setPiece(Location loc, Piece piece) {
        logicMat[loc.getRow()][loc.getCol()].setPiece(piece);
    }

    public void setSquareEmpty(Location loc) {
        logicMat[loc.getRow()][loc.getCol()].setEmpty();
    }

    public Piece getPiece(Location loc) {
        return logicMat[loc.getRow()][loc.getCol()].getPiece();
    }

    public int bothPlayersNumOfPieces(int[] arr) {
        return getNumOfPieces(Player.WHITE, arr) + getNumOfPieces(Player.BLACK, arr);
    }

    public int getNumOfPieces(int player, int[] arr) {
        int ret = 0;
        for (int pieceType : arr) {
            ret += piecesCount[player][pieceType];
        }
        return ret;
    }

    public int bothPlayersNumOfPieces(int pieceType) {
        return getNumOfPieces(Player.WHITE, pieceType) + getNumOfPieces(Player.BLACK, pieceType);
    }

    public int getNumOfPieces(int player, int pieceType) {
        return piecesCount[player][pieceType];
    }

    public boolean isInCheck() {
        return isInCheck(currentPlayer);
    }

    public boolean isInCheck(int player) {
        long hash = getBoardHash().getFullHash();
        hash = Zobrist.combineHashes(hash, Zobrist.playerHash(player));
        if (isInCheckHashMap.containsKey(hash)) {
            return isInCheckHashMap.get(hash);
        }
        boolean ret = isThreatened(getKing(player));
        isInCheckHashMap.put(hash, ret);
        return ret;
    }

    public boolean isThreatened(Piece piece) {
        return isThreatened(piece.getLoc(), piece.getOpponent());
    }

    public boolean isThreatened(Location loc, int threateningPlayer) {
//        long tHash = getBoardHash().getPiecesHash();
//        tHash = Zobrist.combineHashes(tHash, Zobrist.hash(loc, threateningPlayer));
//        if (threatenedHashMap.containsKey(tHash)) {
//            return threatenedHashMap.get(tHash);
//        }
        boolean res = false;
        for (int type : UNIQUE_MOVES_PIECE_TYPES) {
            if (checkThreatening(loc, threateningPlayer, type)) {
                res = true;
                break;
            }
        }
//        threatenedHashMap.put(tHash, res);
        return res;
    }

    public ArrayList<Piece> piecesLookingAt(Piece piece) {
        return piecesLookingAt(piece.getLoc());
    }

    public ArrayList<Piece> piecesLookingAt(Location loc) {
        ArrayList<Piece> ret = new ArrayList<>();
        for (int type : PIECES_TYPES) {
            Piece piece = getPieceLookingAtMe(loc, type);
            if (piece != null)
                ret.add(piece);
        }
        return ret;
    }

    //    can capture any player
    public ArrayList<Move> getPieceMovesFrom(Location from, int type) {
        return getPieceMovesFrom(from, type, Player.NO_PLAYER);
    }

    public ArrayList<Move> getPieceMovesFrom(Location from, int type, int player) {
        assert Piece.isValidPieceType(type);
        ArrayList<ArrayList<Move>> lists;
        switch (type) {
            case BISHOP:
                lists = Bishop.createBishopMoves(from, player);
                break;
            case ROOK:
                lists = Rook.createRookMoves(from, player);
                break;
            case KNIGHT:
                lists = Knight.createKnightMoves(from, player);
                break;
            case PAWN:
                lists = Pawn.createPawnMoves(from, player, true);
                break;
            case QUEEN:
                lists = Queen.createQueenMoves(from, player);
                break;
            case KING:
                lists = King.createKingMoves(from, player);
                break;
            default:
                MyError.error("Wrong Piece Type");
                lists = new ArrayList<>();
                break;
        }
        ArrayList<Move> ret = convertListOfLists(lists);
        return ret;
    }

    private Piece getPieceLookingAtMe(Location loc, int type) {
        for (Move move : getPieceMovesFrom(loc, type)) {
            if (move.isCapturing() && compareMovementType(type, move.getCapturingPieceType()))
                return getPiece(move.getMovingTo());
        }
        return null;
    }


    private boolean checkThreatening(Location loc, int threateningPlayer, int pieceType) {
        ArrayList<Move> moves = getPieceMovesFrom(loc, pieceType, Player.getOpponent(threateningPlayer));
        for (Move move : moves) {
            if (move.isCapturing() && compareMovementType(pieceType, move.getCapturingPieceType()))
                return true;
        }
        return false;
    }

    public King getKing(int player) {
        return kingsArr[player];
    }

    public ConcurrentHashMap<Location, Piece> getPieces(int player) {
        return pieces[player];
    }

    public Collection<Piece> getPlayersPieces(int player) {
//        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//        System.out.println(stackTraceElements[2]);
        return pieces[player].values();
    }


    public boolean anyLegalMove(int player) {
        for (Piece piece : getPlayersPieces(player)) {
            if (!piece.isCaptured()) {
                if (piece.canMoveTo(this).size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Move> generateAllMoves() {
//        long hash = getBoardHash().getFullHash();
//        if (movesGenerationHashMap.containsKey(hash)) {
//            return movesGenerationHashMap.get(hash);
//        }
        ArrayList<Move> ret = new ArrayList<>();
        for (Piece piece : getPlayersPieces(currentPlayer)) {
            if (!piece.isCaptured()) {
                ret.addAll(piece.canMoveTo(this));
            }
        }
//        movesGenerationHashMap.put(hash, ret);
        return ret;
    }

    @Override
    public Iterator<Square[]> iterator() {
        return Arrays.stream(logicMat).iterator();
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void applyMove(Move move) {
//        move.setPrevBoardHash(boardHash);
        Location movingFrom = move.getMovingFrom();
        Location movingTo = move.getMovingTo();

        Piece piece = getPiece(movingFrom, true);
        int pieceColor = piece.getPieceColor();

        makeIntermediateMoveIfExists(move);

        //region move instances
        if (move.getMoveFlag() == Move.MoveFlag.DoublePawnPush) {
            setEnPassantTargetLoc(move.getEnPassantLoc());
            setEnPassantActualLoc(movingTo);
        } else {
            setEnPassantTargetLoc((Location) null);
            setEnPassantActualLoc(null);
        }
        if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
            int promotingTo = move.getPromotingTo();
            piecesCount[pieceColor][promotingTo]++;
            piecesCount[pieceColor][piece.getPieceType()]--;
            Piece newPiece = Piece.promotePiece(piece, promotingTo);
            replacePiece(newPiece, piece);
            piece = newPiece;
        }
        //endregion

        if (move.getMovingPlayer() == Player.BLACK)
            fullMoveClock++;

//        if (move.getDisableCastling() != null)
//            for (Integer[] disable : move.getDisableCastling()) {
//                if (disable.length == 2) {
//                    castlingAbility.disableCastlingAbility(disable[0], disable[1]);
//                } else {
//                    castlingAbility.disableCastlingAbility(disable[0]);
//                }
//            }

        if (move.isCapturing()) {
            Piece otherPiece = getPiece(movingTo);
            assert otherPiece != null;
            piecesCount[otherPiece.getPieceColor()][otherPiece.getPieceType()]--;
            otherPiece.setCaptured(true);
        }
        piece.setLoc(movingTo);
        setPiece(movingTo, piece);

        setSquareEmpty(movingFrom);

        if (!move.isReversible()) {
            setHalfMoveClock(0);
            repetitionHashList.clear();
        }
        switchTurn();
        setBoardHash();
//        if (move.isReversible()) {
//            setHalfMoveClock(move.getPrevHalfMoveClock() + 1);
//            repetitionHashList.add(getBoardHash().getFullHash());
//        }

        moveStack.push(move);
    }


    public void undoMove(Move _m) {
        Move move = moveStack.pop();
        assert _m == move;
//        boardHash = new BoardHash(move.getPrevBoardHash());
        setBoardHash();
        if (move.getMovingPlayer() == Player.BLACK)
            fullMoveClock--;
//        setHalfMoveClock(move.getPrevHalfMoveClock());

//        repetitionHashList = new ArrayList<>(move.getPrevRepetitionHashList());

//        castlingAbility = new CastlingAbility(move.getPrevCastlingAbility());

        Location movingFrom = move.getMovingTo();
        Location movingTo = move.getMovingFrom();

        Piece piece = getPiece(movingFrom, true);
        int pieceColor = piece.getPieceColor();
        if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
            piecesCount[pieceColor][move.getPromotingTo()]--;
            piecesCount[pieceColor][PAWN]++;
            Pawn oldPiece = new Pawn(piece);
            replacePiece(oldPiece, piece);
            piece = oldPiece;
        }
        if (!moveStack.empty()) {
            Move prevMove = moveStack.peek();
            if (prevMove.getMoveFlag() == Move.MoveFlag.DoublePawnPush) {
                setEnPassantTargetLoc(prevMove.getEnPassantLoc());
                setEnPassantActualLoc(prevMove.getMovingTo());
            }
        }
        piece.setLoc(movingTo);
        setPiece(movingTo, piece);
        setSquareEmpty(movingFrom);

        if (move.isCapturing()) {
            int opponent = Player.getOpponent(pieceColor);
            Piece otherPiece = getCapturedPiece(move.getCapturingPieceType(), opponent, movingFrom);
            assert otherPiece != null;
            otherPiece.setCaptured(false);
            setPiece(movingFrom, otherPiece);
            piecesCount[otherPiece.getPieceColor()][otherPiece.getPieceType()]++;
        }
        makeIntermediateMoveIfExists(move, true);

        switchTurn();
    }

    private Piece getCapturedPiece(int capturingPieceType, int player, Location capturedOn) {
        for (Piece piece : getPlayersPieces(player)) {
            if (piece.isCaptured() && piece.getPieceType() == capturingPieceType && piece.isOnMyTeam(player) && piece.getLoc().equals(capturedOn)) {
                return piece;
            }
        }
        return null;
    }

    private void makeIntermediateMoveIfExists(Move move) {
        makeIntermediateMoveIfExists(move, false);
    }

    private void makeIntermediateMoveIfExists(Move move, boolean flip) {
        BasicMove intermediateMove = move.getIntermediateMove();
        if (intermediateMove != null) {
            if (flip) {
                intermediateMove = intermediateMove.flip();
            }
            movePiece(intermediateMove);
        }
    }

    private Piece getPiece(Location startingLoc, int player) {
        return getPieces(player).get(startingLoc);
    }

    public void switchTurn() {
        currentPlayer = Player.getOpponent(currentPlayer);
    }

    public ArrayList<Long> getRepetitionHashList() {
        return repetitionHashList;
    }

    public int[] getPiecesCount(int player) {
        return piecesCount[player];
    }

    public Piece getPiece(Location loc, boolean notNull) {
        Piece ret = getPiece(loc);
        assert !notNull || ret != null;
        return ret;
    }

    private void disableCastling(Rook rook) {
        castlingAbility.disableCastlingAbility(rook.getPieceColor(), rook.getSideRelativeToKing(this));
    }

    private void movePiece(BasicMove basicMove) {
        movePiece(basicMove.getMovingFrom(), basicMove.getMovingTo());
    }

    private void movePiece(Location movingFrom, Location movingTo) {
        assert isSquareEmpty(movingTo);
        Piece piece = getPiece(movingFrom, true);
        setPiece(movingTo, piece);
        piece.setLoc(movingTo);
        setSquareEmpty(movingFrom);
    }

    private Square getSquare(Location loc) {
        return logicMat[loc.getRow()][loc.getCol()];
    }

    private void undoCastle(Castling castling) {
        movePiece(castling.getCastlingLocs()[Castling.ROOK_FINAL_LOC], castling.getCastlingLocs()[Castling.ROOK_STARTING_LOC]);
    }

    private void castle(Castling castling) {
        movePiece(castling.getCastlingLocs()[Castling.ROOK_STARTING_LOC], castling.getCastlingLocs()[Castling.ROOK_FINAL_LOC]);
    }

    @Override
    public String toString() {
        String divider = "|";
        StringBuilder ret = new StringBuilder();

        ret.append(" ");
        for (int j = 0; j < logicMat.length; j++) {
            ret.append(" ").append((char) ('ａ' + j)).append(" ");
        }
        ret.append(Controller.HIDE_PRINT).append("\n");

        for (int i = 0, rowNum = logicMat.length - 1; i < logicMat.length; i++, rowNum--) {
            ret.append(rowNum + 1);
            for (Square square : logicMat[i]) {
                ret.append(divider).append(square.toString()).append(divider);
            }
            ret.append(Controller.HIDE_PRINT).append("\n");
        }

        return ret.toString();
    }

    public void printBoard() {
        System.out.println(this);
    }


    public boolean isSquareEmpty(Location loc) {
        return getSquare(loc).isEmpty();
    }

    public void makeMove(Move move) {
        applyMove(move);
        Evaluation currentEval = boardEval.getEvaluation();
        move.setMoveEvaluation(currentEval);
    }

    public void unmakeMove(Move move) {
        undoMove(move);
    }


    public Square[][] getLogicMat() {
        return logicMat;
    }


    public int getOpponent() {
        return Player.getOpponent(currentPlayer);
    }

    public Map<Location, Piece>[] getPieces() {
        return pieces;
    }

    public BoardHash getBoardHash() {
        return boardHash;
    }

    private void setBoardHash() {
        boardHash.setAll(this);
    }

    private BoardHash hashBoard() {
        return new BoardHash(this);
    }

    public ArrayList<Move> getAllCaptureMoves() {

        return generateAllMoves().stream().filter(Move::isCapturing).collect(Collectors.toCollection(ArrayList::new));
    }
}
