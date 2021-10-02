package ver23_minimax_levels;


import ver23_minimax_levels.model_classes.Board;
import ver23_minimax_levels.model_classes.GameStatus;
import ver23_minimax_levels.model_classes.Model;
import ver23_minimax_levels.model_classes.Square;
import ver23_minimax_levels.model_classes.eval_classes.Evaluation;
import ver23_minimax_levels.moves.Castling;
import ver23_minimax_levels.moves.EnPassant;
import ver23_minimax_levels.moves.Move;
import ver23_minimax_levels.moves.PromotionMove;
import ver23_minimax_levels.types.Piece;
import ver23_minimax_levels.view_classes.*;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static ver23_minimax_levels.types.Piece.*;

public class Controller {
    public static final int MIN_SCAN_TIME = 1;
    public static final int MAX_SCAN_TIME = 60;
    public static final int DEFAULT_SCAN_TIME = 15;

    public static final int MIN_SCAN_TIME_FLEXIBILITY = 0;
    public static final int MAX_SCAN_TIME_FLEXIBILITY = 60;
    public static final int DEFAULT_SCAN_TIME_FLEXIBILITY = 5;

    public static final Color MINIMAX_BEST_MOVE = Color.blue;
    public static final Color MINIMAX_CURRENT_MOVE = Color.green;
    public static final String[] RUNNING_PROCESSES_NAMES = {"Ai Plays White", "Ai Plays Black", "Ai Game", "Ai Move", null};
    public static final int AI_PLAYS_WHITE = 0, AI_PLAYS_BLACK = 1, AI_GAME = 2, AI_MOVE = 3, NO_RUNNING_PROCESS = 4;
    public static final int[] RUNNING_PROCESSES = {AI_PLAYS_WHITE, AI_PLAYS_BLACK, AI_GAME, AI_MOVE};
    public static final String HIDE_PRINT = "ㅤ";
    private static final int COUNT_DEPTH = 5;
    private static final int DEFAULT_STARTING_POSITION = 0;
    private static final int CANCEL_SEARCH = 1, STOP_SEARCH = 2;
    private final int DEFAULT_BOARD_SIZE = 8;
    private final View view;
    private final Model model;
    private int startingPosition = DEFAULT_STARTING_POSITION;
    private int scanTime = DEFAULT_SCAN_TIME;
    private int scanTimeFlexibility = DEFAULT_SCAN_TIME_FLEXIBILITY;
    private int currentPlayer;
    private Piece currentPiece;
    private Dialogs dialog;
    private IconManager iconManager;
    private boolean isFirstClick;
    private boolean showPositionDialog = false;
    private int runningProcess = NO_RUNNING_PROCESS;
    private Location checkLoc;
    private AtomicBoolean keepSearching;
    private int stopSearchType;

    Controller() {
        model = new Model(DEFAULT_BOARD_SIZE, this);
        view = new View(DEFAULT_BOARD_SIZE, this);
    }

    public static void main(String[] args) {
        Controller game = new Controller();
        game.startNewGame();

    }

    public static String convertToHiddenStr(String str) {
        StringBuilder ret = new StringBuilder();
        char[] arr = str.toCharArray();
        for (char c : arr) {
            if (c == '\n') {
                ret.append(HIDE_PRINT);
            }
            ret.append(c);
        }
        ret.append(HIDE_PRINT);
        return ret.toString();
    }

    private static void testFiftyMoveRule(Controller game) {
        int numOfMoves = 99;
        for (int i = 0; i < numOfMoves; i++) {
            ArrayList<Move> moves = game.model.getBoard().getAllMovesForCurrentPlayer();
            for (Move move : moves) {
                if (move.isReversible()) {
                    game.makeMove(move);
                    break;
                }
            }
        }
    }

    public int getScanTimeFlexibility() {
        return scanTimeFlexibility;
    }

