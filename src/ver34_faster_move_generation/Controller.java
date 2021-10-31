package ver34_faster_move_generation;


import Global_Classes.Positions;
import ver34_faster_move_generation.model_classes.Board;
import ver34_faster_move_generation.model_classes.GameStatus;
import ver34_faster_move_generation.model_classes.Model;
import ver34_faster_move_generation.model_classes.Square;
import ver34_faster_move_generation.model_classes.eval_classes.Eval;
import ver34_faster_move_generation.model_classes.eval_classes.Evaluation;
import ver34_faster_move_generation.model_classes.moves.BasicMove;
import ver34_faster_move_generation.model_classes.moves.Move;
import ver34_faster_move_generation.model_classes.pieces.Piece;
import ver34_faster_move_generation.view_classes.IconManager;
import ver34_faster_move_generation.view_classes.SidePanel;
import ver34_faster_move_generation.view_classes.View;
import ver34_faster_move_generation.view_classes.dialogs_classes.List;
import ver34_faster_move_generation.view_classes.dialogs_classes.YesOrNo;
import ver34_faster_move_generation.view_classes.dialogs_classes.dialog_objects.DialogButton;
import ver34_faster_move_generation.view_classes.dialogs_classes.dialog_objects.DialogInput;
import ver34_faster_move_generation.view_classes.dialogs_classes.dialog_objects.DialogLabel;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static ver34_faster_move_generation.model_classes.pieces.Piece.*;

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
    public static final String ACTUAL_HIDE_PRINT = "ㅤ";
    public static final String HIDE_PRINT = ACTUAL_HIDE_PRINT;
    public static final int COLS = 8;
    public static final int ROWS = 8;
    private static final int POSITIONS_COUNT_DEPTH = 5;
    private static final boolean PRINT_POSITIONS_MOVES = false;
    private static final int DEFAULT_STARTING_POSITION = 0;
    private static final long DEFAULT_TIME_CONTROL = TimeUnit.SECONDS.toMillis(65);
    private final int DEFAULT_BOARD_SIZE = 8;
    private final View view;
    private final Model model;
    private int startingPosition = DEFAULT_STARTING_POSITION;
    private int scanTime = DEFAULT_SCAN_TIME;
    private int scanTimeFlexibility = DEFAULT_SCAN_TIME_FLEXIBILITY;
    private Piece currentPiece;
    private IconManager iconManager;
    private boolean isFirstClick;
    private boolean showPositionDialog = false;
    private boolean fancyLoading = false;
    private int runningProcess = AI_PLAYS_BLACK;
    private long[] clocks;
    private Location checkLoc;
    private Timer timer;
    private ArrayList<Move> movesList;

    Controller() {
        model = new Model(this);
        view = new View(DEFAULT_BOARD_SIZE, this, Player.WHITE, DEFAULT_TIME_CONTROL);
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
            ArrayList<Move> moves = game.model.getBoard().generateAllMoves();
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
        ArrayList<Positions.Position> positions = Positions.getAllPositions();
        List list = new List("Starting Position", List.VERTICAL);
        list.setCancelKey(DEFAULT_STARTING_POSITION);
        for (Positions.Position position : positions) {
            list.addItem(new DialogButton(position.getFen(), position.getName()));
        }
        list.addItem(new DialogInput());
        String fen = (String) list.run();
        if (!Positions.positionExists(fen)) {
            YesOrNo savePos = new YesOrNo("Save New Pos", "Do You Want To Save The New Position?");
            if (((int) savePos.run()) == YesOrNo.YES) {
                List namePos = new List("Name New Position", List.VERTICAL);
                namePos.addItem(new DialogLabel("Enter Name For New Position"));
                namePos.addItem(new DialogInput());
                String name = (String) namePos.run();
                startingPosition = Positions.addNewPosition(name, fen);
            } else {
                positions.add(new Positions.Position("temp position", fen));
                startingPosition = Positions.getIndexOfFen(fen);
            }
        } else {
            startingPosition = Positions.getIndexOfFen(fen);
        }
    }

    private void showStopProcessDialog() {

    }

    public void startNewGame() {
        iconManager = new IconManager();
        iconManager.loadAllIcons();

        if (showPositionDialog) {
            showStartingPositionDialog();
        }
        currentPiece = null;
        isFirstClick = true;
        checkLoc = null;
        clocks = new long[2];
        Arrays.fill(clocks, DEFAULT_TIME_CONTROL);
        model.initGame(startingPosition);
        initViewGame();

        checkGameStatus(model.getBoard());
        afterBtnPress();
        fancyLoading = false;
        if (timer != null) {
            timer.stop();
        }
        timer = createClock();
        handleProcess();
    }

    private void initViewGame() {
        view.initGame();
        setBoardButtonsIcons();
        view.getSidePanel().setBothPlayersClocks(clocks);
    }

    private Timer createClock() {
        SidePanel sidePanel = view.getSidePanel();
        int delay = 100;
        return new Timer(delay, e -> {
            int player = model.getBoard().getCurrentPlayer();

            clocks[player] -= delay;
            sidePanel.setTimerLabel(player, clocks[player]);
            if (clocks[player] <= 0) {
                timedOut(player);
            }
        });

    }

    private void timedOut(int player) {
        gameOver(new Evaluation(new GameStatus(GameStatus.TIMED_OUT)));
    }

    private void handleProcess() {
        view.setRunningProcessStr(RUNNING_PROCESSES_NAMES[runningProcess]);
        if (isRunningProcessRN())
            new Thread(this::lookForAiMove).start();
    }

    private boolean isRunningProcessRN() {
        return runningProcess == AI_GAME ||
                runningProcess == AI_MOVE ||
                runningProcess == getCurrentPlayer();
    }

    private void enableBoardButtons() {
        if (!isRunningProcessRN()) {
            ArrayList<Location> locs = model.getPiecesLocations(getCurrentPlayer());
            view.enableSquares(locs);
        } else {
            view.enableAllSquares(false);
        }
    }

    private ArrayList<Location> flipLocs(ArrayList<Location> locs) {
        ArrayList<Location> ret = new ArrayList<>();
        for (Location loc : locs) {
            ret.add(Location.flipLocation(loc));
        }
        return ret;
    }

    private void setStsLbl() {
        view.setStatusLbl(Player.PLAYER_NAMES[getCurrentPlayer()] + " To Move");
    }

    public int getCurrentPlayer() {
        return model.getBoard().getCurrentPlayer();
    }

    public void boardButtonPressed(Location loc) {
        view.resetBackground();
        viewCheck();
        if (isFirstClick) {
            currentPiece = model.getPiece(loc);
            movesList = model.getMoves(currentPiece);
            view.highlightPath(movesList);
            view.enableSquare(loc, true);
            view.colorCurrentPiece(loc);
        } else {
            Move move = findMove(movesList, currentPiece, loc);
            if (move != null && currentPiece != null && !currentPiece.getLoc().equals(loc)) {
                if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
                    move.setPromotingTo(showPromotionDialog());
                }
                makeMove(move);
            } else afterBtnPress();

        }
        isFirstClick = !isFirstClick;
    }

    private void makeMove(Move move) {
        view.resetBackground();
        BasicMove intermediateMove = move.getIntermediateMove();
        if (intermediateMove != null) {
            updateView(intermediateMove.getMovingFrom(), intermediateMove.getMovingTo());
        }
        updateView(move.getMovingFrom(), move.getMovingTo());
        Board board = model.getBoard();

        String moveAnnotation = model.makeMove(move, board);
        Evaluation moveEval = move.getMoveEvaluation();
        int moveNum = getCurrentPlayer() == Player.BLACK ? model.getBoard().getFullMoveClock() : -1;
        view.updateMoveLog(moveAnnotation, moveNum);
        if (checkGameStatus(moveEval, board))
            return;
        if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
            //todo ↓
            view.resetAllBtns();
            setBoardButtonsIcons();
        }
        if (runningProcess == AI_MOVE)
            runningProcess = NO_RUNNING_PROCESS;
