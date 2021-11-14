package ver35_thread_pool.model_classes;

import ver35_thread_pool.Controller;
import ver35_thread_pool.Location;
import ver35_thread_pool.MyError;
import ver35_thread_pool.Player;
import ver35_thread_pool.model_classes.eval_classes.Eval;
import ver35_thread_pool.model_classes.eval_classes.Evaluation;
import ver35_thread_pool.model_classes.moves.BasicMove;
import ver35_thread_pool.model_classes.moves.Move;
import ver35_thread_pool.model_classes.moves.PieceMoves;
import ver35_thread_pool.model_classes.pieces.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static ver35_thread_pool.model_classes.pieces.Piece.*;

public class Board implements Iterable<Square[]> {

    public static final ConcurrentHashMap<Long, ArrayList<Move>> movesGenerationHashMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, Boolean> isInCheckHashMap = new ConcurrentHashMap<>();
    private final FEN fen;
    private final Eval boardEval;
    private final Stack<Move> moveStack;
    private Square[][] logicMat;
    private ConcurrentHashMap<Location, Piece>[] pieces;
    private King[] kingsArr;
    private int[][] piecesCount;
    private int currentPlayer;
    private int halfMoveClock, fullMoveClock;
    private int castlingAbility;
    private Location enPassantTargetLoc;
    private Location enPassantActualLoc;
    private BoardHash boardHash;

    //create empty board
    public Board() {
        this("8/8/8/8/8/8/8/8 w - - 0 1");
    }

