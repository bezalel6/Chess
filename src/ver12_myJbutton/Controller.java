package ver12_myJbutton;


import ver12_myJbutton.moves.Castling;
import ver12_myJbutton.moves.EnPassant;
import ver12_myJbutton.moves.PromotionMove;
import ver12_myJbutton.types.Piece.Player;
import ver12_myJbutton.types.Piece.types;
import ver12_myJbutton.moves.Move;
import ver12_myJbutton.types.*;

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
        //view.drawMove(move);
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
            if (move != null && currentPiece != null && !currentPiece.getLoc().equals(loc)) {
                if (move instanceof PromotionMove) {
                    ((PromotionMove) move).setPromotingTo(types.values()[promote()]);
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
            updateView(castling.getRook().getLoc(), castling.getRookFinalLoc());
        } else if (move instanceof EnPassant) {
            EnPassant epsn = (EnPassant) move;
            updateView(epsn.getCapturingPieceActualLocation(), epsn.getMovingTo());
        }
        updateView(move.getMovingFrom(), move.getMovingTo());
        view.updateMoveLog(model.makeMove(move, model.getBoard()), numOfMoves);
        Board board = model.getBoard();
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
}
