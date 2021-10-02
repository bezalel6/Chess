package ver23_minimax_levels.model_classes;

import ver23_minimax_levels.Controller;
import ver23_minimax_levels.MyError;
import ver23_minimax_levels.Location;
import ver23_minimax_levels.model_classes.eval_classes.Eval;
import ver23_minimax_levels.model_classes.eval_classes.Evaluation;
import ver23_minimax_levels.moves.*;
import ver23_minimax_levels.types.*;

import java.util.*;

import static ver23_minimax_levels.types.Piece.*;

public class Board implements Iterable<Square[]> {

    private static final HashMap<Long, ArrayList<Move>> moveGenerationHashMap = new HashMap<>();
    private final FEN fen;
    private ArrayList<Long> repetitionHashList;
    private Square[][] logicMat;
    private ArrayList<Piece>[] piecesLists;
    private Piece[] kingsArr;
    private Eval boardEval;
    private int[][] piecesCount;
    private int currentPlayer;
    private int halfMoveClock, fullMoveClock;
    private Location enPassantTargetLoc;
    private Location enPassantActualLoc;
    private CastlingAbility castlingAbility;

    public Board(String fenStr) {
        boardEval = new Eval(this);
        this.fen = new FEN(fenStr, this);
        setMat(fen);
        boardEval = new Eval(this);
        repetitionHashList = new ArrayList<>();
        initPiecesArrays();
//        boardHash = hashBoard();
    }

    public Board(Board other) {
        boardEval = new Eval(this);
        this.fen = new FEN(other.fen.generateFEN(), this);
        setMat(fen);
        boardEval = new Eval(this);
        repetitionHashList = new ArrayList<>(other.repetitionHashList);
        initPiecesArrays();
//        boardHash = other.boardHash;
    }

    public CastlingAbility getCastlingAbility() {
        return castlingAbility;
    }

