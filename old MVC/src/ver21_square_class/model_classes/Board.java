package ver21_square_class.model_classes;

import ver21_square_class.Controller;
import ver21_square_class.Error;
import ver21_square_class.Location;
import ver21_square_class.model_classes.eval_classes.BoardEval;
import ver21_square_class.model_classes.eval_classes.Eval;
import ver21_square_class.moves.*;
import ver21_square_class.types.King;
import ver21_square_class.types.Pawn;
import ver21_square_class.types.Piece;
import ver21_square_class.types.Rook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static ver21_square_class.types.Piece.*;

public class Board implements Iterable<Square[]> {

    private final Model model;
    private final ArrayList<String> movesList;
    private final FEN fen;
    private Square[][] logicMat;
    private ArrayList<Piece>[] pieces;
    private Piece[] kings;
    private Eval boardEval;
    private int currentPlayer;
    private int halfMoveClock, fullMoveClock;
    private Location enPassantTargetLoc;
    private Location enPassantActualLoc;
    private CastlingAbility castlingAbility;

    public Board(String fenStr, Model model) {
        boardEval = new Eval(this);
        this.fen = new FEN(fenStr, this);
        setMat(fen);
        this.model = model;
        boardEval = new Eval(this);
        movesList = new ArrayList<>();
        initPiecesArrays();
    }

    public CastlingAbility getCastlingAbility() {
        return castlingAbility;
    }

    public void comparePiecesArrAndMat() {
        int num = 0;
        for (ArrayList<Piece> arr : pieces)
            for (Piece p : arr) {
                boolean b = false;
                for (Square[] row : this) {
                    for (Square square : row) {
                        if (!square.isEmpty()) {
                            Piece piece = square.getPiece();
                            if (piece.equals(p)) {
                                b = true;
                                break;
                            }
                        }
                    }
                    if (b)
                        break;
                }
                if (!b) {
                    num++;
                    System.out.println("Didnt find " + p + " in mat");
                }

            }
        if (num != 0)
            System.out.println("Num of not found pieces = " + num);
        else
            System.out.println("found all pieces");
    }

