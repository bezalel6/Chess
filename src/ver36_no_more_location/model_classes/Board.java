package ver36_no_more_location.model_classes;

import ver36_no_more_location.Controller;
import ver36_no_more_location.Location;
import ver36_no_more_location.Player;
import ver36_no_more_location.model_classes.eval_classes.Eval;
import ver36_no_more_location.model_classes.eval_classes.Evaluation;
import ver36_no_more_location.model_classes.moves.BasicMove;
import ver36_no_more_location.model_classes.moves.Move;
import ver36_no_more_location.model_classes.pieces.MoveGenerator;
import ver36_no_more_location.model_classes.pieces.Piece;
import ver36_no_more_location.model_classes.pieces.PieceType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Board implements Iterable<Square> {

    public static final ConcurrentHashMap<Long, ArrayList<Move>> movesGenerationHashMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, Boolean> isInCheckHashMap = new ConcurrentHashMap<>();
    private final FEN fen;
    private final Eval boardEval;
    private final Stack<Move> moveStack;
    private Square[] logicMat;
    private Bitboard[][] pieces;
    private Bitboard[] allPlayersPieces;
    private Bitboard[] attackedSquares;
    private int[][] piecesCount;
    private Player currentPlayer;
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
        setPieces(logicMat);
        this.boardHash = new BoardHash(this);
    }

    public Board(Board other) {
        this.fen = new FEN(this);
        this.boardEval = new Eval(this);
        this.moveStack = new Stack<>();
        for (Move move : other.moveStack) {
            moveStack.push(Move.copyMove(move));
        }
        createNewEmptyLogicBoard();
        setPieces(other.logicMat);

        this.currentPlayer = other.currentPlayer;
        this.fullMoveClock = other.fullMoveClock;
        this.halfMoveClock = other.halfMoveClock;
        this.castlingAbility = other.castlingAbility;

        this.enPassantActualLoc = other.enPassantActualLoc;
        this.enPassantTargetLoc = other.enPassantTargetLoc;

        this.boardHash = new BoardHash(this);
    }

    private void setAttackedSquares() {
        for (Player player : Player.PLAYERS) {
            Bitboard currentBB = getAttackedSquares(player);
            currentBB.reset();
            Bitboard[] playersPieces = getPlayersPieces(player);
            for (int type = 0; type < playersPieces.length; type++) {
                Bitboard pieceBB = playersPieces[type];
                PieceType pieceType = PieceType.getPieceType(type);
                currentBB.orEqual(pieceType.getAttackingSquares(pieceBB, player, this));
            }
        }
    }


    public Bitboard getAttackedSquares(Player attackingPlayer) {
        return attackedSquares[attackingPlayer.asInt()];
    }

    private void createNewEmptyLogicBoard() {
        logicMat = new Square[Location.NUM_OF_SQUARES];
        for (int i = 0; i < logicMat.length; i++) {
            Location loc = Location.getLoc(i);
            assert loc != null;
            logicMat[i] = new Square(loc);
        }
        pieces = new Bitboard[Player.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
        allPlayersPieces = new Bitboard[Player.NUM_OF_PLAYERS];
        attackedSquares = new Bitboard[Player.NUM_OF_PLAYERS];

        piecesCount = new int[Player.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
        for (Player player : Player.PLAYERS) {
            Arrays.fill(piecesCount[player.asInt()], 0);
            pieces[player.asInt()] = new Bitboard[PieceType.NUM_OF_PIECE_TYPES];
            allPlayersPieces[player.asInt()] = new Bitboard();
            attackedSquares[player.asInt()] = new Bitboard();
            for (PieceType pieceType : PieceType.PIECE_TYPES) {
                pieces[player.asInt()][pieceType.asInt()] = new Bitboard();
            }
        }
    }

    public Bitboard getAllPlayersPieces(Player player) {
        return allPlayersPieces[player.asInt()];
    }

    public int getCastlingAbility() {
        return castlingAbility;
    }

    public void setCastlingAbility(int castlingAbility) {
        this.castlingAbility = castlingAbility;
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
    }

    public void setEnPassantTargetLoc(String enPassantTargetLocStr) {
        if (enPassantTargetLocStr.replaceAll("\\s+", "").equalsIgnoreCase("-")) {
            this.enPassantTargetLoc = null;
        } else {
            this.enPassantTargetLoc = Location.getLoc(enPassantTargetLocStr);
            int row = enPassantTargetLoc.getRow();
            int diff = (row == (Piece.getStartingRow(Player.WHITE) + 3)) ? Piece.getDifference(Player.WHITE) : Piece.getDifference(Player.BLACK);
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

    public Eval getEval() {
        return boardEval;
    }

    public Evaluation getEvaluation() {
        return getEvaluation(currentPlayer);
    }

    public Evaluation getEvaluation(Player player) {
        return boardEval.getEvaluation(player);
    }

    public FEN getFen() {
        return fen;
    }

    public String getFenStr() {
        return fen.generateFEN();
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
        logicMat[loc.asInt()].setPiece(piece);
    }

    public void setSquareEmpty(Location loc) {
        logicMat[loc.asInt()].setEmpty();
    }

    public Piece getPiece(Location loc) {
        return logicMat[loc.asInt()].getPiece();
    }

    public int bothPlayersNumOfPieces(PieceType[] arr) {
        return getNumOfPieces(Player.WHITE, arr) + getNumOfPieces(Player.BLACK, arr);
    }

    public int getNumOfPieces(Player player, PieceType[] arr) {
        int ret = 0;
        for (PieceType pieceType : arr) {
            ret += getNumOfPieces(player, pieceType);
        }
        return ret;
    }

    public int bothPlayersNumOfPieces(PieceType pieceType) {
        return getNumOfPieces(Player.WHITE, pieceType) + getNumOfPieces(Player.BLACK, pieceType);
    }

    public int getNumOfPieces(Player player, PieceType pieceType) {
        return piecesCount[player.asInt()][pieceType.asInt()];
    }

    public boolean isInCheck() {
        return isInCheck(currentPlayer);
    }

    public boolean isInCheck(Player player) {
//        long hash = getBoardHash().getFullHash();
//        hash = Zobrist.combineHashes(hash, Zobrist.playerHash(player));
//        if (isInCheckHashMap.containsKey(hash)) {
//            return isInCheckHashMap.get(hash);
//        }
        boolean ret = isThreatened(getKing(player), Player.getOpponent(player));
//        isInCheckHashMap.put(hash, ret);
        return ret;
    }

    public boolean isThreatened(Location loc, Player threateningPlayer) {
//        long tHash = getBoardHash().getPiecesHash();
//        tHash = Zobrist.combineHashes(tHash, Zobrist.hash(loc, threateningPlayer));
//        if (threatenedHashMap.containsKey(tHash)) {
//            return threatenedHashMap.get(tHash);
//        }
//        boolean res = getAttackedSquares(threateningPlayer).isSet(loc);
        boolean res = getAttackedSquares(threateningPlayer).isSet(loc);
        return res;
    }

    public ArrayList<Piece> piecesLookingAt(Location loc) {
        ArrayList<Piece> ret = new ArrayList<>();
        for (PieceType type : PieceType.PIECE_TYPES) {
            Piece piece = getPieceLookingAtMe(loc, type);
            if (piece != null)
                ret.add(piece);
        }
        return ret;
    }

    //    can capture any player
    public ArrayList<Move> getPieceMovesFrom(Location from, PieceType type) {
        return getPieceMovesFrom(from, type, Player.NO_PLAYER);
    }

    public ArrayList<Move> getPieceMovesFrom(Location from, PieceType type, Player player) {
//        ArrayList<ArrayList<Move>> lists;
//        switch (type) {
//            case BISHOP:
//                lists = Bishop.getPseudoBishopMoves(from);
//                break;
//            case ROOK:
//                lists = Rook.getPseudoRookMoves(from);
//                break;
//            case KNIGHT:
//                lists = Knight.getPseudoKnightMoves(from);
//                break;
//            case PAWN:
//                lists = Pawn.createCaptureMoves(from, player);
//                break;
//            case QUEEN:
//                lists = Queen.createQueenMoves(from);
//                break;
//            case KING:
//                lists = King.getKingMoves(from, player);
//                break;
//            default:
//                MyError.error("Wrong Piece Type");
//                lists = new ArrayList<>();
//                break;
//        }
//        ArrayList<Move> ret = convertListOfLists(lists);
//        return ret;
        return new ArrayList<>();
    }

    private Piece getPieceLookingAtMe(Location loc, PieceType type) {
        for (Move move : getPieceMovesFrom(loc, type)) {
            if (move.isCapturing() && type.compareMovementType(move.getCapturingPieceType()))
                return getPiece(move.getMovingTo());
        }
        return null;
    }

    private boolean checkThreatening(Location loc, Player threateningPlayer, PieceType pieceType) {
        ArrayList<Move> moves = getPieceMovesFrom(loc, pieceType, threateningPlayer.getOpponent());
        for (Move move : moves) {
            if (move.isCapturing() && pieceType.compareMovementType(move.getCapturingPieceType()))
                return true;
        }
        return false;
    }

    public Location getKing(Player player) {
        Bitboard k = getPieceBitBoard(player, PieceType.KING);
        return k.isEmpty() ? null : k.getSetLocs().get(0);
    }

    public Bitboard getPieceBitBoard(PieceType pieceType) {
        return getPieceBitBoard(currentPlayer, pieceType);
    }

    public Bitboard getPieceBitBoard(Player player, PieceType pieceType) {
        return getPlayersPieces(player)[pieceType.asInt()];
    }

    public Bitboard[] getPlayersPieces() {
        return getPlayersPieces(getCurrentPlayer());
    }

    public Bitboard[] getPlayersPieces(Player player) {
        return pieces[player.asInt()];
    }

    public boolean anyLegalMove(Player player) {
        Bitboard[] playersPieces = getPlayersPieces(player);
        //todo can return true if one piece can move
        return !generateAllMoves().isEmpty();
    }

    public ArrayList<Move> generateAllMoves() {

        ArrayList<Move> ret = MoveGenerator.generateMoves(this);
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
                            if (Location.row(otherMovingFrom) == Location.row(movingFrom)) {
                                uniqueRow = false;
                            }
                            if (Location.col(otherMovingFrom) == Location.col(movingFrom)) {
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
    public Iterator<Square> iterator() {
        return Arrays.stream(logicMat).iterator();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
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
        Player pieceColor = piece.getPlayer();

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
            PieceType promotingTo = move.getPromotingTo();
            delPiece(piece, movingFrom);
            Piece newPiece = Piece.getPiece(promotingTo, pieceColor);
            addPiece(newPiece, movingFrom);
            piece = newPiece;
        }
        //endregion

        if (currentPlayer == Player.BLACK)
            fullMoveClock++;

        if (piece.getPieceType() == PieceType.KING) {
            castlingAbility = CastlingAbility.disableCastlingForBothSides(pieceColor, castlingAbility);
        } else if (piece.getPieceType() == PieceType.ROOK) {
            castlingAbility = CastlingAbility.disableCastling(pieceColor, movingTo, castlingAbility, this);
        }
        if (move.isCapturing()) {
            Piece otherPiece = getPiece(movingTo);
            assert otherPiece != null;
            if (otherPiece.getPieceType() == PieceType.ROOK) {
                castlingAbility = CastlingAbility.disableCastling(otherPiece.getPlayer(), movingTo, castlingAbility, this);
            }
            delPiece(otherPiece, movingTo);
        }
        updatePieceLoc(piece, movingFrom, movingTo);
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
        setAttackedSquares();
    }

    public Move undoMove() {
        Move move = moveStack.pop();

        setFullMoveClock(move.getPrevFullMoveClock());
        setHalfMoveClock(move.getPrevHalfMoveClock());

        castlingAbility = move.getPrevCastlingAbility();

        Location movingFrom = move.getMovingTo();
        Location movingTo = move.getMovingFrom();

        Piece piece = getPiece(movingFrom, true);
        Player pieceColor = piece.getPlayer();
        if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
            delPiece(piece, movingFrom);
            Piece oldPiece = Piece.getPiece(PieceType.PAWN, pieceColor);
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
        setPiece(movingTo, piece);
        setSquareEmpty(movingFrom);

        if (move.isCapturing()) {
            Player opponent = Player.getOpponent(pieceColor);
            Piece otherPiece = Piece.getPiece(move.getCapturingPieceType(), opponent);
            addPiece(otherPiece, movingFrom);

        }
        makeIntermediateMove(move, true);

        switchTurn();

        setBoardHash();
        setAttackedSquares();
        return move;
    }

    private void updatePieceLoc(Piece piece, Location movingFrom, Location movingTo) {
        Bitboard bitboard = getPieceBitBoard(piece);
        Bitboard all = getAllPlayersPieces(piece.getPlayer());
        all.set(movingFrom, false);
        bitboard.set(movingFrom, false);
        all.set(movingTo, true);
        bitboard.set(movingTo, true);
    }

    private void addPiece(Piece piece, Location currentLoc) {
        if (piece == null) return;
        piecesCount[piece.getPlayer().asInt()][piece.getPieceType().asInt()]++;
        getPieceBitBoard(piece).set(currentLoc, true);
        getAllPlayersPieces(piece.getPlayer()).set(currentLoc, true);
        setPiece(currentLoc, piece);
    }

    private void delPiece(Piece piece, Location currentLoc) {
        piecesCount[piece.getPlayer().asInt()][piece.getPieceType().asInt()]--;
        getPieceBitBoard(piece).set(currentLoc, false);
        getAllPlayersPieces(piece.getPlayer()).set(currentLoc, false);
        setSquareEmpty(currentLoc);
    }

    private Bitboard getPieceBitBoard(Piece piece) {
        return getPlayersPieces(piece.getPlayer())[piece.getPieceType().asInt()];
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

    public int[] getPiecesCount(Player player) {
        return piecesCount[player.asInt()];
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
        assert isSquareEmpty(movingTo);//not intended for captures
        Piece piece = getPiece(movingFrom, true);
        updatePieceLoc(piece, movingFrom, movingTo);
        setPiece(movingTo, piece);
        setSquareEmpty(movingFrom);
    }

    public Square getSquare(Location loc) {
        return logicMat[loc.asInt()];
    }


    @Override
    public String toString() {
        String divider = "|";
        StringBuilder ret = new StringBuilder();

        ret.append(" ");
        for (int j = 0; j < Controller.COLS; j++) {
            ret.append(" ").append((char) ('ａ' + j)).append(" ");
        }
        ret.append(Controller.HIDE_PRINT).append("\n");

        for (int r = 0, rowNum = Controller.ROWS - 1; r < Controller.ROWS; r++, rowNum--) {
            ret.append(rowNum + 1);
            for (int c = 0; c < Controller.COLS; c++) {
                Location loc = Location.getLoc(r, c);
                ret.append(divider).append(getSquare(loc)).append(divider);
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

    public Square[] getLogicBoard() {
        return logicMat;
    }

    public Player getOpponent() {
        return currentPlayer.getOpponent();
    }

    public Bitboard[][] getPieces() {
        return pieces;
    }

    private void setPieces(Square[] board) {
        for (Square square : board) {
            if (!square.isEmpty()) {
                Piece piece = square.getPiece();
                addPiece(piece, square.getLoc());
            }
        }
        setAttackedSquares();
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

    public boolean isEmpty(Location loc) {
        return getPiece(loc) == null;
    }

    public boolean isSamePlayer(Location loc1, Location loc2) {
        if (isSquareEmpty(loc1) || isSquareEmpty(loc2))
            return false;
        return getPiece(loc1).isOnMyTeam(getPiece(loc2));
    }
}
