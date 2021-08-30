package ver18_icon_manager.moves;

import ver18_icon_manager.types.Piece.Player;
import ver18_icon_manager.Board;
import ver18_icon_manager.Location;
import ver18_icon_manager.types.Pawn;
import ver18_icon_manager.types.Piece;

import java.util.Locale;

public class FEN {
    private static char[] PLAYER_NOTION_LOOKUP = {'w', 'b'};
    private Board board;
    private String fen;
    private String castlingAbilityStr;
    private int playerToMove;

    public FEN(String fen, Board board) {
        this.fen = fen;
        this.board = board;

    }

    public String generateFEN() {
        String ret = "";
//        for (Piece[] row : board) {
//            int emptySquares = 0;
//            for (Piece piece : row) {
//                if (piece == null) {
//                    emptySquares++;
//                    continue;
//                } else if (emptySquares != 0) {
//                    ret += emptySquares;
//                    emptySquares = 0;
//                }
//                if (piece instanceof Pawn)
//                    ret += piece.isWhite() ? 'P' : 'p';
//                else {
//
//                    ret += piece.isWhite() ? piece.getAnnotation().toUpperCase(Locale.ROOT) : piece.getAnnotation().toLowerCase(Locale.ROOT);
//                }
//            }
//            if (emptySquares != 0)
//                ret += emptySquares;
//            ret += "/";
//        }

        int i = board.getLogicMat().length - 1;
        for (; i >= 0; i--) {
            int emptySquares = 0;
            for (Piece piece : board.getLogicMat()[i]) {
                if (piece == null) {
                    emptySquares++;
                    continue;
                } else if (emptySquares != 0) {
                    ret += emptySquares;
                    emptySquares = 0;
                }
                //todo make this better
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
        ret += " " + board.getCastlingAbility();
        Location enPassantTargetSquare = board.getEnPassantTargetLoc();
        ret += " " + (enPassantTargetSquare == null ? "-" : enPassantTargetSquare);
        ret += " " + board.getHalfMoveCounter() + " " + board.getFullMoveCounter();
        return ret;
    }

    public int getPlayerToMove() {
        return playerToMove;
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
        //todo fix this with substrings and stuff
        setMoveCounters();
        Piece[][] ret = new Piece[8][8];
        char[] arr = fen.toCharArray();
        int row = 0, col = 0, index = 0;
        for (char c : arr) {
            if (c != '/' && c != ' ') {
                if (Character.isLetter(c)) {
                    Location loc = new Location(row, col++);
                    loc = Location.convertToMatLoc(loc);
                    Piece piece = Piece.createPieceFromFen(c, loc);
//                    if (piece instanceof King) {
//                        board.setKingLoc(piece.getPieceColor(), loc);
//                    }
                    ret[loc.getRow()][loc.getCol()] = piece;
                } else col += Integer.parseInt(c + "");
            } else if (c == '/') {
                row++;
                col = 0;
            } else break;
            index++;
        }
        playerToMove = arr[index + 1] == 'w' ? Player.WHITE : Player.BLACK;
        board.setCurrentPlayer(playerToMove);
        index += 3;
        String str = fen.substring(index);
        str = str.substring(0, str.indexOf(' '));
        castlingAbilityStr = str;
        index += str.length();
        //en passant location

        board.setEnPassantTargetLoc(fen.substring(index, ++index + 1));
        if (arr[index - 1] == '-') {
            index++;
        }
        index += 2;
        board.setHalfMoveCounter(Integer.parseInt(arr[index] + ""));
        index += 2;
        board.setFullMoveCounter(Integer.parseInt(arr[index] + ""));
        return ret;
    }

    /**
     * ONLY USED ON INIT OF THE BOARD
     *
     * @return
     */
    public String getCastlingAbilityStr() {
        return castlingAbilityStr;
    }
}
