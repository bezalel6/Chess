package ver19_square_control;

import ver19_square_control.moves.*;
import ver19_square_control.types.*;

import java.util.*;

import static ver19_square_control.types.Piece.*;

public class Board implements Iterable<Piece[]> {
    //region vars
    public static final String ANSI_RESET = "\u001B[0m";
    private Piece[][] logicMat;
    private ArrayList<Piece>[] pieces;
    private Piece[] kings;
    private Eval boardEval;
    private int currentPlayer;
    private Model model;
    private ArrayList<String> movesList;
    private int halfMoveClock, fullMoveClock;
    private Location enPassantTargetLoc;
    private Location enPassantActualLoc;
    private FEN fen;
    private CastlingAbility castlingAbility;
    //endregion

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
                for (Piece[] row : this) {
                    for (Piece piece : row) {
                        if (piece != null) {
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
        for (Piece[] row : logicMat)
            for (Piece piece : row)
                if (piece != null) {
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
        if (enPassantTargetLoc == null) {
            this.enPassantTargetLoc = null;
        } else this.enPassantTargetLoc = new Location(enPassantTargetLoc);
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

    public BoardEval getBoardEval() {
        return boardEval.getEvaluation(currentPlayer);
    }

    private void setMat(FEN fen) {
        logicMat = fen.loadFEN();
        castlingAbility = new CastlingAbility(fen.getCastlingAbilityStr());
        currentPlayer = fen.getPlayerToMove();

    }

    public FEN getFen() {
        return fen;
    }

    public String getFenStr() {
        return fen.generateFEN();
    }

    private void replacePiece(Piece piece) {
        if (piece != null) {
            Location pieceLoc = piece.getLoc();
            int index = -1;
            ArrayList<Piece> pieceArrayList = pieces[piece.getPieceColor()];
            for (int i = 0; i < pieceArrayList.size(); i++) {
                Piece p = pieceArrayList.get(i);
                if (p.getLoc().equals(pieceLoc)) {
                    index = i;
                    break;
                }
            }
            if (index != -1)
                pieceArrayList.set(index, piece);
            else {
                Error.error("didnt find piece in pieces array list");

            }
            piece.setLoc(pieceLoc);
            setPiece(pieceLoc, piece);
        } else {
            Error.error("replacing piece: piece was null");
        }
    }

    private void setPiece(Location loc, Piece piece) {
        logicMat[loc.getRow()][loc.getCol()] = piece;
    }

    public Piece getPiece(int row, int col) {
        return logicMat[row][col];
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
            ret.addAll(piece.canMoveTo(this));
        return ret;
    }

    public ArrayList<Move> getAllMovesForCurrentPlayer() {
        return getAllMoves(currentPlayer);
    }

    @Override
    public Iterator<Piece[]> iterator() {
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

        //region move instances
        if (move instanceof DoublePawnPush) {
            setEnPassantTargetLoc(((DoublePawnPush) move).getEnPassantTargetSquare());
            setEnPassantActualLoc(move.getMovingTo());
        } else if (move instanceof PromotionMove) {
            Piece p = getPieceNotNull(movingFrom);
            Piece newPiece = Piece.promotePiece(p, ((PromotionMove) move).getPromotingTo());
            replacePiece(newPiece);
        } else if (move instanceof Castling) {
            castle((Castling) move);
        } else if (move instanceof EnPassant) {
            //TODO BUG
            Location actualLoc = new Location(getEnPassantActualLoc());
            Location targetLoc = new Location(getEnPassantTargetLoc());
            if (actualLoc != null && targetLoc != null) {
                Piece oP = getPiece(actualLoc);
                oP.setLoc(targetLoc);
                oP.setCaptured(true);
                setPiece(actualLoc, null);
                move.setCapturing(oP.hashCode());
            } else {
                Error.error("applying epsn move - one of the squares is null");
            }

        }
        //endregion

        Piece piece = getPieceNotNull(movingFrom);
        Piece otherPiece = getPiece(movingTo);

        setHalfMoveClock(move.isReversible() ? move.getPrevHalfMoveClock() + 1 : 0);

        if (move.getMovingPlayer() == Player.BLACK)
            fullMoveClock++;

        if (piece instanceof King) {
            castlingAbility.setCastlingAbility(piece.getPieceColor());
        } else if (piece instanceof Rook) {
            disableCastling((Rook) piece);
        }
        if (otherPiece != null) {
            if (otherPiece instanceof Rook) {
                disableCastling((Rook) otherPiece);
            }
            otherPiece.setCaptured(true);
        }
        piece.setLoc(movingTo);
        setPiece(movingTo, piece);
        setPiece(movingFrom, null);
        if (!(move instanceof DoublePawnPush)) {
            setEnPassantTargetLoc((Location) null);
            setEnPassantActualLoc(null);
        }

//        move.set
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

        if (move instanceof PromotionMove) {
            Pawn oldPiece = new Pawn(getPiece(movingFrom).getStartingLoc(), move.getMovingPlayer());
            oldPiece.setLoc(movingFrom);
            replacePiece(oldPiece);
        } else if (move instanceof Castling) {
            undoCastle((Castling) move);
        }

        Piece piece = getPieceNotNull(movingFrom);
        Piece otherPiece = null;

        if (move.isCapturing()) {
            otherPiece = getCapturedPiece(move.getCapturingPieceHash(), piece.getOpponent());
            otherPiece.setCaptured(false);
        }

        piece.setLoc(movingTo);
        setPiece(movingTo, piece);
        setPiece(movingFrom, otherPiece);
        if (move instanceof EnPassant) {
            Location actualLoc = getEnPassantActualLoc();
            Location targetLoc = getEnPassantTargetLoc();
            if (actualLoc != null && targetLoc != null) {
                movePiece(targetLoc, actualLoc);
            } else {
                Error.error("undoing epsn move - one of the squares is null");
            }
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
        setPiece(movingFrom, null);
    }

    private void incHalfMoveClock() {
        halfMoveClock++;
    }


    private Piece getCapturedPiece(int pieceHash, int pieceColor) {
        for (Piece piece : pieces[pieceColor]) {
            if (piece.hashCode() == pieceHash) {
                return piece;
            }
        }
        Error.error("Could not get captured piece");
        return null;
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
        String blackPiecesClr = black;
        String whitePiecesClr = white;
        String knight = "♘", bishop = "♗", pawn = "♙", king = "♔", queen = "♕", rook = "♖";
        String space = "\u2003";
        String divider = "|";
        String ret = "";

        ret += " ";
        for (int j = 0; j < logicMat.length; j++) {
            ret += " " + Character.toString('ａ' + j) + " ";
        }
        ret += "\n";

//        ret += " ";
//        for (int j = 0; j < logicMat.length; j++) {
//            ret += " " + "＿" + " ";
//        }
//        ret += "\n";
        for (int i = 0; i < logicMat.length; i++) {
            Piece[] row = logicMat[i];
            ret += i + 1;
            for (int j = 0; j < row.length; j++) {
                Piece piece = row[j];
                String pieceIcon = space;
                String clr = "";
                if (piece != null) {
                    clr = (piece.isWhite() ? whitePiecesClr : blackPiecesClr);
                    int type = piece.getPieceType();
                    switch (type) {
                        case PAWN: {
                            pieceIcon = pawn;
                            break;
                        }
                        case KNIGHT: {
                            pieceIcon = knight;
                            break;
                        }
                        case BISHOP: {
                            pieceIcon = bishop;
                            break;
                        }
                        case ROOK: {
                            pieceIcon = rook;
                            break;
                        }
                        case QUEEN: {
                            pieceIcon = queen;
                            break;
                        }
                        case KING: {
                            pieceIcon = king;
                            break;
                        }
                    }
                }
                ret += divider + clr + pieceIcon + reset + divider;
            }
            ret += "\n";
        }
//        ret += " ";
//        for (int j = 0; j < logicMat.length; j++) {
//            ret += space + "‾" + space;
//        }
//        ret += "\n";
        return ret;
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

    public Piece[][] getLogicMat() {
        return logicMat;
    }

    //todo DELETE
    public Controller getController() {
        return model.getController();
    }

    public int getOpponent() {
        return Player.getOtherColor(currentPlayer);
    }

    static class GamePhase {
        public static final int OPENING = 0, MIDDLE_GAME = 1, END_GAME = 2;
    }
}