    public void setScanTimeFlexibility(int scanTimeFlexibility) {
        this.scanTimeFlexibility = scanTimeFlexibility;
    }

    private void showStartingPositionDialog() {
        dialog = new Dialogs(DialogTypes.VERTICAL_LIST, "Starting Position");
        ArrayList<Position> positions = Positions.getAllPositions();
        ArrayList<DialogObject> objects = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            objects.add(new DialogObject(positions.get(i).getName(), i));
        }
        startingPosition = dialog.run(objects);
    }

    private void showStopProcessDialog() {
        dialog = new Dialogs(DialogTypes.HORIZONTAL_LIST, "Stop Process");
        ArrayList<DialogObject> objects = new ArrayList<>();
        objects.add(new DialogObject("Cancel Process", CANCEL_SEARCH));
        objects.add(new DialogObject("Stop Process", STOP_SEARCH));
        stopSearchType = dialog.run(objects);
        keepSearching.set(false);
    }

    public void startNewGame() {
        iconManager = new IconManager();
        iconManager.loadAllIcons();
        keepSearching = new AtomicBoolean(true);
        if (showPositionDialog) {
            showStartingPositionDialog();
        }
        currentPiece = null;
        isFirstClick = true;
        checkLoc = null;

        model.initGame(startingPosition);
        view.initGame();
        setBoardButtonsIcons();
        currentPlayer = model.getBoard().getCurrentPlayer();
        handleProcess();

        setStsLbl();

        enableBoardButtons();

    }

    private void handleProcess() {
        view.setRunningProcessStr(RUNNING_PROCESSES_NAMES[runningProcess]);
        if (isRunningProcessRN())
            new Thread(this::lookForAiMove).start();
    }

    private boolean isRunningProcessRN() {
        return runningProcess == AI_GAME ||
                runningProcess == AI_MOVE ||
                runningProcess == currentPlayer;
    }

    private void enableBoardButtons() {
        if (!isRunningProcessRN())
            view.enableSquares(model.getPiecesLocations(currentPlayer));
        else view.enableAllSquares(false);
    }

    private void setStsLbl() {
        view.setStatusLbl(Player.PLAYER_NAMES[currentPlayer] + " To Move");
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void boardButtonPressed(Location loc) {
        view.resetBackground();
        viewCheck();
        if (isFirstClick) {
            Board board = model.getBoard();
            currentPiece = model.getPiece(loc, board);
            ArrayList<Move> movesList = model.getMoves(currentPiece, board);
            view.highlightPath(movesList);
            view.enableSquare(loc, true);
            view.colorCurrentPiece(loc);
        } else {
            Move move = findMove(model.getMoves(currentPiece, model.getBoard()), currentPiece, loc);
            if (move != null && currentPiece != null && !currentPiece.getLoc().equals(loc)) {
                if (move instanceof PromotionMove) {
                    ((PromotionMove) move).setPromotingTo(showPromotionDialog());
                }
                makeMove(move);
            } else buttonPressedLaterActions();

        }
        isFirstClick = !isFirstClick;
    }

    private void makeMove(Move move) {
        view.resetBackground();
        if (move instanceof Castling) {
            Castling castling = (Castling) move;
            updateView(castling.getCastlingLocs()[Castling.ROOK_STARTING_LOC], castling.getCastlingLocs()[Castling.ROOK_FINAL_LOC]);
        } else if (move instanceof EnPassant) {
            EnPassant epsn = (EnPassant) move;
            updateView(model.getBoard().getEnPassantActualLoc(), epsn.getMovingTo());
        }
        updateView(move.getMovingFrom(), move.getMovingTo());
        Board board = model.getBoard();

        String moveAnnotation = model.makeMove(move, board);
        Evaluation moveEval = move.getMoveEvaluation();
        if (currentPlayer == Player.WHITE)
            moveAnnotation = model.getBoard().getFullMoveClock() + ". " + moveAnnotation;

        int otherPlayer = Player.getOpponent(currentPlayer);
        checkLoc = moveEval.getGameStatus() == GameStatus.CHECK ? new Location(board.getKing(otherPlayer).getLoc()) : null;

        view.updateMoveLog(moveAnnotation);

        if (moveEval.isGameOver()) {
            gameOver(moveEval);
            return;
        }
        if (move instanceof PromotionMove) {
            //todo ↓
            view.resetAllBtns();
            setBoardButtonsIcons();
        }
        if (runningProcess == AI_MOVE)
            runningProcess = NO_RUNNING_PROCESS;
        switchPlayer();
        handleProcess();
        buttonPressedLaterActions();

    }

    private void buttonPressedLaterActions() {
        setStsLbl();
        enableBoardButtons();
        viewCheck();
    }

    private void viewCheck() {
        if (checkLoc != null)
            view.inCheck(checkLoc);
    }

    private Move findMove(ArrayList<Move> movesList, Piece currentPiece, Location loc) {
        for (Move move : movesList) {
            if (move.getMovingTo().equals(loc) && model.getBoard().getPiece(move.getMovingFrom()).getLoc().equals(currentPiece.getLoc())) {
                return move;
            }
        }
        return null;
    }

    private void makeAiMove(Move move) {
        makeMove(move);
        isFirstClick = true;
    }

    public void updateView(Location prevLoc, Location newLoc) {
        view.updateBoardButton(prevLoc, newLoc);
    }

    private void gameOver(Evaluation gameStatus) {
        view.gameOver();
        //todo change currentPlayer to this player (only relevant to online game)
        ImageIcon icon = iconManager.getGameOverIcon(gameStatus.getGameStatus(), currentPlayer);
        view.showMessage(gameStatus.getGameStatus().getStr(), "", icon);
    }

    private void setBoardButtonsIcons() {
        for (Square[] row : model.getBoard()) {
            for (Square square : row) {
                if (!square.isEmpty()) {
                    Piece piece = square.getPiece();
                    view.setBtnIcon(piece.getLoc(), iconManager.getPieceIcon(piece.getPieceColor(), piece.getPieceType()));
                }
            }
        }
    }

    public void switchPlayer() {
        currentPlayer = Player.getOpponent(currentPlayer);
        model.getBoard().setCurrentPlayer(currentPlayer);
    }

    private ArrayList<DialogObject> getPromotionObjects(int player) {
        ArrayList<DialogObject> ret = new ArrayList<>();
        for (int type : CAN_PROMOTE_TO) {
            ret.add(new DialogObject(iconManager.getPieceIcon(player, type), type));
        }
        return ret;
    }

    public int showPromotionDialog() {
        dialog = new Dialogs(DialogTypes.HORIZONTAL_LIST, "Promote");
        return dialog.run(getPromotionObjects(currentPlayer));
    }

    public void newGameBtnPressed() {
        startNewGame();
    }

    public void evalBtnPressed() {
        model.getBoard().printBoard();
        System.out.println(model.getBoard().getBoardEval());
    }

    public int getScanTime() {
        return scanTime;
    }

    public void setScanTime(int scanTime) {
        this.scanTime = scanTime;
    }

    public void aiMoveButtonPressed() {
        runningProcess = AI_MOVE;
        handleProcess();
    }

    public void lookForAiMove() {
        clearAllDrawings();
        AtomicBoolean b = new AtomicBoolean(true);
        stopWatch(b);
        Move move = model.getAiMove();
        b.set(false);
        assert move != null;
        makeAiMove(move);
    }

    public void clearAllDrawings() {
        view.clearAllDrawings();
    }

    private void stopWatch(AtomicBoolean b) {
        ZonedDateTime t = ZonedDateTime.now();
        new Thread(() -> {
            int prev = -1;
            int num = 0;
            while (b.get()) {
                num = (int) t.until(ZonedDateTime.now(), ChronoUnit.SECONDS);
                if (num != prev && num % 1000 != 0) {
                    System.out.print(num + " ");
                    if (num % 60 == 0)
                        System.out.println();
                    prev = num;
                }
            }
        }).start();
    }

    public void printBoard() {
        model.getBoard().printBoard();
    }

    public boolean enteredMoveText(String text) {
        Board board = model.getBoard();
        try {
            Move move = new Move(text, board);
            makeMove(move);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public void printFEN() {
        System.out.println(model.getBoard().getFenStr());
    }

    public void printAllPieces() {
//        for (Piece[] row : model.getBoard()) {
//            for (Piece piece : row) {
//                if (piece != null)
//                    System.out.println(piece);
//            }
//        }
        for (int j = 0; j < NUM_OF_PLAYERS; j++) {
            for (Piece piece : model.getBoard().getPlayersPieces(j)) {
                System.out.println(piece);
            }
        }
    }

    public void printAllPossibleMoves() {
//        long start2 = System.currentTimeMillis();
        model.printAllPossibleMoves();
//        long end2 = System.currentTimeMillis();
//        System.out.println("Elapsed Time in milli seconds: "+ (end2-start2));
        ArrayList moves = model.getBoard().getAllMovesForCurrentPlayer();
        moves.forEach(move -> view.drawMove((Move) move));
    }

    public void drawArrow(Location from, Location to) {
        drawArrow(from, to, Color.GREEN);
    }

    public void drawArrow(Location from, Location to, Color clr) {
        view.drawArrow(from, to, clr);
    }

    public void highlightEnPassantTargetSquare() {
        Location loc = model.getBoard().getEnPassantTargetLoc();
        if (loc != null)
            view.highlightSquare(loc, Color.BLUE);
        else
            System.out.println("no en passant target square");
    }


    public void printNumOfPositions() {
        new Thread(() -> {
            int numOfVar = 1;
            for (int depth = 1; depth <= COUNT_DEPTH; depth++) {
                int res = 0, time = 0;
                for (int j = 0; j < numOfVar; j++) {
                    int[] arr = testPositions(depth);
                    res = arr[0];
                    time += arr[1];
                }
                time /= numOfVar;
                System.out.println("Depth: " + depth + " Result: " + res + " positions Time: " + time + " milliseconds");
            }
        }).start();

    }

    private int[] testPositions(int depth) {

        ZonedDateTime minimaxStartedTime = ZonedDateTime.now();
        int res = countPositions(depth, model.getBoard());
        int time = (int) minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS);
        return new int[]{res, time};
    }


    public int countPositions(int depth, Board board) {
        if (depth == 0)
            return 1;
        ArrayList<Move> moves = board.getAllMovesForCurrentPlayer();

        int num = 0;
        for (Move move : moves) {
            board.applyMove(move);
            switchPlayer();
            int res = countPositions(depth - 1, board);
            num += res;
            if (depth == COUNT_DEPTH)
                System.out.println(move.getMovingFrom().toString() + "" + move.getMovingTo().toString() + ": " + res);
            board.undoMove(move);
            switchPlayer();
        }
        return num;
    }


    public void selectStartingPosition() {
        showStartingPositionDialog();
        startNewGame();
    }

    public void drawControlledSquares() {
        System.out.println("Queen moves from center: ");
        for (Move move : model.getBoard().getPieceMovesFrom(model.getBoard().getKing(0).getLoc(), QUEEN, 0)) {
            model.getController().drawMove(move, Color.BLUE);
        }
    }

    public void drawMove(Move move, Color color) {
        assert move != null;
        drawArrow(move.getMovingFrom(), move.getMovingTo(), color);
    }

    public void printCapturesEval() {

    }

    public void stopRunningProcess() {
        runningProcess = NO_RUNNING_PROCESS;
        view.setRunningProcessStr(null);
        showStopProcessDialog();
    }
}