    private void initPiecesArrays() {
        kingsArr = new Piece[NUM_OF_PLAYERS];
        piecesLists = new ArrayList[NUM_OF_PLAYERS];
        piecesCount = new int[NUM_OF_PLAYERS][NUM_OF_PIECE_TYPES];
        for (int i = 0; i < NUM_OF_PLAYERS; i++) {
            Arrays.fill(piecesCount[i], 0);
            piecesLists[i] = new ArrayList<>();
        }
        for (Square[] row : logicMat)
            for (Square square : row)
                if (!square.isEmpty()) {
                    Piece piece = square.getPiece();
                    piecesLists[piece.getPieceColor()].add(piece);
                    if (piece instanceof King)
                        kingsArr[piece.getPieceColor()] = piece;
                    piecesCount[piece.getPieceColor()][piece.getPieceType()]++;
                }
        for (ArrayList<Piece> pieceArrayList : piecesLists) {
            pieceArrayList.sort((Piece p1, Piece p2) -> {
                int a = MOST_LIKELY_TO_CHECK[p2.getPieceType()], b = MOST_LIKELY_TO_CHECK[p1.getPieceType()];
                boolean sortForChecks = true;
//                sort for checks vs sort for stockfish
                if (sortForChecks) {
                    return a - b;
                }
                return b - a;
            });
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

    public void setEnPassantTargetLoc(String enPassantTargetLoc) {
        if (enPassantTargetLoc.replaceAll("\\s+", "").equalsIgnoreCase("-")) {
            this.enPassantTargetLoc = null;
        } else this.enPassantTargetLoc = new Location(enPassantTargetLoc);
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

    public Evaluation getBoardEval(int player) {
        return boardEval.getEvaluation(player);
    }

    public Eval getEval() {
        return boardEval;
    }

    public Evaluation getBoardEval() {
        return getBoardEval(currentPlayer);
    }

    private void setMat(FEN fen) {
        logicMat = new Square[ROWS][COLS];
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
        Location pieceLoc = newPiece.getLoc();
        piecesLists[oldPiece.getPieceColor()].remove(oldPiece);
        piecesLists[oldPiece.getPieceColor()].add(newPiece);
        setPiece(pieceLoc, newPiece);
    }

    public void setPiece(Location loc, Piece piece) {
        logicMat[loc.getRow()][loc.getCol()].setPiece(piece);
    }

    public void setSquareEmpty(Location loc) {
        logicMat[loc.getRow()][loc.getCol()].setEmpty();
    }

    public Piece getPiece(int row, int col) {
        return logicMat[row][col].getPiece();
    }

    public Piece getPiece(Location loc) {
        return getPiece(loc.getRow(), loc.getCol());
    }

    public Evaluation isGameOver(int player) {
        int otherPlayer = Player.getOpponent(player);
        if (getAllMoves(otherPlayer).isEmpty()) {
            if (isInCheck(otherPlayer))
                return new Evaluation(GameStatus.CHECKMATE, GameStatus.WINNING_SIDE);
            if (getAllMoves(player).isEmpty())
                return new Evaluation(GameStatus.STALEMATE);
        } else if (getAllMoves(player).isEmpty()) {
            if (isInCheck(player))
                return new Evaluation(GameStatus.CHECKMATE, GameStatus.LOSING_SIDE);
        } else if (halfMoveClock >= 100)
            return new Evaluation(GameStatus.FIFTY_MOVE_RULE);
        if (checkRepetition(player))
            return new Evaluation(GameStatus.THREE_FOLD_REPETITION);
        if (checkForInsufficientMaterial())
            return new Evaluation(GameStatus.INSUFFICIENT_MATERIAL);
        return new Evaluation();
    }

    private boolean checkForInsufficientMaterial() {
        return insufficientMaterial(Player.WHITE) &&
                insufficientMaterial(Player.BLACK);
    }

    private boolean insufficientMaterial(int player) {
        return piecesCount[player][KING] < 1 ||
                (piecesCount[player][PAWN] == 0 &&
                        getNumOfPieces(player, MINOR_PIECES) <= 1 &&
                        getNumOfPieces(player, MAJOR_PIECES) == 0);
    }


    private int getNumOfPieces(int player, int[] arr) {
        int ret = 0;
        for (int pieceType : arr) {
            ret += piecesCount[player][pieceType];
        }
        return ret;
    }

    private boolean checkRepetition(int player) {
        if (repetitionHashList.size() >= 8) {
            long currentHash = hashBoard(player);
            int numOfMatches = 0;
//            int minMatch = currentPlayer == player ? 3 : 2;
            int minMatch = 3;
            for (long hash : repetitionHashList) {
                if (currentHash == hash) {
                    numOfMatches++;
                    if (numOfMatches >= minMatch)
                        return true;
                }
            }
        }
        return false;
    }

    public boolean isInCheck(int player) {
        return isThreatened(getKing(player));
    }

    public boolean isThreatened(Piece piece) {
        return isThreatened(piece.getLoc(), piece.getOpponent());
    }

    public boolean isThreatened(Location loc, int threateningPlayer) {
        for (int type : PIECES_TYPES) {
            if (type != KING && type != QUEEN && checkThreatening(loc, threateningPlayer, type))
                return true;
        }
        return false;
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
        assert Piece.checkValidPieceType(type);
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
        if (!Piece.checkValidPieceType(pieceType))
            MyError.error("Piece type is incorrect");
        ArrayList<ArrayList<Move>> lists;
        switch (pieceType) {
            case BISHOP:
                lists = Bishop.createBishopMoves(loc, Player.getOpponent(threateningPlayer), false, this);
                break;
            case ROOK:
                lists = Rook.createRookMoves(loc, Player.getOpponent(threateningPlayer), false, this);
                break;
            case KNIGHT:
                lists = Knight.createKnightMoves(loc, Player.getOpponent(threateningPlayer), false, this);
                break;
            case PAWN:
                lists = Pawn.createPawnMoves(loc, Player.getOpponent(threateningPlayer), false, this, true);
                break;
            default:
                MyError.error("Wrong Piece Type");
                lists = new ArrayList<>();
                break;
        }
        ArrayList<Move> moves = convertListOfLists(lists);
        for (Move move : moves) {
            if (move.isCapturing() && compareMovementType(pieceType, move.getCapturingPieceType()))
                return true;
        }
        return false;
    }

    public int getGamePhase() {
        return GamePhase.MIDDLE_GAME;
    }

    public Piece getKing(int player) {
        return kingsArr[player];
    }

    public ArrayList<Piece> getPlayersPieces(int player) {
        return piecesLists[player];
    }

    public ArrayList<Move> getAllMoves(int player) {
//        long hash = boardHash;
//        long hash = hashBoard(player);
//        if (moveGenerationHashMap.containsKey(hash)) {
//            return moveGenerationHashMap.get(hash);
//        }
        ArrayList<Move> ret = new ArrayList<>();
        for (Piece piece : getPlayersPieces(player))
            if (!piece.isCaptured())
                ret.addAll(piece.canMoveTo(this));
//        moveGenerationHashMap.put(hash, ret);
        return ret;
    }

    public ArrayList<Move> getAllMovesForCurrentPlayer() {
        return getAllMoves(currentPlayer);
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
        Location movingFrom = move.getMovingFrom();
        Location movingTo = move.getMovingTo();

        Piece piece = getPiece(movingFrom, true);
        int pieceColor = piece.getPieceColor();
        Piece otherPiece = getPiece(movingTo);

//        if (!move.isBoardHashSet())
//            move.setPrevBoardHash(hashBoard(pieceColor));
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

        if (move.isReversible()) {
            setHalfMoveClock(move.getPrevHalfMoveClock() + 1);
//            todo check if needs to give the moving player
            repetitionHashList.add(hashBoard());
        } else {
            if (!(move instanceof DoublePawnPush)) {
                setEnPassantTargetLoc((Location) null);
                setEnPassantActualLoc(null);
            }
            setHalfMoveClock(0);
            repetitionHashList.clear();
        }
//        boardHash = hashBoard();
    }


    public void undoMove(Move move) {
//        boardHash = move.getPrevBoardHash();
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
            Piece otherPiece = piecesLists[Player.getOpponent(pieceColor)].get(move.getCapturingPieceIndex());
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
            ret.append(" ").append(Character.toString('ａ' + j)).append(" ");
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
        move.setMoveEvaluation(getBoardEval());
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

    public ArrayList<Piece>[] getPiecesLists() {
        return piecesLists;
    }

    private long hashBoard() {
        return hashBoard(currentPlayer);
    }

    private long hashBoard(int player) {
        return Zobrist.hash(this, player);
    }

    public static class GamePhase {
        public static final int OPENING = 0, MIDDLE_GAME = 1, END_GAME = 2;
    }

}
