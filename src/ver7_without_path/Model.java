package ver7_without_path;

import ver7_without_path.types.Piece.colors;
import ver7_without_path.types.*;
import ver7_without_path.Move.SpecialMoves;

import java.util.ArrayList;
import java.util.Arrays;

public class Model {
    //public static final int WIN = 100,LOSS = -100,TIE=0;
    private static int ROWS;
    private static int COLS;
    private static int scanDepth = 5;
    private final double halfOpenFile = 0.5, openFile = 1;
    private final double doubledPawns = -0.5;
    private Positions positions = new Positions();
    private Piece[][] logicMat;
    private Controller controller;
    private ArrayList<String[]> posLog = new ArrayList<>();
    private String additions = "";
    private int gameOverInfo;

    public Model(int boardSize, Controller controller) {
        this.controller = controller;
        ROWS = COLS = boardSize;
    }

    public void initGame() {
        logicMat = loadPos(positions.endgame());
    }

    private Piece[][] loadPos(Piece[] pos) {
        Piece[][] ret = new Piece[ROWS][COLS];
        for (Piece piece : pos) {
            ret[piece.getLoc().getRow()][piece.getLoc().getCol()] = piece;
        }
        return ret;
    }

    public String makeMove(Move move, Piece[][] board) {
        Piece piece = move.getPiece();
        Location movingTo = move.getMovingTo();
        String pieceAnnotation = piece.getAnnotation();
        String captures = "";
        additions = "";

        if (piece instanceof Pawn)
            pieceAnnotation = "";
        if (move.isCapturing()) {
            captures = "x";
            pieceAnnotation = piece.getAnnotation();
        }
        applyMove(move, board);

        if (move.getSpecialMove() == SpecialMoves.LONG_CASTLE) {
            castle(((King) piece).queenSide, board);
        } else if (move.getSpecialMove() == SpecialMoves.SHORT_CASTLE) {
            castle(((King) piece).kingSide, board);
        } else if (move.getSpecialMove() == SpecialMoves.PROMOTION) {
            controller.promote(piece);
        }
        pieceAnnotation += captures;

        //posLog.add(matToPos(board));

        if (checkWin(piece.getPieceColor(), board)) {
            additions += "#";
        } else if (isInCheck(piece.getOtherColor(), board))
            additions += "+";
        else if (checkTie(piece.getPieceColor(), board)) {
            additions += "½½";
        }
        pieceAnnotation += movingTo.getStr();
        piece.setMoved();
        String retStr = (pieceAnnotation + additions);

        return retStr;
    }

    private void applyMove(Move move, Piece[][] board) {
        Piece piece = move.getPiece();
        Location prev = piece.getLoc();
        Location movingTo = move.getMovingTo();
        board[movingTo.getRow()][movingTo.getCol()] = piece;
        board[prev.getRow()][prev.getCol()] = null;
        piece.setLoc(movingTo);
    }

    private void castle(Castling castling, Piece[][] board) {
        Rook rook = castling.getRook();
        Location rookFinalLoc = castling.getRookFinalLoc();
        controller.specialUpdateView(rook.getLoc(), rookFinalLoc);
        makeMove(new Move(rook, rookFinalLoc, false), board);
    }

