package ver22_eval_captures;


import ver22_eval_captures.model_classes.Board;
import ver22_eval_captures.model_classes.Model;
import ver22_eval_captures.model_classes.Square;
import ver22_eval_captures.model_classes.eval_classes.BoardEval;
import ver22_eval_captures.moves.Castling;
import ver22_eval_captures.moves.EnPassant;
import ver22_eval_captures.moves.Move;
import ver22_eval_captures.moves.PromotionMove;
import ver22_eval_captures.types.Piece;
import ver22_eval_captures.view_classes.*;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static ver22_eval_captures.types.Piece.CAN_PROMOTE_TO;
import static ver22_eval_captures.types.Piece.Player;

public class Controller {
    public static final int MIN_SCAN_TIME = 1, MAX_SCAN_TIME = 60, DEFAULT_SCAN_TIME = 15;
    private static final int countDepth = 5;
    private static final int DEFAULT_STARTING_POSITION = 0;
    private final int DEFAULT_BOARD_SIZE = 8;
    private final View view;
    private final Model model;
    private int startingPosition = DEFAULT_STARTING_POSITION;
    private int scanTime = DEFAULT_SCAN_TIME;
    private int currentPlayer;
    private Piece currentPiece;
    private Dialogs promotingDialog;
    private IconManager iconManager;
    private boolean isFirstClick;
    private boolean showPositionDialog = false;
    private boolean aiGame = false;
    private boolean aiPlaysBlack = false;

    Controller() {
        model = new Model(DEFAULT_BOARD_SIZE, this);
        view = new View(this, DEFAULT_BOARD_SIZE);
    }

    public static void main(String[] args) {
        Controller game = new Controller();

        game.startNewGame();
        new Thread(() -> {
            {
                while (game.aiGame)
                    game.aiMoveButtonPressed();
            }
        }).start();
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

        if (showPositionDialog) {
            showStartingPositionDialog();
        }
        currentPiece = null;
        isFirstClick = true;
        model.initGame(startingPosition);
        view.initGame();
        setBoardButtonsIcons();
        currentPlayer = model.getBoard().getCurrentPlayer();
        view.setStatusLbl(currentPlayer + " To Move");
        view.enableSquares(model.getPiecesLocations(currentPlayer));

    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void boardButtonPressed(Location loc) {
        view.resetBackground();
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
                    ((PromotionMove) move).setPromotingTo(promote());
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
        if (currentPlayer == Player.WHITE)
            moveAnnotation = model.getBoard().getFullMoveClock() + ". " + moveAnnotation;

        view.updateMoveLog(moveAnnotation);
        BoardEval gameStatus = board.getBoardEval();
        if (gameStatus.isGameOver()) {
            gameOver(gameStatus);
            return;
        }
        if (move instanceof PromotionMove) {
            //todo ↓
            view.resetAllBtns();
            setBoardButtonsIcons();
        }

        switchPlayer();
        buttonPressedLaterActions();
        if (aiPlaysBlack && currentPlayer == Player.BLACK) {
            new Thread(() -> aiMoveButtonPressed()).start();
        }
    }

    private void buttonPressedLaterActions() {
        Board board = model.getBoard();
        view.setStatusLbl(currentPlayer + " to move");
        view.enableSquares(model.getPiecesLocations(currentPlayer));
        if (board.isInCheck(currentPlayer))
            view.inCheck(board.getKing(currentPlayer).getLoc());
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

    private void gameOver(BoardEval gameStatus) {
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

    public int promote() {
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
            while (b.get()) {
                int num = (int) t.until(ZonedDateTime.now(), ChronoUnit.SECONDS);
                if (num != prev && num % 1000 != 0) {
                    System.out.println(num);
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
