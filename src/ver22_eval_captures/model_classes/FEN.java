package ver22_eval_captures.model_classes;

import ver22_eval_captures.types.Piece.Player;
import ver22_eval_captures.Location;
import ver22_eval_captures.types.Piece;

public class FEN {
    private static final char[] PLAYER_NOTION_LOOKUP = {'w', 'b'};
    private final Board board;
    private final String fen;
    private String castlingAbilityStr;
    private int playerToMove;

    public FEN(String fen, Board board) {
        this.fen = fen;
        this.board = board;

    }

    public String generateFEN() {
        StringBuilder ret = new StringBuilder();
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
            for (Square square : board.getLogicMat()[i]) {
                if (square.isEmpty()) {
                    emptySquares++;
                    continue;
                } else if (emptySquares != 0) {
                    ret.append(emptySquares);
                    emptySquares = 0;
                }
                ret.append(square.getFen());
            }
            if (emptySquares != 0)
                ret.append(emptySquares);
            ret.append("/");

        }
        ret = new StringBuilder(ret.substring(0, ret.length() - 1));
        ret.append(" ");
        ret.append(PLAYER_NOTION_LOOKUP[playerToMove]);
        ret.append(" ").append(board.getCastlingAbility());
        Location enPassantTargetSquare = board.getEnPassantTargetLoc();
        ret.append(" ").append(enPassantTargetSquare == null ? "-" : enPassantTargetSquare);
        ret.append(" ").append(board.getHalfMoveClock()).append(" ").append(board.getFullMoveClock());
        return ret.toString();
    }

    public int getPlayerToMove() {
        return playerToMove;
    }

    private void setMoveCounters() {
        char[] arr = fen.toCharArray();
        int fullMoveCounterIndex = arr.length;
        while (arr[--fullMoveCounterIndex] != ' ') {
        }

        board.setFullMoveClock(Integer.parseInt(fen.substring(fullMoveCounterIndex + 1)));

        int halfMoveCounterIndex = fullMoveCounterIndex;
        while (arr[--halfMoveCounterIndex] != ' ') {
        }
        board.setHalfMoveClock(Integer.parseInt(fen.substring(halfMoveCounterIndex + 1, fullMoveCounterIndex)));

    }

    public void loadFEN() {
        //todo fix this with substrings and stuff
        setMoveCounters();
        char[] arr = fen.toCharArray();
        int row = 0, col = 0, index = 0;
        for (char c : arr) {
            if (c != '/' && c != ' ') {
                if (Character.isLetter(c)) {
                    Location loc = new Location(row, col++);
                    loc = Location.convertToMatLoc(loc);
                    board.setPiece(loc, Piece.createPieceFromFen(c, loc));
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
        board.setEnPassantTargetLoc(fen.substring(index, ++index + 1));
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