    public boolean isSquareThreatened(Location square, colors threateningPlayer, Piece[][] pieces) {
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (p != null && p.isOnMyTeam(threateningPlayer)) {
                    ArrayList<Move> canMoveTo = p.canMoveTo(pieces);
                    if (canMoveTo != null)
                        for (Move move : canMoveTo) {
                            if (move.getMovingTo().isEqual(square))
                                return true;
                        }

                }
            }
        }
        return false;
    }

    public void replacePiece(Piece replacingWith) {

        logicMat[replacingWith.getLoc().getRow()][replacingWith.getLoc().getCol()] = replacingWith;
    }

    public ArrayList<Location> getPiecesLocations(colors currentPlayer) {
        ArrayList<Location> ret = new ArrayList<>();
        for (Piece[] row : logicMat) {
            for (Piece piece : row) {
                if (piece != null)
                    if (piece.isOnMyTeam(currentPlayer))
                        ret.add(piece.getLoc());
            }
        }
        return ret;
    }

    public ArrayList<Move> getAllMoves(colors currentPlayer, Piece[][] pieces) {
        ArrayList<Move> ret = new ArrayList<>();
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null)
                    if (piece.isOnMyTeam(currentPlayer)) {
                        ArrayList<Move> movingTo = piece.canMoveTo(pieces);
                        checkLegal(movingTo, pieces);
                        ret.addAll(movingTo);
                    }
            }
        }
        return ret;
    }

    public boolean isInCheck(colors player, Piece[][] pieces) {
        return isThreatened(getKing(player, pieces), pieces);
    }

    public void checkLegal(ArrayList<Move> list, Piece[][] pieces) {
        if (list.isEmpty())
            return;

        ArrayList<Move> delete = new ArrayList<>();
        Piece currentPiece = list.get(0).getPiece();

//        if(currentPiece instanceof Pawn){
//            ArrayList<Piece> otherPlayersPieces = getPlayersPieces(currentPiece.getOtherColor(),pieces);
//            for(Piece piece: otherPlayersPieces){
//                if(piece instanceof Pawn){
//                    
//                }
//            }
//        }

        for (Move move : list) {
            Location loc = move.getMovingTo();
            if (!isInBounds(loc)) {
                delete.add(move);
                continue;
            }
            Piece destination = getPiece(loc, pieces);
            if (destination != null && currentPiece.isOnMyTeam(destination)) {
                delete.add(move);
                continue;
            }
            SpecialMoves specialMove = move.getSpecialMove();
            if (specialMove != SpecialMoves.NONE) {
                if (specialMove == SpecialMoves.SHORT_CASTLE) {
                    Castling kingSide = ((King) currentPiece).kingSide;
                    if (isSquareThreatened(kingSide.getKingMiddleMove(), currentPiece.getOtherColor(), pieces) || getPiece(kingSide.getKingMiddleMove(), pieces) != null) {
                        delete.add(move);
                    }
                } else if (specialMove == SpecialMoves.LONG_CASTLE) {
                    Castling queenSide = ((King) currentPiece).queenSide;
                    if (isSquareThreatened(queenSide.getKingMiddleMove(), currentPiece.getOtherColor(), pieces) ||
                            getPiece(queenSide.getKingMiddleMove(), pieces) != null || getPiece(queenSide.getRookMiddleLoc(), pieces) != null) {
                        delete.add(move);
                    }
                }
            }

            Location storeLoc = currentPiece.getLoc();
            pieces[storeLoc.getRow()][storeLoc.getCol()] = null;
            pieces[loc.getRow()][loc.getCol()] = currentPiece;
            currentPiece.setLoc(loc);
            if (isInCheck(currentPiece.getPieceColor(), pieces)) {
                delete.add(move);
            }
            pieces[storeLoc.getRow()][storeLoc.getCol()] = currentPiece;
            pieces[loc.getRow()][loc.getCol()] = destination;
            currentPiece.setLoc(storeLoc);

        }
        for (Move move : delete)
            list.remove(move);

    }

    public boolean isInBounds(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS;
    }

    public boolean isThreatened(Piece piece, Piece[][] pieces) {
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (p != null && piece != null && !piece.isOnMyTeam(p)) {
                    ArrayList<Move> canMoveTo = p.canMoveTo(pieces);
                    if (canMoveTo != null)
                        for (Move move : canMoveTo) {
                            if (move.getMovingTo().isEqual(piece.getLoc()))
                                return true;
                        }

                }
            }
        }

        return false;
    }

    public Piece getKing(colors player, Piece[][] pieces) {
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (p != null && p instanceof King && p.getPieceColor() == player)
                    return p;
            }
        }
        return null;
    }

    public Piece getPiece(Location loc, Piece[][] pieces) {
        return pieces[loc.getRow()][loc.getCol()];
    }

    public ArrayList<Move> getMoves(Piece piece, Piece[][] pieces) {

        if (piece == null) {
            return null;
        }
        ArrayList<Move> list = piece.canMoveTo(pieces);

        checkLegal(list, pieces);
        return list;
    }

    public Piece[][] getPieces() {
        return logicMat;
    }

    public void printLogicMat(Piece[][] board) {
        for (Piece[] row : board) {
            for (Piece piece : row) {
                System.out.print("|");
                if (piece == null) {
                    System.out.print("  ");
                } else {
                    if (piece.isWhite())
                        System.out.print("W");
                    else System.out.print("B");
                    System.out.print(piece.getAnnotation());
                }
                System.out.print("|");
            }
            System.out.println();
        }
    }

    public double checkGameStatus(colors player, Piece[][] pieces) {
        return eval(player, pieces);
    }

    // מהלך מינימקס
    public Move getAiMove() {
        System.out.println("Model getAiMove() using MINIMAX");

        MinimaxReturn bestMove = getBestMoveUsingMinimax();

        return bestMove.getMove();
    }

    // ============================ for minimax ===========================
    private MinimaxReturn getBestMoveUsingMinimax() {
        MinimaxReturn ret = minimax(logicMat, true, 0);
        System.out.println("minimax move = " + ret);
        return ret;
    }

    public MinimaxReturn minimax(Piece[][] board, boolean isMax, int depth) {
        MinimaxReturn bestMove = new MinimaxReturn(null, isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE);

        colors actualPlayer = controller.getCurrentPlayer();
        colors player = isMax ? actualPlayer : colors.getOtherColor(actualPlayer);
        Piece[][] newBoard = copyBoard(board);

        if (isGameOver(newBoard, player) || depth >= scanDepth) {
            double value = eval(actualPlayer, newBoard);
            return new MinimaxReturn(null, value, depth);
        }
        ArrayList<Move> possibleMoves = getAllMoves(player, newBoard);

        for (int i = 0; i < possibleMoves.size(); i++) {
            Move move = possibleMoves.get(i);

            Location storePieceLocation = move.getPiece().getLoc();

            applyMove(move, newBoard);
//            System.out.println("move applied: " + move);
//            printLogicMat(newBoard);
//            System.out.println();
            MinimaxReturn val = minimax(newBoard, !isMax, depth + 1);
            val.setMove(move);

            applyMove(new Move(move.getPiece(), storePieceLocation, false), newBoard);

            if (depth == 0) {
                System.out.println("Move#" + (i + 1) + ": " + move + " val: " + val.getValue() + " at depth " + val.getDepth());
            }
//            if ((isMax && val.getValue() > bestMove.getValue()) || (!isMax && val.getValue() < bestMove.getValue())) {
//                bestMove.setValue(val.getValue());
//                bestMove.setMove(val.getMove());
//            }
            if (isMax) {
                bestMove = Math.max(bestMove.getValue(), val.getValue()) == bestMove.getValue() ? bestMove : val;

            } else {
                bestMove = Math.min(bestMove.getValue(), val.getValue()) == bestMove.getValue() ? bestMove : val;
            }
            if (bestMove.getValue() == val.getValue()) {
                bestMove = Math.max(bestMove.getDepth(), val.getDepth()) == bestMove.getDepth() ? bestMove : val;
            }

        }
        return bestMove;
    }

    private Piece[] getPieces(Piece[][] pieces) {
        ArrayList<Piece> ret = new ArrayList<>();
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null) {
                    ret.add(piece);
                }
            }
        }
        return ret.toArray(new Piece[]{});
    }

    private Piece[][] copyBoard(Piece[][] og) {
        return loadPos(Positions.posToPieces(Positions.piecesToPos(getPieces(og))));
    }

    private boolean isGameOver(Piece[][] board, colors player) {
        return checkTie(player, board) || checkWin(player, board) || checkLoss(player, board);
    }

    public double eval(colors player, Piece[][] pieces) {
        if (checkTie(player, pieces) || checkWin(player, pieces) || checkLoss(player, pieces)) {
            return gameOverInfo;
        }

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

        return ret;
    }

    private boolean checkLoss(colors player, Piece[][] pieces) {
        ArrayList<Move> moves = getAllMoves(player, pieces);
        if (isInCheck(player, pieces)) {
            if (isListEmpty(moves)) {
                gameOverInfo = -Controller.CHECKMATE;
                return true;
            }
        }
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
        double otherPlayersStructure = countDoubledPawns(getPlayersPieces(colors.getOtherColor(player), pieces)) * doubledPawns;
        double currentPlayersStructure = countDoubledPawns(getPlayersPieces(player, pieces)) * doubledPawns;
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
        ArrayList<Piece> playerPieces = getPlayersPieces(player, pieces);
        ArrayList<Piece> opponentPieces = getPlayersPieces(colors.getOtherColor(player), pieces);
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
        ArrayList<Piece> currentPlayerPieces = getPlayersPieces(player, pieces);
        ArrayList<Piece> opponentPieces = getPlayersPieces(colors.getOtherColor(player), pieces);
        int playerSum = 0, opponentSum = 0;
        for (Piece piece : currentPlayerPieces)
            playerSum += piece.getWorth();
        for (Piece piece : opponentPieces)
            opponentSum += piece.getWorth();
        return playerSum - opponentSum;
    }

    private boolean checkWin(colors player, Piece[][] pieces) {
        colors otherPlayer = colors.getOtherColor(player);
        ArrayList<Move> moves = getAllMoves(otherPlayer, pieces);
        if (isInCheck(otherPlayer, pieces)) {
            if (isListEmpty(moves)) {
                gameOverInfo = Controller.CHECKMATE;
                return true;
            }
        }
        return false;
    }

    private boolean checkTie(colors player, Piece[][] pieces) {
        if (!isInCheck(player, pieces)) {
            if (isListEmpty(getPlayersPieces(colors.getOtherColor(player), pieces))) {
                gameOverInfo = Controller.STALEMATE;
                return true;
            }
            if (!canPlayerMate(player, pieces) && !canPlayerMate(colors.getOtherColor(player), pieces)) {
                gameOverInfo = Controller.INSUFFICIENT_MATERIAL;
                return true;
            }
            String[] pos1 = null, pos2 = null, pos3 = null;
            int i = posLog.size() - 1;
            if (i >= 8) {
                pos1 = posLog.get(i);
                pos2 = posLog.get(i - 4);
                pos3 = posLog.get(i - 8);
//                System.out.println("pos 1 ");
//                printArr(pos1);
//                System.out.println("pos 2 ");
//                printArr(pos2);
//                System.out.println("pos 3 ");
//                printArr(pos3);
                if (Arrays.equals(pos1, pos2) && Arrays.equals(pos1, pos3)) {
                    gameOverInfo = Controller.REPETITION;
                    return true;
                }
            }

        }

        return false;
    }

    void printArr(String[] arr) {
        for (String str : arr) {
            System.out.print(str + " ");
        }
        System.out.println();
    }

    private boolean isListEmpty(ArrayList list) {
        return list == null || list.isEmpty();
    }

    private boolean canPlayerMate(colors player, Piece[][] pieces) {
        ArrayList<Piece> currentPlayerPieces = getPlayersPieces(player, pieces);
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

    private ArrayList<Piece> getPlayersPieces(colors player, Piece[][] pieces) {
        ArrayList<Piece> ret = new ArrayList<>();
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null && piece.isOnMyTeam(player))
                    ret.add(piece);
            }
        }
        return ret;
    }
}


