package ver18_icon_manager;

import ver18_icon_manager.types.Piece.Player;
import ver18_icon_manager.moves.MinimaxMove;
import ver18_icon_manager.moves.Move;
import ver18_icon_manager.types.Piece;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Model {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private static int ROWS;
    private static int COLS;
    public Eval eval;
    private Board logicBoard;
    private Controller controller;
    private int scanTime;
    private ZonedDateTime minimaxStartedTime;
    private long positionsReached;

    public Model(int boardSize, Controller controller) {
        this.controller = controller;
        ROWS = COLS = boardSize;
    }

    public void initGame(int startingPosition) {
        logicBoard = new Board(Positions.getAllPositions().get(startingPosition).getFen(), this);
        eval = new Eval(logicBoard);
    }

    public String makeMove(Move move, Board board) {
//        String moveAnnotation = move.getAnnotation();
        board.makeMove(move);
        return "moveAnnotation";
    }

    public void unmakeMove(Move move, Board board) {
        board.unmakeMove(move);
    }

    public ArrayList<Location> getPiecesLocations(int currentPlayer) {
        ArrayList<Location> ret = new ArrayList<>();
        for (Piece[] row : logicBoard) {
            for (Piece piece : row) {
                if (piece != null)
                    if (piece.isOnMyTeam(currentPlayer))
                        ret.add(piece.getLoc());
            }
        }
        return ret;
    }

    public boolean isInBounds(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS;
    }

    public Piece getPiece(Location loc, Board board) {
        if (isInBounds(loc))
            return board.getPiece(loc);
        return null;
    }

    public ArrayList<Move> getMoves(Piece piece, Board board) {
        if (piece == null) {
            return null;
        }
        ArrayList<Move> list = piece.canMoveTo(board);
        return list;
    }

    public Board getBoard() {
        return logicBoard;
    }

    public void setBoard(Board board) {
        this.logicBoard = board;
    }

    // מהלך מינימקס
    public Move getAiMove() {
        System.out.println("Model getAiMove() using MINIMAX");
        scanTime = controller.getScanTime();
        return getBestMoveUsingMinimax().getMove();
    }

    // ============================ for minimax ===========================
    private MinimaxMove getBestMoveUsingMinimax() {
        minimaxStartedTime = ZonedDateTime.now();
        positionsReached = 0;
        MinimaxMove ret = minimax(logicBoard, true, 0, Double.MIN_VALUE, Double.MAX_VALUE);
        System.out.println("minimax move = " + ret);
        System.out.println("num of positions reached = " + positionsReached);
        return ret;
    }


    public MinimaxMove minimax(Board board, boolean isMax, int depth, double a, double b) {
        positionsReached++;
        MinimaxMove bestMove = new MinimaxMove(null, new BoardEval(isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE), -1);

        BoardEval value = board.getBoardEval();

        long seconds = minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.SECONDS);

        if (depth > 100 || seconds >= scanTime || value.isGameOver()) {
            return new MinimaxMove(null, value, depth);
        }

        int actualPlayer = controller.getCurrentPlayer();
        int player = isMax ? actualPlayer : Player.getOtherColor(actualPlayer);
        ArrayList<Move> possibleMoves = board.getAllMoves(player);

        for (int i = 0; i < possibleMoves.size(); i++) {
            Move move = possibleMoves.get(i);
            board.applyMove(move);
            MinimaxMove minimaxMove = minimax(board, !isMax, depth + 1, a, b);
            minimaxMove.setMove(move);
            board.undoMove(move);
//            if (depth == 0) {
//                System.out.println("Move#" + (i + 1) + ": " + minimaxMove);
//            }
            double bestMoveEval = bestMove.getMoveValue().getEval(), minimaxMoveValue = minimaxMove.getMoveValue().getEval();

//            int bestMoveDepth = bestMove.getMoveDepth(), minimaxMoveDepth = minimaxMove.getMoveDepth();

            if (isMax) {
                if (bestMoveEval < minimaxMoveValue) {
                    bestMove = minimaxMove;
                }
//                else if (depth == 0 && bestMoveEval == minimaxMoveValue) {
//                    if (bestMoveDepth > minimaxMoveDepth)
//                        bestMove = minimaxMove;
//                }
                a = Math.max(a, bestMove.getMoveValue().getEval());

            } else {
                if (bestMoveEval > minimaxMoveValue) {
                    bestMove = new MinimaxMove(minimaxMove);
                }
                b = Math.min(b, bestMove.getMoveValue().getEval());
            }
            if (b <= a) break;

        }
        return bestMove;
    }

    public void printAllPossibleMoves() {
        for (Move move : logicBoard.getAllMovesForCurrentPlayer()) {
            System.out.println(move);
        }
    }

    public Controller getController() {
        return controller;
    }
}


