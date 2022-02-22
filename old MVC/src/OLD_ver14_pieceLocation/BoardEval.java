package OLD_ver14_pieceLocation;

import OLD_ver14_pieceLocation.types.Piece.Player;
import OLD_ver14_pieceLocation.types.Piece;

import java.util.ArrayList;


public class BoardEval {
    private static final double winEval = 1000, tieEval = 0;
    private double eval;
    private GameStatus gameStatus;

    public BoardEval(double eval, GameStatus gameStatus) {
        this.eval = eval;
        this.gameStatus = gameStatus;
    }

    public BoardEval(double eval) {
        this.eval = eval;
        gameStatus = GameStatus.GAME_GOES_ON;
    }

    public BoardEval(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        double res;
        switch (gameStatus) {
            case OPPONENT_TIMED_OUT:
            case CHECKMATE:
                res = winEval;
                break;
            case LOSS:
                res = -winEval;
                break;
            case STALEMATE:
            case REPETITION:
            case INSUFFICIENT_MATERIAL:
            case TIME_OUT_VS_INSUFFICIENT_MATERIAL:
                res = tieEval;
                break;
            default:
                res = 0;
        }
        eval = res;
    }

    public BoardEval() {
        this.gameStatus = GameStatus.GAME_GOES_ON;
    }

    public BoardEval(BoardEval other) {
        eval = other.eval;
        gameStatus = other.gameStatus;
    }

    public double getEval() {
        return eval;
    }

    public void setEval(double eval) {
        this.eval = eval;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public boolean isGameOver() {
        return gameStatus != GameStatus.GAME_GOES_ON;
    }

    @Override
    public String toString() {
        return "Eval{" +
                "eval=" + eval +
                ", gameStatus=" + gameStatus +
                '}';
    }
}

class Eval {

    private final double halfOpenFile = 0.5, openFile = 1;
    private final double doubledPawns = -0.5;
    private Board board;

    public Eval(Board board) {
        this.board = board;
    }

    public BoardEval getEvaluation(Player player) {
        BoardEval checkGameOver = board.isGameOver();
        if (checkGameOver.isGameOver()) return checkGameOver;

        double ret = 0;
        //Material
        ret += compareMaterial(player);

        //Checks

        //Captures
//        for(Piece piece:getPlayersPieces(player, board))
//        {
//            ArrayList<Path> list = piece.canMoveTo(board,false);
//            checkLegal(list,piece,board);
//            for(Path path: list)
//            {
//                if(path.isCapturing())
//                {
//                    System.out.println("Piece = "+ piece+" Taking "+getPiece(path.getLoc())+exchangeResult(player,piece,getPiece(path.getLoc()),board));
//                }
//            }
//        }

        //Attacks

        //Development
//        ret+=compareDevelopment(player,board);

        //Center Control

        return new BoardEval(ret, GameStatus.GAME_GOES_ON);
    }

    private int compareMaterial(Player player) {
        ArrayList<Piece> currentPlayerPieces = this.board.getPlayersPieces(player);
        ArrayList<Piece> opponentPieces = this.board.getPlayersPieces(Player.getOtherColor(player));
        int playerSum = 0, opponentSum = 0;
        for (Piece piece : currentPlayerPieces)
            playerSum += piece.getWorth();
        for (Piece piece : opponentPieces)
            opponentSum += piece.getWorth();
        return playerSum - opponentSum;
    }

}