package ver23_multithreading;


import ver23_multithreading.model_classes.Board;
import ver23_multithreading.model_classes.Model;
import ver23_multithreading.model_classes.Square;
import ver23_multithreading.model_classes.eval_classes.Evaluation;
import ver23_multithreading.moves.Castling;
import ver23_multithreading.moves.EnPassant;
import ver23_multithreading.moves.Move;
import ver23_multithreading.moves.PromotionMove;
import ver23_multithreading.types.Piece;
import ver23_multithreading.view_classes.*;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static ver23_multithreading.types.Piece.CAN_PROMOTE_TO;
import static ver23_multithreading.types.Piece.Player;

public class Controller {
    public static final int MIN_SCAN_TIME = 1, MAX_SCAN_TIME = 60, DEFAULT_SCAN_TIME = 15;
    private static final int countDepth = 5;
    private static final int DEFAULT_STARTING_POSITION = 0;
    private final int DEFAULT_BOARD_SIZE = 8;
    private final View view;
    private final Model model;
    long totalSeconds = 0;
    private int startingPosition = DEFAULT_STARTING_POSITION;
    private int scanTime = DEFAULT_SCAN_TIME;
    private int currentPlayer;
    private Piece currentPiece;
    private Dialogs promotingDialog;
    private IconManager iconManager;
    private String runningProcessStr;
    private boolean isFirstClick;
    private boolean showPositionDialog = false;
    private boolean aiGame = true;
    private boolean aiPlaysBlack = false;
    private Location checkLoc;

    Controller() {
        model = new Model(DEFAULT_BOARD_SIZE, this);
        view = new View(DEFAULT_BOARD_SIZE, this);
    }

    public static void main(String[] args) {
        Controller game = new Controller();

        game.startNewGame();

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

    private void showStartingPositionDialog() {
        Dialogs positionsDialog = new Dialogs(DialogTypes.VERTICAL_LIST, "Starting Position");
        ArrayList<Position> positions = Positions.getAllPositions();
        ArrayList<DialogObject> objects = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            objects.add(new DialogObject(positions.get(i).getName(), i));
        }
        startingPosition = positionsDialog.run(objects);
    }

    public void startNewGame() {
        iconManager = new IconManager();
        iconManager.loadAllIcons();

        runningProcessStr = "";

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
        if (aiGame) {
            view.setProcessRunning(true);
            runningProcessStr = "AI GAME";
            new Thread(this::aiMoveButtonPressed).start();

        }
        setStsLbl();

        enableBoardButtons();

    }

    private void enableBoardButtons() {
        if (runningProcessStr.equals(""))
            view.enableSquares(model.getPiecesLocations(currentPlayer));
        else view.enableAllSquares(false);
    }

    private void setStsLbl() {
        view.setStatusLbl(runningProcessStr + " " + Player.PLAYER_NAMES[currentPlayer] + " To Move");
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

//        checkLoc = move.isCheck() ? new Location(board.getKing(currentPlayer).getLoc()) : null;

        String moveAnnotation = model.makeMove(move, board);
        if (currentPlayer == Player.WHITE)
            moveAnnotation = model.getBoard().getFullMoveClock() + ". " + moveAnnotation;

        int otherPlayer = Player.getOtherColor(currentPlayer);
        checkLoc = board.isInCheck(otherPlayer) ? new Location(board.getKing(otherPlayer).getLoc()) : null;

        view.updateMoveLog(moveAnnotation);
        Evaluation gameOverStatus = model.getBoard().isGameOver(currentPlayer);
        if (gameOverStatus.isGameOver()) {
            gameOver(gameOverStatus);
            return;
        }
        if (move instanceof PromotionMove) {
            //todo ↓
            view.resetAllBtns();
            setBoardButtonsIcons();
        }

        switchPlayer();
        if (aiGame) {
            view.setProcessRunning(true);
            new Thread(this::aiMoveButtonPressed).start();
        } else if (aiPlaysBlack) {
            if (currentPlayer == Player.BLACK) {
                view.setProcessRunning(true);
                runningProcessStr = "AI PLAYS BLACK";
                new Thread(this::aiMoveButtonPressed).start();
            } else {
                view.setProcessRunning(false);
                runningProcessStr = "";
            }
            setStsLbl();
        }
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
        aiGame = false;
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
        currentPlayer = Player.getOtherColor(currentPlayer);
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
        promotingDialog = new Dialogs(DialogTypes.HORIZONTAL_LIST, "Promote");
        return promotingDialog.run(getPromotionObjects(currentPlayer));
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

    public String aiMoveButtonPressed() {
        view.deleteAllDrawings();
        AtomicBoolean b = new AtomicBoolean(true);
        countDown(b);
        Move move = model.getAiMove();
        b.set(false);
        if (move != null)
            makeAiMove(move);
        else Error.error("ai move was null");
        return move.toString();
    }

    private void countDown(AtomicBoolean b) {
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
            totalSeconds += num;
            System.out.println("total minutes until now: " + (totalSeconds / 60));
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
        for (int j = 0; j < 2; j++) {
            ArrayList<Piece> playerPieces = model.getBoard().getPlayersPieces(j);
            for (Piece piece : playerPieces) {
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

    public void comparePiecesArrAndMat() {
        model.getBoard().comparePiecesArrAndMat();
    }

    public void printNumOfPositions() {
        new Thread(() -> {
            int numOfVar = 1;
            for (int depth = 1; depth <= countDepth; depth++) {
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
            if (depth == countDepth)
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

    }
}
