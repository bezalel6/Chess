package ver4.model_classes;


import ver4.SharedClasses.Location;
import ver4.SharedClasses.PlayerColor;
import ver4.SharedClasses.board_setup.Board;
import ver4.SharedClasses.board_setup.Square;
import ver4.SharedClasses.moves.CastlingRights;
import ver4.SharedClasses.pieces.Piece;


public class FEN {
    public static void assertFen(String fen) {
        assert isValidFen(fen);
    }

    public static boolean isValidFen(String fen) {
        return fen.matches("\\s*([rnbqkpRNBQKP1-8]+\\/){7}([rnbqkpRNBQKP1-8]+)\\s[bw-]\\s(([a-hkqA-HKQ]{1,4})|(-))\\s(([a-h][36])|(-))\\s\\d+\\s\\d+\\s*");
    }

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
                .append(model.getCastlingRights().getStr())
                .append(" ")
                .append((model.getEnPassantTargetLoc() == null ? "-" : model.getEnPassantTargetLoc()))
                .append(" ")
                .append(model.getHalfMoveClock())
                .append(" ")
                .append(model.getFullMoveClock());

        assertFen(ret.toString());
        return ret.toString();
    }


    public static void loadFEN(String fen, Model model) {
        assertFen(fen);

        String[] segments = fen.split(" ");
        String pieces = segments[0];
        String sideToMove = segments[1];
        String castlingAbility = segments[2];
        String enPassantTargetSquare = segments[3];
        String halfMoveCLock = segments[4];
        String fullMoveCLock = segments[5];

        //pieces setup
        String[] rows = pieces.split("/");
        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            String row = rows[rowIndex];
            int col = 0;
            for (char currentChar : row.toCharArray()) {
                if (Character.isLetter(currentChar)) {
                    Location currentLoc = Location.getLoc(rowIndex, col);
                    Piece piece = Piece.getPieceFromFen(currentChar);
                    model.getLogicBoard().setPiece(currentLoc, piece);

                    col++;
                } else {
                    col += Integer.parseInt(currentChar + "");
                }
            }
        }

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
