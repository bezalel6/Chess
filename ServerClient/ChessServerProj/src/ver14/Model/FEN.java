package ver14.Model;


import ver14.SharedClasses.Game.BoardSetup.Board;
import ver14.SharedClasses.Game.BoardSetup.Square;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.moves.CastlingRights;
import ver14.SharedClasses.RegEx;


public class FEN {
    private static final boolean flipLocs = Location.flip_fen_locs;

    public static void main(String[] args) {
        System.out.println(generateFEN(new Model() {{
            setup(null);
        }}));
    }

    public static String generateFEN(Model model) {
        StringBuilder bldr = new StringBuilder();
        Board board = model.getLogicBoard();
        normalGen(bldr, board);

        bldr
                .append(" ")
                .append(model.getCurrentPlayer() == PlayerColor.WHITE ? "w" : "b")
                .append(" ")
                .append(model.getCastlingRights().toString())
                .append(" ")
                .append((model.getEnPassantTargetLoc() == null ? "-" : model.getEnPassantTargetLoc()))
                .append(" ")
                .append(model.getHalfMoveClock())
                .append(" ")
                .append(model.getFullMoveClock());

        assertFen(bldr.toString());
        return bldr.toString();
    }

    private static void normalGen(StringBuilder bldr, Board board) {
        for (int rowNum = 0; rowNum < 8; rowNum++) {
            Square[] row = board.getRow(rowNum, flipLocs);
            int emptySquaresNum = 0;
            for (Square square : row) {
                if (square.isEmpty()) {
                    emptySquaresNum++;
                } else {
                    if (emptySquaresNum != 0) {
                        bldr.append(emptySquaresNum);
                    }
                    bldr.append(square.getFen());

                    emptySquaresNum = 0;
                }
            }
            if (emptySquaresNum != 0) {
                bldr.append(emptySquaresNum);
            }
            if (rowNum != 8 - 1) {
                bldr.append("/");
            }
        }
    }


    public static void assertFen(String fen) {
        assert isValidFen(fen);
    }

    public static boolean isValidFen(String fen) {
        return RegEx.Fen.check(fen);
    }

    public static void loadFEN(String fen, Model model) {
        assertFen(fen);

        String[] segments = fen.split(" ");
//        String pieces = segments[0];
        String sideToMove = segments[1];
        String castlingAbility = segments[2];
        String enPassantTargetSquare = segments[3];
        String halfMoveCLock = segments[4];
        String fullMoveCLock = segments[5];

        model.getLogicBoard().fenSetup(fen);

        //current player setup
        model.setCurrentPlayer(PlayerColor.getPlayerFromFen(sideToMove));

        //castling ability setup
        model.setCastlingAbilities(CastlingRights.createFromStr(castlingAbility));

        //en passant target square setup
        model.setEnPassantTargetLoc(enPassantTargetSquare);

        //halfmove clock setup
        model.setHalfMoveClock(Integer.parseInt(halfMoveCLock));

        //fullmove clock setup
        model.setFullMoveClock(Integer.parseInt(fullMoveCLock));

    }
}
