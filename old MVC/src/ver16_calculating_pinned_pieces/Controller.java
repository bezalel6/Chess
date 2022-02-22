package ver16_calculating_pinned_pieces;


import ver16_calculating_pinned_pieces.types.Piece.PieceTypes;
import ver16_calculating_pinned_pieces.types.Piece.Player;
import ver16_calculating_pinned_pieces.moves.Castling;
import ver16_calculating_pinned_pieces.moves.EnPassant;
import ver16_calculating_pinned_pieces.moves.Move;
import ver16_calculating_pinned_pieces.moves.PromotionMove;
import ver16_calculating_pinned_pieces.types.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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
                    ((PromotionMove) move).setPromotingTo(PieceTypes.values()[promote()]);
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
            ret.add(new DialogObject(view.wn, PieceTypes.KNIGHT.ordinal()));
            ret.add(new DialogObject(view.wb, PieceTypes.BISHOP.ordinal()));
            ret.add(new DialogObject(view.wq, PieceTypes.QUEEN.ordinal()));
            ret.add(new DialogObject(view.wr, PieceTypes.ROOK.ordinal()));
        } else {
            ret.add(new DialogObject(view.bn, PieceTypes.KNIGHT.ordinal()));
            ret.add(new DialogObject(view.bb, PieceTypes.BISHOP.ordinal()));
            ret.add(new DialogObject(view.bq, PieceTypes.QUEEN.ordinal()));
            ret.add(new DialogObject(view.br, PieceTypes.ROOK.ordinal()));
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
        System.out.println(model.getBoard().getFen());
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
        model.printAllPossibleMoves();
        System.out.println("getting moves");
        ArrayList moves = model.getBoard().getAllMovesForCurrentPlayer();
        System.out.println("drawing arrows");
        moves.forEach(move -> view.drawMove((Move) move));
        System.out.println("finished drawing arrows");
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
}
