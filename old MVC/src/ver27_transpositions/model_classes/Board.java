package ver27_transpositions.model_classes;

import ver27_transpositions.Controller;
import ver27_transpositions.Location;
import ver27_transpositions.MyError;
import ver27_transpositions.Player;
import ver27_transpositions.model_classes.eval_classes.Eval;
import ver27_transpositions.model_classes.eval_classes.Evaluation;
import ver27_transpositions.model_classes.moves.*;
import ver27_transpositions.model_classes.pieces.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ver27_transpositions.model_classes.pieces.Piece.*;

public class Board implements Iterable<Square[]> {

    private static final ConcurrentHashMap<Long, Boolean> threatenedHashMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, ArrayList<Move>> movesGenerationHashMap = new ConcurrentHashMap<>();
    private final FEN fen;
    private ArrayList<Long> repetitionHashList;
    private Square[][] logicMat;
    private ConcurrentHashMap<Location, Piece>[] pieces;
    private King[] kingsArr;
    private Eval boardEval;
    private int[][] piecesCount;
    private int currentPlayer;
    private int halfMoveClock, fullMoveClock;
    private Location enPassantTargetLoc;
    private Location enPassantActualLoc;
    private CastlingAbility castlingAbility;
    private long boardHash;

    public Board(String fenStr, Controller controller) {
        boardEval = new Eval(this);
        this.fen = new FEN(fenStr, this);
        setMat(fen);
        boardEval = new Eval(this);
        repetitionHashList = new ArrayList<>();
        initPiecesArrays();
        loadMat();
        setBoardHash();
    }

    public Board(Board other) {
        boardEval = new Eval(this);
        this.fen = new FEN(other.fen.generateFEN(), this);
        setMat(fen);
        boardEval = new Eval(this);
        repetitionHashList = new ArrayList<>(other.repetitionHashList);
        initPiecesArrays();
        loadMat(other.logicMat, true);
        setBoardHash();
    }

    private void setBoardHash() {
        boardHash = hashBoard();

    }

