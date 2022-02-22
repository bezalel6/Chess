package ver10_eval_class;

import ver10_eval_class.types.*;
import ver10_eval_class.Move.SpecialMoves;
import ver10_eval_class.types.Piece.colors;

import java.util.ArrayList;
import java.util.ListIterator;

class thread implements Runnable {
    private Thread t;
    private Move move;
    private Controller controller;

    public thread(Move move, Controller controller) {
        this.move = move;
        this.controller = controller;
    }

    @Override
    public void run() {
        controller.aiFoundOption(move);
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }
}

public class Model {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private static int ROWS;
    private static int COLS;
    public Eval eval;
    private Piece[][] logicMat;
    private Controller controller;
    private String additions = "";
    private int scanDepth;
    private String knight = "♘", bishop = "♗", pawn = "♙", king = "♔", queen = "♕", rook = "♖";

    public Model(int boardSize, Controller controller) {
        this.controller = controller;
        ROWS = COLS = boardSize;
    }

    public void initGame(int startingPosition) {
        eval = new Eval(this);
        logicMat = loadPos(Positions.posToPieces(Positions.getAllPositions[startingPosition]));
    }

    public Piece[][] loadPos(Piece[] pos) {
        Piece[][] ret = new Piece[ROWS][COLS];
        for (Piece piece : pos) {
            if (piece != null)
                ret[piece.getLoc().getRow()][piece.getLoc().getCol()] = piece;
        }
        return ret;
    }

    public String makeMove(Move move, Piece[][] board) {
        Piece piece = move.getPiece();
        Location movingTo = move.getMovingFrom();
        String pieceAnnotation = piece.getAnnotation();
        String captures = "";
        additions = "";

        if (piece instanceof Pawn)
            pieceAnnotation = "";

        if (move.isCapturing()) {
            captures = "x";
            pieceAnnotation = piece.getAnnotation();
        }
        if (move.getSpecialMove() == SpecialMoves.EN_PASSANT && piece instanceof Pawn) {
            Location loc = move.getEnPassantActualCapturingLoc();
            Piece capturingPiece = getPiece(loc, board);
            controller.updateView(capturingPiece.getLoc(), loc);
            applyMove(new Move(capturingPiece, loc, false), board);
        }
        applyMove(move, board);

        if (move.getSpecialMove() == SpecialMoves.LONG_CASTLE) {
            castle(((King) piece).queenSide, board);
        } else if (move.getSpecialMove() == SpecialMoves.SHORT_CASTLE) {
            castle(((King) piece).kingSide, board);
        } else if (move.getPromotionMove() != null) {
            controller.promote(piece);
        }
        pieceAnnotation += captures;
        if (eval.checkWin(piece.getPieceColor(), board).isGameOver()) {
            additions += "#";
        } else if (eval.isInCheck(piece.getOtherColor(), board))
            additions += "+";
        else if (eval.checkTie(piece.getPieceColor(), board).isGameOver()) {
            additions += "½½";
        }
        pieceAnnotation += movingTo.toString();
        piece.setMoved();
        String retStr = (pieceAnnotation + additions);

        printLogicMat(board);

        return retStr;
    }

    public void applyMove(Move move, Piece[][] board) {
        Piece piece = move.getPiece();
        Location prev = move.getMovingTo();
        Location movingTo = move.getMovingFrom();
        board[movingTo.getRow()][movingTo.getCol()] = piece;
        board[prev.getRow()][prev.getCol()] = null;
        piece.setLoc(movingTo);
    }

    private void undoMove(Move move, Piece[][] board) {
        Piece piece = move.getPiece();
        Location prev = move.getMovingFrom();
        Location movingTo = move.getMovingTo();
        board[movingTo.getRow()][movingTo.getCol()] = piece;
        board[prev.getRow()][prev.getCol()] = null;
        piece.setLoc(movingTo);
    }

    private void castle(Castling castling, Piece[][] board) {
        Rook rook = castling.getRook();
        Location rookFinalLoc = castling.getRookFinalLoc();
        controller.updateView(rook.getLoc(), rookFinalLoc);
        makeMove(new Move(rook, rookFinalLoc, false), board);
    }

