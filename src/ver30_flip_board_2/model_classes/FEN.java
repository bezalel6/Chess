package ver30_flip_board_2.model_classes;

import ver30_flip_board_2.Location;
import ver30_flip_board_2.Player;
import ver30_flip_board_2.model_classes.pieces.Piece;

public class FEN {
    private static final char[] PLAYER_NOTATION_LOOKUP = {'w', 'b'};
    private final Board board;
    private final String fen;
    private String boardStr, sideToMoveStr, castlingAbilityStr, enPassantStr, halfMoveStr, fullMoveStr;
    private int initialPlayerToMove;

    public FEN(String fen, Board board) {
        this.fen = fen;
        this.board = board;

    }

    public FEN(Board board) {
        this("", board);

    }

    public static boolean isValidFen(String fen) {
        return fen.matches("\\s*([rnbqkpRNBQKP1-8]+\\/){7}([rnbqkpRNBQKP1-8]+)\\s[bw-]\\s(([a-hkqA-HKQ]{1,4})|(-))\\s(([a-h][36])|(-))\\s\\d+\\s\\d+\\s*");
    }

    public String generateFEN() {
        return generateFEN(true);
    }

    public String generateFEN(boolean addCounters) {
        return generateFEN(addCounters, board.getCurrentPlayer());
    }

    public String generateFEN(boolean addCounters, int player2Move) {
        StringBuilder ret = new StringBuilder();

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
        ret.append(PLAYER_NOTATION_LOOKUP[player2Move]);
        ret.append(" ").append(board.getCastlingAbility());
        Location enPassantTargetSquare = board.getEnPassantTargetLoc();
        ret.append(" ").append(enPassantTargetSquare == null ? "-" : enPassantTargetSquare);
        if (addCounters)
            ret.append(" ").append(board.getHalfMoveClock()).append(" ").append(board.getFullMoveClock());
        return ret.toString();
    }

    public int getInitialPlayerToMove() {
        return initialPlayerToMove;
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
        assert isValidFen(fen);
        //todo fix this with substrings and stuff
        setMoveCounters();
        char[] arr = fen.toCharArray();
        int row = 0, col = 0, index = 0;
        for (char c : arr) {
            if (c != '/' && c != ' ') {
                if (Character.isLetter(c)) {
                    Location loc = new Location(row, col++);
                    
                    board.setPiece(loc, Piece.createPieceFromFen(c, loc));
                } else col += Integer.parseInt(c + "");
            } else if (c == '/') {
                row++;
                col = 0;
            } else break;
            index++;
        }
        initialPlayerToMove = arr[index + 1] == 'w' ? Player.WHITE : Player.BLACK;
        board.setCurrentPlayer(initialPlayerToMove);
        index += 3;
        String str = fen.substring(index);
        str = str.substring(0, str.indexOf(' '));
        castlingAbilityStr = str;
        index += str.length();
        index++;
        board.setEnPassantTargetLoc(fen.substring(index, index + 2));
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
