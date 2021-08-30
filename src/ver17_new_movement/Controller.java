package ver17_new_movement;


import ver17_new_movement.View_classes.DialogObject;
import ver17_new_movement.View_classes.DialogTypes;
import ver17_new_movement.View_classes.Dialogs;
import ver17_new_movement.View_classes.View;
import ver17_new_movement.types.Piece.Player;
import ver17_new_movement.moves.Castling;
import ver17_new_movement.moves.EnPassant;
import ver17_new_movement.moves.Move;
import ver17_new_movement.moves.PromotionMove;
import ver17_new_movement.types.Piece;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static ver17_new_movement.types.Piece.*;

public class Controller {
    public static int MIN_SCAN_DEPTH = 1, MAX_SCAN_DEPTH = 10, SCAN_INIT_VALUE = 8;
    public static int MIN_SCAN_TIME = 1, MAX_SCAN_TIME = 60, SCAN_TIME_INIT_VALUE = 20;
    private final int DEFAULT_BOARD_SIZE = 8;
    public int numOfMoves;
    private View view;
    private Model model;
    private int scanDepth = SCAN_INIT_VALUE;
    private int scanTime = SCAN_TIME_INIT_VALUE;

    private int currentPlayer;
    private Piece currentPiece;
    private Dialogs promotingDialog;

    private boolean isFirstClick = true;
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
        while (game.aiGame) {
            game.aiMoveButtonPressed();
        }
    }

    public void startNewGame() {
        int startingPosition = 0;
        if (showPositionDialog) {
            Dialogs positionsDialog = new Dialogs(DialogTypes.VERTICAL_LIST, "Starting Position");
            ArrayList<Position> positions = Positions.getAllPositions();
            ArrayList<DialogObject> objects = new ArrayList<>();
            for (int i = 0; i < positions.size(); i++) {
                objects.add(new DialogObject(positions.get(i).getName(), i));
            }
            startingPosition = positionsDialog.run(objects);
        }
        currentPiece = null;
        isFirstClick = true;
        model.initGame(startingPosition);
        numOfMoves = model.getBoard().getHalfMoveCounter();
        view.initGame(model.getBoard());
        currentPlayer = model.getBoard().getCurrentPlayer();
        view.setLbl(currentPlayer + " To Move");
        view.enableSquares(model.getPiecesLocations(currentPlayer));

    }


    public void aiFoundOption(Move move) {
        //view.drawMove(move);
    }

    public int getScanDepth() {
        return scanDepth;
    }

    public void setScanDepth(int scanDepth) {
        this.scanDepth = scanDepth;
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
            ArrayList<Move> movesList = model.getMoves(currentPiece, model.getBoard());
            Move move = findMove(movesList, currentPiece, loc);
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
            updateView(model.getBoard().getEnPassantActualSquare(), epsn.getMovingTo());
        }
        updateView(move.getMovingFrom(), move.getMovingTo());
        Board board = model.getBoard();
        String moveAnnotation = model.makeMove(move, board);
        view.updateMoveLog(moveAnnotation, numOfMoves);
        BoardEval gameStatus = board.getBoardEval();
        if (gameStatus.isGameOver()) {
            gameOver(gameStatus);
            return;
        }
        if (move instanceof PromotionMove) {
            view.setPieces(model.getBoard());
        }

        numOfMoves++;
        switchPlayer();
        buttonPressedLaterActions();
        if (aiPlaysBlack && currentPlayer == Player.BLACK) {
            aiMoveButtonPressed();
        }

    }

    private void buttonPressedLaterActions() {
        Board board = model.getBoard();
        view.setLbl(currentPlayer + " to move");
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
        aiFoundOption(move);
        makeMove(move);
        isFirstClick = true;
    }

    public void updateView(Location prevLoc, Location newLoc) {
        view.updateBoardButton(prevLoc, newLoc);
    }

    private void gameOver(BoardEval gameStatus) {
        aiGame = false;

        view.gameOver();
        ImageIcon icon = null;
        switch (gameStatus.getGameStatus()) {
            case REPETITION:
            case STALEMATE:
            case INSUFFICIENT_MATERIAL:
            case TIME_OUT_VS_INSUFFICIENT_MATERIAL:
                icon = view.getTieIcon();
                break;
            case CHECKMATE:
            case OPPONENT_TIMED_OUT:
                icon = view.getWiningIcons()[currentPlayer];
        }
        view.showMessage(gameStatus.getGameStatus().getStr(), "", icon);
    }


    private void switchPlayer() {
        currentPlayer = Player.getOtherColor(currentPlayer);
        model.getBoard().setCurrentPlayer(currentPlayer);
    }


    private ArrayList<DialogObject> getPromotionObjects(int player) {
        ArrayList<DialogObject> ret = new ArrayList<>();
        if (player == Player.WHITE) {
            ret.add(new DialogObject(view.wn, KNIGHT));
            ret.add(new DialogObject(view.wb, BISHOP));
            ret.add(new DialogObject(view.wq, QUEEN));
            ret.add(new DialogObject(view.wr, ROOK));
        } else {
            ret.add(new DialogObject(view.bn, KNIGHT));
            ret.add(new DialogObject(view.bb, BISHOP));
            ret.add(new DialogObject(view.bq, QUEEN));
            ret.add(new DialogObject(view.br, ROOK));
        }
        return ret;
    }

    public int promote() {
        promotingDialog = new Dialogs(DialogTypes.HORIZONTAL_LIST, "Promote");
        int choice = promotingDialog.run(getPromotionObjects(currentPlayer));
        return choice;
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
        view.deleteAllDrawings();
        Move move = model.getAiMove();
        if (move != null)
            makeAiMove(move);
        else System.out.println("ai move was null");
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

    public void gotToMove(int row) {
        //model.getBoard().goToMove(row);
    }

    public void printFEN() {
        System.out.println(model.getBoard().getFenStr());
    }

    public void printAllPieces() {
        for (Piece[] row : model.getBoard()) {
            for (Piece piece : row) {
                if (piece != null)
                    System.out.println(piece);
            }
        }
    }

    public void printAllPossibleMoves() {
//        long start2 = System.currentTimeMillis();
        model.printAllPossibleMoves();
//        long end2 = System.currentTimeMillis();
//        System.out.println("Elapsed Time in milli seconds: "+ (end2-start2));
//        ArrayList moves = model.getBoard().getAllMovesForCurrentPlayer();
//        moves.forEach(move -> view.drawMove((Move) move));
    }

    public void drawArrow(Location from, Location to, Color clr) {
        view.drawArrow(from, to, clr);
    }

    public void highlightEnPassantTargetSquare() {
        Location loc = model.getBoard().getEnPassantTargetSquare();
        if (loc != null)
            view.highlightSquare(loc, Color.BLUE);
        else
            System.out.println("no en passant target square");
    }

    public void printNumOfControlledSquares() {
        model.getBoard().printNumOfControlledSquares();
    }

    public void drawPins() {
        for (Piece[] row : model.getBoard().getLogicMat()) {
            for (Piece piece : row) {
                if (piece != null) {

                }
            }
        }
    }
}
