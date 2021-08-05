package ver13_FEN;

import ver13_FEN.types.Piece.Player;
import ver13_FEN.types.Pawn;
import ver13_FEN.types.Piece;
import ver13_FEN.types.Queen;
import ver13_FEN.types.Rook;

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

        //Pawn structure
        ret += comparePawnStructure(player);

        //Files control
        ret += compareOpenFilesControl(player);

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


    private double compareOpenFilesControl(Player player) {
        double otherPlayersFileControl = playerFilesControl(Player.getOtherColor(player), board);
        double currentPlayersFileControl = playerFilesControl(player, board);
        return currentPlayersFileControl - otherPlayersFileControl;
    }

    private double playerFilesControl(Player player, Board board) {
        double ret = 0;
        for (Piece[] row : board) {
            for (Piece piece : row) {
                if (piece != null && piece.isOnMyTeam(player) && (piece instanceof Rook || piece instanceof Queen)) {
                    ret += getFileStatus(piece.getLoc().getCol(), board);
                }
            }
        }
        return ret;
    }

    private double getFileStatus(int col, Board board) {
        double ret = openFile;
        for (Piece[] row : board) {
            for (Piece piece : row) {
                if (piece != null && piece.getLoc().getCol() == col && piece instanceof Pawn) {
                    if (ret == openFile) ret = halfOpenFile;
                    else return 0;
                }
            }
        }
        return ret;
    }

    private double comparePawnStructure(Player player) {
        double otherPlayersStructure = countDoubledPawns(this.board.getPlayersPieces(Player.getOtherColor(player))) * doubledPawns;
        double currentPlayersStructure = countDoubledPawns(this.board.getPlayersPieces(player)) * doubledPawns;
        return currentPlayersStructure - otherPlayersStructure;
    }

    private int countDoubledPawns(ArrayList<Piece> playersPieces) {
        int ret = 0;
        ArrayList foundPawnsOnCols = new ArrayList();
        for (Piece piece : playersPieces) {
            if (piece instanceof Pawn) {
                if (foundPawnsOnCols.contains(piece.getLoc().getCol())) {
                    ret++;
                }
                foundPawnsOnCols.add(piece.getLoc().getCol());
            }
        }
        return ret;
    }

    private int compareDevelopment(Player player) {
        ArrayList<Piece> playerPieces = this.board.getPlayersPieces(player);
        ArrayList<Piece> opponentPieces = this.board.getPlayersPieces(Player.getOtherColor(player));
        int playerUndeveloped = 0, opponentUndeveloped = 0;
        for (Piece piece : playerPieces) {
            if (!piece.getHasMoved()) {
                playerUndeveloped += piece.getWorth();
            }
        }
        for (Piece piece : opponentPieces) {
            if (!piece.getHasMoved()) {
                opponentUndeveloped += piece.getWorth();
            }
        }
        return playerUndeveloped - opponentUndeveloped;
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