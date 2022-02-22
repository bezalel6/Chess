package ver11_board_class;


import ver11_board_class.moves.Move;
import ver11_board_class.moves.PromotionMove;
import ver11_board_class.types.Piece.Player;
import ver11_board_class.types.Piece.types;
import ver11_board_class.types.*;

import java.util.ArrayList;

public class Controller {
    public static int MIN_SCAN_DEPTH = 1, MAX_SCAN_DEPTH = 10, SCAN_INIT_VALUE = 3;
    private final int DEFAULT_BOARD_SIZE = 8;
    public int numOfMoves;
    private View view;
    private Model model;
    private int scanDepth = SCAN_INIT_VALUE;

    private Player currentPlayer;
    private Piece currentPiece;
    private Dialogs promotingDialog;


    private boolean showPositionDialog = true;
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
        currentPlayer = Player.WHITE;
        currentPiece = null;
        numOfMoves = 1;
        isFirstClick = true;
        model.initGame(startingPosition);
        view.initGame(model.getBoard());
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

    public Player getCurrentPlayer() {
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
            if (move != null && currentPiece != null && !currentPiece.getLoc().isEqual(loc)) {
                if (move instanceof PromotionMove) {
                    //move.setPromotionMove(new PromotionMove(move, types.values()[promote(move.getPiece())]));
                } else
                    updateView(currentPiece.getLoc(), loc);
                view.updateMoveLog(model.makeMove(move, model.getBoard()), numOfMoves);
                Board board = model.getBoard();
                BoardEval gameStatus = board.getBoardEval();
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
        Board board = model.getBoard();
        if (board.isInCheck(currentPlayer))
            view.inCheck(board.getKing(currentPlayer).getLoc());
    }

    private void makeMove(Move move) {

    }

    private Move findMove(ArrayList<Move> movesList, Piece currentPiece, Location loc) {
        for (Move move : movesList) {
            if (move.getMovingTo().isEqual(loc) && model.getBoard().getPiece(move.getMovingFrom()).getLoc().isEqual(currentPiece.getLoc())) {
                return move;
            }
        }
        return null;
    }

    private void makeAiMove(Move move) {
//        view.resetBackground();
//        Piece currentPiece = move.getPiece();
//        move = findMove(model.getAllMoves(currentPlayer, model.getBoard()), currentPiece, move.getMovingFrom());
//        view.updateMoveLog(model.makeMove(move, model.getBoard()), numOfMoves);
//        updateView(currentPiece.getLoc(), move.getMovingFrom());
//        Board board = model.getBoard();
//        BoardEval gameStatus = model.checkGameStatus(currentPlayer, board);
//        System.out.println("game status= " + gameStatus);
//        if (gameStatus.isGameOver()) {
//            gameOver(gameStatus);
//            return;
//        }
//        numOfMoves++;
//        switchPlayer();
//
//        view.setLbl(currentPlayer.name() + " to move");
//        view.enableSquares(model.getPiecesLocations(currentPlayer));
//        if (model.eval.isInCheck(currentPlayer, model.getBoard()))
//            view.inCheck(model.getKing(currentPlayer, model.getBoard()).getLoc());
//        isFirstClick = false;
    }

    public void updateView(Location prevLoc, Location newLoc) {
        view.updateBoardButton(prevLoc, newLoc);
    }


    private void gameOver(BoardEval gameStatus) {
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
        model.getBoard().setCurrentPlayer(currentPlayer);
    }


    private ArrayList<DialogObject> getPromotionObjects(Player player) {
        ArrayList<DialogObject> ret = new ArrayList<>();
        if (player == Player.WHITE) {
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
        Player player = piece.getPieceColor();
        int choice = promotingDialog.run(getPromotionObjects(player));
        Location loc = piece.getLoc();
        Piece newPiece;
        if (choice == types.KNIGHT.ordinal()) {
            newPiece = new Knight(loc, player, true);
        } else if (choice == types.BISHOP.ordinal()) {
            newPiece = new Bishop(loc, player, true);
        } else if (choice == types.ROOK.ordinal()) {
            newPiece = new Rook(loc, player, true);
        } else {
            newPiece = new Queen(loc, player, true);
        }

        //model.replacePiece(newPiece, model.getBoard());
        view.setPieces(model.getBoard());
        return choice;
    }

    public void newGameBtnPressed() {
        startNewGame();
    }

    public void evalBtnPressed() {
        model.getBoard().printBoard();
        System.out.println(model.getBoard().getBoardEval());
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
}
