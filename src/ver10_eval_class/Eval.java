package ver10_eval_class;

import ver10_eval_class.types.Piece;

import java.util.ArrayList;

import ver10_eval_class.types.Piece.*;
import ver10_eval_class.types.*;


enum GameStatus {CHECKMATE, INSUFFICIENT_MATERIAL, OPPONENT_TIMED_OUT, TIME_OUT_VS_INSUFFICIENT_MATERIAL, STALEMATE, REPETITION, GAME_GOES_ON, LOSS}

class EvalValue {
    private static final double winEval = 1000, tieEval = 0;
    private double eval;
    private GameStatus gameStatus;

    public EvalValue(double eval, GameStatus gameStatus) {
        this.eval = eval;
        this.gameStatus = gameStatus;
    }

    public EvalValue(double eval) {
        this.eval = eval;
        gameStatus = GameStatus.GAME_GOES_ON;
    }

    public EvalValue(GameStatus gameStatus) {
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

    public EvalValue() {
        this.gameStatus = GameStatus.GAME_GOES_ON;
    }

    public EvalValue(EvalValue other) {
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
    private Model model;

    public Eval(Model model) {
        this.model = model;
    }

    private EvalValue isGameOver(Piece[][] board, colors player) {
        EvalValue tie = checkTie(player, board);
        EvalValue win = checkWin(player, board);
        EvalValue loss = checkLoss(player, board);

        return win.isGameOver() ? win : loss.isGameOver() ? loss : tie;
    }

    public EvalValue getEvaluation(colors player, Piece[][] pieces) {

        EvalValue gameOver = isGameOver(pieces, player);
        if (gameOver.isGameOver()) return gameOver;

        double ret = 0;
        //Material
        ret += compareMaterial(player, pieces);

        //Pawn structure
        ret += comparePawnStructure(player, pieces);

        //Files control
        ret += compareOpenFilesControl(player, pieces);

        //Checks

        //Captures
//        for(Piece piece:getPlayersPieces(player, pieces))
//        {
//            ArrayList<Path> list = piece.canMoveTo(pieces,false);
//            checkLegal(list,piece,pieces);
//            for(Path path: list)
//            {
//                if(path.isCapturing())
//                {
//                    System.out.println("Piece = "+ piece+" Taking "+getPiece(path.getLoc())+exchangeResult(player,piece,getPiece(path.getLoc()),pieces));
//                }
//            }
//        }

        //Attacks

        //Development
//        ret+=compareDevelopment(player,pieces);

        //Center Control

        return new EvalValue(ret, GameStatus.GAME_GOES_ON);
    }

    private EvalValue checkLoss(colors player, Piece[][] pieces) {
        ArrayList<Move> moves = model.getAllMoves(player, pieces);
        if (isInCheck(player, pieces)) {
            if (moves.isEmpty()) {
                return new EvalValue(GameStatus.LOSS);
            }
        }
        return new EvalValue();
    }

    public EvalValue checkWin(colors player, Piece[][] pieces) {
        colors otherPlayer = colors.getOtherColor(player);
        ArrayList<Move> moves = model.getAllMoves(otherPlayer, pieces);
        if (isInCheck(otherPlayer, pieces)) {
            if (moves.isEmpty()) {
                return new EvalValue(GameStatus.CHECKMATE);
            }
        }
        return new EvalValue();
    }

    public EvalValue checkTie(colors player, Piece[][] pieces) {
        if (!isInCheck(player, pieces)) {
            if (model.getAllMoves(colors.getOtherColor(player), pieces).isEmpty()) {
                return new EvalValue(GameStatus.STALEMATE);
            }
            if (!canPlayerMate(player, pieces) && !canPlayerMate(colors.getOtherColor(player), pieces)) {
                return new EvalValue(GameStatus.INSUFFICIENT_MATERIAL);
            }
//            String[] pos1 = null, pos2 = null, pos3 = null;
//            int i = posLog.size() - 1;
//            if (i >= 8) {
//                pos1 = posLog.get(i);
//                pos2 = posLog.get(i - 4);
//                pos3 = posLog.get(i - 8);
////                System.out.println("pos 1 ");
////                printArr(pos1);
////                System.out.println("pos 2 ");
////                printArr(pos2);
////                System.out.println("pos 3 ");
////                printArr(pos3);
//                if (Arrays.equals(pos1, pos2) && Arrays.equals(pos1, pos3)) {
//                    return new EvalValue(GameStatus.REPETITION);
//                }
//            }

        }

        return new EvalValue();
    }

    public boolean isThreatened(Piece piece, Piece[][] pieces) {
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (p != null && piece != null && !piece.isOnMyTeam(p)) {
                    ArrayList<Move> canMoveTo = p.canMoveTo(pieces);
                    if (canMoveTo != null)
                        for (Move move : canMoveTo) {
                            if (move.getMovingFrom().isEqual(piece.getLoc()))
                                return true;
                        }

                }
            }
        }

        return false;
    }