    public boolean isSquareThreatened(Location square, colors threateningPlayer, Piece[][] pieces) {
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (p != null && p.isOnMyTeam(threateningPlayer)) {
                    ArrayList<Move> canMoveTo = p.canMoveTo(pieces);
                    if (canMoveTo != null)
                        for (Move move : canMoveTo) {
                            if (move.getMovingFrom().isEqual(square))
                                return true;
                        }

                }
            }
        }
        return false;
    }

    public void replacePiece(Piece replacingWith, Piece[][] board) {
        board[replacingWith.getLoc().getRow()][replacingWith.getLoc().getCol()] = replacingWith;
    }

    public ArrayList<Location> getPiecesLocations(colors currentPlayer) {
        ArrayList<Location> ret = new ArrayList<>();
        for (Piece[] row : logicMat) {
            for (Piece piece : row) {
                if (piece != null)
                    if (piece.isOnMyTeam(currentPlayer))
                        ret.add(piece.getLoc());
            }
        }
        return ret;
    }

    public ArrayList<Move> getAllMoves(colors currentPlayer, Piece[][] pieces) {
        ArrayList<Move> ret = new ArrayList<>();
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null)
                    if (piece.isOnMyTeam(currentPlayer)) {
                        ArrayList<Move> movingTo = piece.canMoveTo(pieces);
                        checkLegal(movingTo, pieces);
                        ret.addAll(movingTo);
                    }
            }
        }
        return ret;
    }

    private boolean isLocInMoveList(ArrayList<Move> list, Location loc) {
        for (Move move : list) {
            if (move.getMovingFrom().isEqual(loc))
                return true;
        }
        return false;
    }

    public void checkLegal(ArrayList<Move> list, Piece[][] pieces) {
        if (list.isEmpty())
            return;
        Piece currentPiece = list.get(0).getPiece();

        if (currentPiece instanceof Pawn) {
            ArrayList<Piece> otherPlayersPieces = getPlayersPieces(currentPiece.getOtherColor(), pieces);
            for (Piece piece : otherPlayersPieces) {
                if (piece instanceof Pawn && ((Pawn) piece).enPassant != null) {
                    Piece[][] copy = copyBoard(pieces);
                    Location storeLoc = piece.getLoc();
                    Move tmpMove = new Move(piece, ((Pawn) piece).enPassant, false);
                    applyMove(tmpMove, copy);
                    //replacePiece(piece, copy);
                    if (isLocInMoveList(currentPiece.canMoveTo(copy), piece.getLoc())) {
                        Move thisMove = new Move(currentPiece, ((Pawn) piece).enPassant, storeLoc);
                        thisMove.setSpecialMove(SpecialMoves.EN_PASSANT);
                        list.add(thisMove);
                    }
                    undoMove(tmpMove, copy);
                }
            }
        }
        ListIterator iter = list.listIterator();
        ArrayList<Move> delete = new ArrayList<>();
        while (iter.hasNext()) {
            Move move = (Move) iter.next();
            Location loc = move.getMovingFrom();
            if (!isInBounds(loc)) {
                delete.add(move);
            }
            Piece destination = getPiece(loc, pieces);

            if (destination != null && currentPiece.isOnMyTeam(destination)) {
                delete.add(move);
            }

            SpecialMoves specialMove = move.getSpecialMove();
            if (specialMove != SpecialMoves.NONE) {
                if (specialMove == SpecialMoves.SHORT_CASTLE) {
                    Castling kingSide = ((King) currentPiece).kingSide;
                    if (isSquareThreatened(kingSide.getKingMiddleMove(), currentPiece.getOtherColor(), pieces) || getPiece(kingSide.getKingMiddleMove(), pieces) != null) {
                        delete.add(move);

                    }
                } else if (specialMove == SpecialMoves.LONG_CASTLE) {
                    Castling queenSide = ((King) currentPiece).queenSide;
                    if (isSquareThreatened(queenSide.getKingMiddleMove(), currentPiece.getOtherColor(), pieces) ||
                            getPiece(queenSide.getKingMiddleMove(), pieces) != null || getPiece(queenSide.getRookMiddleLoc(), pieces) != null) {
                        delete.add(move);

                    }
                } else if (specialMove == SpecialMoves.PROMOTION) {
                    delete.add(move);

                }
            }
            applyMove(move, pieces);
            if (eval.isInCheck(currentPiece.getPieceColor(), pieces)) {
                delete.add(move);
            }
            undoMove(move, pieces);
        }
        for (Move move : delete) {
            list.remove(move);
        }
    }

    public ArrayList<Piece> getPlayersPieces(colors player, Piece[][] pieces) {
        ArrayList<Piece> ret = new ArrayList<>();
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null && piece.isOnMyTeam(player))
                    ret.add(piece);
            }
        }
        return ret;
    }

    public boolean isInBounds(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS;
    }

    public Piece getKing(colors player, Piece[][] pieces) {
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (p != null && p instanceof King && p.getPieceColor() == player)
                    return p;
            }
        }
        return null;
    }

    public Piece getPiece(Location loc, Piece[][] pieces) {
        if (isInBounds(loc))
            return pieces[loc.getRow()][loc.getCol()];
        return null;
    }

    public ArrayList<Move> getMoves(Piece piece, Piece[][] pieces) {

        if (piece == null) {
            return null;
        }
        ArrayList list = piece.canMoveTo(pieces);

        checkLegal(list, pieces);
        return list;
    }

    public Piece[][] getPieces() {
        return logicMat;
    }

    public void setPieces(Piece[][] board) {
        logicMat = board;
    }

    public void printLogicMat(Piece[][] board) {
        String space = " ";
        for (Piece[] row : board) {
            for (Piece piece : row) {
                String prt = space;
                System.out.print("|");
                if (piece != null) {
                    if (piece.isWhite())
                        System.out.print(ANSI_WHITE);
                    else System.out.print(ANSI_BLACK);
                    Piece.types type = piece.getType();
                    switch (type) {
                        case PAWN: {
                            prt = pawn;
                            break;
                        }
                        case KNIGHT: {
                            prt = knight;
                            break;
                        }
                        case BISHOP: {
                            prt = bishop;
                            break;
                        }
                        case ROOK: {
                            prt = rook;
                            break;
                        }
                        case QUEEN: {
                            prt = queen;
                            break;
                        }
                        case KING: {
                            prt = king;
                            break;
                        }
                    }

                }
                System.out.print(prt + ANSI_RESET + "|");
            }
            System.out.println();
        }
    }

    public EvalValue checkGameStatus(colors player, Piece[][] pieces) {
        return eval.getEvaluation(player, pieces);
    }

    // מהלך מינימקס
    public Move getAiMove() {
        System.out.println("Model getAiMove() using MINIMAX");
        scanDepth = controller.getScanDepth();
        return getBestMoveUsingMinimax().getMove();
    }

    // ============================ for minimax ===========================
    private MinimaxMove getBestMoveUsingMinimax() {
        MinimaxMove ret = minimax(logicMat, true, 0, Double.MIN_VALUE, Double.MAX_VALUE);
        System.out.println("minimax move = " + ret);
        return ret;
    }

    public MinimaxMove minimax(Piece[][] board, boolean isMax, int depth, double a, double b) {
        MinimaxMove bestMove = new MinimaxMove(null, new EvalValue(isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE));
        colors actualPlayer = controller.getCurrentPlayer();
        colors player = isMax ? actualPlayer : colors.getOtherColor(actualPlayer);
        Piece[][] newBoard = copyBoard(board);

        ArrayList<Move> possibleMoves = getAllMoves(player, newBoard);
        EvalValue value = eval.getEvaluation(actualPlayer, newBoard);
        if (value.isGameOver() || depth >= scanDepth) {
            return new MinimaxMove(null, value);
        }

        for (int i = 0; i < possibleMoves.size(); i++) {
            Move move = possibleMoves.get(i);
            applyMove(move, newBoard);
            MinimaxMove val = minimax(newBoard, !isMax, depth + 1, a, b);
            val.setMove(move);

            undoMove(move, newBoard);
            if (depth == 0) {
                System.out.println("Move#" + (i + 1) + ": " + move + " val: " + val);
            }
            if (isMax) {
                if (bestMove.getMoveValue().getEval() < val.getMoveValue().getEval()) {
                    bestMove = val;
                }
                a = Math.max(a, bestMove.getMoveValue().getEval());

            } else {
                if (bestMove.getMoveValue().getEval() > val.getMoveValue().getEval()) {
                    bestMove = val;
                }
                b = Math.min(b, bestMove.getMoveValue().getEval());
            }
            if (b <= a) break;

        }
        return bestMove;
    }


    private Piece[] getPieces(Piece[][] pieces) {
        ArrayList<Piece> ret = new ArrayList<>();
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null) {
                    ret.add(piece);
                }
            }
        }
        return ret.toArray(new Piece[]{});
    }

    private Piece[][] copyBoard(Piece[][] og) {
        return loadPos(Positions.posToPieces(Positions.piecesToPos(getPieces(og))));
    }

}


