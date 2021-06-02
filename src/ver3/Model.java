package ver3;

import ver3.types.*;

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
        logicMat = new Piece[ROWS][COLS];
        for (String str : pieces) {
            int color = str.charAt(0) - '0', type = str.charAt(1) - '0';
            Location loc = new Location(str.substring(2));
            int r = loc.getRow(), l = loc.getCol();

            switch (type) {
                case (Piece.KNIGHT):
                    logicMat[r][l] = new Knight(loc, color);
                    break;
                case (Piece.BISHOP):
                    logicMat[r][l] = new Bishop(loc, color);
                    break;
                case (Piece.ROOK):
                    logicMat[r][l] = new Rook(loc, color);
                    break;
                case (Piece.KING):
                    logicMat[r][l] = new King(loc, color);
                    break;
                case (Piece.QUEEN):
                    logicMat[r][l] = new Queen(loc, color);
                    break;
                default:
                    logicMat[r][l] = new Pawn(loc, color);
                    logicMat[r][l].setAnnotation(loc.getStr().charAt(0) + "");

            }

        }

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
                    controller.updateView(((King) piece).kingSide.getRook().getLoc(),((King) piece).kingSide.getRookFinalLoc());
                    makeMove(((King) piece).kingSide.getRookFinalLoc(), ((King) piece).kingSide.getRook());

                }
            }
            if (((King) piece).queenSide != null) {
                if (((King) piece).queenSide.getKingFinalLoc().isEqual(loc)) {
                    controller.updateView(((King) piece).queenSide.getRook().getLoc(),((King) piece).queenSide.getRookFinalLoc());
                    makeMove(((King) piece).queenSide.getRookFinalLoc(), ((King) piece).queenSide.getRook());
                }
            }
        }
        if (checkWin(piece.getPieceColor())) {
            additions += "#";
        } else if (isInCheck(piece.getOtherColor()))
            additions += "+";
        else if (checkTie(piece.getPieceColor())) {
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
                    ArrayList<Path> canMoveTo = p.canMoveTo(logicMat, true);
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

    public ArrayList<Path> getAllMoves(int currentPlayer) {
        ArrayList<Path> ret = new ArrayList<>();
        for (Piece[] row : logicMat) {
            for (Piece piece : row) {
                if (piece != null)
                    if (piece.getPieceColor() == currentPlayer) {
                        ArrayList<Path> movingTo = piece.canMoveTo(logicMat, true);
                        checkLegal(movingTo, piece);
                        ret.addAll(movingTo);

                    }
            }
        }
        return ret;
    }

    public boolean isInCheck(int player) {
        return isThreatened(getKing(player), false);
    }

    public void checkLegal(ArrayList<Path> list, Piece currentPiece) {
        ArrayList<Path> delete = new ArrayList<>();
        if (currentPiece instanceof King&&!isInCheck(currentPiece.getPieceColor())) {
            if (((King) currentPiece).kingSide != null) {
                Castling kingSide = ((King) currentPiece).kingSide;
                if(!isSquareThreatened(kingSide.getKingMiddleMove(),getOpponent(currentPiece.getPieceColor())))
                list.add(new Path(kingSide.getKingFinalLoc(), false));

            }
            if (((King) currentPiece).queenSide != null) {
                Castling queenSide = ((King) currentPiece).queenSide;
                if(!isSquareThreatened(queenSide.getKingMiddleMove(),getOpponent(currentPiece.getPieceColor())))
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
            logicMat[storeLoc.getRow()][storeLoc.getCol()] = null;
            logicMat[loc.getRow()][loc.getCol()] = currentPiece;
            currentPiece.setLoc(loc);
            if (isInCheck(currentPiece.getPieceColor())) {
                delete.add(list.get(i));
            }
            logicMat[storeLoc.getRow()][storeLoc.getCol()] = currentPiece;
            logicMat[loc.getRow()][loc.getCol()] = piece;
            currentPiece.setLoc(storeLoc);

        }
        for (Path path : delete)
            list.remove(path);

    }

    public boolean isInBounds(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS;
    }

    public boolean isThreatened(Piece piece, boolean isCheckKing) {
        isCheckKing = true;
        for (Piece[] row : logicMat) {
            for (Piece p : row) {
                if (p != null && !piece.isOnMyTeam(p)) {
                    ArrayList<Path> canMoveTo = p.canMoveTo(logicMat, isCheckKing);
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

    public Piece getKing(int player) {
        for (Piece[] row : logicMat) {
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

    public ArrayList<Path> getMovableSquares(Piece piece) {

        if (piece == null) {
            return null;
        }
        ArrayList<Path> list = piece.canMoveTo(logicMat, true);


        checkLegal(list, piece);
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

    public int checkGameStatus(int player) {
        System.out.println("checking game status....");
        if (checkTie(player)) {
            System.out.println("tie");
            return gameOverInfo;
        }
        if (checkWin(player)) {
            System.out.println(Piece.colorAStringArr[player] + " Won!!!!");

            return gameOverInfo;
        }
//        if(checkLoss(player))
//        {
//            System.out.println(Piece.colorAStringArr[player]+" Lost!!!!");
//            return LOSS;
//        }
        return 55;
    }

//    private int eval(int player) {
//    }

    private int getOpponent(int player) {
        return Math.abs(player - 1);
    }

    private boolean checkWin(int player) {
        player = getOpponent(player);
        ArrayList<Path> moves = getAllMoves(player);
        if (isInCheck(player)) {
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

    private boolean checkTie(int player) {
        if (!isInCheck(player)) {
            if (isListEmpty(getPlayersPieces(getOpponent(player)))) {
                gameOverInfo = Controller.STALEMATE;
                return true;
            }
            if (!canPlayerMate(player) && !canPlayerMate(getOpponent(player))) {
                gameOverInfo = Controller.INSUFFICIENT_MATERIAL;
                return true;
            }

        }
        return false;
    }

    private boolean isListEmpty(ArrayList list) {
        return list == null || list.isEmpty();
    }

    private boolean canPlayerMate(int player) {
        ArrayList<Piece> pieces = getPlayersPieces(player);
        if (pieces.size() <= 1)
            return false;
        int numOfKnights = 0, numOfBishops = 0;
        for (Piece piece : pieces) {
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

    private ArrayList<Piece> getPlayersPieces(int player) {
        ArrayList<Piece> ret = new ArrayList<>();
        for (Piece[] row : logicMat) {
            for (Piece piece : row) {
                if (piece != null && piece.isOnMyTeam(player))
                    ret.add(piece);
            }
        }
        return ret;
    }
}


