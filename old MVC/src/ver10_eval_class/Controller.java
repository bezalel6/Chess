package ver10_eval_class;


import ver10_eval_class.types.*;
import ver10_eval_class.types.Piece.colors;
import ver10_eval_class.types.Piece.types;

import java.util.ArrayList;

public class Controller {
    public static int MIN_SCAN_DEPTH = 1, MAX_SCAN_DEPTH = 10, SCAN_INIT_VALUE = 3;
    private final int DEFAULT_BOARD_SIZE = 8;
    public int numOfMoves;
    private View view;
    private Model model;
    private int scanDepth = SCAN_INIT_VALUE;

    private colors currentPlayer;
    private Piece currentPiece;
    private Dialogs promotingDialog;


    private boolean showPositionDialog = false;
    private boolean isFirstClick = true;
    private boolean aiGame = false;


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
            ArrayList<String[]> positions = Positions.getAllPositionsNamesAndIndexes();
            ArrayList<DialogObject> objects = new ArrayList<>();
            for (String[] position : positions) {
                objects.add(new DialogObject(position[0], Integer.parseInt(position[1])));
            }
            startingPosition = positionsDialog.run(objects);
        }
        currentPlayer = colors.WHITE;
        currentPiece = null;
        numOfMoves = 1;
        isFirstClick = true;
        model.initGame(startingPosition);
        view.initGame(model.getPieces());
        view.enableSquares(model.getPiecesLocations(currentPlayer));

    }

    public void aiFoundOption(Move move) {
        view.drawMove(move);
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
        System.out.println("btn pressed");
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
                if (move.getPromotionMove() != null) {
                    move.setPromotionMove(new PromotionMove(move, types.values()[promote(move.getPiece())]));
                } else
                    updateView(currentPiece.getLoc(), loc);
                view.updateMoveLog(model.makeMove(move, model.getPieces()), numOfMoves);
                Piece[][] pieces = model.getPieces();
                EvalValue gameStatus = model.checkGameStatus(currentPlayer, pieces);
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
        if (model.eval.isInCheck(currentPlayer, model.getPieces()))
            view.inCheck(model.getKing(currentPlayer, model.getPieces()).getLoc());
    }

    private Move findMove(ArrayList<Move> movesList, Piece currentPiece, Location loc) {
        for (Move move : movesList) {
            if (move.getMovingFrom().isEqual(loc) && move.getPiece().getLoc().isEqual(currentPiece.getLoc())) {
                return move;
            }
        }
        return null;
    }

    private void makeAiMove(Move move) {
        view.resetBackground();
        Piece currentPiece = move.getPiece();
        move = findMove(model.getAllMoves(currentPlayer, model.getPieces()), currentPiece, move.getMovingFrom());
        view.updateMoveLog(model.makeMove(move, model.getPieces()), numOfMoves);
        updateView(currentPiece.getLoc(), move.getMovingFrom());
        Piece[][] pieces = model.getPieces();
        EvalValue gameStatus = model.checkGameStatus(currentPlayer, pieces);
        System.out.println("game status= " + gameStatus);
        if (gameStatus.isGameOver()) {
            gameOver(gameStatus);
            return;
        }
        numOfMoves++;
        switchPlayer();

        view.setLbl(currentPlayer.name() + " to move");
        view.enableSquares(model.getPiecesLocations(currentPlayer));
        if (model.eval.isInCheck(currentPlayer, model.getPieces()))
            view.inCheck(model.getKing(currentPlayer, model.getPieces()).getLoc());
    }

    public void updateView(Location prevLoc, Location newLoc) {
        view.updateBoardButton(prevLoc, newLoc);
    }


    private void gameOver(EvalValue gameStatus) {
        aiGame = false;

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


    private ArrayList<DialogObject> getPromotionObjects(colors color) {
        ArrayList<DialogObject> ret = new ArrayList<>();
        if (color == colors.WHITE) {
            ret.add(new DialogObject(view.wn, types.KNIGHT.ordinal()));
            ret.add(new DialogObject(view.wb, types.BISHOP.ordinal()));
            ret.add(new DialogObject(view.wq, types.QUEEN.ordinal()));
            ret.add(new DialogObject(view.wr, types.ROOK.ordinal()));
        } else {
            ret.add(new DialogObject(view.bn, types.KNIGHT.ordinal()));
            ret.add(new DialogObject(view.bb, types.BISHOP.ordinal()));
            ret.add(new DialogObject(view.bq, types.QUEEN.ordinal()));
            ret.add(new DialogObject(view.br, types.ROOK.ordinal()));
        }
        return ret;
    }

    public int promote(Piece piece) {
        promotingDialog = new Dialogs(DialogTypes.HORIZONTAL_LIST, "Promote");
        colors color = piece.getPieceColor();
        int choice = promotingDialog.run(getPromotionObjects(color));
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
        return choice;
    }

    public void newGameBtnPressed() {
        startNewGame();
    }

    public void evalBtnPressed() {
        System.out.println("eval = " + model.eval.getEvaluation(currentPlayer, model.getPieces()));
    }

    public void aiMoveButtonPressed() {
        view.deleteAllDrawings();
        Move move = model.getAiMove();
        if (move != null)
            makeAiMove(move);
        else System.out.println("ai move was null");
    }
}
