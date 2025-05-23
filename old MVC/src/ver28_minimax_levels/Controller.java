package ver28_minimax_levels;


import Global_Classes.Positions;
import ver28_minimax_levels.model_classes.Board;
import ver28_minimax_levels.model_classes.Model;
import ver28_minimax_levels.model_classes.Square;
import ver28_minimax_levels.model_classes.eval_classes.Evaluation;
import ver28_minimax_levels.model_classes.moves.Castling;
import ver28_minimax_levels.model_classes.moves.EnPassant;
import ver28_minimax_levels.model_classes.moves.Move;
import ver28_minimax_levels.model_classes.moves.PromotionMove;
import ver28_minimax_levels.model_classes.pieces.Piece;
import ver28_minimax_levels.view_classes.IconManager;
import ver28_minimax_levels.view_classes.View;
import ver28_minimax_levels.view_classes.dialogs_classes.List;
import ver28_minimax_levels.view_classes.dialogs_classes.YesOrNo;
import ver28_minimax_levels.view_classes.dialogs_classes.dialog_objects.DialogButton;
import ver28_minimax_levels.view_classes.dialogs_classes.dialog_objects.DialogInput;
import ver28_minimax_levels.view_classes.dialogs_classes.dialog_objects.DialogLabel;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static ver28_minimax_levels.model_classes.pieces.Piece.*;

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
    private static final int COUNT_DEPTH = 5;
    private static final int DEFAULT_STARTING_POSITION = 0;
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
    private int runningProcess = NO_RUNNING_PROCESS;
    private Location checkLoc;

    Controller() {
        model = new Model(this);
        view = new View(DEFAULT_BOARD_SIZE, this, Player.BLACK);
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

        model.initGame(startingPosition);
        view.initGame();
        setBoardButtonsIcons();
        handleProcess();

        setStsLbl();

        enableBoardButtons();

        checkGameStatus(model.getBoard());
        afterBtnPress();
        fancyLoading = false;
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
            } else afterBtnPress();

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
        if (getCurrentPlayer() == Player.WHITE)
            moveAnnotation = model.getBoard().getFullMoveClock() + ". " + moveAnnotation;
        view.updateMoveLog(moveAnnotation);
        System.out.println(moveAnnotation);
        if (checkGameStatus(moveEval, board))
            return;
        if (move instanceof PromotionMove) {
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
        assert currentPiece != null && movesList != null;
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
        ImageIcon icon = iconManager.getGameOverIcon(gameStatus.getGameStatus(), getCurrentPlayer());
        view.showMessage(gameStatus.getGameStatus().toString(), "", icon);
    }

    private void setBoardButtonsIcons() {
        for (Square[] row : model.getBoard()) {
            for (Square square : row) {
                if (!square.isEmpty()) {
                    Piece piece = square.getPiece();
                    ImageIcon icon = iconManager.getPieceIcon(piece);
                    if (fancyLoading) {
                        new Thread(() ->
                                view.getBoardOverlay().animateAndSetIcon(view.getBtn(piece.getLoc()), icon)).start();
                        try {
                            Thread.sleep(125);
                        } catch (InterruptedException e) {
                            MyError.error("");
                        }
                    } else {
                        view.setBtnIcon(square.getLoc(), icon);
                    }
                }
            }
        }
    }

//    public void switchPlayer() {
//        currentPlayer = Player.getOpponent(currentPlayer);
//        model.getBoard().setCurrentPlayer(currentPlayer);
//    }

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
        int res = countPositions(depth, model.getBoard(), true);
        int time = (int) minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS);
        return new int[]{res, time};
    }


    public int countPositions(int depth, Board board, boolean isRoot) {
        isRoot = false;
        if (depth == 0)
            return 1;
        ArrayList<Move> moves = board.generateAllMoves();
        int size = moves.size();
        final int[] nums = new int[size];
        Arrays.fill(nums, -1);
        int num = 0;
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            if (isRoot) {
                int finalI = i;
                new Thread(() -> {
                    nums[finalI] = executePos(depth, new Board(board), Move.copyMove(move));
                }).start();
            } else {
                num += executePos(depth, board, move);
            }
        }
        if (isRoot) {
            while (Arrays.stream(nums).anyMatch(n -> n == -1)) ;
            num += Arrays.stream(nums).sum();
        }
        return num;
    }

    private int executePos(int depth, Board board, Move move) {
        board.applyMove(move);
        int res = countPositions(depth - 1, board, false);
        if (depth == COUNT_DEPTH)
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
//        System.out.println(model.getBoard().getEval().getCapturesEvaluation(getCurrentPlayer()));
    }

    public void stopRunningProcess() {
        runningProcess = NO_RUNNING_PROCESS;
        view.setRunningProcessStr(null);
        showStopProcessDialog();
    }

    public void flipBoard(boolean selected) {
        view.setBoardOrientation(Player.getOpponent(view.getBoardOrientation()));
        view.getBoardOverlay().flipButtons();
    }

    public void exitButtonPressed() {
        System.exit(0);
    }
}
