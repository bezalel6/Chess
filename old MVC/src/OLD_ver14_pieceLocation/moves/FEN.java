package OLD_ver14_pieceLocation.moves;

import OLD_ver14_pieceLocation.types.Piece.Player;
import OLD_ver14_pieceLocation.Board;
import OLD_ver14_pieceLocation.Location;
import OLD_ver14_pieceLocation.types.Pawn;
import OLD_ver14_pieceLocation.types.Piece;

import java.util.Locale;

class CastlingAbility {
    private boolean whiteK, whiteQ, blackK, blackQ;

    public CastlingAbility(String str) {
        if (str.equals("-")) {
            whiteK = whiteQ = blackQ = blackK = false;
            return;
        }
        char arr[] = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];
            switch (c) {
                case 'K':
                    whiteK = true;
                    break;
                case 'Q':
                    whiteQ = true;
                    break;
                case 'k':
                    blackK = true;
                    break;
                case 'q':
                    blackQ = true;
            }
        }

    }

    @Override
    public String toString() {
        String ret = "";
        if (!whiteQ && whiteK && !blackQ && !blackK)
            return "-";
        if (whiteK)
            ret += 'K';
        if (whiteQ)
            ret += 'Q';
        if (blackK)
            ret += 'k';
        if (blackQ)
            ret += 'q';

        return "" + ret + "";
    }

    public boolean isWhiteK() {
        return whiteK;
    }

    public void setWhiteK(boolean whiteK) {
        this.whiteK = whiteK;
    }

    public boolean isWhiteQ() {
        return whiteQ;
    }

    public void setWhiteQ(boolean whiteQ) {
        this.whiteQ = whiteQ;
    }

    public boolean isBlackK() {
        return blackK;
    }

    public void setBlackK(boolean blackK) {
        this.blackK = blackK;
    }

    public boolean isBlackQ() {
        return blackQ;
    }

    public void setBlackQ(boolean blackQ) {
        this.blackQ = blackQ;
    }
}

public class FEN {
    private Board board;
    private String fen;
    private CastlingAbility castlingAbility;
    private Player player;

    public FEN(String fen, Board board) {
        this.fen = fen;
        this.board = board;

    }

    public String generateFEN() {
        String ret = "";
        for (Piece[] row : board) {
            int emptySquares = 0;
            for (Piece piece : row) {
                if (piece == null) {
                    emptySquares++;
                    continue;
                } else if (emptySquares != 0) {
                    ret += emptySquares;
                    emptySquares = 0;
                }
                if (piece instanceof Pawn)
                    ret += piece.isWhite() ? 'P' : 'p';
                else {

                    ret += piece.isWhite() ? piece.getAnnotation().toUpperCase(Locale.ROOT) : piece.getAnnotation().toLowerCase(Locale.ROOT);
                }
            }
            if (emptySquares != 0)
                ret += emptySquares;
            ret += "/";
        }
        ret = ret.substring(0, ret.length() - 1);
        ret += " ";
        ret += getPlayerToMove() == Player.WHITE ? "w" : "b";
        ret += " " + castlingAbility;
        Location enPassantTargetSquare = board.getEnPassantTargetSquare();
        ret += " " + (enPassantTargetSquare == null ? "-" : enPassantTargetSquare);
        ret += " " + board.getHalfMoveCounter() + " " + board.getFullMoveCounter();
        fen = ret;
        return ret;
    }

    public Player getPlayerToMove() {
        return fen.charAt(fen.indexOf(' ') + 1) == 'w' ? Player.WHITE : Player.BLACK;
    }

    private void setMoveCounters() {
        char arr[] = fen.toCharArray();
        int fullMoveCounterIndex = arr.length;
        while (arr[--fullMoveCounterIndex] != ' ') {
        }

        board.setFullMoveCounter(Integer.parseInt(fen.substring(fullMoveCounterIndex + 1)));

        int halfMoveCounterIndex = fullMoveCounterIndex;
        while (arr[--halfMoveCounterIndex] != ' ') {
        }
        board.setHalfMoveCounter(Integer.parseInt(fen.substring(halfMoveCounterIndex + 1, fullMoveCounterIndex)));

    }

    public Piece[][] loadFEN() {
        setMoveCounters();
        Piece[][] ret = new Piece[8][8];
        char[] arr = fen.toCharArray();
        int row = 0, col = 0, index = 0;
        for (char c : arr) {
            if (c != '/' && c != ' ') {
                if (Character.isLetter(c)) {
                    Location pieceLocation = new Location(row, col);
                    pieceLocation = Location.convertToMatLoc(pieceLocation);
                    ret[pieceLocation.getRow()][pieceLocation.getCol()] = Piece.createPieceFromFen(c, pieceLocation);
                    col++;
                } else col += Integer.parseInt(c + "");
            } else if (c == '/') {
                row++;
                col = 0;
            } else break;
            index++;
        }
        index += 3;
        String str = fen.substring(index);
        str = str.substring(0, str.indexOf(' '));
        castlingAbility = new CastlingAbility(str);
        index += 4;
        //en passant location
        if (arr[index] != '-') {
            board.setEnPassantTargetSquare(fen.substring(index, ++index + 1));
        } else board.setEnPassantTargetLoc(null);
        index += 2;
        board.setHalfMoveCounter(Integer.parseInt(arr[index] + ""));
        index += 2;
        board.setFullMoveCounter(Integer.parseInt(arr[index] + ""));
        return ret;
    }
}
