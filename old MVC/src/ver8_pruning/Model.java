package ver8_pruning;

import ver8_pruning.Move.SpecialMoves;
import ver8_pruning.types.Piece.colors;
import ver8_pruning.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

class threads implements Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread t;
    private Piece[][] board;
    private boolean isMax;
    private int depth;
    private double a, b;
    private Move move;
    private Model model;
    private int threadId;
    private Thread worker;
    private int interval = 10;


    public threads(Piece[][] board, boolean isMax, int depth, double a, double b, Move move, Model model, int threadId) {
        this.threadId = threadId;
        this.model = model;
        this.board = board;
        this.isMax = isMax;
        this.depth = depth;
        this.a = a;
        this.b = b;
        this.move = move;
    }

    public void stop() {
        running.set(false);
    }

    public void run() {
        running.set(true);
        while (running.get()) {
//            try {
//                Thread.sleep(interval);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                System.out.println(
//                        "Thread was interrupted, Failed to complete operation");
//            }
            Location storePieceLocation = move.getPiece().getLoc();
            model.applyMove(move, board);

            MinimaxReturn tmp = model.minimax(board, isMax, depth, a, b);
            MinimaxReturn ret = new MinimaxReturn(move, tmp.getEval());
            running.set(false);
            model.applyMove(new Move(move.getPiece(), storePieceLocation, false), board);
            //model.printLogicMat(board);
            System.out.println("thread res = " + ret);
        }
        //System.out.println("thread " + threadId + " stopped");
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            System.out.println("thread " + threadId + " starting");
            t.start();
        }
    }
}


public class Model {
    private static int ROWS;
    private static int COLS;
    private final double halfOpenFile = 0.5, openFile = 1;
    private final double doubledPawns = -0.5;
    private Piece[][] logicMat;
    private Controller controller;
    private ArrayList<String[]> posLog = new ArrayList<>();
    private String additions = "";

    public Model(int boardSize, Controller controller) {
        this.controller = controller;
        ROWS = COLS = boardSize;
    }

    public void initGame(int startingPosition) {
        logicMat = loadPos(Positions.posToPieces(Positions.getAllPositions[startingPosition]));
    }

