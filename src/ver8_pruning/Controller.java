package ver8_pruning;


import ver8_pruning.types.Piece.colors;
import ver8_pruning.types.Piece.types;
import ver8_pruning.types.*;

import java.util.ArrayList;

public class Controller {
    public static int MIN_SCAN_DEPTH = 1, MAX_SCAN_DEPTH = 10, SCAN_INIT_VALUE = 3;
    private final int DEFAULT_BOARD_SIZE = 8;
    public int numOfMoves;
    private int scanDepth = SCAN_INIT_VALUE;
    private View view;
    private Model model;
    private colors currentPlayer;
    private Piece currentPiece;
    private boolean isFirstClick = true;
    private boolean run = false;

    Controller() {
        model = new Model(DEFAULT_BOARD_SIZE, this);
        view = new View(this, DEFAULT_BOARD_SIZE);
    }

    public static void main(String[] args) {
        Controller game = new Controller();
        game.startNewGame();

        while (game.run) {
            game.aiMoveButtonPressed();
        }
    }

    public void startNewGame() {
        int startingPosition = view.positionsDialog.run();
        currentPlayer = colors.WHITE;
        currentPiece = null;
        numOfMoves = 1;
        isFirstClick = true;
        model.initGame(startingPosition);
        view.initGame(model.getPieces());
        view.enableSquares(model.getPiecesLocations(currentPlayer));

    }

    public int getScanDepth() {
        return scanDepth;
    }

    public void setScanDepth(int scanDepth) {
        this.scanDepth = scanDepth;
    }

    public colors getCurrentPlayer() {
        return currentPlayer;
    }

    public void boardButtonPressed(Location loc) {
        view.resetBackground();

        if (isFirstClick) {
            currentPiece = model.getPiece(loc, model.getPieces());
            ArrayList<Move> movesList = model.getMoves(currentPiece, model.getPieces());
            view.highlightPath(movesList);
            view.enableSquare(loc, true);
            view.colorCurrentPiece(loc);

        } else {
            ArrayList<Move> movesList = model.getMoves(currentPiece, model.getPieces());
            Move move = findMove(movesList, currentPiece, loc);
            if (move != null && currentPiece != null && !currentPiece.getLoc().isEqual(loc)) {
                updateView(currentPiece.getLoc(), loc);
                view.updateMoveLog(model.makeMove(move, model.getPieces()), numOfMoves);
                Piece[][] pieces = model.getPieces();
                Eval gameStatus = model.checkGameStatus(currentPlayer, pieces);
                if (gameStatus.isGameOver()) {
                    gameOver(gameStatus);
                    return;
                }
                numOfMoves++;
                switchPlayer();

                view.setLbl(currentPlayer + " to move");
            }
            view.enableSquares(model.getPiecesLocations(currentPlayer));
        }
        isFirstClick = !isFirstClick;
        if (model.isInCheck(currentPlayer, model.getPieces()))
            view.inCheck(model.getKing(currentPlayer, model.getPieces()).getLoc());


    }

    private Move findMove(ArrayList<Move> movesList, Piece currentPiece, Location loc) {
        for (Move move : movesList) {
            if (move.getMovingTo().isEqual(loc) && move.getPiece().getLoc().isEqual(currentPiece.getLoc())) {
                return move;
            }
        }
        System.out.println("didnt find move!!!");
        return null;
    }

    private void makeAiMove(Move move) {
        view.resetBackground();
        Piece currentPiece = move.getPiece();
        move = findMove(model.getAllMoves(currentPlayer, model.getPieces()), currentPiece, move.getMovingTo());
        view.updateMoveLog(model.makeMove(move, model.getPieces()), numOfMoves);
        specialUpdateView(currentPiece.getLoc(), move.getMovingTo());
        Piece[][] pieces = model.getPieces();
        Eval gameStatus = model.checkGameStatus(currentPlayer, pieces);
        System.out.println("game status= " + gameStatus);
        if (gameStatus.isGameOver()) {
            gameOver(gameStatus);
            return;
        }
        numOfMoves++;
        switchPlayer();

        view.setLbl(currentPlayer.name() + " to move");
        view.enableSquares(model.getPiecesLocations(currentPlayer));
        if (model.isInCheck(currentPlayer, model.getPieces()))
            view.inCheck(model.getKing(currentPlayer, model.getPieces()).getLoc());
    }

    public void updateView(Location prevLoc, Location newLoc) {
        view.updateBoardButton(prevLoc, newLoc);
    }

    public void specialUpdateView(Location prevLoc, Location newLoc) {
        view.updateBoardButtonWithoutStoppingDrag(prevLoc, newLoc);
    }

    private void gameOver(Eval gameStatus) {
        run = false;

        view.gameOver();
        if (gameStatus.getGameStatus() == GameStatus.CHECKMATE) {
            view.wonByCheckmate(currentPlayer);
        } else if (gameStatus.getGameStatus() == GameStatus.INSUFFICIENT_MATERIAL) {
            view.tieByInsufficientMaterial();
        } else if (gameStatus.getGameStatus() == GameStatus.OPPONENT_TIMED_OUT) {
            view.wonByOpponentTimedOut(currentPlayer);
        } else if (gameStatus.getGameStatus() == GameStatus.TIME_OUT_VS_INSUFFICIENT_MATERIAL) {
            view.tieByTimeOutVsInsufficientMaterial();
        } else if (gameStatus.getGameStatus() == GameStatus.STALEMATE) {
            view.tieByStalemate(currentPlayer);
        } else if (gameStatus.getGameStatus() == GameStatus.REPETITION) {
            view.tieByRepetition();
        }
    }


    private void switchPlayer() {
        currentPlayer = currentPlayer.getOtherColor(currentPlayer);
    }

    public char promote(Piece piece) {
        colors color = piece.getPieceColor();
        int choice = view.promotingDialog.run(color);
        Location loc = piece.getLoc();
        Piece newPiece;
        if (choice == types.KNIGHT.ordinal()) {
            newPiece = new Knight(loc, color, true);
        } else if (choice == types.BISHOP.ordinal()) {
            newPiece = new Bishop(loc, color, true);
        } else if (choice == types.ROOK.ordinal()) {
            newPiece = new Rook(loc, color, true);
        } else {
            newPiece = new Queen(loc, color, true);
        }

        model.replacePiece(newPiece, model.getPieces());
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
        Move move = model.getAiMove();
        if (move != null)
            makeAiMove(move);
        else System.out.println("ai move was null");
    }

}