//        switchPlayer();
        handleProcess();
        afterBtnPress();

    }

    /**
     * @return true if game over
     */
    private boolean checkGameStatus(Board board) {
        return checkGameStatus(board.getEvaluation(), board);
    }

    private boolean checkGameStatus(Evaluation evaluation, Board board) {
        checkLoc = evaluation.getGameStatus().isCheck() ? new Location(board.getKing(getCurrentPlayer()).getLoc()) : null;
        if (evaluation.isGameOver()) {
            gameOver(evaluation);
            return true;
        }
        return false;
    }

    private void afterBtnPress() {
        setStsLbl();
        enableBoardButtons();
        viewCheck();
    }

    private void viewCheck() {
        if (checkLoc != null)
            view.inCheck(checkLoc);
    }

    private Move findMove(ArrayList<Move> movesList, Piece currentPiece, Location loc) {
//        if (currentPiece == null || movesList == null)
//            return null;
        for (Move move : movesList) {
            if (currentPiece.getLoc().equals(move.getMovingFrom()) && move.getMovingTo().equals(loc))
                return move;
        }
        return null;
    }

    private void makeAiMove(Move move) {
        makeMove(move);
        isFirstClick = true;
    }

    public void clearAllHashMaps() {
        Board.movesGenerationHashMap.clear();
        Model.transpositionsHashMap.clear();
        Eval.evaluationHashMap.clear();
        Eval.capturesEvaluationHashMap.clear();
    }

    public void updateView(Location prevLoc, Location newLoc) {
        view.updateBoardButton(prevLoc, newLoc);
    }

    private void gameOver(Evaluation gameStatus) {
        timer.stop();
        view.gameOver();
        //todo change currentPlayer to this player (only relevant to online game)
        ImageIcon icon = iconManager.getGameOverIcon(gameStatus.getGameStatus(), getCurrentPlayer());
        view.showMessage(gameStatus.getGameStatus().toString(), "Game Over", icon);
    }

    private void setBoardButtonsIcons() {
        for (Square[] row : model.getBoard()) {
            for (Square square : row) {
                if (!square.isEmpty()) {
                    Piece piece = square.getPiece();
                    ImageIcon icon = iconManager.getPieceIcon(piece);
                    view.setBtnIcon(square.getLoc(), icon);
                }
            }
        }
    }

    public int showPromotionDialog() {
        List list = new List("Promote", List.HORIZONTAL);
        list.setCancelKey(QUEEN);
        for (int type : CAN_PROMOTE_TO) {
            list.addItem(new DialogButton(type, iconManager.getPieceIcon(getCurrentPlayer(), type)));
        }
        return (int) list.run();
    }

    public void newGameBtnPressed() {
        startNewGame();
    }

    public void evalBtnPressed() {
        model.getBoard().printBoard();
        System.out.println(model.getBoard().getEvaluation());
//        System.out.println(model.getBoard().calcEval());
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
        if (checkGameStatus(model.getBoard().getEvaluation(), model.getBoard()))
            return;
        AtomicLong seconds = new AtomicLong(1);
        Timer t = new Timer(1000, e -> {
            System.out.print(seconds + " ");
            seconds.getAndIncrement();
        }) {{
            start();
        }};
        Move move = model.getAiMove();
        t.stop();
        assert move != null;
        makeAiMove(move);
        clearAllHashMaps();
    }

    public void clearAllDrawings() {
        view.clearAllDrawings();
    }

    public void printBoard() {
        model.getBoard().printBoard();
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
        ArrayList moves = model.getBoard().generateAllMoves();
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
            for (int depth = 1; depth <= POSITIONS_COUNT_DEPTH; depth++) {
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
        int res = countPositions(depth, model.getBoard(), true);
        int time = (int) minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS);
        return new int[]{res, time};
    }

    public int countPositions(int depth, Board board, boolean isRoot) {
        if (depth == 0)
            return 1;
        ArrayList<Move> moves = board.generateAllMoves();
        int size = moves.size();
        int num;
        if (isRoot) {
            num = IntStream.range(0, size).parallel().map(i -> executePos(depth, new Board(board), moves.get(i))).sum();
        } else {
            num = moves.stream().mapToInt(move -> executePos(depth, board, move)).sum();
        }
        return num;
    }

    private int executePos(int depth, Board board, Move move) {
        board.applyMove(move);
        int res = countPositions(depth - 1, board, false);
        if (PRINT_POSITIONS_MOVES && depth == POSITIONS_COUNT_DEPTH)
            System.out.println(move.getMovingFrom().toString() + "" + move.getMovingTo().toString() + ": " + res);
        board.undoMove(move);
        return res;
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

    public void drawMove(Move move) {
        assert move != null;
        drawArrow(move.getMovingFrom(), move.getMovingTo());
    }

    public void drawMove(Move move, Color color) {
        assert move != null;
        drawArrow(move.getMovingFrom(), move.getMovingTo(), color);
    }

    public void printCapturesEval() {
        System.out.println(model.getBoard().getEval().getCapturesEvaluation(getCurrentPlayer()));
    }

    public void stopRunningProcess() {
        runningProcess = NO_RUNNING_PROCESS;
        view.setRunningProcessStr(null);
        showStopProcessDialog();
    }

    public void flipBoard() {
        view.setBoardOrientation(Player.getOpponent(view.getBoardOrientation()));
        isFirstClick = true;
        initViewGame();
        afterBtnPress();
        view.getSidePanel().setFlipped(view.isBoardFlipped());
    }

    public void exitButtonPressed() {
        System.exit(0);
    }
}
