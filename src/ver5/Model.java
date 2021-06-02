package ver5;

import ver5.types.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Model {
    //public static final int WIN = 100,LOSS = -100,TIE=0;
    private static int ROWS;
    private static int COLS;
    private Positions positions = new Positions();
    private Piece[][] logicMat;
    private Controller controller;

    public Model(int boardSize, Controller controller) {
        this.controller = controller;
        ROWS = COLS = boardSize;
        initGame();
    }

    public void initGame() {
        start();
    }

    public void start() {
        logicMat = new Piece[ROWS][COLS];
        loadPos(positions.endgame);
    }

    private void loadPos(String[] pieces) {
        logicMat = posToMat(pieces);

    }

    private String[] matToPos(Piece[][] mat) {
        ArrayList<String> list = new ArrayList<>();
        for (Piece[] row : mat) {
            for (Piece piece : row) {
                if (piece != null) {
                    list.add(pieceToPos(piece));
                }
            }
        }
        String[] ret = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }

        return ret;
    }

    private String pieceToPos(Piece piece) {
        return piece.getPieceColor() + "" + piece.getPieceType() + piece.getLoc().getStr();
    }

    private Piece[][] posToMat(String[] pos) {
        Piece[][] ret = new Piece[ROWS][COLS];
        for (String str : pos) {
            int color = str.charAt(0) - '0', type = str.charAt(1) - '0';
            Location loc = new Location(str.substring(2));
            int r = loc.getRow(), l = loc.getCol();
            switch (type) {
                case (Piece.KNIGHT):
                    ret[r][l] = new Knight(loc, color);
                    break;
                case (Piece.BISHOP):
                    ret[r][l] = new Bishop(loc, color);
                    break;
                case (Piece.ROOK):
                    ret[r][l] = new Rook(loc, color);
                    break;
                case (Piece.KING):
                    ret[r][l] = new King(loc, color);
                    break;
                case (Piece.QUEEN):
                    ret[r][l] = new Queen(loc, color);
                    break;
                default:
                    ret[r][l] = new Pawn(loc, color);
                    ret[r][l].setAnnotation(loc.getStr().charAt(0) + "");

            }
        }
        return ret;

    }

    private ArrayList<String[]> posLog = new ArrayList<>();
    private String additions = "";
    private ArrayList<SpecialMove> specialMoves;

    public String makeMove(Location loc, Piece piece, Piece[][] board) {
        String move = piece.getAnnotation();
        Location prev = piece.getLoc();
        String captures = "";
        additions = "";

        if (piece instanceof Pawn)
            move = "";
        if (getPiece(loc, board) != null) {
            captures = "x";
            move = piece.getAnnotation();
        }
        board[loc.getRow()][loc.getCol()] = piece;
        board[prev.getRow()][prev.getCol()] = null;
        piece.setLoc(loc);

        ArrayList<SpecialMove> delete = new ArrayList<>();
        if (specialMoves != null)
            for (SpecialMove specialMove : specialMoves) {
                if (specialMove.getPiece().equals(piece) && specialMove.getPath().getLoc().isEqual(loc)) {
                    switch (specialMove.getMoveType()) {
                        case (SpecialMove.SHORT_CASTLE):
                            shortCastle(specialMove, board);
                            move = "O-O";
                            break;
                        case (SpecialMove.LONG_CASTLE):
                            longCastle(specialMove, board);
                            break;
                        case (SpecialMove.PROMOTION):
                            move = piece.getAnnotation();
                            additions += "=" + controller.promote(piece);
                            break;
                    }
                    delete.add(specialMove);
                }
            }
        for (SpecialMove del : delete) {
            specialMoves.remove(del);
        }
        move += captures;

        posLog.add(matToPos(board));

        if (checkWin(piece.getPieceColor(), board)) {
            additions += "#";
        } else if (isInCheck(piece.getOtherColor(), board))
            additions += "+";
        else if (checkTie(piece.getPieceColor(), board)) {
            additions += "=";
        }
        move += loc.getStr();
        piece.setMoved();
        String retStr = (move + additions);

        return retStr;
    }

    private void longCastle(SpecialMove specialMove, Piece[][] board) {
        King king = ((King) specialMove.getPiece());
        Rook rook = king.queenSide.getRook();
        controller.spacialUpdateView(rook.getLoc(), king.queenSide.getRookFinalLoc());
        makeMove(king.queenSide.getRookFinalLoc(), rook, board);
    }

    private void shortCastle(SpecialMove specialMove, Piece[][] board) {
        King king = ((King) specialMove.getPiece());
        Rook rook = king.kingSide.getRook();
        controller.spacialUpdateView(rook.getLoc(), king.kingSide.getRookFinalLoc());
        makeMove(king.kingSide.getRookFinalLoc(), rook, board);
    }

    public boolean isSquareThreatened(Location square, int threateningPlayer, Piece[][] pieces) {
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (p != null && p.isOnMyTeam(threateningPlayer)) {
                    ArrayList<Path> canMoveTo = p.canMoveTo(pieces);
                    if (canMoveTo != null)
                        for (Path path : canMoveTo) {
                            if (path.getLoc().isEqual(square))
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

    public ArrayList<Location> getPiecesLocations(int currentPlayer) {
        ArrayList<Location> ret = new ArrayList<>();
        for (Piece[] row : logicMat) {
            for (Piece piece : row) {
                if (piece != null)
                    if (piece.getPieceColor() == currentPlayer)
                        ret.add(piece.getLoc());
            }
        }
        return ret;
    }

    public ArrayList<Move> getAllMoves(int currentPlayer, Piece[][] pieces) {
        ArrayList<Move> ret = new ArrayList<>();
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null)
                    if (piece.isOnMyTeam(currentPlayer)) {
                        ArrayList<Path> movingTo = piece.canMoveTo(pieces);
                        checkLegal(movingTo, piece, pieces);
                        for (Path path : movingTo) ret.add(new Move(path, piece));

                    }
            }
        }
        return ret;
    }

    public boolean isInCheck(int player, Piece[][] pieces) {
        return isThreatened(getKing(player, pieces), pieces);
    }

    public void checkLegal(ArrayList<Path> list, Piece currentPiece, Piece[][] pieces) {
        ArrayList<Path> delete = new ArrayList<>();
        specialMoves = new ArrayList<>();
        if (currentPiece instanceof King && !isInCheck(currentPiece.getPieceColor(), pieces)) {
            King king = (King) currentPiece;
            if (king.kingSide != null) {
                Castling kingSide = king.kingSide;
                if (!isSquareThreatened(kingSide.getKingMiddleMove(), getOpponent(currentPiece.getPieceColor()), pieces) &&
                        getPiece(king.kingSide.getKingMiddleMove(), pieces) == null)
                    specialMoves.add(new SpecialMove(king, new Path(kingSide.getKingFinalLoc(), false), SpecialMove.SHORT_CASTLE));
            }
            if (king.queenSide != null) {
                Castling queenSide = king.queenSide;
                if (!isSquareThreatened(queenSide.getKingMiddleMove(), getOpponent(currentPiece.getPieceColor()), pieces) &&
                        getPiece(king.queenSide.getKingMiddleMove(), pieces) == null && getPiece(king.queenSide.getRookMiddleLoc(), pieces) == null)
                    specialMoves.add(new SpecialMove(king, new Path(queenSide.getKingFinalLoc(), false), SpecialMove.LONG_CASTLE));
            }
        }
//        if(currentPiece instanceof Pawn){
//            Pawn pawn = (Pawn) currentPiece;
//            for(Piece[] row : pieces){
//                for(Piece piece: row){
//                    if(piece!=null&&!piece.isOnMyTeam(pawn)&&piece instanceof Pawn){
//                        if(((Pawn)piece).enPassant!=null){
//                            specialMoves.add(new SpecialMove(piece,new Path(((Pawn)piece).enPassant,true),SpecialMove.EN_PASSANT));
//                        }
//                    }
//                }
//            }
////            if(pawn.enPassant!=null){
////                list.add(new Path(pawn.enPassant,true));
////            }
//        }
        if (specialMoves != null) {
            for (SpecialMove specialMove : specialMoves) {
                list.add(specialMove.getPath());
            }
        }
        for (int i = 0; i < list.size(); i++) {
            Location loc = list.get(i).getLoc();
            if (!isInBounds(loc)) {
                delete.add(list.get(i));
                continue;
            }
            Piece piece = getPiece(loc, pieces);
            if (piece != null && currentPiece.isOnMyTeam(piece)) {
                delete.add(list.get(i));
                continue;
            }
            if (list.get(i).isPromoting()) {
                specialMoves.add(new SpecialMove(currentPiece, list.get(i), SpecialMove.PROMOTION));
//                delete.add(list.get(i));
//                continue;
            }

            Location storeLoc = currentPiece.getLoc();
            pieces[storeLoc.getRow()][storeLoc.getCol()] = null;
            pieces[loc.getRow()][loc.getCol()] = currentPiece;
            currentPiece.setLoc(loc);
            if (isInCheck(currentPiece.getPieceColor(), pieces)) {
                delete.add(list.get(i));
            }
            pieces[storeLoc.getRow()][storeLoc.getCol()] = currentPiece;
            pieces[loc.getRow()][loc.getCol()] = piece;
            currentPiece.setLoc(storeLoc);

        }
        for (Path path : delete)
            list.remove(path);

    }

    public boolean isInBounds(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS;
    }

    public boolean isThreatened(Piece piece, Piece[][] pieces) {
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (p != null && piece != null && !piece.isOnMyTeam(p)) {
                    ArrayList<Path> canMoveTo = p.canMoveTo(pieces);
                    if (canMoveTo != null)
                        for (Path path : canMoveTo) {
                            if (path.getLoc().isEqual(piece.getLoc()))
                                return true;
                        }

                }
            }
        }

        return false;
    }

    public Piece getKing(int player, Piece[][] pieces) {
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

    public ArrayList<Path> getMovableSquares(Piece piece, Piece[][] pieces) {

        if (piece == null) {
            return null;
        }
        ArrayList<Path> list = piece.canMoveTo(pieces);


        checkLegal(list, piece, pieces);
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

    private int gameOverInfo;

    public double checkGameStatus(int player, Piece[][] pieces) {
        return eval(player, pieces);
    }

    // מהלך מינימקס
    public MinimaxReturn getAiMove() {
        System.out.println("Model getAiMove() using MINIMAX");

        MinimaxReturn bestMove = getBestMoveUsingMinimax();

        return bestMove;
    }

    // ============================ for minimax ===========================
    private MinimaxReturn getBestMoveUsingMinimax() {
        MinimaxReturn ret = minimax(logicMat, true, 0);
        System.out.println("minimax move = " + ret);
        return ret;
    }

    private static int scanDepth = 4;
//
//    public MinimaxReturn minimax(Piece[][] board, boolean isMax, int depth, Move lastMove) {
//        MinimaxReturn bestMove = new MinimaxReturn(null, isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE);
//        int player = isMax ? controller.getCurrentPlayer() : getOpponent(controller.getCurrentPlayer());
//        if (isGameOver(board, player)&&depth<=0) {
//            return new MinimaxReturn(lastMove, eval(player, board));
//        }
//        ArrayList<Move> possibleMoves = getAllMoves(player, board);
//        if (isMax) {
//            for (int i = 0; i < possibleMoves.size(); i++) {
//                Piece[][] newBoard = copyBoard(board);
//                possibleMoves = getAllMoves(player, newBoard);
//                Move move = possibleMoves.get(i);
//                Location storePieceLocation = move.getPiece().getLoc();
//                makeMove(move.getPath().getLoc(), move.getPiece(), newBoard);
////                System.out.println("move applied: " + move);
////                printLogicMat(newBoard);
////                System.out.println();
//                MinimaxReturn val = minimax(newBoard, false, depth - 1, move);
//                move.getPiece().setLoc(storePieceLocation);
//                if (depth <= 0) {
//                    System.out.println("Move#" + (i + 1) + ": " + move + " val: " + val.value);
//                    return bestMove;
//                }
//                makeMove(storePieceLocation,move.getPiece(),newBoard);
//                posLog.remove(posLog.size() - 1);
//                //posLog.remove(posLog.size() - 2);
//                if (val.value > bestMove.getValue()) {
//                    bestMove = val;
//                }
//
//            }
//        } else {
//            for (int i = 0; i < possibleMoves.size(); i++) {
//                Piece[][] newBoard = copyBoard(board);
//                possibleMoves = getAllMoves(player, newBoard);
//                Move move = possibleMoves.get(i);
//                Location storePieceLocation = move.getPiece().getLoc();
//
//                makeMove(move.getPath().getLoc(), move.getPiece(), newBoard);
////                System.out.println("move applied: " + move);
////                printLogicMat(newBoard);
////                System.out.println();
//                MinimaxReturn val = minimax(newBoard, true, depth - 1, move);
//                move.getPiece().setLoc(storePieceLocation);
//                if (depth <= 0) {
//                    System.out.println("Move#" + (i + 1) + ": " + move + " val: " + val.value);
//                    return bestMove;
//                }
//                makeMove(storePieceLocation,move.getPiece(),newBoard);
//                posLog.remove(posLog.size() - 1);
//                //posLog.remove(posLog.size() - 2);
//                if (val.value < bestMove.getValue())
//                    bestMove = val;
//            }
//        }
//
//        return bestMove;
//    }

    public MinimaxReturn minimax(Piece[][] board, boolean isMax, int depth) {
        MinimaxReturn bestMove = new MinimaxReturn(null, isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        int player = isMax ? controller.getCurrentPlayer() : getOpponent(controller.getCurrentPlayer());
        Piece[][] newBoard = copyBoard(board);

        if (isGameOver(board, player) || depth == scanDepth) {
            return new MinimaxReturn(null, eval(player, board));
        }
        ArrayList<Move> possibleMoves = getAllMoves(player, newBoard);

        for (int i = 0; i < possibleMoves.size(); i++) {
            Move move = possibleMoves.get(i);

            Location storePieceLocation = move.getPiece().getLoc();

            makeMove(move.getPath().getLoc(), move.getPiece(), newBoard);
            posLog.remove(posLog.size() - 1);
//            System.out.println("move applied: " + move);
//            printLogicMat(newBoard);
//            System.out.println();
            MinimaxReturn val = minimax(newBoard, !isMax, depth + 1);


            //move.getPiece().setLoc(storePieceLocation);
            if (depth == 0) {
                if (bestMove.move == null)
                    bestMove.move = move;

                if (val.move == null)
                    val.move = move;

                System.out.println("Move#" + (i + 1) + ": " + move + " val: " + val.value);
            }
            if ((isMax && val.value > bestMove.getValue()) || (!isMax && val.value < bestMove.getValue())) {
                bestMove.setMove(val.move);
                bestMove.setValue(val.value);
            }

        }


        return bestMove;
    }

    private Piece[][] copyBoard(Piece[][] og) {
        return posToMat(matToPos(og));
    }

    private boolean isGameOver(Piece[][] board, int player) {
        return checkTie(player, board) || checkWin(player, board) || checkWin(getOpponent(player), board);
    }


    public double eval(int player, Piece[][] pieces) {
        if (checkTie(player, pieces) || checkWin(player, pieces) || checkWin(getOpponent(player), pieces)) {
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

    private final double halfOpenFile = 0.5, openFile = 1;

    private double compareOpenFilesControl(int player, Piece[][] pieces) {
        double otherPlayersFileControl = playerFilesControl(getOpponent(player), pieces);
        double currentPlayersFileControl = playerFilesControl(player, pieces);
        return currentPlayersFileControl - otherPlayersFileControl;
    }

    private double playerFilesControl(int player, Piece[][] pieces) {
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

    private final double doubledPawns = -0.5;

    private double comparePawnStructure(int player, Piece[][] pieces) {
        double otherPlayersStructure = countDoubledPawns(getPlayersPieces(getOpponent(player), pieces)) * doubledPawns;
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


    private int compareDevelopment(int player, Piece[][] pieces) {
        ArrayList<Piece> playerPieces = getPlayersPieces(player, pieces);
        ArrayList<Piece> opponentPieces = getPlayersPieces(getOpponent(player), pieces);
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

    private int compareMaterial(int player, Piece[][] pieces) {
        ArrayList<Piece> currentPlayerPieces = getPlayersPieces(player, pieces);
        ArrayList<Piece> opponentPieces = getPlayersPieces(getOpponent(player), pieces);
        int playerSum = 0, opponentSum = 0;
        for (Piece piece : currentPlayerPieces)
            playerSum += piece.getWorth();
        for (Piece piece : opponentPieces)
            opponentSum += piece.getWorth();
        return playerSum - opponentSum;
    }

//    private ArrayList<Piece> getThreatenedPieces(int player) {
//        ArrayList<Piece> ret = new ArrayList<>();
//
//        for (Piece piece : getPlayersPieces(player)) {
//            if (isThreatened(piece, true)) {
//                ret.add(piece);
//            }
//        }
//        return ret;
//    }

//    private int exchangeResult(int player,Piece takingWith, Piece pieceToTake,Piece[][] pieces) {
//
//        //return checkGameStatus(getOpponent(player),hypotheticalMove(takingWith,pieceToTake.getLoc(),pieces));
//    }

    private int getOpponent(int player) {
        return Math.abs(player - 1);
    }

    private boolean checkWin(int player, Piece[][] pieces) {
        int otherPlayer = getOpponent(player);
        ArrayList<Move> moves = getAllMoves(otherPlayer, pieces);
        if (isInCheck(otherPlayer, pieces)) {
            if (isListEmpty(moves)) {
                gameOverInfo = Controller.CHECKMATE;
                return true;
            }
        }
        return false;
    }

    private boolean checkTie(int player, Piece[][] pieces) {
        if (!isInCheck(player, pieces)) {
            if (isListEmpty(getPlayersPieces(getOpponent(player), pieces))) {
                gameOverInfo = Controller.STALEMATE;
                return true;
            }
            if (!canPlayerMate(player, pieces) && !canPlayerMate(getOpponent(player), pieces)) {
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

    private boolean canPlayerMate(int player, Piece[][] pieces) {
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

    private ArrayList<Piece> getPlayersPieces(int player, Piece[][] pieces) {
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


