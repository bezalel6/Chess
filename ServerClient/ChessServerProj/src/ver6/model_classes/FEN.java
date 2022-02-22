package ver6.model_classes;


import ver6.SharedClasses.PlayerColor;
import ver6.SharedClasses.RegEx;
import ver6.SharedClasses.board_setup.Board;
import ver6.SharedClasses.board_setup.Square;
import ver6.SharedClasses.moves.CastlingRights;


public class FEN {
    public static String generateFEN(Model model) {
        StringBuilder ret = new StringBuilder();
        Board board = model.getLogicBoard();
        for (int rowNum = 7; rowNum >= 0; rowNum--) {
            Square[] row = board.getRow(rowNum);
            int emptySquaresNum = 0;
            for (Square square : row) {
                if (square.isEmpty()) {
                    emptySquaresNum++;
                } else {
                    if (emptySquaresNum != 0) {
                        ret.append(emptySquaresNum);
                    }
                    ret.append(square.getFen());

                    emptySquaresNum = 0;
                }
            }
            if (emptySquaresNum != 0) {
                ret.append(emptySquaresNum);
            }
            if (rowNum != 0) {
                ret.append("/");
            }
        }
        ret
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

        assertFen(ret.toString());
        return ret.toString();
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

        System.out.println();
    }
}