    public Piece[][] loadPos(Piece[] pos) {
        Piece[][] ret = new Piece[ROWS][COLS];
        for (Piece piece : pos) {
            if (piece != null)
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
        if (move.getSpecialMove() == SpecialMoves.EN_PASSANT && piece instanceof Pawn) {
            Location loc = move.getEnPassantActualCapturingLoc();
            Piece capturingPiece = getPiece(loc, board);
            controller.specialUpdateView(capturingPiece.getLoc(), loc);
            applyMove(new Move(capturingPiece, loc, false), board);
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

        if (checkWin(piece.getPieceColor(), board).isGameOver()) {
            additions += "#";
        } else if (isInCheck(piece.getOtherColor(), board))
            additions += "+";
        else if (checkTie(piece.getPieceColor(), board).isGameOver()) {
            additions += "½½";
        }
        pieceAnnotation += movingTo.toString();
        piece.setMoved();
        String retStr = (pieceAnnotation + additions);

        return retStr;
    }

    public void applyMove(Move move, Piece[][] board) {
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

    public void replacePiece(Piece replacingWith, Piece[][] board) {
        board[replacingWith.getLoc().getRow()][replacingWith.getLoc().getCol()] = replacingWith;
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

    private boolean isLocInMoveList(ArrayList<Move> list, Location loc) {
        for (Move move : list) {
            if (move.getMovingTo().isEqual(loc))
                return true;
        }
        return false;
    }

    public void checkLegal(ArrayList<Move> list, Piece[][] pieces) {
        if (list.isEmpty())
            return;

        ArrayList<Move> delete = new ArrayList<>();
        Piece currentPiece = list.get(0).getPiece();

        if (currentPiece instanceof Pawn) {
            ArrayList<Piece> otherPlayersPieces = getPlayersPieces(currentPiece.getOtherColor(), pieces);
            for (Piece piece : otherPlayersPieces) {
                if (piece instanceof Pawn && ((Pawn) piece).enPassant != null) {
                    Piece[][] copy = copyBoard(pieces);
                    Location storeLoc = piece.getLoc();
                    applyMove(new Move(piece, ((Pawn) piece).enPassant, false), copy);
                    //replacePiece(piece, copy);
                    if (isLocInMoveList(currentPiece.canMoveTo(copy), piece.getLoc())) {
                        Move thisMove = new Move(currentPiece, ((Pawn) piece).enPassant, storeLoc);
                        thisMove.setSpecialMove(SpecialMoves.EN_PASSANT);
                        list.add(thisMove);
                    }
                    applyMove(new Move(piece, storeLoc, false), copy);
                }
            }
        }

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
                } else if (specialMove == SpecialMoves.PROMOTION) {

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
        if (isInBounds(loc))
            return pieces[loc.getRow()][loc.getCol()];
        return null;
    }

    public ArrayList<Move> getMoves(Piece piece, Piece[][] pieces) {

        if (piece == null) {
            return null;
        }
        ArrayList list = piece.canMoveTo(pieces);

        checkLegal(list, pieces);
        return list;
    }

    public Piece[] getPiecesArr() {
        ArrayList<Piece> ret = new ArrayList<>();
        for (Piece[] row : logicMat) {
            ret.addAll(Arrays.asList(row));
        }
        return ret.toArray(new Piece[]{});
    }

    public Piece[][] getPieces() {
        return logicMat;
    }

    public void setPieces(Piece[][] board) {
        logicMat = board;
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

                    String prt = piece.getAnnotation();
                    if (prt.equals(""))
                        prt += " ";
                    System.out.print(prt);
                }
                System.out.print("|");
            }
            System.out.println();
        }
    }

    public Eval checkGameStatus(colors player, Piece[][] pieces) {
        return eval(player, pieces);
    }

    // מהלך מינימקס
    public Move getAiMove() {
        System.out.println("Model getAiMove() using MINIMAX");

        MinimaxReturn bestMove = getBestMoveUsingMinimax();
        if (bestMove != null)
            return bestMove.getMove();
        return null;
    }

    // ============================ for minimax ===========================
    private MinimaxReturn getBestMoveUsingMinimax() {
        MinimaxReturn ret = minimax(logicMat, true, 0, Double.MIN_VALUE, Double.MAX_VALUE);
        System.out.println("minimax move = " + ret);
        return ret;

//        createThreads(logicMat, true, 0, Double.MIN_VALUE, Double.MAX_VALUE);
//        return null;
    }

    private void createThreads(Piece[][] board, boolean isMax, int depth, double a, double b) {
        colors actualPlayer = controller.getCurrentPlayer();
        board = copyBoard(board);
        ArrayList<Move> possibleMoves = getAllMoves(actualPlayer, board);

        for (int i = 0, possibleMovesSize = possibleMoves.size(); i < possibleMovesSize; i++) {
            Move move = possibleMoves.get(i);
            threads thread = new threads(board, isMax, depth, a, b, move, this, i);
            thread.start();
        }
    }

    public MinimaxReturn minimax(Piece[][] board, boolean isMax, int depth, double a, double b) {
        MinimaxReturn bestMove = new MinimaxReturn(null, new Eval(isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE));
        colors actualPlayer = controller.getCurrentPlayer();
        colors player = isMax ? actualPlayer : colors.getOtherColor(actualPlayer);
        Piece[][] newBoard = copyBoard(board);

        ArrayList<Move> possibleMoves = getAllMoves(player, newBoard);
        Eval value = eval(actualPlayer, newBoard);
        if (value.isGameOver() || depth >= controller.getScanDepth()) {
            return new MinimaxReturn(null, value, depth);
        }

        for (int i = 0; i < possibleMoves.size(); i++) {
            Move move = possibleMoves.get(i);
            applyMove(move, newBoard);

//            System.out.println("move applied: " + move);
//            printLogicMat(newBoard);

            MinimaxReturn val = minimax(newBoard, !isMax, depth + 1, a, b);
            val.setMove(move);

            undoMove(move, newBoard);
            if (depth == 0) {
                System.out.println("Move#" + (i + 1) + ": " + move + " val: " + val.getEval() + " at depth " + val.getDepth());
            }
            if (isMax) {
                if (bestMove.getEval().getEval() < val.getEval().getEval()) {
                    bestMove = val;
                }
//                if (bestMove.getEval().getEval() == val.getEval().getEval()) {
//                    if (bestMove.getDepth() > val.getDepth()) {
//                        bestMove = val;
//                    }
//                }
                a = Math.max(a, bestMove.getEval().getEval());

            } else {
                if (bestMove.getEval().getEval() > val.getEval().getEval()) {
                    bestMove = val;
                }
//                if (bestMove.getEval().getEval() == val.getEval().getEval()) {
//                    if (bestMove.getDepth() < val.getDepth()) {
//                        bestMove = val;
//                    }
//                }
                b = Math.min(b, bestMove.getEval().getEval());
            }
            if (b <= a) break;

        }
        return bestMove;
    }

    private void undoMove(Move move, Piece[][] board) {
        Piece piece = move.getPiece();
        Location prev = move.getMovingTo();
        Location movingTo = move.getMovingFrom();
        board[movingTo.getRow()][movingTo.getCol()] = piece;
        board[prev.getRow()][prev.getCol()] = null;
        piece.setLoc(movingTo);
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

    private Eval isGameOver(Piece[][] board, colors player) {
        Eval tie = checkTie(player, board);
        Eval win = checkWin(player, board);
        Eval loss = checkLoss(player, board);

        return win.isGameOver() ? win : loss.isGameOver() ? loss : tie;
    }

    public Eval eval(colors player, Piece[][] pieces) {

        Eval gameOver = isGameOver(pieces, player);
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

        return new Eval(ret, GameStatus.GAME_GOES_ON);
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

    private Eval checkLoss(colors player, Piece[][] pieces) {
        ArrayList<Move> moves = getAllMoves(player, pieces);
        if (isInCheck(player, pieces)) {
            if (isListEmpty(moves)) {
                return new Eval(GameStatus.LOSS);
            }
        }
        return new Eval();
    }

    private Eval checkWin(colors player, Piece[][] pieces) {
        colors otherPlayer = colors.getOtherColor(player);
        ArrayList<Move> moves = getAllMoves(otherPlayer, pieces);
        if (isInCheck(otherPlayer, pieces)) {
            if (isListEmpty(moves)) {
                return new Eval(GameStatus.CHECKMATE);
            }
        }
        return new Eval();
    }

    private Eval checkTie(colors player, Piece[][] pieces) {
        if (!isInCheck(player, pieces)) {
            if (isListEmpty(getPlayersPieces(colors.getOtherColor(player), pieces))) {
                return new Eval(GameStatus.STALEMATE);
            }
            if (!canPlayerMate(player, pieces) && !canPlayerMate(colors.getOtherColor(player), pieces)) {
                return new Eval(GameStatus.INSUFFICIENT_MATERIAL);
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
                    return new Eval(GameStatus.REPETITION);
                }
            }

        }

        return new Eval();
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