    public Board(String fenStr) {
        boardEval = new Eval(this);
        this.fen = new FEN(fenStr, this);
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
        initPiecesArrays();

        createNewEmptyLogicBoard();
        loadMat(other.logicMat, true);

        this.currentPlayer = other.currentPlayer;
        this.fullMoveClock = other.fullMoveClock;
        this.halfMoveClock = other.halfMoveClock;
        this.castlingAbility = other.castlingAbility;

        this.enPassantActualLoc = new Location(other.enPassantActualLoc);
        this.enPassantTargetLoc = new Location(other.enPassantTargetLoc);

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


    public int getCastlingAbility() {
        return castlingAbility;
    }

    public void setCastlingAbility(int castlingAbility) {
        this.castlingAbility = castlingAbility;
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

    public Move findMove(BasicMove basicMove) {
        for (Move move : generateAllMoves()) {
            if (move.getBasicMoveString().equals(basicMove.getBasicMoveString())) {
                return move;
            }
        }
        return null;
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
                lists = Bishop.getPseudoBishopMoves(from);
                break;
            case ROOK:
                lists = Rook.getPseudoRookMoves(from);
                break;
            case KNIGHT:
                lists = Knight.getPseudoKnightMoves(from);
                break;
            case PAWN:
                lists = Pawn.createCaptureMoves(from, player);
                break;
            case QUEEN:
                lists = Queen.createQueenMoves(from);
                break;
            case KING:
                lists = King.getKingMoves(from, player);
                break;
            default:
                MyError.error("Wrong Piece Type");
                lists = new ArrayList<>();
                break;
        }
        lists = PieceMoves.createPseudoLegalMoves(this, lists, type, player);
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
        long hash = getBoardHash().getFullHash();
        if (movesGenerationHashMap.containsKey(hash)) {
            return movesGenerationHashMap.get(hash);
        }

        ArrayList<Move> ret = new ArrayList<>();
        for (Piece piece : getPlayersPieces(currentPlayer)) {
            if (!piece.isCaptured()) {
                ret.addAll(piece.canMoveTo(this));
            }
        }

        setUniqueStrs(ret);
        movesGenerationHashMap.put(hash, ret);
        return ret;
    }

    private void setUniqueStrs(ArrayList<Move> list) {
        HashMap<Integer, ArrayList<Move>> uniqueMovesNotation = new HashMap<>();
        for (Move move : list) {
            String str = move.getAnnotation();
            int hash = str.hashCode();
            if (!uniqueMovesNotation.containsKey(hash)) {
                uniqueMovesNotation.put(hash, new ArrayList<>());
            }
            uniqueMovesNotation.get(hash).add(move);
        }
        for (ArrayList<Move> moves : uniqueMovesNotation.values()) {
            if (moves.size() > 1) {
                for (Move move : moves) {
                    Location movingFrom = move.getMovingFrom();
                    String uniqueStr = movingFrom.toString();
                    boolean uniqueRow = true;
                    boolean uniqueCol = true;
                    for (Move other : moves) {
                        if (!other.equals(move)) {
                            Location otherMovingFrom = other.getMovingFrom();
                            if (otherMovingFrom.getRow() == movingFrom.getRow()) {
                                uniqueRow = false;
                            }
                            if (otherMovingFrom.getCol() == movingFrom.getCol()) {
                                uniqueCol = false;
                            }
                        }
                    }
                    if (uniqueCol) {
                        uniqueStr = movingFrom.getColString();
                    } else if (uniqueRow) {
                        uniqueStr = movingFrom.getRowString();
                    }
                    move.getMoveAnnotation().setUniqueStr(uniqueStr);
                }
            }
        }
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

    public void applyMove(String moveStr) {
        for (Move move : generateAllMoves()) {
            if (move.getAnnotation().equals(moveStr)) {
                applyMove(move);
                return;
            }
        }
        System.out.println("didnt find move str to play");

    }

    public void applyMove(Move move) {

        Location movingFrom = move.getMovingFrom();
        Location movingTo = move.getMovingTo();

        Piece piece = getPiece(movingFrom, true);
        int pieceColor = piece.getPieceColor();

        move.getMoveAnnotation().setDetailedAnnotation(piece.getPieceType());
        move.setPrevFullMoveClock(fullMoveClock);
        move.setPrevHalfMoveClock(halfMoveClock);
        move.setReversible(piece);
        move.setPrevCastlingAbility(castlingAbility);

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
            int promotingTo = move.getPromotingTo();
            piecesCount[pieceColor][promotingTo]++;
            piecesCount[pieceColor][piece.getPieceType()]--;
            Piece newPiece = Piece.promotePiece(piece, promotingTo);
            replacePiece(newPiece, piece);
            piece = newPiece;
        }
        //endregion

        if (currentPlayer == Player.BLACK)
            fullMoveClock++;

        if (piece instanceof King) {
            castlingAbility = CastlingAbility.disableCastlingForBothSides(pieceColor, castlingAbility);
        } else if (piece instanceof Rook) {
            castlingAbility = CastlingAbility.disableCastling(pieceColor, ((Rook) piece).getSideRelativeToKing(this), castlingAbility);
        }
        if (move.isCapturing()) {
            Piece otherPiece = getPiece(movingTo);
            assert otherPiece != null;
            if (otherPiece instanceof Rook) {
                int side = ((Rook) otherPiece).getSideRelativeToKing(this);
                castlingAbility = CastlingAbility.disableCastling(otherPiece.getPieceColor(), side, castlingAbility);
            }
            piecesCount[otherPiece.getPieceColor()][otherPiece.getPieceType()]--;
            otherPiece.setCaptured(true);
        }
        piece.setLoc(movingTo);
        setPiece(movingTo, piece);

        setSquareEmpty(movingFrom);

        if (!move.isReversible()) {
            setHalfMoveClock(0);
        }

        switchTurn();
        if (move.isReversible()) {
            setHalfMoveClock(move.getPrevHalfMoveClock() + 1);
        }

        if (isInCheck()) {
            Evaluation e = new Evaluation();
            e.setGameStatusType(GameStatus.GameStatusType.CHECK);
            move.setMoveEvaluation(e);
        }
        moveStack.push(move);

        setBoardHash();

    }


    public Move undoMove() {
        Move move = moveStack.pop();

        setFullMoveClock(move.getPrevFullMoveClock());
        setHalfMoveClock(move.getPrevHalfMoveClock());

        castlingAbility = move.getPrevCastlingAbility();

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
        setEnPassantTargetLoc((Location) null);
        setEnPassantActualLoc(null);
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
        makeIntermediateMove(move, true);

        switchTurn();

        setBoardHash();
        return move;
    }

    private Piece getCapturedPiece(int capturingPieceType, int player, Location capturedOn) {
        for (Piece piece : getPlayersPieces(player)) {
            if (piece.isCaptured() && piece.getPieceType() == capturingPieceType && piece.isOnMyTeam(player) && piece.getLoc().equals(capturedOn)) {
                return piece;
            }
        }
        return null;
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
        currentPlayer = Player.getOpponent(currentPlayer);
    }

    public int[] getPiecesCount(int player) {
        return piecesCount[player];
    }

    public Piece getPiece(Location loc, boolean notNull) {
        Piece ret = getPiece(loc);
        assert !notNull || ret != null;
        return ret;
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
        move.setMoveEvaluation(boardEval.getEvaluation());
    }

    public void unmakeMove() {
        undoMove();
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

    public BoardHash hashBoard() {
        return new BoardHash(this);
    }

    public ArrayList<Move> getAllCaptureMoves() {

        return generateAllMoves().stream().filter(Move::isCapturing).collect(Collectors.toCollection(ArrayList::new));
    }
}
