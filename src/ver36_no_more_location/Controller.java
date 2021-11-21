package ver36_no_more_location;


import Global_Classes.Positions;
import ver36_no_more_location.model_classes.Board;
import ver36_no_more_location.model_classes.GameStatus;
import ver36_no_more_location.model_classes.Model;
import ver36_no_more_location.model_classes.Square;
import ver36_no_more_location.model_classes.eval_classes.Eval;
import ver36_no_more_location.model_classes.eval_classes.Evaluation;
import ver36_no_more_location.model_classes.moves.BasicMove;
import ver36_no_more_location.model_classes.moves.Move;
import ver36_no_more_location.model_classes.pieces.Piece;
import ver36_no_more_location.model_classes.pieces.PieceType;
import ver36_no_more_location.view_classes.IconManager;
import ver36_no_more_location.view_classes.SidePanel;
import ver36_no_more_location.view_classes.View;
import ver36_no_more_location.view_classes.dialogs_classes.List;
import ver36_no_more_location.view_classes.dialogs_classes.YesOrNo;
import ver36_no_more_location.view_classes.dialogs_classes.dialog_objects.DialogButton;
import ver36_no_more_location.view_classes.dialogs_classes.dialog_objects.DialogInput;
import ver36_no_more_location.view_classes.dialogs_classes.dialog_objects.DialogLabel;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class Controller {
    public static final int MIN_SCAN_TIME = 1;
    public static final int MAX_SCAN_TIME = 360;
    public static final int DEFAULT_SCAN_TIME = 10;

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
    public static final boolean USE_OPENING_BOOK = false;
    private static final int POSITIONS_COUNT_DEPTH = 5;
    private static final boolean PRINT_POSITIONS_MOVES = false;
    private static final int DEFAULT_STARTING_POSITION = 0;

    private static final boolean TIME = false;
    private static final long DEFAULT_TIME_CONTROL = TimeUnit.SECONDS.toMillis(65);

    private final int DEFAULT_BOARD_SIZE = 8;

    private final View view;
    private final Model model;

    private int startingPosition = DEFAULT_STARTING_POSITION;

    private int scanTime = DEFAULT_SCAN_TIME;
    private int scanTimeFlexibility = DEFAULT_SCAN_TIME_FLEXIBILITY;

    private Location currentLoc;
    private boolean isFirstClick;

    private IconManager iconManager;

    private boolean showPositionDialog = false;

    private int runningProcess = NO_RUNNING_PROCESS;

    private Timer timer;
    private long[] clocks;

    private Location checkLoc;

    private ArrayList<Move> movesList;
    private String gamePgn;


    public Controller() {
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

    public Model getModel() {
        return model;
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
                saveNewPosition(fen);
            } else {
                positions.add(new Positions.Position("temp position", fen));
                startingPosition = Positions.getIndexOfFen(fen);
            }
        } else {
            startingPosition = Positions.getIndexOfFen(fen);
        }
    }

    private void saveNewPosition(String fen) {
        List namePos = new List("Name New Position", List.VERTICAL);
        namePos.addItem(new DialogLabel("Enter Name For New Position"));
        namePos.addItem(new DialogInput());
        String name = (String) namePos.run();
        startingPosition = Positions.addNewPosition(name, fen);
    }

    private void showStopProcessDialog() {

    }

    public void startNewGame() {
        iconManager = new IconManager();
        iconManager.loadAllIcons();

        if (showPositionDialog) {
            showStartingPositionDialog();
        }
        currentLoc = null;
        isFirstClick = true;
        checkLoc = null;
        clocks = new long[2];
        Arrays.fill(clocks, DEFAULT_TIME_CONTROL);
        model.initGame(startingPosition);
        initViewGame();

        gamePgn = "";

        checkGameOver(model.getBoard());
        afterBtnPress();
        if (timer != null) {
            timer.stop();
        }
        timer = createClock();
        handleProcess();
    }

    private void initViewGame() {
        view.initGame(clocks);
        setBoardButtonsIcons();
    }

    private Timer createClock() {
        SidePanel sidePanel = view.getSidePanel();
        int delay = 100;
        return new Timer(delay, e -> {
            Player player = model.getBoard().getCurrentPlayer();

            clocks[player.asInt()] -= delay;
            sidePanel.setTimerLabel(player, clocks[player.asInt()]);
            if (clocks[player.asInt()] <= 0) {
                timedOut(player);
            }
        });

    }

    private void timedOut(Player player) {
        gameOver(new Evaluation(new GameStatus(GameStatus.TIMED_OUT)));
    }

    private void handleProcess() {
        view.setRunningProcessStr(RUNNING_PROCESSES_NAMES[runningProcess]);
        if (isRunningProcessRN())
            new Thread(this::lookForAiMove).start();
    }

    private boolean isRunningProcessRN() {
        return runningProcess == AI_GAME ||
                runningProcess == AI_MOVE;
//                runningProcess == getCurrentPlayer();
//        return false;
    }

    private void enableBoardButtons() {
        if (!isRunningProcessRN()) {
            ArrayList<Location> locs = model.getPiecesLocations(getCurrentPlayer());
            view.enableSquares(locs);
            view.enableAiMoveBtn(true);
        } else {
            view.enableAllSquares(false);
            view.enableAiMoveBtn(false);
        }
    }

    private void setStsLbl() {
        view.setStatusLbl(getCurrentPlayer().getName() + " To Move");
    }

    public Player getCurrentPlayer() {
        return model.getBoard().getCurrentPlayer();
    }

    public void boardButtonPressed(Location loc) {
        view.resetBackground();
        viewCheck();
        if (isFirstClick) {
            currentLoc = loc;
            movesList = model.getMovesFrom(currentLoc);
            view.highlightPath(movesList);
            view.enablePath(movesList);
            view.enableSquare(loc, true);
            view.colorCurrentPiece(loc);
        } else {
            Move move = findMove(movesList, currentLoc, loc);
            if (move != null && currentLoc != null && !currentLoc.equals(loc)) {
                if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
                    move.setPromotingTo(showPromotionDialog());
                }
                makeMove(move);
            } else afterBtnPress();

        }
        isFirstClick = !isFirstClick;
    }

    private void makeMove(Move move) {
        if (TIME)
            timer.start();

        view.resetBackground();
        updateView(move);
        Board board = model.getBoard();

        String moveAnnotation = model.makeMove(move, board);
        Evaluation moveEval = move.getMoveEvaluation();
        int moveNum = getCurrentPlayer() == Player.BLACK ? model.getBoard().getFullMoveClock() : -1;
        String currentAnn = moveAnnotation;
        if (moveNum != -1) {
            currentAnn = moveNum + ". " + currentAnn + " ";
        } else {
            currentAnn = currentAnn + "\n";
        }
        gamePgn += currentAnn;
        view.updateMoveLog(moveAnnotation, moveNum, model.getBoard().getMoveStack().size() - 1);
        if (checkGameOver(moveEval, board))
            return;
        if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
            //todo ↓
            view.resetAllBtns();
            setBoardButtonsIcons();
        }
        if (runningProcess == AI_MOVE)
            runningProcess = NO_RUNNING_PROCESS;
        handleProcess();
        afterBtnPress();

    }

    /**
     * @return true if game over
     */
    private boolean checkGameOver(Board board) {
        return checkGameOver(board.getEvaluation(), board);
    }

    private boolean checkGameOver(Evaluation evaluation, Board board) {
        checkLoc = evaluation.getGameStatus().isCheck() ? board.getKing(getCurrentPlayer()) : null;
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

    private Move findMove(ArrayList<Move> movesList, Location movingFrom, Location loc) {
//        if (currentPiece == null || movesList == null)
//            return null;
        for (Move move : movesList) {
            if (movingFrom.equals(move.getMovingFrom()) && move.getMovingTo().equals(loc))
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
        Board.isInCheckHashMap.clear();
        Model.transpositionsHashMap.clear();
        Eval.evaluationHashMap.clear();
        Eval.capturesEvaluationHashMap.clear();
        Eval.evaluationHashMap.clear();
    }

    public void updateView(BasicMove move) {
        if (move instanceof Move && ((Move) move).getIntermediateMove() != null) {
            updateView(((Move) move).getIntermediateMove());
        }
        view.updateBoardButton(move.getMovingFrom(), move.getMovingTo());
    }

    private void gameOver(Evaluation gameStatus) {
        timer.stop();
        view.gameOver();
        //todo change currentPlayer to this player (only relevant to online game)
        ImageIcon icon = iconManager.getGameOverIcon(gameStatus.getGameStatus(), getCurrentPlayer());
        view.showMessage(gameStatus.getGameStatus().toString(), "Game Over", icon);
    }

    private void setBoardButtonsIcons() {
        for (Square square : model.getBoard()) {
            if (!square.isEmpty()) {
                Piece piece = square.getPiece();
                ImageIcon icon = iconManager.getPieceIcon(piece);
                view.setBtnIcon(square.getLoc(), icon);
            }
        }
    }

    public PieceType showPromotionDialog() {
        List list = new List("Promote", List.HORIZONTAL);
        list.setCancelKey(PieceType.QUEEN);
        for (PieceType type : PieceType.CAN_PROMOTE_TO) {
            list.addItem(new DialogButton(type, iconManager.getPieceIcon(getCurrentPlayer(), type)));
        }
        return (PieceType) list.run();
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
        enableBoardButtons();
    }

    public void lookForAiMove() {
        clearAllDrawings();
        if (checkGameOver(model.getBoard().getEvaluation(), model.getBoard()))
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


    public String getCurrentFen() {
        return model.getBoard().getFenStr();
    }

    public void printFEN() {
        System.out.println(getCurrentFen());
    }

    public void printAllPieces() {
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
        int res = countPositions(depth, model.getBoard(), false);
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
        board.undoMove();
        return res;
    }

    public void selectStartingPosition() {
        showStartingPositionDialog();
        startNewGame();
    }

    public void drawControlledSquares() {
        for (Player p : Player.PLAYERS) {
            for (Location loc : model.getBoard().getAttackedSquares(p).getSetLocs()) {
                Color clr = p == getCurrentPlayer() ? Color.GREEN : Color.RED;
                view.highlightSquare(loc, clr);
            }
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

    public void goToMove(int moveIndex, int movingFromIndex) {
        Stack<Move> moveStack = model.getBoard().getMoveStack();
        view.enableAllSquares(false);
        for (int i = moveStack.size() - 1; i >= moveIndex; i--) {
            Move m = moveStack.get(i);
            if (moveIndex < movingFromIndex) {
                m = Move.flipMove(m);
            }
            updateView(m);
        }
    }

    public void rewind(int i) {
        Stack<Move> moveStack = model.getBoard().getMoveStack();
        view.enableAllSquares(false);
        updateView(Move.flipMove(moveStack.get(i)));
    }

    public void fastForward(int i) {
        Stack<Move> moveStack = model.getBoard().getMoveStack();
        view.enableAllSquares(false);
        updateView(moveStack.get(i));
    }

    public void saveCurrentPosition() {
        saveNewPosition(getCurrentFen());
    }

    public void printPGN() {
        System.out.println(gamePgn);
    }
}
