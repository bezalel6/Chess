package ver5;


import ver5.types.*;

import java.util.ArrayList;

public class Controller {
    public int numOfMoves = 1;
    private final int DEFAULT_BOARD_SIZE = 8;
    private View view;
    private Model model;
    private int currentPlayer;
    public static final int CHECKMATE = 1000, INSUFFICIENT_MATERIAL = 200, OPPONENT_TIMED_OUT = 300, TIME_OUT_VS_INSUFFICIENT_MATERIAL = 400, STALEMATE = 500, REPETITION = 600;

    Controller() {
        Piece.setIsWhitePerspective(true);
        model = new Model(DEFAULT_BOARD_SIZE, this);
        view = new View(this, DEFAULT_BOARD_SIZE);
    }

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";


    public void startNewGame() {
        System.out.println(ANSI_RED + "This text is red!" + ANSI_RESET);

        currentPlayer = Piece.WHITE;
        currentPiece = null;
        numOfMoves = 1;
        isFirstClick = true;
        model.start();
        view.initGame(model.getPieces());
        view.enableSquares(model.getPiecesLocations(currentPlayer));

    }

    public static void main(String[] args) {
        Controller game = new Controller();
        game.startNewGame();
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    private Piece currentPiece;
    private boolean isFirstClick = true;

    public void setFirstClick(boolean firstClick) {
        isFirstClick = firstClick;
    }

    public void boardButtonPressed(Location loc) {
        view.resetBackground();

        if (isFirstClick) {
            currentPiece = model.getPiece(loc, model.getPieces());
            ArrayList<Path> list = model.getMovableSquares(currentPiece, model.getPieces());
            view.highlightPath(list);
            view.enableSquare(loc, true);
            view.colorCurrentPiece(loc);

        } else {
            ArrayList<Path> list = model.getMovableSquares(currentPiece, model.getPieces());
            if (isLocInPathList(list, loc) && currentPiece != null && !currentPiece.getLoc().isEqual(loc)) {
                updateView(currentPiece.getLoc(), loc);
                view.updateMoveLog(model.makeMove(loc, currentPiece, model.getPieces()), numOfMoves);
                Piece[][] pieces = model.getPieces();
                double gameStatus = model.checkGameStatus(currentPlayer, pieces);
                if (gameStatus >= 100) {
                    gameOver(gameStatus);
                    return;
                }
                numOfMoves++;
                switchPlayer();

                view.setLbl(Piece.colorAStringArr[currentPlayer] + " to move");
            }
            view.enableSquares(model.getPiecesLocations(currentPlayer));
        }
        isFirstClick = !isFirstClick;
        if (model.isInCheck(currentPlayer, model.getPieces()))
            view.inCheck(model.getKing(currentPlayer, model.getPieces()).getLoc());


    }

    private void makeAiMove(MinimaxReturn move) {
        view.resetBackground();

        Piece currentPiece = model.getPiece(move.getMove().getPiece().getLoc(), model.getPieces());
        view.updateBoardButtonWithoutStoppingDrag(currentPiece.getLoc(), move.move.getPath().getLoc());
        view.updateMoveLog(model.makeMove(move.move.getPath().getLoc(), currentPiece, model.getPieces()), numOfMoves);
        Piece[][] pieces = model.getPieces();
        double gameStatus = model.checkGameStatus(currentPlayer, pieces);
        System.out.println("game status= " + gameStatus);
        if (gameStatus >= 100) {
            gameOver(gameStatus);
            return;
        }
        numOfMoves++;
        switchPlayer();

        view.setLbl(Piece.colorAStringArr[currentPlayer] + " to move");
        view.enableSquares(model.getPiecesLocations(currentPlayer));
        if (model.isInCheck(currentPlayer, model.getPieces()))
            view.inCheck(model.getKing(currentPlayer, model.getPieces()).getLoc());


    }

    private int getOpponent(int currentPlayer) {
        return Math.abs(currentPlayer - 1);
    }

    private boolean isLocInPathList(ArrayList<Path> list, Location loc) {
        for (Path path : list) {
            if (path.getLoc().isEqual(loc))
                return true;
        }
        return false;
    }

    public void updateView(Location prevLoc, Location newLoc) {
        view.updateBoardButton(prevLoc, newLoc);
    }

    public void spacialUpdateView(Location prevLoc, Location newLoc) {
        view.updateBoardButtonWithoutStoppingDrag(prevLoc, newLoc);
    }

    private void gameOver(double gameStatus) {
        view.gameOver();
        switch ((int) gameStatus) {
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
            case (REPETITION):
                view.tieByRepetition();
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
        System.out.println("eval = " + model.eval(currentPlayer, model.getPieces()));
    }

    public void aiMoveButtonPressed() {
        makeAiMove(model.getAiMove());
    }
}