    public boolean isInCheck(colors player, Piece[][] pieces) {
        return isThreatened(model.getKing(player, pieces), pieces);
    }

    private boolean canPlayerMate(colors player, Piece[][] pieces) {
        ArrayList<Piece> currentPlayerPieces = model.getPlayersPieces(player, pieces);
        if (currentPlayerPieces.size() <= 1)
            return false;
        int numOfKnights = 0, numOfBishops = 0;
        for (Piece piece : currentPlayerPieces) {
            if (piece instanceof Rook || piece instanceof Queen || piece instanceof Pawn)
                return true;
            if (piece instanceof Knight)
                numOfKnights++;
            else if (piece instanceof Bishop)
                numOfBishops++;
        }
        if (numOfKnights > 0 && numOfBishops > 0)
            return true;
        return false;
    }


    private double compareOpenFilesControl(colors player, Piece[][] pieces) {
        double otherPlayersFileControl = playerFilesControl(colors.getOtherColor(player), pieces);
        double currentPlayersFileControl = playerFilesControl(player, pieces);
        return currentPlayersFileControl - otherPlayersFileControl;
    }

    private double playerFilesControl(colors player, Piece[][] pieces) {
        double ret = 0;
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null && piece.isOnMyTeam(player) && (piece instanceof Rook || piece instanceof Queen)) {
                    ret += getFileStatus(piece.getLoc().getCol(), pieces);
                }
            }
        }
        return ret;
    }

    private double getFileStatus(int col, Piece[][] pieces) {
        double ret = openFile;
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null && piece.getLoc().getCol() == col && piece instanceof Pawn) {
                    if (ret == openFile) ret = halfOpenFile;
                    else return 0;
                }
            }
        }
        return ret;
    }

    private double comparePawnStructure(colors player, Piece[][] pieces) {
        double otherPlayersStructure = countDoubledPawns(model.getPlayersPieces(colors.getOtherColor(player), pieces)) * doubledPawns;
        double currentPlayersStructure = countDoubledPawns(model.getPlayersPieces(player, pieces)) * doubledPawns;
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

    private int compareDevelopment(colors player, Piece[][] pieces) {
        ArrayList<Piece> playerPieces = model.getPlayersPieces(player, pieces);
        ArrayList<Piece> opponentPieces = model.getPlayersPieces(colors.getOtherColor(player), pieces);
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

    private int compareMaterial(colors player, Piece[][] pieces) {
        ArrayList<Piece> currentPlayerPieces = model.getPlayersPieces(player, pieces);
        ArrayList<Piece> opponentPieces = model.getPlayersPieces(colors.getOtherColor(player), pieces);
        int playerSum = 0, opponentSum = 0;
        for (Piece piece : currentPlayerPieces)
            playerSum += piece.getWorth();
        for (Piece piece : opponentPieces)
            opponentSum += piece.getWorth();
        return playerSum - opponentSum;
    }

}