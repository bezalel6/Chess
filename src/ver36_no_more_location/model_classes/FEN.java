package ver36_no_more_location.model_classes;

import ver36_no_more_location.Location;
import ver36_no_more_location.Player;
import ver36_no_more_location.model_classes.pieces.Piece;

public class FEN {
    private static final char[] PLAYER_NOTATION_LOOKUP = {'w', 'b'};
    private final Board board;
    private final String fen;
    private String boardStr, sideToMoveStr, castlingAbilityStr, enPassantStr, halfMoveStr, fullMoveStr;

    public FEN(String fen, Board board) {
        this.fen = fen.trim();
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

    public String generateFEN(boolean addCounters, Player player2Move) {
        StringBuilder ret = new StringBuilder(createNormalFen());
        ret = new StringBuilder(ret.substring(0, ret.length() - 1));
        ret.append(" ");
        ret.append(PLAYER_NOTATION_LOOKUP[player2Move.asInt()]);
        ret.append(" ").append(CastlingAbility.generateCastlingAbilityStr(board.getCastlingAbility()));
        Location enPassantTargetSquare = board.getEnPassantTargetLoc();
        ret.append(" ").append(enPassantTargetSquare == null ? "-" : enPassantTargetSquare);
        if (addCounters)
            ret.append(" ").append(board.getHalfMoveClock()).append(" ").append(board.getFullMoveClock());
        return ret.toString();
    }
//
//    private String createUpsideDownFEN() {
//        StringBuilder ret = new StringBuilder();
//
//        int i = board.getLogicMat().length - 1;
//        for (; i >= 0; i--) {
//            int emptySquares = 0;
//            for (Square square : board.getLogicMat()[i]) {
//                if (square.isEmpty()) {
//                    emptySquares++;
//                    continue;
//                } else if (emptySquares != 0) {
//                    ret.append(emptySquares);
//                    emptySquares = 0;
//                }
//                ret.append(square.getFen());
//            }
//            if (emptySquares != 0)
//                ret.append(emptySquares);
//            ret.append("/");
//
//        }
//        return ret.toString();
//    }

    private String createNormalFen() {
        StringBuilder ret = new StringBuilder();

        Square[] logicBoard = board.getLogicBoard();
        for (int loc = 0; loc < logicBoard.length; loc++) {
            int emptySquares = 0;
            //fixme shouldnt it be ++loc?
            while (loc++ % 8 == 0) {
                Square square = logicBoard[loc];
                if (square.isEmpty()) {
                    emptySquares++;
                } else {
                    if (emptySquares != 0) {
                        ret.append(emptySquares);
                        emptySquares = 0;
                    }
                    ret.append(square.getFen());
                }
            }
            if (emptySquares != 0)
                ret.append(emptySquares);
            ret.append("/");
        }
        return ret.toString();
    }

    private void setMoveCounters() {

        int fullMoveStartIndex = fen.lastIndexOf(' ') + 1;
        board.setFullMoveClock(Integer.parseInt(fen.substring(fullMoveStartIndex)));

        int halfMoveStartIndex = fen.substring(0, fullMoveStartIndex).length() - 2;
        board.setHalfMoveClock(Integer.parseInt(fen.substring(halfMoveStartIndex, fullMoveStartIndex).trim()));

    }

    public void loadFEN() {
        assert isValidFen(fen);
        //todo fix this with substrings and stuff
        setMoveCounters();
        char[] arr = fen.toCharArray();
        int row = 0, col = 0;

        String boardSetup = fen.split(" ")[0];
        Piece.createPieceFromFen('p');
        for (char c : arr) {
            if (c != '/' && c != ' ') {
                if (Character.isLetter(c)) {
                    Location loc = Location.getLoc(row, col++);

                    board.setPiece(loc, Piece.createPieceFromFen(c));
                } else col += Integer.parseInt(c + "");
            } else if (c == '/') {
                row++;
                col = 0;
            } else break;
        }
        String playerToMoveStr = fen.split(" ")[1];
        Player initialPlayerToMove = playerToMoveStr.charAt(0) == 'w' ? Player.WHITE : Player.BLACK;
        board.setCurrentPlayer(initialPlayerToMove);

        String str = fen.split(" ")[2];
        castlingAbilityStr = str.split(" ")[0];
        board.setCastlingAbility(CastlingAbility.createFromStr(str));

        board.setEnPassantTargetLoc(fen.split(" ")[3].trim());
    }
}
