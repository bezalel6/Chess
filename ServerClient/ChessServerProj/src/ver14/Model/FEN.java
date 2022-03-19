package ver14.Model;


import ver14.SharedClasses.PlayerColor;
import ver14.SharedClasses.RegEx;
import ver14.SharedClasses.board_setup.Board;
import ver14.SharedClasses.board_setup.Square;
import ver14.SharedClasses.moves.CastlingRights;

import java.util.Random;


/**
 * The type Fen.
 */
public class FEN {

    /**
     * The constant rndFens.
     */
    public static final String[] rndFens = {"rnb1kb1r/pppppppp/3q1n2/8/4K3/8/PPPPPPPP/RNBQ1BNR w kq - 0 1", "3K4/n2b3q/1PP1r1p1/2k1P3/5Qp1/3P1N2/P4P2/8 w - - 0 1", "3K4/n2b3q/1PP1r1p1/2k1P3/5Qp1/3P1N2/P4P2/8 w - - 0 1", "8/4b2p/P5Bk/3KR1p1/1N1pp1pP/q7/1QP5/8 w - - 0 1", "5q2/2n1p3/1P1P1PN1/7K/5p2/1n6/b1P2kPP/3B4 w - - 0 1", "k5n1/3pbp2/2P2P2/p2N4/P1PP3R/8/2K3Qp/8 w - - 0 1", "4B2r/8/1P6/6K1/qP2p3/Q1P2Rp1/2P3kp/Nb6 w - - 0 1"};


    /**
     * Rnd fen string.
     * not actually random just random element from the arr
     *
     * @return the string
     */
    public static String rndFen() {
        return rndFens[new Random().nextInt(rndFens.length)];
    }

    public static void main(String[] args) {
        System.out.println(generateFEN(new Model() {{
            setup(null);
        }}));
    }

    /**
     * Generate fen string.
     *
     * @param model the model
     * @return the string
     */
    public static String generateFEN(Model model) {
        StringBuilder ret = new StringBuilder();
        Board board = model.getLogicBoard();
        for (int rowNum = 0; rowNum < 8; rowNum++) {
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
            if (rowNum != 8 - 1) {
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

    /**
     * Assert fen.
     *
     * @param fen the fen
     */
    public static void assertFen(String fen) {
        assert isValidFen(fen);
    }

    /**
     * Is valid fen boolean.
     *
     * @param fen the fen
     * @return the boolean
     */
    public static boolean isValidFen(String fen) {
        return RegEx.Fen.check(fen);
    }

    /**
     * Load fen.
     *
     * @param fen   the fen
     * @param model the model
     */
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
