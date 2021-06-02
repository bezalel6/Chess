package ver4;

import ver4.types.*;

import java.util.ArrayList;

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
        loadPos(positions.startingPos);
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

    private String additions = "";

    public String makeMove(Location loc, Piece piece) {

        Location prev = piece.getLoc();
        boolean captures = getPiece(loc) != null;
        additions = "";
        logicMat[loc.getRow()][loc.getCol()] = piece;
        logicMat[prev.getRow()][prev.getCol()] = null;
        String move = piece.getAnnotation();

        if (captures) {
            move += "x";
        }
        piece.setLoc(loc);
        if (piece instanceof Pawn) {
            ArrayList<Location> promotionLocs = ((Pawn) (piece)).promotionLocs;
            if (!promotionLocs.isEmpty()) {
                for (Location promLoc : promotionLocs) {
                    if (promLoc.isEqual(loc)) {
                        additions += "=" + controller.promote(piece);
                        break;
                    }
                }
            } else if (prev.getCol() == loc.getCol()) {
                move = "";
            }
        }
        if (piece instanceof King) {
            if (((King) piece).kingSide != null) {
                if (((King) piece).kingSide.getKingFinalLoc().isEqual(loc)) {
                    controller.updateView(((King) piece).kingSide.getRook().getLoc(), ((King) piece).kingSide.getRookFinalLoc());
                    makeMove(((King) piece).kingSide.getRookFinalLoc(), ((King) piece).kingSide.getRook());

                }
            }
            if (((King) piece).queenSide != null) {
                if (((King) piece).queenSide.getKingFinalLoc().isEqual(loc)) {
                    controller.updateView(((King) piece).queenSide.getRook().getLoc(), ((King) piece).queenSide.getRookFinalLoc());
                    makeMove(((King) piece).queenSide.getRookFinalLoc(), ((King) piece).queenSide.getRook());
                }
            }
        }
        if (checkWin(piece.getPieceColor(),logicMat)) {
            additions += "#";
        } else if (isInCheck(piece.getOtherColor(),logicMat))
            additions += "+";
        else if (checkTie(piece.getPieceColor(),logicMat)) {
            additions += "=";
        }
        move += loc.getStr();
        piece.setMoved();
        return (move + additions);
    }

    public boolean isSquareThreatened(Location square, int threateningPlayer) {
        for (Piece[] row : logicMat) {
            for (Piece p : row) {
                if (p != null && p.isOnMyTeam(threateningPlayer)) {
                    ArrayList<Path> canMoveTo = p.canMoveTo(logicMat);
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

    public ArrayList<Path> getAllMoves(int currentPlayer,Piece[][] pieces) {
        ArrayList<Path> ret = new ArrayList<>();
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null)
                    if (piece.getPieceColor() == currentPlayer) {
                        ArrayList<Path> movingTo = piece.canMoveTo(logicMat);
                        checkLegal(movingTo, piece,pieces);
                        ret.addAll(movingTo);

                    }
            }
        }
        return ret;
    }

    public boolean isInCheck(int player,Piece[][] pieces) {
        return isThreatened(getKing(player,pieces),pieces);
    }

    public void checkLegal(ArrayList<Path> list, Piece currentPiece,Piece[][] pieces) {
        ArrayList<Path> delete = new ArrayList<>();
        if (currentPiece instanceof King && !isInCheck(currentPiece.getPieceColor(),pieces)) {
            if (((King) currentPiece).kingSide != null) {
                Castling kingSide = ((King) currentPiece).kingSide;
                if (!isSquareThreatened(kingSide.getKingMiddleMove(), getOpponent(currentPiece.getPieceColor())) &&
                        getPiece(((King) currentPiece).kingSide.getKingMiddleMove()) == null)

                    list.add(new Path(kingSide.getKingFinalLoc(), false));

            }
            if (((King) currentPiece).queenSide != null) {
                Castling queenSide = ((King) currentPiece).queenSide;
                if (!isSquareThreatened(queenSide.getKingMiddleMove(), getOpponent(currentPiece.getPieceColor())) &&
                        getPiece(((King) currentPiece).queenSide.getKingMiddleMove()) == null && getPiece(((King) currentPiece).queenSide.getRookMiddleLoc()) == null)

                    list.add(new Path(queenSide.getKingFinalLoc(), false));
            }
        }
        for (int i = 0; i < list.size(); i++) {
            Location loc = list.get(i).getLoc();
            if (!isInBounds(loc)) {
                delete.add(list.get(i));
                continue;
            }
            Piece piece = getPiece(loc);
            if (piece != null && currentPiece.isOnMyTeam(piece)) {
                delete.add(list.get(i));
                continue;
            }
            Location storeLoc = currentPiece.getLoc();
            pieces[storeLoc.getRow()][storeLoc.getCol()] = null;
            pieces[loc.getRow()][loc.getCol()] = currentPiece;
            currentPiece.setLoc(loc);
            if (isInCheck(currentPiece.getPieceColor(),pieces)) {
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

    public boolean isThreatened(Piece piece,Piece[][] pieces) {
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (p != null && piece!=null&& !piece.isOnMyTeam(p)) {
                    ArrayList<Path> canMoveTo = p.canMoveTo(logicMat);
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

    public Piece getKing(int player,Piece[][] pieces) {
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (p != null && p instanceof King && p.getPieceColor() == player)
                    return p;
            }
        }
        return null;
    }

    public Piece getPiece(Location loc) {

        return logicMat[loc.getRow()][loc.getCol()];
    }

    public ArrayList<Path> getMovableSquares(Piece piece,Piece[][] pieces) {

        if (piece == null) {
            return null;
        }
        ArrayList<Path> list = piece.canMoveTo(pieces);


        checkLegal(list, piece,pieces);
        return list;
    }

    public Piece[][] getPieces() {
        return logicMat;
    }

    public void printLogicMat() {
        for (Piece[] row : logicMat) {
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

    public int checkGameStatus(int player,Piece[][] pieces) {
        if (checkTie(player,pieces)||checkWin(player,pieces)) {
            return gameOverInfo;
        }
//        if(checkLoss(player))
//        {
//            System.out.println(Piece.colorAStringArr[player]+" Lost!!!!");
//            return LOSS;
//        }
        return eval(player,pieces,--depth);
    }
    int depth = 5;
    private int eval(int player,Piece[][] pieces,int depth) {
        int ret = 0;
        //Material
        ret += compareMaterial(player,pieces);
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
        ret+=compareDevelopment(player,pieces);

        //Center Control

        return ret;
    }
    private int compareDevelopment(int player, Piece[][] pieces)
    {
        ArrayList<Piece> playerPieces= getPlayersPieces(player,pieces);
        ArrayList<Piece> opponentPieces = getPlayersPieces(getOpponent(player),pieces);
        int playerUndeveloped = 0,opponentUndeveloped = 0;
        for(Piece piece:playerPieces)
        {
            if(!piece.getHasMoved())
            {
                playerUndeveloped+=piece.getWorth();
            }
        }
        for(Piece piece:opponentPieces)
        {
            if(!piece.getHasMoved())
            {
                opponentUndeveloped+=piece.getWorth();
            }
        }
        return playerUndeveloped-opponentUndeveloped;
    }

    private int compareMaterial(int player,Piece[][] pieces) {
        ArrayList<Piece> currentPlayerPieces = getPlayersPieces(player,pieces);
        ArrayList<Piece> opponentPieces = getPlayersPieces(getOpponent(player),pieces);
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

    private Piece[][] hypotheticalMove(Piece current, Location dest, Piece[][] pieces) {
        String[] newPos = matToPos(pieces);
        Piece[][] newPieces = posToMat(newPos);
        newPieces[dest.getRow()][dest.getCol()] = current;
        newPieces[current.getLoc().getRow()][current.getLoc().getCol()] = null;
        current.setLoc(dest);
        return newPieces;
    }

    private int getOpponent(int player) {
        return Math.abs(player - 1);
    }

    private boolean checkWin(int player,Piece[][] pieces) {
        player = getOpponent(player);
        ArrayList<Path> moves = getAllMoves(player,pieces);
        if (isInCheck(player,pieces)) {
            if (isListEmpty(moves)) {
                gameOverInfo = Controller.CHECKMATE;
                return true;
            }
        }
        return false;
    }

//    private boolean checkLoss(int player) {
//        return checkWin(getOpponent(player));
//    }

    private boolean checkTie(int player,Piece[][] pieces) {
        if (!isInCheck(player,pieces)) {
            if (isListEmpty(getPlayersPieces(getOpponent(player),pieces))) {
                gameOverInfo = Controller.STALEMATE;
                return true;
            }
            if (!canPlayerMate(player,pieces) && !canPlayerMate(getOpponent(player),pieces)) {
                gameOverInfo = Controller.INSUFFICIENT_MATERIAL;
                return true;
            }

        }
        return false;
    }

    private boolean isListEmpty(ArrayList list) {
        return list == null || list.isEmpty();
    }

    private boolean canPlayerMate(int player,Piece[][] pieces) {
        ArrayList<Piece> currentPlayerPieces = getPlayersPieces(player,pieces);
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

    private ArrayList<Piece> getPlayersPieces(int player,Piece[][] pieces) {
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