    private void initPiecesArrays() {
        kings = new Piece[2];
        pieces = new ArrayList[2];
        pieces[Player.WHITE] = new ArrayList<>();
        pieces[Player.BLACK] = new ArrayList<>();
        for (Square[] row : logicMat)
            for (Square square : row)
                if (!square.isEmpty()) {
                    Piece piece = square.getPiece();
                    pieces[piece.getPieceColor()].add(piece);
                    if (piece instanceof King)
                        kings[piece.getPieceColor()] = piece;
                }
        for (ArrayList<Piece> pieceArrayList : pieces) {
            pieceArrayList.sort((Piece p1, Piece p2) -> {
                int a = MOST_LIKELY_TO_CHECK[p2.getPieceType()], b = MOST_LIKELY_TO_CHECK[p1.getPieceType()];
                boolean sortForChecks = true;
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

    public BoardEval getBoardEval(int player) {
        return boardEval.getEvaluation(player);
    }

    public Eval getEval() {
        return boardEval;
    }

    public BoardEval getBoardEval() {
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
        currentPlayer = fen.getPlayerToMove();

    }

    public FEN getFen() {
        return fen;
    }

    public String getFenStr() {
        return fen.generateFEN();
    }

    private void replacePiece(Piece newPiece, Piece oldPiece) {
        if (newPiece != null) {
            Location pieceLoc = newPiece.getLoc();
            ArrayList<Piece> pieceArrayList = pieces[oldPiece.getPieceColor()];
            int index = pieceArrayList.indexOf(oldPiece);
            if (index != -1)
                pieceArrayList.set(index, newPiece);
            else {
                Error.error("didnt find piece in pieces array list");

            }
            setPiece(pieceLoc, newPiece);
        } else {
            Error.error("replacing piece: piece was null");
        }
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

    public BoardEval isGameOver(int player) {
        int otherPlayer = Player.getOtherColor(player);
        if (getAllMoves(otherPlayer).isEmpty()) {
            if (isInCheck(otherPlayer))
                return new BoardEval(GameStatus.CHECKMATE, GameStatus.WINNING_SIDE);
            if (getAllMoves(player).isEmpty())
                return new BoardEval(GameStatus.STALEMATE);
        } else if (getAllMoves(player).isEmpty()) {
            if (isInCheck(player))
                return new BoardEval(GameStatus.CHECKMATE, GameStatus.LOSING_SIDE);
        } else if (halfMoveClock >= 100)
            return new BoardEval(GameStatus.FIFTY_MOVE_RULE);
        if (checkRepetition())
            return new BoardEval(GameStatus.THREE_FOLD_REPETITION);
        if (checkForInsufficientMaterial())
            return new BoardEval(GameStatus.INSUFFICIENT_MATERIAL);
        return new BoardEval();
    }

    private boolean checkForInsufficientMaterial() {

        return false;
    }

    private boolean checkRepetition() {

        return false;
    }

    public boolean isInCheck(int player) {
        return isThreatened(getKing(player));
    }

    public boolean isThreatened(Piece piece) {
        return isThreatened(piece.getLoc(), piece.getOpponent());
    }

    public boolean isThreatened(Location loc, int threateningPlayer) {
        for (Piece piece : pieces[threateningPlayer]) {
            for (Move move : piece.getPseudoMovesList(this)) {
                if (move.getMovingTo().equals(loc) && !(piece instanceof Pawn && !move.isCapturing()))
                    return true;
            }
        }
        return false;
    }

    public int getGamePhase() {
        return GamePhase.MIDDLE_GAME;
    }

    public Piece getKing(int player) {
        return kings[player];
    }

    public ArrayList<Piece> getPlayersPieces(int player) {
        return pieces[player];
    }

    public ArrayList<Move> getAllMoves(int player) {
        ArrayList<Move> ret = new ArrayList<>();
        for (Piece piece : pieces[player])
            if (!piece.isCaptured())
                ret.addAll(piece.canMoveTo(this));
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

        Piece piece = getPieceNotNull(movingFrom);
        Piece otherPiece = getPiece(movingTo);

        Square movingFromSquare = getSquare(movingFrom);
        Square movingToSquare = getSquare(movingTo);

        //region move instances
        if (move instanceof DoublePawnPush) {
            setEnPassantTargetLoc(((DoublePawnPush) move).getEnPassantTargetSquare());
            setEnPassantActualLoc(move.getMovingTo());
        } else if (move instanceof PromotionMove) {
            Piece newPiece = Piece.promotePiece(piece, ((PromotionMove) move).getPromotingTo());
            replacePiece(newPiece, piece);
            piece = newPiece;
        } else if (move instanceof Castling) {
            castle((Castling) move);
        }
        //endregion

        setHalfMoveClock(move.isReversible() ? move.getPrevHalfMoveClock() + 1 : 0);

        if (move.getMovingPlayer() == Player.BLACK)
            fullMoveClock++;

        if (piece instanceof King) {
            castlingAbility.setCastlingAbility(piece.getPieceColor());
        } else if (piece instanceof Rook) {
            disableCastling((Rook) piece);
        }

        piece.setLoc(movingTo);

        if (move.isCapturing()) {
            if (otherPiece instanceof Rook) {
                disableCastling((Rook) otherPiece);
            } else if (move instanceof EnPassant) {
                Location actualLoc = new Location(getEnPassantActualLoc());
                Location targetLoc = new Location(getEnPassantTargetLoc());
                Piece oP = getPieceNotNull(actualLoc);
                movePiece(actualLoc, targetLoc);
                move.setCapturing(oP.hashCode());
            }
            movingToSquare.capturePiece(piece);
        } else setPiece(movingTo, piece);

        setSquareEmpty(movingFrom);

        if (!(move instanceof DoublePawnPush)) {
            setEnPassantTargetLoc((Location) null);
            setEnPassantActualLoc(null);
        }
    }

    public void undoMove(Move move) {

        if (move.getMovingPlayer() == Player.BLACK)
            fullMoveClock--;
        setHalfMoveClock(move.getPrevHalfMoveClock());

        setEnPassantTargetLoc(move.getPrevEnPassantTargetLoc());
        setEnPassantActualLoc(move.getPrevEnPassantActualLoc());
        castlingAbility = new CastlingAbility(move.getPrevCastlingAbility());

        Location movingFrom = move.getMovingTo();
        Location movingTo = move.getMovingFrom();

        Piece piece = getPieceNotNull(movingFrom);

        if (move instanceof PromotionMove) {
            Pawn oldPiece = new Pawn(piece);
            replacePiece(oldPiece, piece);
            piece = oldPiece;
        } else if (move instanceof Castling) {
            undoCastle((Castling) move);
        }

        piece.setLoc(movingTo);
        setPiece(movingTo, piece);
        if (move.isCapturing()) {
            getSquare(movingFrom).revivePiece(move.getCapturingPieceHash());
            if (move instanceof EnPassant) {
                Location actualLoc = getEnPassantActualLoc();
                Location targetLoc = getEnPassantTargetLoc();
                if (actualLoc != null && targetLoc != null) {
                    movePiece(targetLoc, actualLoc);
                } else {
                    Error.error("undoing epsn move - one of the squares is null");
                }
            }

        } else {
            setSquareEmpty(movingFrom);
        }

    }

    public Piece getPieceNotNull(Location loc) {
        Piece ret = getPiece(loc);
        if (ret == null)
            Error.error("Piece is null");
        return ret;
    }

    private void disableCastling(Rook rook) {
        int clr = rook.getPieceColor();
        Piece king = getKing(clr);
        int side = QUEEN_SIDE;
        if (king.getStartingLoc().getCol() < rook.getStartingLoc().getCol() * rook.getDifference()) {
            side = KING_SIDE;
        }
        castlingAbility.setCastlingAbility(rook.getPieceColor(), side);
    }

    private void movePiece(Location movingFrom, Location movingTo) {
        Piece otherPiece = getPiece(movingTo);
        Piece piece = getPieceNotNull(movingFrom);
        if (otherPiece != null) {
            otherPiece.setCaptured(true);
        }
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
        ret.append("\n");

        for (int i = 0; i < logicMat.length; i++) {
            ret.append(i + 1);
            for (Square square : logicMat[i]) {
                ret.append(divider).append(square.toString()).append(divider);
            }
            ret.append("\n");
        }
        return ret.toString();
    }

    public void printBoard() {
        System.out.println(this);
    }

    public Model getModel() {
        return model;
    }

    public boolean isSquareEmpty(Location loc) {
        return getPiece(loc) == null;
    }

    public void makeMove(Move move) {
        applyMove(move);
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

    //todo DELETE
    public Controller getController() {
        return model.getController();
    }

    public int getOpponent() {
        return Player.getOtherColor(currentPlayer);
    }

    public ArrayList<Move> getAllCaptureMoves(int player) {
        ArrayList<Move> ret = new ArrayList<>();
        ArrayList<Piece> playersPieces = getPlayersPieces(player);
        for (Piece piece : playersPieces) {
            ArrayList<Move> canMoveTo = piece.canMoveTo(this);
            for (Move move : canMoveTo) {
                if (move.isCapturing())
                    ret.add(move);
            }
        }
        return ret;
    }

    public static class GamePhase {
        public static final int OPENING = 0, MIDDLE_GAME = 1, END_GAME = 2;
    }

}
