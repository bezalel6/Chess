package ver3;


import ver3.types.*;

import java.util.ArrayList;

public class Controller {
    public int numOfMoves = 1;
    private final int DEFAULT_BOARD_SIZE = 8;
    private View view;
    private Model model;
    private int currentPlayer;
    public static final int CHECKMATE = 100, INSUFFICIENT_MATERIAL = 200, OPPONENT_TIMED_OUT = 300, TIME_OUT_VS_INSUFFICIENT_MATERIAL = 400, STALEMATE = 500;

    Controller() {
        Piece.setIsWhitePerspective(true);
        model = new Model(DEFAULT_BOARD_SIZE, this);
        view = new View(this, DEFAULT_BOARD_SIZE);
    }

    public void startNewGame() {
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

    public void boardButtonPressed(Location loc) {
        view.resetBackground();
        if (isFirstClick) {
            currentPiece = model.getPiece(loc);
            ArrayList<Path> list = model.getMovableSquares(currentPiece);
            view.highlightPath(list);
            view.enableSquare(loc, true);

        } else {
            ArrayList<Path> list = model.getMovableSquares(currentPiece);
            if (isLocInPathList(list,loc)&&currentPiece != null && !currentPiece.getLoc().isEqual(loc)) {
                updateView(currentPiece.getLoc(), loc);
                view.updateMoveLog(model.makeMove(loc, currentPiece));
                int gameStatus = model.checkGameStatus(currentPlayer);
                System.out.println("Status = " + gameStatus);
                if (gameStatus >= 100) {
                    gameOver(gameStatus);
                    return;
                }
                switchPlayer();
                if (model.isInCheck(currentPlayer))
                    view.inCheck(model.getKing(currentPlayer).getLoc());
                if (currentPlayer == Piece.WHITE)
                    numOfMoves++;
                view.setLbl(Piece.colorAStringArr[currentPlayer] + " to move");
            }
            view.enableSquares(model.getPiecesLocations(currentPlayer));
        }
        isFirstClick = !isFirstClick;

    }
    private boolean isLocInPathList(ArrayList<Path> list, Location loc)
    {
        for(Path path:list)
        {
            if(path.getLoc().isEqual(loc))
                return true;
        }
        return false;
    }

    public void updateView(Location prevLoc, Location newLoc)
    {
        view.updateBoardButton(prevLoc,newLoc);
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
}