    private long hashBoard() {
        return hashBoard(currentPlayer);
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
                    logicMat[piece.getLoc().getRow()][piece.getLoc().getCol()].setPiece(piece);
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

    private void setMat(FEN fen) {
        logicMat = new Square[Controller.ROWS][Controller.COLS];
        for (int i = 0; i < logicMat.length; i++) {
            for (int j = 0; j < logicMat[i].length; j++) {
                logicMat[i][j] = new Square(new Location(i, j));
            }
        }
        fen.loadFEN();
        castlingAbility = new CastlingAbility(fen.getCastlingAbilityStr());
        currentPlayer = fen.getInitialPlayerToMove();
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

    public boolean isInCheck(int player) {
        return isThreatened(getKing(player));
    }

    public boolean isThreatened(Piece piece) {
        return isThreatened(piece.getLoc(), piece.getOpponent());
    }

    public boolean isThreatened(Location loc, int threateningPlayer) {
        long tHash = getBoardHash();
        tHash = Zobrist.combineHashes(tHash, Zobrist.hash(loc, threateningPlayer));
        if (threatenedHashMap.containsKey(tHash)) {
            return threatenedHashMap.get(tHash);
        }
        boolean res = false;
        for (int type : UNIQUE_MOVES_PIECE_TYPES) {
            if (checkThreatening(loc, threateningPlayer, type)) {
                res = true;
                break;
            }
        }
        threatenedHashMap.put(tHash, res);
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
                lists = Bishop.createBishopMoves(from, player, false, this);
                break;
            case ROOK:
                lists = Rook.createRookMoves(from, player, false, this);
                break;
            case KNIGHT:
                lists = Knight.createKnightMoves(from, player, false, this);
                break;
            case PAWN:
                lists = Pawn.createPawnMoves(from, player, false, this, true);
                break;
            case QUEEN:
                lists = Queen.createQueenMoves(from, player, false, this);
                break;
            case KING:
                lists = King.createKingMoves(from, player, false, this);
                break;
            default:
                MyError.error("Wrong Piece Type");
                lists = new ArrayList<>();
                break;
        }
        return convertListOfLists(lists);
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

    public ArrayList<Move> getAllMoves() {
        return getAllMoves(currentPlayer);
    }

    public ArrayList<Move> getAllMoves(int player) {
        long hash = getBoardHash(player);
        if (movesGenerationHashMap.containsKey(hash)) {
            return movesGenerationHashMap.get(hash);
        }
        ArrayList<Move> ret = new ArrayList<>();
        for (Piece piece : getPlayersPieces(player)) {
            if (!piece.isCaptured())
                ret.addAll(piece.canMoveTo(this));
        }
        movesGenerationHashMap.put(hash, ret);
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
        move.setPrevBoardHash(boardHash);
        Location movingFrom = move.getMovingFrom();
        Location movingTo = move.getMovingTo();

        Piece piece = getPiece(movingFrom, true);
        int pieceColor = piece.getPieceColor();

        //region move instances
        if (move instanceof DoublePawnPush) {
            setEnPassantTargetLoc(((DoublePawnPush) move).getEnPassantTargetSquare());
            setEnPassantActualLoc(move.getMovingTo());
        } else if (move instanceof PromotionMove) {
            int promotingTo = ((PromotionMove) move).getPromotingTo();
            piecesCount[pieceColor][promotingTo]++;
            piecesCount[pieceColor][piece.getPieceType()]--;
            Piece newPiece = Piece.promotePiece(piece, promotingTo);
            replacePiece(newPiece, piece);
            piece = newPiece;
        } else if (move instanceof Castling) {
            castle((Castling) move);
        }
        //endregion

        if (move.getMovingPlayer() == Player.BLACK)
            fullMoveClock++;

        for (Integer[] disable : move.getDisableCastling()) {
            if (disable.length == 2) {
                castlingAbility.disableCastlingAbility(disable[0], disable[1]);
            } else {
                castlingAbility.disableCastlingAbility(disable[0]);
            }
        }

        if (move.isCapturing()) {
            assert Piece.isValidPieceType(move.getCapturingPieceType());
            Piece otherPiece = getPiece(move.getCapturingPieceStartingLoc(), Player.getOpponent(pieceColor));
            assert otherPiece != null;
            if (move instanceof EnPassant) {
                Location actualLoc = new Location(getEnPassantActualLoc());
                Location targetLoc = new Location(getEnPassantTargetLoc());
                Piece oP = getPiece(actualLoc, true);
                movePiece(actualLoc, targetLoc);
                move.setCapturing(oP, this);
                otherPiece = oP;
            }
            piecesCount[otherPiece.getPieceColor()][otherPiece.getPieceType()]--;
            otherPiece.setCaptured(true);
        }
        piece.setLoc(movingTo);
        setPiece(movingTo, piece);

        setSquareEmpty(movingFrom);
        if (!(move instanceof DoublePawnPush)) {
            setEnPassantTargetLoc((Location) null);
            setEnPassantActualLoc(null);
        }
        if (!move.isReversible()) {
            setHalfMoveClock(0);
            repetitionHashList.clear();
        }
        switchTurn();
        setBoardHash();
        if (move.isReversible()) {
            setHalfMoveClock(move.getPrevHalfMoveClock() + 1);
            repetitionHashList.add(getBoardHash());
        }


    }


    public void undoMove(Move move) {
        boardHash = move.getPrevBoardHash();
        if (move.getMovingPlayer() == Player.BLACK)
            fullMoveClock--;
        setHalfMoveClock(move.getPrevHalfMoveClock());

        repetitionHashList = new ArrayList<>(move.getPrevRepetitionHashList());

        setEnPassantTargetLoc(move.getPrevEnPassantTargetLoc());
        setEnPassantActualLoc(move.getPrevEnPassantActualLoc());
        castlingAbility = new CastlingAbility(move.getPrevCastlingAbility());

        Location movingFrom = move.getMovingTo();
        Location movingTo = move.getMovingFrom();

        Piece piece = getPiece(movingFrom, true);
        int pieceColor = piece.getPieceColor();
        if (move instanceof PromotionMove) {
            piecesCount[pieceColor][((PromotionMove) move).getPromotingTo()]--;
            piecesCount[pieceColor][PAWN]++;
            Pawn oldPiece = new Pawn(piece);
            replacePiece(oldPiece, piece);
            piece = oldPiece;
        } else if (move instanceof Castling) {
            undoCastle((Castling) move);
        }

        piece.setLoc(movingTo);
        setPiece(movingTo, piece);
        if (move.isCapturing()) {
//            Piece otherPiece = piecesLists[Player.getOpponent(pieceColor)].get(move.getCapturingPieceIndex());
            int opponent = Player.getOpponent(pieceColor);
            Piece otherPiece = getPiece(move.getCapturingPieceStartingLoc(), opponent);
            assert otherPiece != null;
            otherPiece.setCaptured(false);
            setPiece(movingFrom, otherPiece);
            piecesCount[otherPiece.getPieceColor()][otherPiece.getPieceType()]++;
            if (move instanceof EnPassant) {
                Location actualLoc = getEnPassantActualLoc();
                Location targetLoc = getEnPassantTargetLoc();
                assert actualLoc != null && targetLoc != null;
                movePiece(targetLoc, actualLoc);
            }
        } else {
            setSquareEmpty(movingFrom);
        }
        switchTurn();
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
        String reset = "\u001B[0m";
        String black = "\u001B[30m", white = "\u001B[37m", blue = "\u001B[34m";
        String whiteBg = "\u001B[47m", blackBg = "\u001B[40m";
        String divider = "|";
        StringBuilder ret = new StringBuilder();

        ret.append(" ");
        for (int j = 0; j < logicMat.length; j++) {
            ret.append(" ").append(Character.toString((char) ('ａ' + j))).append(" ");
        }
        ret.append(Controller.HIDE_PRINT).append("\n");

        for (int i = 0; i < logicMat.length; i++) {
            ret.append(i + 1);
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

    public boolean isInCheck() {
        return isInCheck(currentPlayer);
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

    public long getBoardHash() {
        return getBoardHash(currentPlayer);
    }

    public long getBoardHash(int player) {
        if (player == currentPlayer) {
            return boardHash;
        }
        return hashBoard(player);
    }

    private long hashBoard(int player) {
        return Zobrist.hash(this, player);
    }

    public ArrayList<Move> getAllCaptureMoves() {
        ArrayList<Move> ret = new ArrayList<>();
        for (Piece piece : getPlayersPieces(currentPlayer)) {
            for (Move move : piece.canMoveTo(this)) {
                if (move.isCapturing()) {
                    ret.add(move);
                }
            }
        }
        return ret;
    }


}
