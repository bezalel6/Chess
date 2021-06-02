package ver4;


import ver4.types.*;

import java.util.ArrayList;

public class Controller {
    public int numOfMoves = 1;
    private final int DEFAULT_BOARD_SIZE = 8;
    private ver4.View view;
    private ver4.Model model;
    private int currentPlayer;
    public static final int CHECKMATE = 100, INSUFFICIENT_MATERIAL = 200, OPPONENT_TIMED_OUT = 300, TIME_OUT_VS_INSUFFICIENT_MATERIAL = 400, STALEMATE = 500;

    Controller() {
        Piece.setIsWhitePerspective(true);
        model = new Model(DEFAULT_BOARD_SIZE, this);
        view = new View(this, DEFAULT_BOARD_SIZE);
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public void startNewGame() {
        System.out.println(ANSI_RED + "This text is red!" + ANSI_RESET);

        currentPlayer = Piece.WHITE;
        isFirstClick = true;
        model.start();
        view.initGame(model.getPieces());
        view.enableSquares(model.getPiecesLocations(currentPlayer));

    }

    public static void main(String[] args) {
        Controller game = new Controller();
        game.startNewGame();
    }

    private Piece currentPiece;
    private boolean isFirstClick = true;

    public void setFirstClick(boolean firstClick) {
        isFirstClick = firstClick;
    }

    public void boardButtonPressed(ver4.Location loc) {
        view.resetBackground();
        if (isFirstClick) {
            currentPiece = model.getPiece(loc);
            ArrayList<Path> list = model.getMovableSquares(currentPiece, model.getPieces());
            view.highlightPath(list);
            view.enableSquare(loc, true);

        } else {
            ArrayList<Path> list = model.getMovableSquares(currentPiece, model.getPieces());
            if (isLocInPathList(list, loc) && currentPiece != null && !currentPiece.getLoc().isEqual(loc)) {
                updateView(currentPiece.getLoc(), loc);
                view.updateMoveLog(model.makeMove(loc, currentPiece), numOfMoves);
                Piece[][] pieces = model.getPieces();
                int gameStatus = model.checkGameStatus(currentPlayer, pieces);
                System.out.println("Status = " + gameStatus);
                if (gameStatus >= 100) {
                    gameOver(gameStatus);
                    return;
                }
                numOfMoves++;
                switchPlayer();
                if (model.isInCheck(currentPlayer, pieces))
                    view.inCheck(model.getKing(currentPlayer, pieces).getLoc());

                view.setLbl(Piece.colorAStringArr[currentPlayer] + " to move");
            }
            view.enableSquares(model.getPiecesLocations(currentPlayer));
        }
        isFirstClick = !isFirstClick;

    }

    private boolean isLocInPathList(ArrayList<Path> list, Location loc) {
        for (Path path : list) {
            if (path.getLoc().isEqual(loc))
                return true;
        }
        return false;
    }

    public void updateView(ver4.Location prevLoc, ver4.Location newLoc) {
        view.updateBoardButton(prevLoc, newLoc);
    }

    private void gameOver(int gameStatus) {
        view.gameOver();
        switch (gameStatus) {
            case (CHECKMATE):
                view.wonByCheckmate(currentPlayer);
                break;
            case (INSUFFICIENT_MATERIAL):
                view.tieByInsufficientMaterial();
                break;
            case (OPPONENT_TIMED_OUT):
                view.wonByOpponentTimedOut(currentPlayer);
                break;
            case (TIME_OUT_VS_INSUFFICIENT_MATERIAL):
                view.tieByTimeOutVsInsufficientMaterial();
                break;
            case (STALEMATE):
                view.tieByStalemate(currentPlayer);
                break;


        }
    }


    private void switchPlayer() {
        currentPlayer = Math.abs(currentPlayer - 1);
    }

    public char promote(Piece piece) {
        int color = piece.getPieceColor();
        int choice = view.promotingDialog.run(color);
        Location loc = piece.getLoc();
        Piece newPiece;
        switch (choice) {
            case (Piece.KNIGHT):
                newPiece = new Knight(loc, color);
                break;
            case (Piece.BISHOP):
                newPiece = new Bishop(loc, color);
                break;
            case (Piece.ROOK):
                newPiece = new Rook(loc, color);
                break;
            default:
                newPiece = new Queen(loc, color);
        }

        model.replacePiece(newPiece);
        view.setPieces(model.getPieces());
        return newPiece.getAnnotation().charAt(0);
    }

    public void newGameBtnPressed() {
        startNewGame();
    }

    public void evalBtnPressed() {

    }
}
